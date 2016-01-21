package org.sigmah.server.domain.element;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
