/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

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


import com.extjs.gxt.ui.client.widget.form.TextField;
import org.sigmah.client.i18n.I18N;

/**
 * GXT Field for Geographical coordinates. The type of the field is double,
 * but users can enter coordinates in practically any format, which are converted on the fly.
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class CoordinateField extends TextField<Double> {

    public enum Axis {
        LATITUDE ("y") {
			@Override
			public CoordinateEditor createCoordinateEditor() {
				return new CoordinateEditor(I18N.CONSTANTS.southHemiChars(), I18N.CONSTANTS.northHemiChars());
			}
		},
        LONGITUDE ("x") {
			@Override
			public CoordinateEditor createCoordinateEditor() {
				return new CoordinateEditor(I18N.CONSTANTS.westHemiChars(), I18N.CONSTANTS.eastHemiChars());
			}
		};

		private final String label;

		private Axis(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return label;
		}
		
		public abstract CoordinateEditor createCoordinateEditor();
    }

    /**
     * Because of the conversion between DMS and degrees decimal,
     * we may loose some precision. This becomes a problem when the
     * coordinate is clamped to the adminstrative bounds, and the
     * resulting value is *exactly* on the boundary. When rounded,
     * the coordinate can fall on the wrong side of the boundary,
     * resulting in a validation error.
     *
     * The delta value below should be sufficient to allow for such
     * imprecision.
     */
    public final static double DELTA = 0.00001;
	
	private final CoordinateEditor editor;

    /**
     * @param axis
     */
	public CoordinateField(Axis axis) {
		this.editor = axis.createCoordinateEditor();
		
		setName(axis.getLabel());
		setPropertyEditor(editor);
        setValidator(editor);
        setValidateOnBlur(true);
	}

    /**
     * Sets the bounds for this field 
     * @param name the name of the bounds to present to users in the event of violation,
     * (e.g. "Kapisa Province Boundary"
     * @param minValue minimum allowed value for this field
     * @param maxValue maximum allowed value for this field
     */
	public void setBounds(String name, double minValue, double maxValue) {
		editor.setMinValue(minValue - DELTA);
		editor.setMaxValue(maxValue + DELTA);
        editor.setOutOfBoundsMessage(I18N.MESSAGES.coordOutsideBounds(name));
	}
}
