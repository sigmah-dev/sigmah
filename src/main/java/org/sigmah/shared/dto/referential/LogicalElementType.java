package org.sigmah.shared.dto.referential;

/**
 * Regroups <code>ElementTypeEnum</code> and <code>TextAreaType</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface LogicalElementType {
    
    /**
     * Returns the <code>ElementTypeEnum</code> value matching this type.
     * 
     * @return the <code>ElementTypeEnum</code> value.
     */
    ElementTypeEnum toElementTypeEnum();
    
    /**
     * Returns the <code>TextAreaType</code> value matching this type or 
     * <code>null</code> if it is unknown.
     * 
     * @return the <code>TextAreaType</code> value or <code>null</code>.
     */
    TextAreaType toTextAreaType();
    
    /**
     * Returns the <code>DefaultFlexibleElementType</code> value matching this type or 
     * <code>null</code> if it is unknown.
     * 
     * @return the <code>DefaultFlexibleElementType</code> value or <code>null</code>.
     */
    DefaultFlexibleElementType toDefaultFlexibleElementType();
    
    /**
     * Returns a <code>String</code> representation of this type.
     * 
     * @return a <code>String</code> representation.
     */
    String getDescription();
    
    /**
     * Returns the technical name of this type.
     * 
     * @return the technical name of this type
     */
    String name();
    
}
