package org.sigmah.shared.dto.element;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.Computation;
import org.sigmah.shared.computation.Computations;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		
		final LabelField labelField = new LabelField();
		
		labelField.setFieldLabel(getLabel());
		
		preferredWidth = 120;
		
		// Sets the value of the field.
		if (valueResult != null && valueResult.isValueDefined()) {
			labelField.setValue(valueResult.getValueObject());
		}
		
		return labelField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		// TODO: Renvoyer vrai/faux en fonction des valeurs min / max.
		return result != null && result.getValueObject() != null;
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
	
}
