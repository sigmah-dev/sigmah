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

import java.util.Date;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

/**
 * Command to retrieves all the history tokens of an element.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetHistory extends AbstractCommand<ListResult<HistoryTokenListDTO>> {

	/**
	 * The element id.
	 */
	private int elementId;

	/**
	 * The project id.
	 */
	private int projectId;

	/**
	 * The group iteration id.
	 */
	private Integer iterationId;

	/**
	 * The date before which the history is ignored. Set to <code>null</code> to retrieves the complete history.
	 */
	private Date maxDate;

	protected GetHistory() {
		// Serialization.
	}

	public GetHistory(int elementId, int projectId) {
		this(elementId, projectId, null, null);
	}

	public GetHistory(int elementId, int projectId, Integer iterationId) {
		this(elementId, projectId, iterationId, null);
	}

	public GetHistory(int elementId, int projectId, Integer iterationId, Date maxDate) {
		this.elementId = elementId;
		this.projectId = projectId;
		this.iterationId = iterationId;
		this.maxDate = maxDate;
	}

	public int getElementId() {
		return elementId;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public int getProjectId() {
		return projectId;
	}

	public Integer getIterationId() {
		return iterationId;
	}
}
