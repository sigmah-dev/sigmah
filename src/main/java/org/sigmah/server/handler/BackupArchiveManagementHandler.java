package org.sigmah.server.handler;

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


import java.io.IOException;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.file.BackupArchiveManager;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.BackupArchiveManagementCommand;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.BackupDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Handler for the {@link BackupArchiveManagementCommand}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BackupArchiveManagementHandler extends AbstractCommandHandler<BackupArchiveManagementCommand, BackupDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(BackupArchiveManagementHandler.class);

	/**
	 * Injected {@link BackupArchiveManager}.
	 */
	private final BackupArchiveManager backupArchiveManager;

	@Inject
	public BackupArchiveManagementHandler(final BackupArchiveManager backupArchiveManager) {
		this.backupArchiveManager = backupArchiveManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BackupDTO execute(final BackupArchiveManagementCommand cmd, final UserExecutionContext context) throws CommandException {

		final BackupDTO backupConf = cmd.getBackupConfiguration();
		final Integer organizationId = cmd.getOrganizationId();

		try {

			if (backupConf != null) {
				// Launches a new generation process.
				return launchNewBackupGeneration(backupConf, context);

			} else if (organizationId != null) {
				// Checks if an existing backup archive file exists.
				return getExistingBackupArchive(organizationId, context);

			} else {
				throw new CommandException("Invalid command arguments.");
			}

		} catch (final IOException e) {
			throw new CommandException(e);
		}
	}

	/**
	 * Launches a new backup generation process for the given {@code backupConf}.
	 * 
	 * @param backupConf
	 *          The backup configuration.
	 * @param context
	 *          The user context.
	 * @return Returns either:
	 *         <ul>
	 *         <li>The existing {@link BackupDTO} temporary file details if a process is currently running ({@code running}
	 *         attribute is set to {@code true}).</li>
	 *         <li>{@code null} if the generation process has been successfully launched.</li>
	 *         </ul>
	 * @throws IOException
	 *           If an error occurs.
	 */
	private BackupDTO launchNewBackupGeneration(final BackupDTO backupConf, final UserExecutionContext context) throws IOException, CommandException {

		final BackupDTO tempBackup = backupArchiveManager.getRunningBackupProcessFile(backupConf.getOrganizationId());

		if (tempBackup != null) {

			if (LOG.isInfoEnabled()) {
				LOG.info("A backup generation process is currently running for organization #{}.", backupConf.getOrganizationId());
			}

			return tempBackup;
		}

		backupArchiveManager.startBackupArchiveGeneration(backupConf, context.getUser());
		return null;
	}

	/**
	 * Checks if a backup archive file already exists for the given {@code organizationId}.
	 * 
	 * @param organizationId
	 *          The organization id.
	 * @param context
	 *          The user context.
	 * @return Returns either:
	 *         <ul>
	 *         <li>The existing {@link BackupDTO} archive file details ({@code running} attribute is set to {@code false}
	 *         ).</li>
	 *         <li>The existing {@link BackupDTO} temporary file details if a process is currently running ({@code running}
	 *         attribute is set to {@code true}).</li>
	 *         <li>{@code null} if no existing archive has been found and no process is currently running.</li>
	 *         </ul>
	 * @throws IOException
	 *           If an error occurs.
	 */
	private BackupDTO getExistingBackupArchive(final Integer organizationId, final UserExecutionContext context) throws IOException {

		final BackupDTO tempBackup = backupArchiveManager.getRunningBackupProcessFile(organizationId);

		if (tempBackup != null) {

			if (LOG.isInfoEnabled()) {
				LOG.info("A backup generation process is currently running for organization #{}.", organizationId);
			}

			return tempBackup;
		}

		final BackupDTO existingBackup = backupArchiveManager.getExistingBackup(organizationId);

		if (existingBackup != null) {

			if (LOG.isInfoEnabled()) {
				LOG.info("A backup archive already exists for organization with id #{}.", organizationId);
			}

			// Existing backup archive.
			return existingBackup;
		}

		return null;
	}

}
