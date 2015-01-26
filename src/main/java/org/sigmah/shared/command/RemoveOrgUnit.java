package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Removes an OrgUnit (unless it has a child).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class RemoveOrgUnit extends AbstractCommand<VoidResult> {

	/**
	 * The OrgUnit id to remove.
	 */
	private Integer id;

	protected RemoveOrgUnit() {
		// Serialization.
	}

	public RemoveOrgUnit(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
}
