package org.sigmah.shared.command;

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
