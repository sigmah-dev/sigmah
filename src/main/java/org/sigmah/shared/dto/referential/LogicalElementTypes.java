package org.sigmah.shared.dto.referential;

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

import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;

/**
 * Utility class to retrieve instances of <code>LogicalElementType</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public final class LogicalElementTypes {
    
    /**
     * Private constructor.
     */
    private LogicalElementTypes() {
        // No initialization.
    }
    
    /**
	 * Returns the logical element type associated to the given {@code flexibleElement}.
     * <br>
	 * If the given {@code flexibleElement} is {@code null}, the method returns a non-null value of
	 * {@link NoElementType}.
	 * 
	 * @param flexibleElement
	 *          The flexible element.
	 * @return The given {@code flexibleElement} corresponding flexible element type, or an instance of
     * {@code NoElementType}.
	 */
	public static LogicalElementType of(final FlexibleElementDTO flexibleElement) {
        
        final LogicalElementType type;
        
        if (flexibleElement instanceof TextAreaElementDTO) {
            type = TextAreaType.fromCode(((TextAreaElementDTO) flexibleElement).getType());
        } else if (flexibleElement instanceof DefaultFlexibleElementDTO) {
            type = ((DefaultFlexibleElementDTO) flexibleElement).getType();
        } else if (flexibleElement != null) {
			final ElementTypeEnum elementType = flexibleElement.getElementType();
			if (elementType == ElementTypeEnum.TEXT_AREA) {
				// A case where type is null exists in production but is the result
				// of a bug. Until the cause is found and fixed, null is handled
				// the same as PARAGRAPH.
				// TODO: This special case should be removed to return only the element type.
				type = TextAreaType.PARAGRAPH;
			} else {
				type = elementType;
			}
        } else {
            type = null;
        }
        
        return notNull(type);
	}
	
	public static LogicalElementType fromName(final String name) {
		
		if (name == null) { 
			return NoElementType.INSTANCE;
		}
		
		try {
			return TextAreaType.valueOf(name);
		} catch (IllegalArgumentException e) {
			// Ignored.
		}
		
		try {
			return DefaultFlexibleElementType.valueOf(name);
		} catch (IllegalArgumentException e) {
			// Ignored.
		}
		
		try {
			return ElementTypeEnum.valueOf(name);
		} catch (IllegalArgumentException e) {
			// Ignored.
		}
		
		return NoElementType.INSTANCE;
	}
	
	/**
	 * Returns a non-null value from the given type.
	 * 
	 * @param type
	 *         Any logical element type (may be null).
	 * @return The given type if non-null, {@link NoElementType#INSTANCE} otherwise.
	 */
	public static LogicalElementType notNull(final LogicalElementType type) {
		if (type != null) {
			return type;
		} else {
			return NoElementType.INSTANCE;
		}
	}
	
}
