package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.HashMap;
import org.sigmah.server.dispatch.FunctionalExceptions;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.SynchronizeResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeHandler extends AbstractCommandHandler<Synchronize, SynchronizeResult> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizeHandler.class);

    @Override
    protected SynchronizeResult execute(Synchronize synchronize, UserDispatch.UserExecutionContext context) throws CommandException {
		final ArrayList<String> errors = new ArrayList<String>();
		final HashMap<Integer, Integer> files = new HashMap<Integer, Integer>();
		
        for(final Command<?> command : synchronize.getCommands()) {
			try {
				if(command instanceof UpdateProject) {
					((UpdateProject)command).setComment("Synchronized from offline mode.");
				}
				
				final Result result = context.execute(command);
				
				if(command instanceof PrepareFileUpload) {
					final String negativeId = ((PrepareFileUpload)command).getProperties().get(FileUploadUtils.GENERATED_ID);
					final FileVersionDTO fileVersion = (FileVersionDTO)result;
					
					files.put(Integer.parseInt(negativeId), fileVersion.getId());
				}
				
			} catch(FunctionalException e) {
				errors.add(FunctionalExceptions.getMessage(context.getLanguage(), e));
			}
        }
		
		if(!errors.isEmpty()) {
			for(final String error : errors) {
				LOGGER.error(error);
			}
			
			// TODO: Send a mail to the current user.
		}
		
        return new SynchronizeResult(errors, files);
    }
    
}
