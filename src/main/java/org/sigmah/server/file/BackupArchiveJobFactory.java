package org.sigmah.server.file;

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

import org.sigmah.server.file.impl.BackupArchiveJob;
import org.sigmah.server.file.impl.BackupArchiveJob.BackupArchiveJobArgument;

import com.google.inject.assistedinject.Assisted;

/**
 * {@link BackupArchiveJob} factory allowing dynamic instantiation of {@link BackupArchiveJob} instances with injected
 * arguments (through assisted injection) or services (through regular injection).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface BackupArchiveJobFactory {

	/**
	 * Initializes a new {@link BackupArchiveJob} implementation with the given {@code arguments} injected.
	 * 
	 * @param arguments
	 *          The job necessary input data (injected with assisted injection).<br>
	 *          Assisted injected arguments cannot be {@code null} (that's why we use a wrapper).
	 * @return A new {@link BackupArchiveJob} implementation.
	 */
	BackupArchiveJob newJob(@Assisted BackupArchiveJobArgument arguments);

}
