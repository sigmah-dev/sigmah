package org.sigmah.server.domain.element;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Checkbox Element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.CHECKBOX_ELEMENT_TABLE)
public class CheckboxElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -9203240565522245252L;

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
}
