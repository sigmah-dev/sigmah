package org.sigmah.offline.js;

import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ValueJSIdentifierFactory {
    private ValueJSIdentifierFactory() {
    }
    
    public static String toIdentifier(GetValue getValue) {
		return toIdentifier(getValue.getElementEntityName(), getValue.getProjectId(), (int)getValue.getElementId(), getValue.getAmendmentId());
	}
	
	public static String toIdentifier(UpdateProject updateProject, ValueEventWrapper valueEventWrapper) {
		return toIdentifier(valueEventWrapper.getSourceElement().getEntityName(), updateProject.getProjectId(), valueEventWrapper.getSourceElement().getId(), null);
	}
	
	public static String toIdentifier(String elementEntityName, int projectId, int elementId, Integer amendmentId) {
		return new StringBuilder()
				.append(elementEntityName).append('-')
				.append(projectId).append('-')
				.append(elementId).append('-')
				.append(amendmentId).toString();
	}
}
