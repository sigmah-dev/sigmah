package org.sigmah.shared.dto.referential;

import org.sigmah.shared.command.result.Result;

/**
 * State of an amendment.
 */
public enum AmendmentState implements Result {

	DRAFT(AmendmentAction.LOCK),
	LOCKED(AmendmentAction.UNLOCK, AmendmentAction.REJECT, AmendmentAction.VALIDATE),
	ACTIVE(AmendmentAction.CREATE),
	REJECTED,
	ARCHIVED,
	PROJECT_ENDED;

	private final AmendmentAction[] actions;

	private AmendmentState(AmendmentAction... actions) {
		this.actions = actions;
	}

	/**
	 * Retrieves the list of actions that are available when an amendment is in a given state.
	 * 
	 * @return An array containing the possible actions.
	 */
	public AmendmentAction[] getActions() {
		return actions;
	}
}
