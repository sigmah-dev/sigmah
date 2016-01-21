package org.sigmah.shared.command;

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


import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.AmendmentAction;

/**
 * Command handling actions on project amendments.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AmendmentActionCommand extends AbstractCommand<ProjectDTO> {

	private Integer projectId;
	private AmendmentAction action;
	private String name;

	public AmendmentActionCommand() {
		// Serialization.
	}

	public AmendmentActionCommand(final Integer projectId, final AmendmentAction action) {
		this(projectId, action, null);
	}
	
	public AmendmentActionCommand(final Integer projectId, final AmendmentAction action, String name) {
		this.projectId = projectId;
		this.action = action;
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("action", action);
	}

	public Integer getProjectId() {
		return projectId;
	}

	public AmendmentAction getAction() {
		return action;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
