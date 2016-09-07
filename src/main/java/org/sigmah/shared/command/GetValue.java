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
	private Integer iterationId;

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
		this(projectId, elementId, elementEntityName, null, null);
	}

	public GetValue(Integer projectId, Integer elementId, String elementEntityName, Integer amendmentId) {
		this(projectId, elementId, elementEntityName, amendmentId, null);
	}

	public GetValue(Integer projectId, Integer elementId, String elementEntityName, Integer amendmentId, Integer iterationId) {
		this.projectId = projectId;
		this.elementId = elementId;
		this.elementEntityName = elementEntityName;
		this.amendmentId = amendmentId;
		this.iterationId = iterationId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("elementId", elementId);
		builder.append("iterationId", iterationId);
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

	public Integer getIterationId() {
		return iterationId;
	}

	public void setIterationId(Integer iterationId) {
		this.iterationId = iterationId;
	}
}
