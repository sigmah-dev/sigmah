package org.sigmah.server.handler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UploadSlice;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.value.FileVersionDTO;
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
					final String slice = fileVersionDTO.getPath() + '.' + offset;
					
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
					sendFileByEmail(path, context);
					
				} finally {
					try {
						fileStorageProvider.delete(path);

					} catch(IOException ex) {
						LOGGER.error("An error occured while removing the unused file '" + path + "'.", ex);
					}
				}
			}
		}
		
		return null;
	}
	
	private String generateNameForNullPathFile(UploadSlice command, UserDispatch.UserExecutionContext context) {
		return context.getUser().getEmail() + '_' + command.getFileVersionDTO().getId() + '_' + command.getFileVersionDTO().getName();
	}
	
	private void sendFileByEmail(String path, UserDispatch.UserExecutionContext context) {
		// TODO: Move this method to MailSender
		
	}
	
}
