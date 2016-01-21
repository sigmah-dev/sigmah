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
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mail.ModelMailService;
import org.sigmah.shared.command.UploadSlice;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailKeyEnum;
import org.sigmah.shared.dto.referential.EmailType;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UploadSliceHandler extends AbstractCommandHandler<UploadSlice, VoidResult> {
	private static final Logger LOGGER = LoggerFactory.getLogger(PrepareFileUploadHandler.class);

	@Inject
	private FileStorageProvider fileStorageProvider;
	
	@Inject
	private ModelMailService modelMailService;
	
	@Override
	protected VoidResult execute(UploadSlice command, UserDispatch.UserExecutionContext context) throws CommandException {
		final FileVersionDTO fileVersionDTO = command.getFileVersionDTO();
		
		// Searching the original file version in database.
		final FileVersion fileVersion = em().find(FileVersion.class, fileVersionDTO.getId());
		
		final String path;
		if(fileVersion != null) {
			path = fileVersion.getPath();
		} else {
			path = generateNameForNullPathFile(command, context);
		}
		
		final String sliceName = path + '.' + command.getOffset();
		
		try(final OutputStream outputStream = fileStorageProvider.create(sliceName)) {
			outputStream.write(command.getData());
			
		} catch (IOException ex) {
			LOGGER.error("An error occured while creating the file slice '" + sliceName + "'.", ex);
		}
		
		if(command.isLast()) {
			// Patch everything together.
			final byte[] bytes = new byte[1024];
			
			try(final OutputStream outputStream = fileStorageProvider.create(path)) {
				for(int offset = 0; offset < fileVersionDTO.getSize();) {
					final String slice = path + '.' + offset;
					
					// Read the content of the slice.
					try(final InputStream inputStream = fileStorageProvider.open(slice)) {
						int read = inputStream.read(bytes);
						while(read != -1) {
							offset += read;
							outputStream.write(bytes, 0, read);
							read = inputStream.read(bytes);
						}
						
					} catch(IOException ex) {
						LOGGER.error("An error occured while reading the slice '" + slice + "'.", ex);
					}
					
					// Remove the slice.
					try {
						fileStorageProvider.delete(slice);

					} catch(IOException ex) {
						LOGGER.error("An error occured while removing the slice '" + slice + "'.", ex);
					}
				}
				
			} catch (IOException ex) {
				LOGGER.error("An error occured while patching the file slice together into file '" + fileVersionDTO.getPath() + "'.", ex);
			}
			
			if(fileVersion == null) {
				try {
					try {
						sendFileByEmail(fileVersionDTO, path, context);
					} finally {
						fileStorageProvider.delete(path);
					}
				} catch(IOException ex) {
					LOGGER.error("An error occured while accessing the unused file '" + path + "'.", ex);
				}
			}
		}
		
		return null;
	}
	
	private String generateNameForNullPathFile(UploadSlice command, UserDispatch.UserExecutionContext context) {
		final String fileName = ValueResultUtils.normalizeFileName(command.getFileVersionDTO().getName());
		return context.getUser().getEmail() + '_' + command.getFileVersionDTO().getId() + '_' + fileName;
	}
	
	private void sendFileByEmail(FileVersionDTO fileVersion, String path, UserDispatch.UserExecutionContext context) throws IOException {
		// Full name.
		final String firstName = context.getUser().getFirstName();
		final String lastName = context.getUser().getName();
		final String fullName = firstName.isEmpty() ? lastName : firstName + ' ' + lastName;
		
		// Normalized file name.
		final String fileName = fileVersion.getName() + '.' + fileVersion.getExtension();
		
		// Send the mail.
		final HashMap<EmailKey, String> parameters = new HashMap<>();
		parameters.put(EmailKeyEnum.USER_USERNAME, fullName);
		parameters.put(EmailKeyEnum.FILE_NAME, fileName);
		
		try(InputStream inputStream = fileStorageProvider.open(path)) {
			modelMailService.send(EmailType.OFFLINE_FILE_CONFLICT, parameters, fileName, inputStream, context.getLanguage(), new String[] {context.getUser().getEmail()});
		}
	}
	
}
