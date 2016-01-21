package org.sigmah.server.domain.element;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Computation element domain entity.
 * </p>
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
@Entity
@Table(name = EntityConstants.COMPUTATION_ELEMENT_TABLE)
public class ComputationElement extends FlexibleElement {
	
	/**
	 * Computation rule used to evaluate the value of this element.
	 */
	@Column(name = EntityConstants.COMPUTATION_ELEMENT_COLUMN_RULE, nullable = true, length = EntityConstants.COMPUTATION_ELEMENT_RULE_MAX_LENGTH)
	private String rule;
	
	// TODO: Ajouter le type de retour.
	// TODO: Ajouter la valeur minimum (String).
	// TODO: Ajouter la valeur maximum (String).
	
	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	@Transient
	public boolean isHistorable() {
		return true;
	}
	
	// --------------------------------------------------------------------------------
	//
	// GETTERS AND SETTERS.
	//
	// --------------------------------------------------------------------------------

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}
	
}
