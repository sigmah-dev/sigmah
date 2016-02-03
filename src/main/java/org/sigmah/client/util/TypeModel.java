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

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import org.sigmah.shared.dto.referential.LogicalElementType;

/**
 * {@link ModelData} implementation for <code>LogicalElementType</code>.
 * <p>
 * Based on <code>EnumModel</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class TypeModel extends BaseModelData {

    /**
     * Retrieves the concrete type of the given model.
     * <p>
     * If the given model is <code>null</code> the returned value will be <code>null</code>.
     * 
     * @param typeModel 
     *          A model.
     * @return the concrete type.
     */
    public static LogicalElementType getType(TypeModel typeModel) {
        if (typeModel != null) {
            return typeModel.getType();
        } else {
            return null;
        }
    }
    
    /**
     * Concrete type.
     */
    private final LogicalElementType type;
    
    /**
	 * Initializes a new {@code EnumModel} for the given {@code language}.
	 * 
	 * @param type
	 *          The type.
	 */
    public TypeModel(final LogicalElementType type) {
        this.type = type;
        setValuesOfType(type);
    }
    
    /**
     * Retrieves the concrete type.
     * 
     * @return the concrete type.
     */
    public LogicalElementType getType() {
        return type;
    }
    
    /**
     * Defines the value and display properties for the given type.
     * 
     * @param type 
     *          The type.
     */
    private void setValuesOfType(final LogicalElementType type) {
        if (type != null) {
            set(EnumModel.VALUE_FIELD, type.name());
            set(EnumModel.DISPLAY_FIELD, type.getDescription());
        }
    }

}
