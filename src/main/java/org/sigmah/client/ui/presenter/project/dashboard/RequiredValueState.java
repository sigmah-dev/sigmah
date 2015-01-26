package org.sigmah.client.ui.presenter.project.dashboard;

/**
 * Useful internal class to manage the required elements completion.
 *
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class RequiredValueState {

	/**
	 * The required element saved value completion (in db).
	 */
	private Boolean savedState;

	/**
	 * The actual element saved value completion (in local).
	 */
	private Boolean actualState;

	public void setSavedState(Boolean savedState) {
		this.savedState = savedState;
	}

	public void setActualState(Boolean actualState) {
		this.actualState = actualState;
	}

	/**
	 * Informs that the actual value completion has been saved to the data layer.
	 */
	public void saveState() {
		if (actualState != null) {
			savedState = actualState;
			actualState = null;
		}
	}

	/**
	 * Informs that the actual value completion has been discarded.
	 */
	public void clearState() {
		actualState = null;
	}

	/**
	 * Returns if the saved value completion is valid.
	 *
	 * @return If the saved value completion is valid.
	 */
	public boolean isTrue() {
		return !Boolean.FALSE.equals(savedState);
	}

	/**
	 * Returns if the actual value completion is valid.
	 *
	 * @return If the actual value completion is valid.
	 */
	public boolean isActuallyTrue() {
		return !Boolean.FALSE.equals(actualState);
	}

	@Override
	public String toString() {
		return "saved: " + savedState + " ; actual: " + actualState;
	}
}
