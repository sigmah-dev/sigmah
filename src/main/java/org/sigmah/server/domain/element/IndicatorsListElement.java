package org.sigmah.server.domain.element;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Indicators list element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.INDICATORS_LIST_ELEMENT_TABLE)
public class IndicatorsListElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -5664112184584673986L;

}
