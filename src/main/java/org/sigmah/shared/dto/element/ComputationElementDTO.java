package org.sigmah.shared.dto.element;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.form.StringField;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.element.event.ValueEvent;

/**
 * Computation element DTO.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class ComputationElementDTO extends FlexibleElementDTO {
	
	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.ComputationElement";
	public static final String RULE = "rule";
	public static final String MINIMUM_VALUE = "minimumValue";
	public static final String MAXIMUM_VALUE = "maximumValue";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		
		final StringField field = new StringField();
		field.setFieldLabel(getLabel());
        field.setValidator(new Validator() {
            
            @Override
            public String validate(Field<?> field, String value) {
                return validateValue(value);
            }
            
        });
		
		preferredWidth = 120;
		
		// Sets the value of the field.
		if (valueResult != null && valueResult.isValueDefined()) {
			field.setValue(valueResult.getValueObject());
		}
        
		return field;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		return ComputedValues.from(result).matchesConstraints(this) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}
	
	/**
	 * Method in charge of firing value events.
	 * 
	 * @param value
	 *          The raw value which is serialized to the server and saved to the data layer.
	 */
	public void fireValueEvent(final String value) {
		if (handlerManager == null) {
			init();
		}
		handlerManager.fireEvent(new ValueEvent(ComputationElementDTO.this, value));
	}
	
	/**
	 * Creates a <code>Computation</code> from the current rule.
	 * 
	 * @param model Model containing the flexible elements to use as variables.
	 * @return A new <code>Computation</code> object.
	 */
	public Computation getComputationForModel(IsModel model) {
		return Computations.parse(getRule(), model.getAllElements());
	}
	
	/**
	 * Returns <code>true</code> if this element has a minimum or maximum value
	 * constraint.
	 * 
	 * @return <code>true</code> if this element has a minimum or maximum value
	 * constraint, <code>false</code> otherwise.
	 */
	public boolean hasConstraints() {
		return getMinimumValueConstraint().get() != null || getMaximumValueConstraint().get() != null;
	}
	
	/**
	 * Returns the minimum value constraint.
	 * 
	 * @return minimum value constraint.
	 */
	public ComputedValue getMinimumValueConstraint() {
		return ComputedValues.from(getMinimumValue(), false);
	}
	
	/**
	 * Returns the maximum value constraint.
	 * 
	 * @return maximum value constraint.
	 */
	public ComputedValue getMaximumValueConstraint() {
		return ComputedValues.from(getMaximumValue(), false);
	}
    
    /**
     * Validate the given value.
     * 
     * @param labelField View of this element.
     * @param value Value to validate.
     */
    private String validateValue(final String value) {
        
        final ComputedValue computedValue = ComputedValues.from(value);
        switch (computedValue.matchesConstraints(this)) {
        case -1:
            return I18N.MESSAGES.flexibleElementComputationTooLow(value, getMinimumValue());
        case 1:
            return I18N.MESSAGES.flexibleElementComputationTooHigh(value, getMaximumValue());
        default:
            return null;
        }
    }
	
	// ---------------------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// ---------------------------------------------------------------------------------------------
	
	public String getRule() {
		return get(RULE);
	}
	
	public void setRule(String rule) {
		set(RULE, rule);
	}
	
	public String getMinimumValue() {
		return get(MINIMUM_VALUE);
	}

	public void setMinimumValue(String minimumValue) {
		set(MINIMUM_VALUE, minimumValue);
	}

	public String getMaximumValue() {
		return get(MAXIMUM_VALUE);
	}

	public void setMaximumValue(String maximumValue) {
		set(MAXIMUM_VALUE, maximumValue);
	}
	
}
