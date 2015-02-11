package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.command.base.Command;

/**
 * Parent class of the JavaScript versions of {@link Command}s.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CommandJS extends JavaScriptObject {
	
	public static enum Type {
		CREATE_ENTITY,
		DELETE,
		UPDATE_ENTITY,
		UPDATE_LOG_FRAME,
		UPDATE_MONITORED_POINTS,
		UPDATE_PROJECT,
		UPDATE_PROJECT_FAVORITE,
		UPDATE_REMINDERS
	}
	
	public static final Class[] SUPPORTED_COMMANDS = new Class[] {
		CreateEntity.class,
		Delete.class,
		UpdateEntity.class,
		UpdateLogFrame.class,
		UpdateMonitoredPoints.class,
		UpdateProject.class,
		UpdateProjectFavorite.class,
		UpdateReminders.class
	};
	
	protected CommandJS() {
	}
	
	public static CommandJS toJavaScript(Command command) {
		CommandJS commandJS = null;
		
		if(command instanceof UpdateProject) {
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
			
		} else if(command instanceof UpdateReminders) {
			commandJS = UpdateRemindersJS.toJavaScript((UpdateReminders)command);
			commandJS.setCommandType(Type.UPDATE_REMINDERS);
			
		} else if(command instanceof UpdateEntity) {
			commandJS = UpdateEntityJS.toJavaScript((UpdateEntity)command);
			commandJS.setCommandType(Type.UPDATE_ENTITY);
		}
		
		return commandJS;
	}
	
	public Command<?> toCommand() {
		final Command<?> command;
		
		switch(getCommandTypeEnum()) {
			case CREATE_ENTITY:
				command = ((CreateEntityJS)this).toCreateEntity();
				break;
			case DELETE:
				command = ((DeleteJS)this).toDelete();
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
			case UPDATE_PROJECT_FAVORITE:
				command = ((UpdateProjectFavoriteJS)this).toUpdateProjectFavorite();
				break;
			case UPDATE_REMINDERS:
				command = ((UpdateRemindersJS)this).toUpdateReminders();
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
	
	public Type getCommandTypeEnum() {
		if(getCommandType() != null) {
			return Type.valueOf(getCommandType());
		} else {
			return null;
		}
	}

	public void setCommandType(Type type) {
		if(type != null) {
			setCommandType(type.name());
		}
	}
	
	public final native void setCommandType(String elementType) /*-{
		this.elementType = elementType;
	}-*/;
}
