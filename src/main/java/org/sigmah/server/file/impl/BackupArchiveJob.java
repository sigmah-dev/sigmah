package org.sigmah.server.file.impl;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.file.util.FileElement;
import org.sigmah.server.file.util.FolderElement;
import org.sigmah.server.file.util.RepositoryElement;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.servlet.util.ResponseHelper;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.persist.Transactional;

/**
 * <p>
 * Runnable job in charge of generating an organization backup archive.
 * </p>
 * <p>
 * As this process may take a while, the job is executed in a parallel thread.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BackupArchiveJob implements Runnable {

	/**
	 * Backup archive job argument POJO.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static final class BackupArchiveJobArgument {

		private BackupDTO backup;
		private Integer userId;
		private Path tempArchiveFile;
		private Path finalArchiveFile;

		BackupArchiveJobArgument(final BackupDTO backup, final Integer userId, final Path tempArchiveFile, final Path finalArchiveFile) {
			this.backup = backup;
			this.userId = userId;
			this.tempArchiveFile = tempArchiveFile;
			this.finalArchiveFile = finalArchiveFile;
		}
	}

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BackupArchiveJob.class);

	/**
	 * Assisted injected job arguments.
	 */
	private final BackupArchiveJobArgument arguments;

	/**
	 * Injected {@link ProjectDAO}.
	 */
	@Inject
	private ProjectDAO projectDAO;

	/**
	 * Injected {@link FileDAO}.
	 */
	@Inject
	private FileDAO fileDAO;

	/**
	 * Injected {@link ValueDAO}.
	 */
	@Inject
	private ValueDAO valueDAO;

	/**
	 * Injected {@link OrgUnitDAO}.
	 */
	@Inject
	private OrgUnitDAO orgUnitDAO;

	/**
	 * Injected {@link UserDAO}.
	 */
	@Inject
	private UserDAO userDAO;

	/**
	 * Injected {@link FileStorageProvider}.
	 */
	@Inject
	private FileStorageProvider fileStorageProvider;

	@Inject
	public BackupArchiveJob(@Assisted final BackupArchiveJobArgument arguments) {
		this.arguments = arguments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {

		final Path tempArchiveFile = arguments.tempArchiveFile;
		final Path finalArchiveFile = arguments.finalArchiveFile;

		try (final ZipArchiveOutputStream zipOutputStream = new ZipArchiveOutputStream(Files.newOutputStream(tempArchiveFile))) {

			zipOutputStream.setMethod(ZipOutputStream.DEFLATED);
			zipOutputStream.setLevel(Deflater.BEST_COMPRESSION);

			final RepositoryElement repository = buildOrgUnitRepository(arguments.backup, arguments.userId);
			repository.setName("");

			zipRepository(repository, zipOutputStream, "");

			// TODO Delete existing previous organization file(s).

			// Renames temporary '.tmp' file to complete '.zip' file.
			Files.move(tempArchiveFile, finalArchiveFile, StandardCopyOption.REPLACE_EXISTING);

		} catch (final Throwable t) {

			if (LOG.isErrorEnabled()) {
				LOG.error("An error occurred during backup archive generation process.", t);
			}

			try {

				Files.deleteIfExists(tempArchiveFile);
				Files.deleteIfExists(finalArchiveFile);

			} catch (final IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("An error occurred while deleting archive error file.", e);
				}
			}
		}
	}

	// --------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Builds the given {@code orgUnit} corresponding files repository tree.
	 * 
	 * @param backup
	 *          The backup configuration.
	 * @param userId
	 *          The id of the user requesting the repository build.
	 * @return The given {@code orgUnit} corresponding files repository tree, or {@code null} if the given {@code user} is
	 *         not authorized to access the given {@code backup} corresponding OrgUnit.
	 * @throws IllegalArgumentException
	 *           If one of the arguments is missing.
	 * @throws UnsupportedOperationException
	 *           If the OrgUnit is not visible to the user.
	 */
	@Transactional
	// IMPORTANT: '@Transactional' annotation ensures proper database connections closure.
	protected RepositoryElement buildOrgUnitRepository(final BackupDTO backup, final Integer userId) {

		if (backup == null || userId == null) {
			throw new IllegalArgumentException("Invalid arguments necessary to build OrgUnit files repository.");
		}

		final LoadingScope loadingScope = backup.getLoadingScope();
		final OrgUnit orgUnit = orgUnitDAO.findById(backup.getOrgUnitId());
		final User user = userDAO.findById(userId);

		// --
		// Controls.
		// --

		if (orgUnit == null) {
			throw new IllegalArgumentException("Cannot find OrgUnit with id #" + backup.getOrgUnitId() + ".");
		}

		if (user == null) {
			throw new IllegalArgumentException("Cannot find User with id #" + userId + ".");
		}

		if (!Handlers.isOrgUnitVisible(orgUnit, user)) {
			throw new UnsupportedOperationException("OrgUnit #" + orgUnit.getId() + " is not visible to user #" + userId + ".");
		}

		// --
		// Starts repository building.
		// --

		final FolderElement root = new FolderElement("root", "root");

		final Set<OrgUnit> orgUnitTree = new HashSet<OrgUnit>();
		Handlers.crawlUnits(orgUnit, orgUnitTree, true);

		// Retrieves values for OrgUnits full tree.
		final List<Value> values = valueDAO.findValuesForOrgUnits(orgUnitTree);

		for (final Value value : values) {

			if (!(value.getElement() instanceof FilesListElement)) {
				// Not a file element.
				continue;
			}

			final Project ud = projectDAO.findById(value.getContainerId());
			final OrgUnit o;

			if (ud != null) {
				// There is only one partner.
				o = ud.getPartners().iterator().next();

			} else {
				// Container is an OrgUnit.
				o = orgUnitDAO.findById(value.getContainerId());
			}

			final List<FileVersion> versions = fileDAO.findVersions(ValueResultUtils.splitValuesAsInteger(value.getValue()), loadingScope);

			FolderElement orgUnitRepository = (FolderElement) root.getById("o" + o.getId());
			if (orgUnitRepository == null) {
				orgUnitRepository = new FolderElement("o" + o.getId(), validateFileName(o.getFullName()));
				root.appendChild(orgUnitRepository);
			}

			FolderElement fileFolderElementParent = null;

			if (ud != null) {
				fileFolderElementParent = (FolderElement) orgUnitRepository.getById("p" + ud.getId());
				if (fileFolderElementParent == null) {
					fileFolderElementParent = new FolderElement("p" + ud.getId(), validateFileName(ud.getFullName()));
					orgUnitRepository.appendChild(fileFolderElementParent);
				}
			} else {
				fileFolderElementParent = orgUnitRepository;
			}

			for (final FileVersion version : versions) {

				if (loadingScope == LoadingScope.ALL_VERSIONS) {

					FolderElement fileRepository = (FolderElement) fileFolderElementParent.getById("f" + version.getParentFile().getId());

					if (fileRepository == null) {
						fileRepository =
								new FolderElement("f" + version.getParentFile().getId(), validateFileName(version.getParentFile().getName())
									+ "_f"
									+ version.getParentFile().getId());
						fileFolderElementParent.appendChild(fileRepository);
					}

					final FileElement file =
							new FileElement("fv" + version.getId(), validateFileName(version.getName()) + "_v" + version.getId() + "." + version.getExtension(),
								version.getPath());
					fileRepository.appendChild(file);

				} else if (loadingScope == LoadingScope.LAST_VERSION) {

					final FileElement file =
							new FileElement("f" + version.getId(), validateFileName(version.getName()) + "_f" + version.getId() + "." + version.getExtension(),
								version.getPath());
					fileFolderElementParent.appendChild(file);
				}
			}
		}

		return root;
	}

	/**
	 * <p>
	 * Recursively browses the given {@code root} repository elements to populate the given {@code zipOutputStream} with
	 * corresponding files.
	 * </p>
	 * <p>
	 * If a referenced file cannot be found in the storage folder, it will be ignored (a {@code WARN} log is generated).
	 * </p>
	 * 
	 * @param root
	 *          The root repository element.
	 * @param zipOutputStream
	 *          The stream to populate with files hierarchy.
	 * @param actualPath
	 *          The current repository path.
	 */
	private void zipRepository(final RepositoryElement root, final ZipArchiveOutputStream zipOutputStream, final String actualPath) {

		final String path = (actualPath.equals("") ? root.getName() : actualPath + "/" + root.getName());

		if (root instanceof FileElement) {

			final FileElement file = (FileElement) root;
			final String fileStorageId = file.getStorageId();
			
			if(fileStorageProvider.exists(fileStorageId)) {
				try (final InputStream is = new BufferedInputStream(fileStorageProvider.open(fileStorageId), ResponseHelper.BUFFER_SIZE)) {

					zipOutputStream.putArchiveEntry(new ZipArchiveEntry(path));

					final byte data[] = new byte[ResponseHelper.BUFFER_SIZE];

					while ((is.read(data)) != -1) {
						zipOutputStream.write(data);
					}

					zipOutputStream.closeArchiveEntry();

				} catch (final IOException e) {
					LOG.warn("File '" + fileStorageId + "' cannot be found ; continuing with next file.", e);
				}
			} else {
				LOG.warn("File '{0}' does not exists on the server ; continuing with next file.", fileStorageId);
			}

		} else if (root instanceof FolderElement) {

			final FolderElement folder = (FolderElement) root;

			for (final RepositoryElement element : folder.getChildren()) {
				zipRepository(element, zipOutputStream, path);
			}
		}
	}

	/**
	 * Deletes the {@code "C:\fakepath\"} string from given {@code fileName} (which comes from an issue in Google Chrome).
	 * It also replaces all wrong characters that can't be displayed in a file name or a directory name by "{@code _}".
	 * 
	 * @param fileName
	 *          The file name to validate.
	 * @return The validated file name.
	 */
	private static String validateFileName(final String fileName) {
		return fileName.replaceFirst("[cC]:\\\\fakepath\\\\", "").replaceAll("[\\/:*?\"<>|]", "_");
	}

}
