package org.sigmah.shared.command;

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
	 * The date before which the history is ignored. Set to <code>null</code> to retrieves the complete history.
	 */
	private Date maxDate;

	protected GetHistory() {
		// Serialization.
	}

	public GetHistory(int elementId, int projectId) {
		this.elementId = elementId;
		this.projectId = projectId;
	}

	public GetHistory(int elementId, int projectId, Date maxDate) {
		this.elementId = elementId;
		this.maxDate = maxDate;
		this.projectId = projectId;
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

}
