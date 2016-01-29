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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.mail.ModelMailService;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.command.Synchronize;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.SynchronizeResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.referential.ContainerInformation;
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
	
    /**
     * Access to the localized strings.
     */
	@Inject
	private I18nServer i18nServer;
	
    /**
     * Service to send emails.
     */
	@Inject
	private ModelMailService modelMailService;
	
    /**
     * {@inheritDoc}
     */
    @Override
    protected SynchronizeResult execute(Synchronize synchronize, UserDispatch.UserExecutionContext context) throws CommandException {
        
		final HashMap<ContainerInformation, List<String>> errors = new HashMap<>();
		final HashMap<Integer, Integer> files = new HashMap<>();
		boolean errorConcernFiles = false;
		
        for (final Command<?> command : synchronize.getCommands()) {
			try {
				if (command instanceof UpdateProject) {
					((UpdateProject)command).setComment(i18nServer.t(context.getLanguage(), "sigmahOfflineSynchronizeUpdateComment"));
				}
				
				final Result result = context.execute(command);
				
				if (command instanceof PrepareFileUpload) {
					final String negativeId = ((PrepareFileUpload)command).getProperties().get(FileUploadUtils.GENERATED_ID);
					final FileVersionDTO fileVersion = (FileVersionDTO)result;
					
					files.put(Integer.parseInt(negativeId), fileVersion.getId());
				}
				
			} catch (UpdateConflictException e) {
				errorConcernFiles |= e.isFile();
				
				final ContainerInformation container = e.getContainer();
				List<String> list = errors.get(container);
				
				if(list == null) {
					list = new ArrayList<>();
					errors.put(container, list);
				}
				
				list.addAll(Arrays.asList(e.getParameters()));
			}
        }
		
		if (!errors.isEmpty()) {
			sendErrorsByMailToCurrentUser(errors, errorConcernFiles, context);
		}
		
        return new SynchronizeResult(errors, errorConcernFiles, files);
    }
    
    /**
     * Send errors of the synchronization by email.
     * 
     * @param errors
     *          Map of errors for each container (project or orgunit).
     * @param hasFiles
     *          <code>true</code> if at least one error concern files.
     * @param context 
     *          Execution context.
     */
	private void sendErrorsByMailToCurrentUser(Map<ContainerInformation, List<String>> errors, boolean hasFiles, UserDispatch.UserExecutionContext context) {
        
		// Full name.
		final String firstName = context.getUser().getFirstName();
		final String lastName = context.getUser().getName();
		final String fullName = firstName.isEmpty() ? lastName : firstName + ' ' + lastName;
		
		// Send the mail.
		final HashMap<EmailKey, String> parameters = new HashMap<>();
		parameters.put(EmailKeyEnum.USER_USERNAME, fullName);
		parameters.put(EmailKeyEnum.ERROR_LIST, errorListBody(errors, hasFiles, context.getLanguage()));
		
		modelMailService.send(EmailType.OFFLINE_SYNC_CONFLICT, parameters, context.getLanguage(), context.getUser().getEmail());
	}

    /**
     * Format the error list.
     * 
     * @param errors
     *          Map of errors for each container (project or orgunit).
     * @param hasFiles
     *          <code>true</code> if at least one error concern files.
     * @param context
     *          Execution context.
     * @return Body of the mail to be sent.
     */
    private String errorListBody(Map<ContainerInformation, List<String>> errors, boolean hasFiles, Language language) {
        
        final StringBuilder ulBuilder = new StringBuilder();
        
        for (final Map.Entry<ContainerInformation, List<String>> entry : errors.entrySet()) {
            
            if (entry.getKey().isProject()) {
                ulBuilder.append(i18nServer.t(language, "project"));
            } else {
                ulBuilder.append(i18nServer.t(language, "orgunit"));
            }
            
            ulBuilder.append(' ')
                    .append(entry.getKey().getName())
                    .append(" - ")
                    .append(entry.getKey().getFullName())
                    .append("<ul>");
            
            for (final String error : entry.getValue()) {
                ulBuilder.append("<li>").append(error.replace("\n", "<br>")).append("</li>");
            }
            ulBuilder.append("</ul>");
        }
        
        if (hasFiles) {
            ulBuilder.append(i18nServer.t(language, "conflictFiles"));
        }
        
        return ulBuilder.toString();
    }
	
}
