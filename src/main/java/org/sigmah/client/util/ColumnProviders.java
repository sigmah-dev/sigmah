package org.sigmah.client.util;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Columns providers utility class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ColumnProviders {

	// CSS style names.
	private static final String STYLE_PROJECT_GRID_CODE = "project-grid-code";
	private static final String STYLE_LABEL_SMALL = "label-small";

	/**
	 * Renders a link for the given arguments.
	 * 
	 * @param label
	 *          The link label (may contain HTML).
	 * @param clickHandler
	 *          The link click handler.
	 * @return A link rendering for the given arguments.
	 */
	public static Object renderLink(final String label, final ClickHandler clickHandler) {
		final Anchor link = new Anchor(label, true);
		if (clickHandler != null) {
			link.addClickHandler(clickHandler);
		}

		final SimplePanel wrapper = new SimplePanel(link);
		wrapper.setStyleName(STYLE_PROJECT_GRID_CODE);
		return wrapper;
	}

	/**
	 * Renders the given {@code value} as a text.
	 * 
	 * @param value
	 *          The text value.
	 * @return A text rendering for the given {@code value}.
	 */
	public static Object renderText(final Object value) {

		// Renders direct HTML to improve performances.
		final StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"").append(STYLE_LABEL_SMALL).append(" x-component\">");
		builder.append(value != null ? String.valueOf(value) : "").append("</div>");

		return builder.toString();
	}

	/**
	 * Renders the given {@code value} as a striked text.
	 * 
	 * @param value
	 *          The text value.
	 * @return A text rendering for the given {@code value}.
	 */
	public static Object renderDisabled(final Object value) {

		// Renders direct HTML to improve performances.
		final StringBuilder builder = new StringBuilder();
		builder.append("<div class=\"").append(STYLE_LABEL_SMALL).append(" x-component\" style=\"text-decoration: line-through;\">");
		builder.append(value != null ? String.valueOf(value) : "").append("</div>");

		return builder.toString();
	}

	/**
	 * Renders the given boolean value.
	 * 
	 * @param value
	 *          The boolean value ({@code null} means {@code false}).
	 * @param title
	 *          The title property (tooltip).
	 * @return A boolean rendering for the given arguments.
	 */
	public static Object renderBoolean(final Boolean value, final String title) {

		// Renders direct HTML to improve performances.
		final StringBuilder builder = new StringBuilder();

		// Wrapper div.
		builder.append("<div class=\"x-form-check-wrap x-form-field x-component x-item-disabled\" ");
		builder.append("role=\"presentation\" style=\"position: relative;\" ");
		builder.append(" title=\"").append(title != null ? title : "").append("\" >");

		// Input checkbox.
		builder.append("<input type=\"checkbox\" class=\"x-form-checkbox\" value=\"\" ");
		builder.append(" style=\"position: relative; left: 4px; top: 2px;\" tabindex=\"0\" disabled=\"\" ");
		builder.append(ClientUtils.isTrue(value) ? "checked" : "").append(" />");

		builder.append("</div>");

		return builder.toString();
	}

	private ColumnProviders() {
		// Only provides static constants.
	}

}
