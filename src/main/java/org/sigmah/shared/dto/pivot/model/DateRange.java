package org.sigmah.shared.dto.pivot.model;

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

import java.util.Date;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * <p>
 * Server-side version of the {@link org.sigmah.shared.util.DateRange} object.
 * </p>
 * <p>
 * This version adds {@code javax.xml.bind.annotation}s to the class (not supported by client-side).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DateRange extends org.sigmah.shared.util.DateRange {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4567802237955870185L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = "min")
	public Date getMinDate() {
		return super.getMinDate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@XmlAttribute(name = "max")
	public Date getMaxDate() {
		return super.getMaxDate();
	}
}
