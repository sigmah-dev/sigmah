package org.sigmah.client.util;

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
