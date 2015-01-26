package org.sigmah.client.ui.widget.button;

import org.sigmah.client.ui.res.icon.IconImageBundle;

import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;

/**
 * Label field with a delete icon.
 * 
 * @author nrebiai v1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ClickableLabel extends AdapterField implements HasClickHandlers {

	private final Image deleteIcon;

	/**
	 * Initializes a new removable label field.
	 * 
	 * @param label
	 *          The label, may be {@code null}.
	 */
	public ClickableLabel(final String label) {
		super(new Grid(1, 2));

		deleteIcon = IconImageBundle.ICONS.deleteIcon().createImage();

		asGrid().setWidget(0, 0, deleteIcon);
		asGrid().setCellSpacing(0);
		asGrid().getCellFormatter().getElement(0, 0).getStyle().setPaddingRight(5, Unit.PX);

		setLabelSeparator(" ");
		setLabel(label);
	}

	/**
	 * Simply casts the parent {@code widget} attribute into the proper type.
	 * 
	 * @return The grid widget instance.
	 */
	private Grid asGrid() {
		return (Grid) widget;
	}

	/**
	 * Sets the label.
	 * 
	 * @param label
	 *          The new label.
	 */
	public void setLabel(String label) {
		asGrid().setText(0, 1, label);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return deleteIcon.addClickHandler(handler);
	}

}
