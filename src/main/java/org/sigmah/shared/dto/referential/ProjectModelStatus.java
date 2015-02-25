package org.sigmah.shared.dto.referential;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.util.Pair;

import com.google.gwt.core.client.GWT;

/**
 * The different status of projects.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public enum ProjectModelStatus implements Result {

	DRAFT,
	READY,
	USED,
	UNAVAILABLE,
	UNDER_MAINTENANCE;

	/**
	 * Gets the translation value for the given {@code status}. To use only on the client-side.
	 * 
	 * @param status
	 *          The {@code ProjectModelStatus} value.
	 * @return The translation value for the given {@code status}.
	 */
	public static String getName(final ProjectModelStatus status) {

		if (status == null) {
			return null;
		}

		if (!GWT.isClient()) {
			return status.name();
		}

		switch (status) {
			case DRAFT:
				return I18N.CONSTANTS.DRAFT();
			case READY:
				return I18N.CONSTANTS.READY();
			case USED:
				return I18N.CONSTANTS.USED();
			case UNAVAILABLE:
				return I18N.CONSTANTS.UNAVAILABLE();
			case UNDER_MAINTENANCE:
				return I18N.CONSTANTS.UNDER_MAINTENANCE();
			default:
				return status.name();
		}
	}

	/**
	 * Returns the given {@code status} corresponding {@code ProjectModelStatus} instance.<br/>
	 * Default returned value is {@link #DRAFT}.
	 * 
	 * @param status
	 *          The project model status text.
	 * @return the given {@code status} corresponding {@code ProjectModelStatus} instance.
	 */
	public static ProjectModelStatus getStatus(final String status) {

		final ProjectModelStatus statusEnum;

		if (I18N.CONSTANTS.DRAFT().equals(status)) {
			statusEnum = DRAFT;

		} else if (I18N.CONSTANTS.READY().equals(status)) {
			statusEnum = READY;

		} else if (I18N.CONSTANTS.USED().equals(status)) {
			statusEnum = USED;

		} else if (I18N.CONSTANTS.UNAVAILABLE().equals(status)) {
			statusEnum = UNAVAILABLE;
			
		} else if(I18N.CONSTANTS.UNDER_MAINTENANCE().equals(status)) {
			statusEnum = UNDER_MAINTENANCE;

		} else {
			statusEnum = DRAFT;
		}

		return statusEnum;
	}

	/**
	 * Check if a model's status change is valid.
	 * 
	 * @param currentStatus
	 *          The current status of the model.
	 * @param targetStatus
	 *          The new selected status that should be controlled.
	 * @return A pair containing the change valid flag with its error message (if not valid).
	 */
	public static Pair<Boolean, String> isValidStatusChange(final ProjectModelStatus currentStatus, final ProjectModelStatus targetStatus) {

		if (currentStatus == ProjectModelStatus.DRAFT && targetStatus != ProjectModelStatus.READY) {
			// "DRAFT" status is only allowed to shift to "READY" status.
			return new Pair<Boolean, String>(false, I18N.CONSTANTS.draftModelStatusChangeError());

		} else if (currentStatus == ProjectModelStatus.READY && targetStatus != ProjectModelStatus.DRAFT && targetStatus != ProjectModelStatus.UNAVAILABLE) {
			// "READY" status is only allowed to shift to "DRAFT" or "UNAVAILABLE" status.
			return new Pair<Boolean, String>(false, I18N.CONSTANTS.readyModelStatusChangeError());

		} else if (currentStatus.equals(ProjectModelStatus.USED) && !targetStatus.equals(ProjectModelStatus.UNAVAILABLE)) {
			// "USED" status is only allowed to shift to "UNAVAILABLE" status.
			return new Pair<Boolean, String>(false, I18N.CONSTANTS.usedModelStatusChangeError());

		} else {
			// Others cases are all allowed.
			return new Pair<Boolean, String>(true, null);
		}
	}

	private ProjectModelStatus() {
	}
}
