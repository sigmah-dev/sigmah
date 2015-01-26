package org.sigmah.client.ui.presenter.reports;

/**
 * Report actions interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ReportActionsHandler {

	/**
	 * Returns the report edit button enabled/visibility state.
	 * 
	 * @return {@code true} if the edit button should be visible and enabled.
	 */
	boolean isEditionEnabled();

	/**
	 * Callback executed on report <b>close</b> action (upper right close icon).
	 */
	void onCloseReport();

	/**
	 * Callback executed on report <b>delete</b> button action.
	 */
	void onDeleteReport();

	/**
	 * Callback executed on report <b>share (send)</b> button action.
	 */
	void onShareReport();

	/**
	 * Callback executed on report <b>save</b> button action.
	 */
	void onSaveReport();

	/**
	 * Callback executed on report <b>edit</b> action.
	 */
	void onEditReport();

	/**
	 * Callback executed on report <b>export</b> button action.
	 */
	void onExportReport();

}
