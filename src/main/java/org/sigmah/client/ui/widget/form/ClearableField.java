package org.sigmah.client.ui.widget.form;

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


import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import org.sigmah.client.ui.res.icon.IconImageBundle;

/**
 * Field that encapsulates an other field and adds a clear button next to it.
 * 
 * @param <T> Type of the value of the encapsulated field.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ClearableField<T> extends AdapterField {
	
	private static final int BUTTON_WIDTH = 21;
	
	private final Image deleteIcon;
	private final Field<T> field;
	
	public ClearableField(final Field<T> field) {
		super(new Grid(1, 2));
		
		setLabelSeparator(field.getLabelSeparator());
		setFieldLabel(field.getFieldLabel());

		this.field = field;
		this.deleteIcon = IconImageBundle.ICONS.deleteIcon().createImage();
		
		Grid grid = (Grid) widget;
		grid.setWidget(0, 0, field);
		grid.setWidget(0, 1, deleteIcon);
		grid.setCellSpacing(1);

		grid.getCellFormatter().setWidth(0, 1, "5px");
		
		addClearHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				field.clear();
			}
		});
	}
	
	/**
	 * Returns the encapsulated field.
	 * 
	 * @return the encapsulated field.
	 */
	public Field<T> getField() {
		return field;
	}
	
	/**
	 * Adds an handler to the clear button.
	 * 
	 * @param handler Handler to attach to the clear button.
	 */
	public final void addClearHandler(ClickHandler handler){
		deleteIcon.addClickHandler(handler);
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void clear() {
		field.clear();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void clearInvalid() {
		field.clearInvalid();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	public void clearState() {
		field.clearState();
	}

	/**
	 * {@inheritDoc} 
	 */
	@Override
	protected void onResize(int width, int height) {
		super.onResize(width, height);
		field.setSize(width - BUTTON_WIDTH, height);
	}
	
}

