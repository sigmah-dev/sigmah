package org.sigmah.shared.dto.referential;

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
