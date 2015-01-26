package org.sigmah.shared.command;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ValueResult;

/**
 * Retrieves the value of a {@link FlexibleElement} referenced in a given project.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetValue extends AbstractCommand<ValueResult> {

	private Integer projectId;
	private Integer elementId;

	/**
	 * Do not use the getClass().getName() on client side to identify a flexible element type. Always use the
	 * getEntityName() !
	 */
	private String elementEntityName;

	private Integer amendmentId;

	public GetValue() {
		// Serialization.
	}

	public GetValue(Integer projectId, Integer elementId, String elementEntityName) {
		this(projectId, elementId, elementEntityName, null);
	}

	public GetValue(Integer projectId, Integer elementId, String elementEntityName, Integer amendmentId) {
		this.projectId = projectId;
		this.elementId = elementId;
		this.elementEntityName = elementEntityName;
		this.amendmentId = amendmentId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("elementId", elementId);
		builder.append("elementEntityName", elementEntityName);
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public Integer getElementId() {
		return elementId;
	}

	public void setElementId(Integer elementId) {
		this.elementId = elementId;
	}

	public String getElementEntityName() {
		return elementEntityName;
	}

	public void setElementEntityName(String elementClassName) {
		this.elementEntityName = elementClassName;
	}

	public Integer getAmendmentId() {
		return amendmentId;
	}

	public void setAmendmentId(Integer amendmentId) {
		this.amendmentId = amendmentId;
	}

}
