package org.sigmah.server.file;

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
