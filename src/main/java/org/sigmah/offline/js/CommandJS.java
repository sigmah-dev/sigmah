package org.sigmah.offline.js;

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

import com.google.gwt.core.client.JavaScriptObject;

import org.sigmah.shared.command.*;
import org.sigmah.shared.command.base.Command;

/**
 * Parent class of the JavaScript versions of {@link Command}s.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CommandJS extends JavaScriptObject {
	
	public static enum Type {
		CREATE_ENTITY,
		DELETE,
		PREPARE_FILE_UPLOAD,
		UPDATE_ENTITY,
		UPDATE_LOG_FRAME,
		UPDATE_MONITORED_POINTS,
		UPDATE_LAYOUT_GROUP_ITERATION,
		UPDATE_PROJECT,
		UPDATE_CONTACT,
		UPDATE_PROJECT_FAVORITE,
		UPDATE_REMINDERS,
		UPDATE_PROJECT_TEAM_MEMBERS
	}
	
	public static final Class[] SUPPORTED_COMMANDS = new Class[] {
		CreateEntity.class,
		Delete.class,
		PrepareFileUpload.class,
		UpdateEntity.class,
		UpdateLogFrame.class,
		UpdateMonitoredPoints.class,
		UpdateProject.class,
		UpdateProjectFavorite.class,
		UpdateReminders.class,
		UpdateLayoutGroupIterations.class,
		UpdateContact.class,
		UpdateProjectTeamMembers.class
	};
	
	protected CommandJS() {
	}
	
	public static CommandJS toJavaScript(Command command) {
		CommandJS commandJS = null;
		
		if(command instanceof UpdateContact) {
			commandJS = UpdateContactJS.toJavaScript((UpdateContact)command);
			commandJS.setCommandType(Type.UPDATE_CONTACT);

		} else if(command instanceof UpdateProject) {
			commandJS = UpdateProjectJS.toJavaScript((UpdateProject)command);
			commandJS.setCommandType(Type.UPDATE_PROJECT);
			
		} else if(command instanceof Delete) {
			commandJS = DeleteJS.toJavaScript((Delete)command);
			commandJS.setCommandType(Type.DELETE);

		} else if(command instanceof UpdateProjectFavorite) {
			commandJS = UpdateProjectFavoriteJS.toJavaScript((UpdateProjectFavorite)command);
			commandJS.setCommandType(Type.UPDATE_PROJECT_FAVORITE);
			
		} else if(command instanceof UpdateLogFrame) {
			commandJS = UpdateLogFrameJS.toJavaScript((UpdateLogFrame)command);
			commandJS.setCommandType(Type.UPDATE_LOG_FRAME);

		} else if(command instanceof CreateEntity) {
			commandJS = CreateEntityJS.toJavaScript((CreateEntity)command);
			commandJS.setCommandType(Type.CREATE_ENTITY);

		} else if(command instanceof UpdateMonitoredPoints) {
			commandJS = UpdateMonitoredPointsJS.toJavaScript((UpdateMonitoredPoints)command);
			commandJS.setCommandType(Type.UPDATE_MONITORED_POINTS);

		} else if(command instanceof UpdateLayoutGroupIterations) {
			commandJS = UpdateLayoutGroupIterationsJS.toJavaScript((UpdateLayoutGroupIterations)command);
			commandJS.setCommandType(Type.UPDATE_LAYOUT_GROUP_ITERATION);
			
		} else if(command instanceof UpdateReminders) {
			commandJS = UpdateRemindersJS.toJavaScript((UpdateReminders)command);
			commandJS.setCommandType(Type.UPDATE_REMINDERS);
			
		} else if(command instanceof UpdateEntity) {
			commandJS = UpdateEntityJS.toJavaScript((UpdateEntity)command);
			commandJS.setCommandType(Type.UPDATE_ENTITY);
			
		} else if(command instanceof PrepareFileUpload) {
			commandJS = PrepareFileUploadJS.toJavaScript((PrepareFileUpload)command);
			commandJS.setCommandType(Type.PREPARE_FILE_UPLOAD);

		} else if(command instanceof UpdateProjectTeamMembers) {
			commandJS = UpdateProjectTeamMembersJs.toJavascript((UpdateProjectTeamMembers) command);
			commandJS.setCommandType(Type.UPDATE_PROJECT_TEAM_MEMBERS);
		}
		
		return commandJS;
	}
	
	public final Command<?> toCommand() {
		final Command<?> command;
		
		switch(getCommandTypeEnum()) {
			case CREATE_ENTITY:
				command = ((CreateEntityJS)this).toCreateEntity();
				break;
			case DELETE:
				command = ((DeleteJS)this).toDelete();
				break;
			case PREPARE_FILE_UPLOAD:
				command = ((PrepareFileUploadJS)this).toPrepareFileUpload();
				break;
			case UPDATE_ENTITY:
				command = ((UpdateEntityJS)this).toUpdateEntity();
				break;
			case UPDATE_LOG_FRAME:
				command = ((UpdateLogFrameJS)this).toUpdateLogFrame();
				break;
			case UPDATE_MONITORED_POINTS:
				command = ((UpdateMonitoredPointsJS)this).toUpdateMonitoredPoints();
				break;
			case UPDATE_PROJECT:
				command = ((UpdateProjectJS)this).toUpdateProject();
				break;
			case UPDATE_CONTACT:
				command = ((UpdateContactJS)this).toUpdateContact();
				break;
			case UPDATE_PROJECT_FAVORITE:
				command = ((UpdateProjectFavoriteJS)this).toUpdateProjectFavorite();
				break;
			case UPDATE_REMINDERS:
				command = ((UpdateRemindersJS)this).toUpdateReminders();
				break;
			case UPDATE_PROJECT_TEAM_MEMBERS:
				command = ((UpdateProjectTeamMembersJs)this).toUpdateProjectTeamMembers();
				break;
			case UPDATE_LAYOUT_GROUP_ITERATION:
				command = ((UpdateLayoutGroupIterationsJS)this).toUpdateLayoutGroupIterations();
				break;
			default:
				command = null;
				break;
		}
		
		return command;
	}
	
	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public final native String getCommandType() /*-{
		return this.elementType;
	}-*/;
	
	public final Type getCommandTypeEnum() {
		if(getCommandType() != null) {
			return Type.valueOf(getCommandType());
		} else {
			return null;
		}
	}

	public final void setCommandType(Type type) {
		if(type != null) {
			setCommandType(type.name());
		}
	}
	
	public final native void setCommandType(String elementType) /*-{
		this.elementType = elementType;
	}-*/;
}
