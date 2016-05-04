package org.sigmah.shared.command;

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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.BackupDTO;

/**
 * <p>
 * Retrieves a backup archive file, or launches its generation process.<br>
 * If a backup generation process is already running, the command returns the temporary file configuration.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BackupArchiveManagementCommand extends AbstractCommand<BackupDTO> {

	/**
	 * <p>
	 * The backup configuration.
	 * </p>
	 * <p>
	 * If {@code null}, the command simply checks if an existing previous backup archive already exists.
	 * </p>
	 */
	private BackupDTO backupConfiguration;

	/**
	 * The organization id.
	 */
	private Integer organizationId;

	/**
	 * Empty action. Does nothing.
	 */
	public BackupArchiveManagementCommand() {
		// Serialization.
	}

	/**
	 * <p>
	 * Checks if an existing previous backup archive already exists for the given {@code organizationId}.<br>
	 * </p>
	 *
	 * Returns either:
	 * <ul>
	 * <li>The existing {@link BackupDTO} archive file details ({@code running} attribute is set to {@code false}).</li>
	 * <li>The existing {@link BackupDTO} temporary file details if a process is currently running ({@code running}
	 * attribute is set to {@code true}).</li>
	 * <li>{@code null} if no existing archive has been found and no process is currently running.</li>
	 * </ul>
	 *
	 * @param organizationId
	 *          The organization id.
	 */
	public BackupArchiveManagementCommand(final Integer organizationId) {
		this.organizationId = organizationId;
	}

	/**
	 * <p>
	 * Launches a new backup archive generation process.
	 * </p>
	 *
	 * Returns either:
	 * <ul>
	 * <li>The existing {@link BackupDTO} temporary file details if a process is currently running ({@code running}
	 * attribute is set to {@code true}).</li>
	 * <li>{@code null} if the generation process has been successfully launched.</li>
	 * </ul>
	 *
	 * @param backupConfiguration
	 *          The backup configuration (required).
	 */
	public BackupArchiveManagementCommand(final BackupDTO backupConfiguration) {
		this.backupConfiguration = backupConfiguration;
	}

	public BackupDTO getBackupConfiguration() {
		return backupConfiguration;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

}
