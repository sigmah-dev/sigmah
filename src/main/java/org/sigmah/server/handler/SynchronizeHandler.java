package org.sigmah.server.handler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.mail.ModelMailService;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.SynchronizeResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.referential.Container;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailKeyEnum;
import org.sigmah.shared.dto.referential.EmailType;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * Handler for {@link Synchronize} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SynchronizeHandler extends AbstractCommandHandler<Synchronize, SynchronizeResult> {
	
	@Inject
	private I18nServer i18nServer;
	
	@Inject
	private ModelMailService modelMailService;
	
    @Override
    protected SynchronizeResult execute(Synchronize synchronize, UserDispatch.UserExecutionContext context) throws CommandException {
		final HashMap<Container, List<String>> errors = new HashMap<>();
		final HashMap<Integer, Integer> files = new HashMap<>();
		boolean errorConcernFiles = false;
		
        for(final Command<?> command : synchronize.getCommands()) {
			try {
				if(command instanceof UpdateProject) {
					((UpdateProject)command).setComment(i18nServer.t(context.getLanguage(), "sigmahOfflineSynchronizeUpdateComment"));
				}
				
				final Result result = context.execute(command);
				
				if(command instanceof PrepareFileUpload) {
					final String negativeId = ((PrepareFileUpload)command).getProperties().get(FileUploadUtils.GENERATED_ID);
					final FileVersionDTO fileVersion = (FileVersionDTO)result;
					
					files.put(Integer.parseInt(negativeId), fileVersion.getId());
				}
				
			} catch(UpdateConflictException e) {
				errorConcernFiles |= e.isFile();
				
				final Container container = e.getContainer();
				List<String> list = errors.get(container);
				
				if(list == null) {
					list = new ArrayList<>();
					errors.put(container, list);
				}
				
				for(int index = 0; index < e.getParameterCount(); index++) {
					list.add(e.getParameter(index));
				}
			}
        }
		
		if(!errors.isEmpty()) {
			sendErrorsByMailToCurrentUser(errors, errorConcernFiles, context);
		}
		
        return new SynchronizeResult(errors, errorConcernFiles, files);
    }
    
	private void sendErrorsByMailToCurrentUser(Map<Container, List<String>> errors, boolean hasFiles, UserDispatch.UserExecutionContext context) {
		// Format the error list.
		final StringBuilder ulBuilder = new StringBuilder();
		for(final Map.Entry<Container, List<String>> entry : errors.entrySet()) {
			if(entry.getKey().getType() == Container.Type.PROJECT) {
				ulBuilder.append(i18nServer.t(context.getLanguage(), "project"));
			} else {
				ulBuilder.append(i18nServer.t(context.getLanguage(), "orgunit"));
			}
			
			ulBuilder.append(' ')
				.append(entry.getKey().getName())
				.append(" - ")
				.append(entry.getKey().getFullName())
				.append("<ul>");
			
			for(final String error : entry.getValue()) {
				ulBuilder.append("<li>").append(error).append("</li>");
			}
			ulBuilder.append("</ul>");
		}
		
		if(hasFiles) {
			ulBuilder.append(i18nServer.t(context.getLanguage(), "conflictFiles"));
		}
		
		// Full name.
		final String firstName = context.getUser().getFirstName();
		final String lastName = context.getUser().getName();
		final String fullName = firstName.isEmpty() ? lastName : firstName + ' ' + lastName;
		
		// Send the mail.
		final HashMap<EmailKey, String> parameters = new HashMap<>();
		parameters.put(EmailKeyEnum.USER_USERNAME, fullName);
		parameters.put(EmailKeyEnum.ERROR_LIST, ulBuilder.toString());
		
		modelMailService.send(EmailType.OFFLINE_SYNC_CONFICT, parameters, context.getLanguage(), context.getUser().getEmail());
	}
	
}
