package org.sigmah.server.file;

import java.io.IOException;
import java.io.InputStream;

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.BackupDTO;

/**
 * Manages {@link OrgUnit} files backup.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface BackupArchiveManager {

	/**
	 * Opens the given {@code backupId} corresponding file.
	 * 
	 * @param backupId
	 *          The unique backup archive id for this version of the file.
	 * @return and input stream from which the contents can be read.
	 * @throws IOException
	 *           If the given {@code backupId} does not exist or cannot be opened.
	 */
	InputStream open(String backupId) throws IOException;

	/**
	 * Returns the given {@code organizationId} corresponding running process file.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @return The given {@code organizationId} corresponding running process file, or {@code null} if no running is
	 *         currently running.
	 * @throws IOException
	 *           If an error occurs.
	 */
	BackupDTO getRunningBackupProcessFile(Integer organizationId) throws IOException;

	/**
	 * Returns the existing backup archive file for the given {@code organizationId}.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @return The existing backup archive file configuration, or {@code null} if no existing archive has been found.
	 * @throws IOException
	 *           If an error occurs.
	 */
	BackupDTO getExistingBackup(Integer organizationId) throws IOException;

	/**
	 * Activates the given {@code backup} configuration corresponding archive file generation.<br>
	 * This generation process is launched within a parallel thread in order to release current process.
	 * 
	 * @param backup
	 *          The backup configuration.
	 * @param user
	 *          The user executing the process.
	 * @throws IOException
	 *          If an error occurs while reading archive file.
	 * @throws org.sigmah.shared.dispatch.CommandException
	 *			if an error occurs while creating the backup.
	 */
	void startBackupArchiveGeneration(BackupDTO backup, User user) throws IOException, CommandException;

}
