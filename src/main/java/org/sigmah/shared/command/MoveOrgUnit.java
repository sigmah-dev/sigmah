package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Move a given {@code OrgUnitDTO} within its hierarchy.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MoveOrgUnit extends AbstractCommand<VoidResult> {

	/**
	 * The org unit id to move.
	 */
	private Integer id;

	/**
	 * The new parent org unit id.
	 */
	private Integer parentId;

	public MoveOrgUnit() {
		// Serialization.
	}

	public MoveOrgUnit(final Integer id, final Integer parentId) {
		this.id = id;
		this.parentId = parentId;
	}

	public Integer getId() {
		return id;
	}

	public Integer getParentId() {
		return parentId;
	}

}
