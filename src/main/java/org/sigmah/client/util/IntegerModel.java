package org.sigmah.client.util;

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

import org.sigmah.client.i18n.I18N;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;

/**
 * Integer model data.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class IntegerModel extends BaseModelData {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1437428285046516706L;

	/**
	 * Enum {@link ModelData} value field key.
	 */
	public static final String VALUE_FIELD = "value";

	/**
	 * Enum {@link ModelData} display field key.
	 */
	public static final String DISPLAY_FIELD = "display";

	/**
	 * The inner integer value.<br>
	 * {@code null} means <em>unlimited</em>.
	 */
	private final Integer value;

	/**
	 * Initializes a new {@code IntegerModel}.
	 * 
	 * @param value
	 *          The integer value, {@code null} means <em>unlimited</em>.
	 */
	public IntegerModel(final Integer value) {
		this.value = value;
		set(VALUE_FIELD, value != null ? value.intValue() : "unlimited");
		set(DISPLAY_FIELD, value != null ? value.intValue() : I18N.CONSTANTS.adminLogFrameUnlimited());
	}

	public Integer getValue() {
		return value;
	}

	/**
	 * Utility method providing a null-safe access to the given {@code integer} inner integer value.
	 * 
	 * @param integerModel
	 *          The integer model instance, may be {@code null}.
	 * @return The given {@code integerModel} inner integer value, or {@code null}.
	 */
	public static Integer getValue(final IntegerModel integerModel) {
		return integerModel != null ? integerModel.getValue() : null;
	}

}
