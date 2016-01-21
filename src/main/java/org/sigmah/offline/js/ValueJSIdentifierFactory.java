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
