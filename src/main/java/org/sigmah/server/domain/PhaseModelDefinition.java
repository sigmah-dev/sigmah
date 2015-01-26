package org.sigmah.server.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Phase model definition domain entity.
 * </p>
 * <p>
 * Defines a global definition/concept for all phase models. This concept permits to aggregate phases which belongs to
 * the same definition.
 * <p>
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.PHASE_MODEL_DEFINITION_TABLE)
public class PhaseModelDefinition extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3321666998351127589L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.PHASE_MODEL_DEFINITION_COLUMN_ID)
	private Integer id;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	public PhaseModelDefinition() {
	}

	/**
	 * Reset the identifiers of the object.
	 */
	public void resetImport() {
		this.id = null;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

}
