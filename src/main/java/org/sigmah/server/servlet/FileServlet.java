package org.sigmah.server.servlet;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.gwt.http.client.Response;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.MonitoredPointDAO;
import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.reminder.MonitoredPointList;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.BackupArchiveManager;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.file.LogoManager;
import org.sigmah.server.file.util.MultipartRequest;
import org.sigmah.server.file.util.MultipartRequestCallback;
import org.sigmah.server.handler.util.Conflicts;
import org.sigmah.server.service.util.ImageMinimizer;
import org.sigmah.server.servlet.base.AbstractServlet;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.base.StatusServletException;
import org.sigmah.server.servlet.util.ResponseHelper;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.servlet.FileUploadResponse;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.util.FileType;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * File upload and download servlet.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class FileServlet extends AbstractServlet {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8126580127468427311L;

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(FileServlet.class);

	/**
	 * Injected application {@link FileStorageProvider}.
	 */
	@Inject
	private FileStorageProvider fileStorageProvider;

	/**
	 * Injected application {@link LogoManager}.
	 */
	@Inject
	private LogoManager logoManager;

	/**
	 * Injected {@link OrganizationDAO}.
	 */
	@Inject
	private OrganizationDAO organizationDAO;

	/**
	 * Injected {@link FileDAO}.
	 */
	@Inject
	private FileDAO fileDAO;

	/**
	 * Injected {@link BackupArchiveManager}.
	 */
	@Inject
	private BackupArchiveManager backupArchiveManager;

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	/**
	 * Injected {@link MonitoredPointDAO}.
	 */
	@Inject
	private MonitoredPointDAO monitoredPointDAO;
	
	/**
	 * Injected {@link Conflicts}.
	 */
	@Inject
	private Conflicts conflicts;

	@Inject
	private ImageMinimizer imageMinimizer;

	// ---------------------------------------------------------------------------------------
	//
	// DOWNLOAD METHODS.
	//
	// ---------------------------------------------------------------------------------------

	/**
	 * See {@link ServletMethod#DOWNLOAD_LOGO} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void downloadLogo(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		// Retrieves the file id.
		final String id = getParameter(request, RequestParameter.ID, false);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Downloads logo with id '{}'.", id);
		}

		try {

			downloadBase64(id, fileStorageProvider.open(id), response);

		} catch (final NoSuchFileException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("No logo found for id '" + id + "'.", e);
			}
			throw new StatusServletException(Response.SC_NOT_FOUND, e);
		}
	}

	/**
	 * See {@link ServletMethod#DOWNLOAD_FILE} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void downloadFile(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		// Retrieves the file version id.
		final Integer fileVersionId = getIntegerParameter(request, RequestParameter.ID, false);

		LOG.debug("Downloads file with version id '{}'.", fileVersionId);

		try {

			final FileVersion version = fileDAO.getVersion(fileVersionId);

			final String name = version.getName() + '.' + version.getExtension();
			final String path = version.getPath();

			download(path, name, fileStorageProvider.open(path), response);

		} catch (final NoSuchFileException e) {
			LOG.info("No file found for version id '" + fileVersionId + "'.", e);
			throw new StatusServletException(Response.SC_NOT_FOUND, e);
		}
	}

	/**
	 * See {@link ServletMethod#DOWNLOAD_ARCHIVE} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void downloadArchive(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		// Retrieves the file id.
		final String id = getParameter(request, RequestParameter.ID, false);

		if (LOG.isDebugEnabled()) {
			LOG.debug("Downloads archive with id '{}'.", id);
		}

		try {

			download(id, backupArchiveManager.open(id), response);

		} catch (final NoSuchFileException e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("No archive found for id '" + id + "'.", e);
			}
			throw new StatusServletException(Response.SC_NOT_FOUND, e);
		}
	}

	/**
	 * Downloads the given {@code id} file on given {@code response} stream.
	 * 
	 * @param id
	 *          The file id.
	 * @param in
	 *          The file input stream.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	private static void download(final String id, final InputStream in, final HttpServletResponse response) throws Exception {
		download(id, "file_" + id, in, response);
	}

	/**
	 * Downloads the given {@code id} file on given {@code response} stream.
	 * 
	 * @param id
	 *          The file id.
	 * @param fileName
	 *          Name sent in the response header.
	 * @param in
	 *          The file input stream.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	private static void download(final String id, String fileName, final InputStream in, final HttpServletResponse response) throws Exception {

		final FileType fileType = fileTypeFromFileId(id);
		ResponseHelper.executeDownload(response, in, fileType != null ? fileType.getContentType() : null, fileName, null);

	}
	
	private static void downloadBase64(final String id, final InputStream in, final HttpServletResponse response) throws IOException {
		final FileType fileType = fileTypeFromFileId(id);
		ResponseHelper.executeDownload(response, in, fileType != null ? fileType.getContentType() : null, null, null, ResponseHelper.ContentDisposition.BASE64);
	}
	
	private static FileType fileTypeFromFileId(String fileName) {
		final String extension = FilenameUtils.getExtension(fileName);
		return FileType.fromExtension(extension);
	}

	// ---------------------------------------------------------------------------------------
	//
	// UPLOAD METHODS.
	//
	// ---------------------------------------------------------------------------------------

	/**
	 * See {@link ServletMethod#UPLOAD_ORGANIZATION_LOGO} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the Organization id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws java.io.IOException
	 *           If an error occured while reading or writing to the socket or if an error occured while storing the
	 *           uploaded file.
	 * @throws org.sigmah.server.servlet.base.StatusServletException
	 *           If the id parameter was not found or not parseable or if the request type is not MULTIPART or if the file
	 *           exceeded the maximum allowed size.
	 * @throws org.apache.commons.fileupload.FileUploadException
	 *           If an error occured while reading the uploaded file.
	 * @throws javax.servlet.ServletException
	 *           If the given organization could not be found.
	 */
	protected void uploadOrganizationLogo(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws IOException, StatusServletException, ServletException, FileUploadException {

		// --
		// Retrieving parameters from request.
		// --

		final Integer organizationId = getIntegerParameter(request, RequestParameter.ID, false);

		// --
		// Retrieving Organization entity.
		// --

		final Organization organization = organizationDAO.findById(organizationId);
		if (organization == null) {
			throw new ServletException("Cannot find Organization with id '" + organizationId + "'.");
		}

		final String previousLogoFileName = organization.getLogo();

		// --
		// Verifying content length.
		// --

		final int contentLength = request.getContentLength();

		if (contentLength == 0) {
			LOG.error("Empty logo file.");
			throw new StatusServletException(Response.SC_NO_CONTENT);
		}

		if (contentLength > FileUploadUtils.MAX_UPLOAD_IMAGE_SIZE) {
			LOG.error("Logo file's size is too big to be uploaded (size: {}, maximum : {}).", contentLength, FileUploadUtils.MAX_UPLOAD_IMAGE_SIZE);
			throw new StatusServletException(Response.SC_REQUEST_ENTITY_TOO_LARGE);
		}

		// --
		// Saving new logo.
		// --

		organization.setLogo(organization.getId() + "_" + new Date().getTime());
		processUpload(new MultipartRequest(request), response, organization.getLogo(), true, null);
		organizationDAO.persist(organization, context.getUser());

		// --
		// Deleting previous logo file.
		// --

		if (StringUtils.isNotBlank(previousLogoFileName)) {
			fileStorageProvider.delete(previousLogoFileName);
		}

		response.getWriter().write(organization.getLogo());
	}

	/**
	 * See {@link ServletMethod#UPLOAD} for JavaDoc.
	 * 
	 * @param request
	 *          The HTTP request containing the file id parameter.
	 * @param response
	 *          The HTTP response on which the file content is written.
	 * @param context
	 *          The execution context.
	 * @throws Exception
	 *           If an error occurs during process.
	 */
	protected void upload(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		// --
		// Verify content length.
		// --

		final int contentLength = request.getContentLength();

		if (contentLength == 0) {
			LOG.error("Empty file.");
			throw new StatusServletException(Response.SC_NO_CONTENT);
		}

		if (contentLength > FileUploadUtils.MAX_UPLOAD_FILE_SIZE) {
			LOG.error("File's size is too big to be uploaded (size: {}, maximum : {}).", contentLength, FileUploadUtils.MAX_UPLOAD_FILE_SIZE);
			throw new StatusServletException(Response.SC_REQUEST_ENTITY_TOO_LARGE);
		}
		
		final String fileName = generateUniqueName();

		// --
		// Writing the file.
		// --

		final MultipartRequest multipartRequest = new MultipartRequest(request);
		final long size = this.processUpload(multipartRequest, response, fileName, false, null);
		final Map<String, String> properties = multipartRequest.getProperties();
		
		conflicts.searchForFileAddConflicts(properties, context.getLanguage(), context.getUser());

		// --
		// Create the associated entries in File and FileVersion tables.
		// --

		final Integer fileId = fileDAO.saveOrUpdate(properties, fileName, (int) size);
		final FileVersion fileVersion = fileDAO.getLastVersion(fileId);

		// --
		// If a monitored point must be created.
		// --

		final MonitoredPoint monitoredPoint = parseMonitoredPoint(properties);

		if (monitoredPoint != null) {

			final Integer projectId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_PROJECT));
			final Project project = projectDAO.findById(projectId);

			monitoredPoint.setFile(fileDAO.findById(fileId));

			MonitoredPointList list = project.getPointsList();

			if (list == null) {
				list = new MonitoredPointList();
				project.setPointsList(list);
			}

			if (list.getPoints() == null) {
				list.setPoints(new ArrayList<MonitoredPoint>());
			}

			// Adds the point to the list.
			list.addMonitoredPoint(monitoredPoint);

			// Saves monitored point.
			monitoredPointDAO.persist(monitoredPoint, context.getUser());
		}

		final MonitoredPointDTO monitoredPointDTO = mapper().map(monitoredPoint, new MonitoredPointDTO(), MonitoredPointDTO.Mode.BASE);
		final FileVersionDTO fileVersionDTO = mapper().map(fileVersion, new FileVersionDTO());

		response.setContentType(FileType.HTML.getContentType());
		response.getWriter().write(FileUploadResponse.serialize(fileVersionDTO, monitoredPointDTO));
	}

	protected void uploadAvatar(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {
		final int contentLength = request.getContentLength();

		if (contentLength == 0) {
			LOG.error("Empty file.");
			throw new StatusServletException(Response.SC_NO_CONTENT);
		}

		if (contentLength > FileUploadUtils.MAX_UPLOAD_FILE_SIZE) {
			LOG.error("File's size is too big to be uploaded (size: {}, maximum : {}).", contentLength, FileUploadUtils.MAX_UPLOAD_FILE_SIZE);
			throw new StatusServletException(Response.SC_REQUEST_ENTITY_TOO_LARGE);
		}

		final String fileName = generateUniqueName();

		// --
		// Writing the file.
		// --

		final MultipartRequest multipartRequest = new MultipartRequest(request);
		processUpload(multipartRequest, response, fileName, false, FileUploadUtils.MAX_AVATAR_SIZE);

		response.setStatus(Response.SC_OK);
		response.setContentType(FileType.TXT.getContentType());
		response.getWriter().write(fileName);
	}

	// ---------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------

	/**
	 * Processes the file upload.
	 * 
	 * @param request
	 *          The HTTP request.
	 * @param response
	 *          The HTTP response.
	 * @param context
	 *          The execution context.
	 * @param filename
	 *          The uploaded physical file name.
	 * @param logo
	 *          {@code true} if the upload concerns an organization logo, {@code false} otherwise.
	 * @throws java.io.IOException
	 *           If an error occured while reading or writing to the socket or if an error occured while storing the
	 *           uploaded file.
	 * @throws org.sigmah.server.servlet.base.StatusServletException
	 *           if the request type is not MULTIPART or if the file exceeded the maximum allowed size.
	 * @throws org.apache.commons.fileupload.FileUploadException
	 *           If an error occured while reading the uploaded file.
	 */
	private long processUpload(final MultipartRequest multipartRequest, final HttpServletResponse response, final String filename, final boolean logo, final Integer resizeTo)
			throws StatusServletException, IOException, FileUploadException {
		LOG.debug("Starting file uploading...");

		final long[] size = { 0L
		};
		multipartRequest.parse(new MultipartRequestCallback() {

			@Override
			public void onInputStream(InputStream inputStream, String itemName, String mimeType) throws IOException {
				// Retrieving file name.
				// If a name (id) is provided, we use it. If not, using the name of the uploaded file.
				final String name = StringUtils.isNotBlank(filename) ? filename : itemName;

				try (final InputStream stream = inputStream) {
					LOG.debug("Reads image content from the field ; name: '{}'.", name);

					if (logo) {
						size[0] = logoManager.updateLogo(stream, name);
					} else if (resizeTo == null) {
						size[0] = fileStorageProvider.copy(stream, name, StandardCopyOption.REPLACE_EXISTING);
					} else {
						try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
							imageMinimizer.resizeImage(inputStream, byteArrayOutputStream, resizeTo);
							try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray())) {
								size[0] = fileStorageProvider.copy(byteArrayInputStream, name, StandardCopyOption.REPLACE_EXISTING);
							}
						}
					}

					response.setStatus(Response.SC_ACCEPTED);

					// FIXME : perhaps keep the response above for error catching
					// response.getWriter().write("ok");
					LOG.debug("File '{}' upload has been successfully processed.", name);
				}
			}
		});

		return size[0];
	}

	/**
	 * Generates a {@link MonitoredPoint} instance from the given {@code properties}.<br>
	 * Following attributes of the generated monitored point are set:
	 * <ul>
	 * <li>{@code label}</li>
	 * <li>{@code expectedDate}</li>
	 * <li>{@code deleted} (set to {@code false})</li>
	 * </ul>
	 * 
	 * @param properties
	 *          The properties.
	 * @return The monitored point instance.
	 * @throws UnsupportedOperationException
	 *           If the {@code properties} cannot be used to generate a <em>valid</em> {@link MonitoredPoint} instance.
	 */
	private static MonitoredPoint parseMonitoredPoint(final Map<String, String> properties) {

		if (MapUtils.isEmpty(properties)) {
			return null;
		}

		final String label = properties.get(FileUploadUtils.MONITORED_POINT_LABEL);
		final String expectedDateTime = properties.get(FileUploadUtils.MONITORED_POINT_DATE);

		if (StringUtils.isBlank(label) || StringUtils.isBlank(expectedDateTime)) {
			return null;
		}

		try {

			final MonitoredPoint monitoredPoint = new MonitoredPoint();

			monitoredPoint.setLabel(label);
			monitoredPoint.setExpectedDate(new Date(Long.valueOf(expectedDateTime)));
			monitoredPoint.setDeleted(false);

			return monitoredPoint;

		} catch (final Exception e) {
			throw new UnsupportedOperationException("Error occures while generating monitored point from properties.", e);
		}
	}

	/**
	 * Computes and returns a unique string identifier to name files.
	 * 
	 * @return A unique string identifier.
	 */
	public static String generateUniqueName() {
		// Adds the timestamp to ensure the id uniqueness.
		return UUID.randomUUID().toString() + new Date().getTime();
	}

}
