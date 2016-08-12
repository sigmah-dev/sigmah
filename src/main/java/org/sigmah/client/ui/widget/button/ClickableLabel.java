package org.sigmah.client.ui.widget.button;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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

	@Override
	public void addStyleName(String style) {
		widget.addStyleName(style);
	}
}
