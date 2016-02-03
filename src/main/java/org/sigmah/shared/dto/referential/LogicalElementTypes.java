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
        } else if(flexibleElement instanceof DefaultFlexibleElementDTO) {
            type = ((DefaultFlexibleElementDTO) flexibleElement).getType();
        } else if (flexibleElement != null) {
            type = flexibleElement.getElementType();
        } else {
            type = null;
        }
        
        if (type != null) {
            return type;
        } else {
            return NoElementType.INSTANCE;
        }
	}
    
}
