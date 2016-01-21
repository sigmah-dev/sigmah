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
