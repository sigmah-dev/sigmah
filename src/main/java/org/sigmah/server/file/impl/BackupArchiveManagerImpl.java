package org.sigmah.server.file.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.domain.User;
import org.sigmah.server.file.BackupArchiveJobFactory;
import org.sigmah.server.file.BackupArchiveManager;
import org.sigmah.server.file.impl.BackupArchiveJob.BackupArchiveJobArgument;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.dto.BackupDTO;
import org.sigmah.shared.dto.value.FileDTO.LoadingScope;
import org.sigmah.shared.util.FileType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import java.util.Arrays;
import java.util.Comparator;
import org.sigmah.shared.dispatch.FunctionalException;

/**
 * {@link BackupArchiveManager} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BackupArchiveManagerImpl implements BackupArchiveManager {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BackupArchiveManagerImpl.class);

	/**
	 * Separator used in backup archive files name.
	 */
	private static final String BACKUP_ARCHIVE_NAME_SEP = "-";

	/**
	 * Archive files extension (with separator).
	 */
	private static final String BACKUP_ARCHIVE_EXT = FileType.ZIP.getExtension();

	/**
	 * Temporary files extension (with separator).
	 */
	private static final String BACKUP_ARCHIVE_TEMP_EXT = ".tmp";

	/**
	 * <p>
	 * Backup archive file name pattern.
	 * </p>
	 * <p>
	 * Detects following groups:
	 * </p>
	 * 
	 * <pre>
	 * {organizationId}_{orgUnitId}_{loadingScope}.{extension}
	 * </pre>
	 */
	private static final Pattern BACKUP_ARCHIVE_NAME_PATTERN = Pattern.compile("(\\d*)"
		+ BACKUP_ARCHIVE_NAME_SEP
		+ "(\\d*)"
		+ BACKUP_ARCHIVE_NAME_SEP
		+ "("
		+ LoadingScope.ALL_VERSIONS
		+ '|'
		+ LoadingScope.LAST_VERSION
		+ ")"
		+ "("
		+ BACKUP_ARCHIVE_EXT
		+ '|'
		+ BACKUP_ARCHIVE_TEMP_EXT
		+ ")");

	/**
	 * Injected application properties.
	 */
	private final Properties properties;

	/**
	 * Injected {@link OrgUnitDAO}.
	 */
	private final OrgUnitDAO orgUnitDAO;

	/**
	 * Injected {@link BackupArchiveJobFactory}.
	 */
	private final BackupArchiveJobFactory backupArchiveJobFactory;

	@Inject
	public BackupArchiveManagerImpl(final Properties properties, final OrgUnitDAO orgUnitDAO, final BackupArchiveJobFactory backupArchiveJobFactory) {
		this.properties = properties;
		this.orgUnitDAO = orgUnitDAO;
		this.backupArchiveJobFactory = backupArchiveJobFactory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream open(final String archiveId) throws IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Opening a new stream to archive file '{}'.", archiveId);
		}

		return Files.newInputStream(Paths.get(getArchiveRootPath(), archiveId));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BackupDTO getRunningBackupProcessFile(final Integer organizationId) throws IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Looking for running backup process file for organization #{}.", organizationId);
		}

		final File[] tempFiles = Paths.get(getArchiveRootPath()).toFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(final File file, final String name) {
				return name.startsWith(organizationId + BACKUP_ARCHIVE_NAME_SEP) && name.endsWith(BACKUP_ARCHIVE_TEMP_EXT);
			}
		});

		if (ArrayUtils.isEmpty(tempFiles)) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("No running backup process file has been found for organization #{}.", organizationId);
			}
			return null;
		}

		return fromFile(tempFiles[0].toPath());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BackupDTO getExistingBackup(final Integer organizationId) throws IOException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Looking for existing backup file for organization #{}.", organizationId);
		}

		final File[] archiveFiles = Paths.get(getArchiveRootPath()).toFile().listFiles(new FilenameFilter() {

			@Override
			public boolean accept(final File file, final String name) {
				return name.startsWith(organizationId + BACKUP_ARCHIVE_NAME_SEP) && name.endsWith(BACKUP_ARCHIVE_EXT);
			}
		});

		if (ArrayUtils.isEmpty(archiveFiles)) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("No existing backup file has been found for organization #{}.", organizationId);
			}
			return null;
		}
		
		// BUGFIX #671 & #772: Sorting files by creation date to retrieve the most recent backup.
		Arrays.sort(archiveFiles, new Comparator<File>() {

			@Override
			public int compare(File o1, File o2) {
				return getCreationDate(o2).compareTo(getCreationDate(o1));
			}
			
		});

		return fromFile(archiveFiles[0].toPath());
	}

	/**
	 * Returns the creation date of the given file or the 1st january 1970
	 * if an error occured while trying to read the date.
	 * 
	 * @param file File to access.
	 * @return Creation date of the given file.
	 */
	private Date getCreationDate(File file) {
		try {
			final BasicFileAttributeView view = Files.getFileAttributeView(file.toPath(), BasicFileAttributeView.class);
			final BasicFileAttributes attributes = view.readAttributes();

			return new Date(attributes.creationTime().toMillis());
			
		} catch(IOException e) {
			return new Date(0L);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startBackupArchiveGeneration(final BackupDTO backup, final User user) throws IOException, FunctionalException {

		if (LOG.isTraceEnabled()) {
			LOG.trace("Starting new backup process for configuration '{}' ; Backup launched by user '{}'.", backup, user);
		}

		if (backup == null || backup.getOrganizationId() == null || backup.getOrgUnitId() == null) {
			throw new IllegalArgumentException("Backup configuration is invalid.");
		}

		// Archive files.
		final Path tempArchiveFile = Paths.get(getArchiveRootPath(), buildArchiveFileName(backup, true));
		final Path finalArchiveFile = Paths.get(getArchiveRootPath(), buildArchiveFileName(backup, false));

		// Creates temporary file.
		try {
			Files.createFile(tempArchiveFile);
			
		} catch(IOException e) {
			throw new FunctionalException(e, FunctionalException.ErrorCode.ADMIN_BACKUP_ARCHIVE_CREATION_FAILED, tempArchiveFile.toString());
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("Backup process file has been created: '{}'. Initializing job.", tempArchiveFile);
		}

		// Process is executed in a different thread in order to release current thread.
		Executors.newSingleThreadExecutor().execute(
			backupArchiveJobFactory.newJob(new BackupArchiveJobArgument(backup, user.getId(), tempArchiveFile, finalArchiveFile)));
	}

	// --------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the archives storage root directory path.
	 * 
	 * @return The archives storage root directory path.
	 */
	private String getArchiveRootPath() {
		return properties.getProperty(PropertyKey.ARCHIVE_REPOSITORY_NAME);
	}

	/**
	 * <p>
	 * Builds the given {@code backup} corresponding archive file name.
	 * </p>
	 * <p>
	 * Archive file name is generated with following format: {@code <organizationId>_<orgUnitId>_<loadingMode>.zip}
	 * </p>
	 * 
	 * @param backup
	 *          The backup configuration.
	 * @param tempFile
	 *          {@code true} to generate a temporary file name, {@code false} to generate a complete file name.
	 * @return The {@code backup} corresponding archive file name (with extension).
	 */
	private static String buildArchiveFileName(final BackupDTO backup, final boolean tempFile) {

		final StringBuilder builder = new StringBuilder();

		builder.append(backup.getOrganizationId());
		builder.append(BACKUP_ARCHIVE_NAME_SEP);
		builder.append(backup.getOrgUnitId());
		builder.append(BACKUP_ARCHIVE_NAME_SEP);
		builder.append(backup.getLoadingScope().name());
		builder.append(tempFile ? BACKUP_ARCHIVE_TEMP_EXT : BACKUP_ARCHIVE_EXT);

		return builder.toString();
	}

	/**
	 * Builds the given {@code file} corresponding {@link BackupDTO}.
	 * 
	 * @param file
	 *          The backup file path.
	 * @return The given {@code file} corresponding {@link BackupDTO}.
	 * @throws IOException
	 *           If an I/O error occurs.
	 * @throws IllegalArgumentException
	 *           If the given {@code file} does not reference a valid backup file.
	 */
	private BackupDTO fromFile(final Path file) throws IOException {

		final String filename = file.getFileName().toString();
		final Matcher matcher = BACKUP_ARCHIVE_NAME_PATTERN.matcher(filename);

		if (!matcher.matches()) {
			throw new IllegalArgumentException("Invalid backup archive file name '" + filename + "'.");
		}

		final Integer organizationId = Integer.parseInt(matcher.group(1));
		final Integer orgUnitId = Integer.parseInt(matcher.group(2));
		final LoadingScope loadingScope = LoadingScope.valueOf(matcher.group(3));
		final String extension = matcher.group(4);

		final BasicFileAttributeView view = Files.getFileAttributeView(file, BasicFileAttributeView.class);
		final BasicFileAttributes attributes = view.readAttributes();

		final BackupDTO result = new BackupDTO();

		result.setOrganizationId(organizationId);
		result.setOrgUnitId(orgUnitId);
		result.setOrgUnitName(orgUnitDAO.findById(orgUnitId).getFullName());
		result.setLoadingScope(loadingScope);
		result.setCreationDate(new Date(attributes.creationTime().toMillis()));
		result.setArchiveFileName(filename);
		result.setRunning(extension.equals(BACKUP_ARCHIVE_TEMP_EXT));

		return result;
	}
}
