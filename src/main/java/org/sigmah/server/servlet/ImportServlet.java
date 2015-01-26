package org.sigmah.server.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.sigmah.server.servlet.base.AbstractServlet;
import org.sigmah.server.servlet.base.ServletExecutionContext;
import org.sigmah.server.servlet.exporter.models.CategoryTypeHandler;
import org.sigmah.server.servlet.exporter.models.ModelHandler;
import org.sigmah.server.servlet.exporter.models.OrgUnitModelHandler;
import org.sigmah.server.servlet.exporter.models.ProjectModelHandler;
import org.sigmah.server.servlet.exporter.models.ProjectReportModelHandler;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import org.apache.commons.fileupload.FileUploadException;
import org.sigmah.server.file.FileStorageProvider;

/**
 * Servlet handling the import of model and values.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */

@Singleton
public class ImportServlet extends AbstractServlet {

	/**
	 * Serial
	 */
	private static final long serialVersionUID = 3249000141532724402L;

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(ImportServlet.class);

	private static final long MAXIMUM_FILE_SIZE = 2097152; // 2 Mo

	/**
	 * Injected application injector.
	 */
	private final Injector injector;
	
	/**
	 * Injected application {@link FileStorageProvider}.
	 */
	private final FileStorageProvider fileStorageProvider;

	@Inject
	public ImportServlet(Injector injector, FileStorageProvider fileStorageProvider) {
		this.injector = injector;
		this.fileStorageProvider = fileStorageProvider;
	}

	/**
	 * Import Report Model
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public void importReportModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		runImport(new ProjectReportModelHandler(), request, response, context);

	}

	/**
	 * import Orgunit Model
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public void importOrgUnitModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		runImport(new OrgUnitModelHandler(), request, response, context);

	}

	/**
	 * import Project Model
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public void importProjectModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		runImport(new ProjectModelHandler(), request, response, context);

	}

	/**
	 * import Category model
	 * 
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public void importCategoryModel(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws Exception {

		runImport(new CategoryTypeHandler(), request, response, context);

	}
	
	/**
	 * Run Import model format dat
	 * 
	 * @param handler
	 * @param request
	 * @param response
	 * @param context
	 * @throws Exception
	 */
	public void runImport(final ModelHandler handler, final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context)
			throws Exception {

		final HashMap<String, String> properties = new HashMap<String, String>();
		final byte[] data;
		
		try {
			data = readFileAndProperties(request, properties);
			
		} catch (FileUploadException | IOException ex) {
			LOG.error("Error while receiving a serialized model.", ex);
			throw ex;
		}

		if (data != null) {
			// A file has been received

			if (handler != null) {

				if (handler instanceof ProjectModelHandler) {

					final String projectModelTypeAsString = properties.get("project-model-type");

					try {

						final ProjectModelType projectModelType = ProjectModelType.valueOf(projectModelTypeAsString);
						((ProjectModelHandler) handler).setProjectModelType(projectModelType);

					}

					catch (IllegalArgumentException e) {
						LOG.debug("Bad value for project model type: " + projectModelTypeAsString, e);
					}

				}

				final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

				try {

					handler.importModel(inputStream, injector.getInstance(EntityManager.class), context.getUser());

				} catch (Exception ex) {
					LOG.error("Model import error,  ", ex);
					throw ex;
				}

			} else {
				LOG.warn("The asked model type doesn't have any handler registered.");
			}
		} else {
			LOG.warn("No file has been received.");
		}

	}
	
	/**
	 * Store the uploaded file on the disk and return the identifier of the
	 * file to the user.
	 * 
	 * @param request Http request.
	 * @param response Http response.
	 * @param context Execution context.
	 * @throws FileUploadException If an error occured while reading the http request.
	 * @throws IOException If the file could not be written on the server.
	 */
	public void storeFile(final HttpServletRequest request, final HttpServletResponse response, final ServletExecutionContext context) throws FileUploadException, IOException {
		final HashMap<String, String> properties = new HashMap<String, String>();
		final byte[] data;
		
		try {
			data = readFileAndProperties(request, properties);
			
		} catch (FileUploadException | IOException ex) {
			LOG.error("Error while receiving a file containing values.", ex);
			throw ex;
		}

		if (data != null) {
			// A file has been received
			final String uniqueFileId = FileServlet.generateUniqueName();
			final long length = fileStorageProvider.copy(new ByteArrayInputStream(data), uniqueFileId, StandardCopyOption.REPLACE_EXISTING);
			
			if(length > 0) {
				response.setContentType("text/html");
				response.getWriter().write(uniqueFileId);
			}
		}
	}

	private byte[] readFileAndProperties(final HttpServletRequest request, final Map<String, String> properties) throws FileUploadException, IOException {
		byte[] data = null;
		final ServletFileUpload fileUpload = new ServletFileUpload();
		
		final FileItemIterator iterator = fileUpload.getItemIterator(request);
		
		// Iterating on the fields sent into the request
		while (iterator.hasNext()) {

			final FileItemStream item = iterator.next();
			final String name = item.getFieldName();

			final InputStream stream = item.openStream();

			if (item.isFormField()) {

				final String value = Streams.asString(stream);

				LOG.debug("field '" + name + "' = '" + value + '\'');

				// The current field is a property
				properties.put(name, value);

			} else {
				// The current field is a file
				LOG.debug("field '" + name + "' (FILE)");

				final ByteArrayOutputStream serializedData = new ByteArrayOutputStream();
				long dataSize = 0L;

				int b = stream.read();

				while (b != -1 && dataSize < MAXIMUM_FILE_SIZE) {
					serializedData.write(b);

					dataSize++;
					b = stream.read();
				}

				stream.close();

				data = serializedData.toByteArray();
			}
		}

		return data;
	}
}
