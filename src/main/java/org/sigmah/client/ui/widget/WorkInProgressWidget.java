package org.sigmah.client.ui.widget;

import org.sigmah.client.ui.notif.N10N;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * A <em>work-in-progress</em> widget informing the user that a functionality is not available yet.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class WorkInProgressWidget extends Composite {

	/**
	 * The <em>work-in-progress</em> title.
	 */
	private static final String TITLE = "Available soon";

	/**
	 * The "<em>available <u>back</u></em>" work-in-progress text.
	 */
	private static final String TEXT_BACK = "Available back in Sigmah v2.0 final version";

	/**
	 * The "<em>available <u>soon</u></em>" work-in-progress text.
	 */
	private static final String TEXT_SOON = "Available soon in v2.1 released during first quarter 2015";

	/**
	 * CSS style name for the 'work-in-progress' label.
	 */
	private static final String CSS_WORK_IN_PROGRESS_LABEL = "work-in-progress-label";

	/**
	 * Builds a "<em>available <u>back</u></em>" work-in-progress widget informing the user that a functionality is not
	 * available yet.
	 * 
	 * @see WorkInProgressWidget#TEXT_BACK
	 */
	public WorkInProgressWidget() {
		this(false);
	}

	/**
	 * Builds a work-in-progress widget informing the user that a functionality is not available yet.
	 * 
	 * @param soon
	 *          If {@code true}, the popup will display an "<em>available <u>soon</u></em>" message. If {@code false}, it
	 *          will display an "<em>available <u>back</u></em>" message.
	 * @see WorkInProgressWidget#TEXT_BACK
	 * @see WorkInProgressWidget#TEXT_SOON
	 */
	public WorkInProgressWidget(final boolean soon) {
		final Label label = new Label(soon ? TEXT_SOON : TEXT_BACK);
		label.setStyleName(CSS_WORK_IN_PROGRESS_LABEL);
		initWidget(new SimplePanel(label));
	}

	/**
	 * Shows an "<em>available <u>back</u></em>" work-in-progress popup informing the user that a functionality is not
	 * available yet.
	 * 
	 * @see WorkInProgressWidget#TEXT_BACK
	 */
	public static void popup() {
		popup(false);
	}

	/**
	 * Shows a work-in-progress popup informing the user that a functionality is not available yet.
	 * 
	 * @param soon
	 *          If {@code true}, the popup will display an "<em>available <u>soon</u></em>" message. If {@code false}, it
	 *          will display an "<em>available <u>back</u></em>" message.
	 * @see WorkInProgressWidget#TEXT_BACK
	 * @see WorkInProgressWidget#TEXT_SOON
	 */
	public static void popup(final boolean soon) {
		N10N.warn(WorkInProgressWidget.TITLE, soon ? TEXT_SOON : TEXT_BACK);
	}

}
