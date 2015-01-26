package org.sigmah.server.handler;

import com.google.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DownloadSlice;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.file.FileSlice;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DownloadSliceHandler extends AbstractCommandHandler<DownloadSlice, FileSlice> {
	
	/**
	 * Injected application {@link FileStorageProvider}.
	 */
	private final FileStorageProvider fileStorageProvider;
	
	/**
	 * Injected {@link FileDAO}.
	 */
	private final FileDAO fileDAO;

	@Inject
	public DownloadSliceHandler(FileStorageProvider fileStorageProvider, FileDAO fileDAO) {
		this.fileStorageProvider = fileStorageProvider;
		this.fileDAO = fileDAO;
	}

	@Override
	protected FileSlice execute(DownloadSlice command, UserDispatch.UserExecutionContext context) throws CommandException {
		final FileVersion fileVersion = fileDAO.getVersion(command.getFileVersionId());
		
		FileSlice slice = new FileSlice();
			
		final long start = command.getOffset();
		if(start > fileVersion.getSize()) {
			slice.setData(new byte[0]);
			slice.setLast(true);

		} else {
			final int length = Math.min(toInteger(fileVersion.getSize() - start), command.getSize());
			final byte[] bytes = new byte[length];

			try {
				try(final InputStream inputStream = fileStorageProvider.open(fileVersion.getPath())) {
					long location = 0L;
					while(location < start) {
						location += inputStream.skip(start - location);
					}
					final int read = inputStream.read(bytes);
					slice.setLast(read < command.getSize());
				}

				slice.setData(bytes);

			} catch(IOException ex) {
				throw new CommandException(ex);
			}
		}
		
		return slice;
	}

	private int toInteger(long value) {
		if(value > Integer.MAX_VALUE) {
			return Integer.MAX_VALUE;
		} else {
			return (int)value;
		}
	}
}
