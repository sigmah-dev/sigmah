package org.sigmah.shared.dto.referential;

/**
 * Nil implmentation of <code>LogicalElementType</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class NoElementType implements LogicalElementType {
    
    /**
     * Shared instance.
     */
    public static final NoElementType INSTANCE = new NoElementType();
    
    /**
     * Private constructor.
     */
    private NoElementType() {
        // No initialization.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ElementTypeEnum toElementTypeEnum() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TextAreaType toTextAreaType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DefaultFlexibleElementType toDefaultFlexibleElementType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String name() {
        return null;
    }
    
}
