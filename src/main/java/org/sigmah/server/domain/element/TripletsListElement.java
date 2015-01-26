package org.sigmah.server.domain.element;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Triplets list element domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.TRIPLETS_LIST_ELEMENT_TABLE)
public class TripletsListElement extends FlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1816428096000083612L;

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

	// FIXME A décaler partie client
	// @Override
	// @Transient
	// public String asHistoryToken(ListEntity value) {
	//
	// if (!(value instanceof TripletValue)) {
	// return null;
	// }
	//
	// final TripletValue tValue = (TripletValue) value;
	// return ValueResultUtils.mergeElements(tValue.getCode(), tValue.getName(), tValue.getPeriod());
	// }
}
