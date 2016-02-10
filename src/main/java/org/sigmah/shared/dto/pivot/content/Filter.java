package org.sigmah.shared.dto.pivot.content;

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

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.sigmah.server.report.model.adapter.FilterAdapter;
import org.sigmah.shared.util.DateRange;

/**
 * <p>
 * Server-side version of the {@link org.sigmah.shared.util.Filter} object.
 * </p>
 * <p>
 * This version adds {@code javax.xml.bind.annotation}s to the class (not supported by client-side).
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@XmlJavaTypeAdapter(FilterAdapter.class)
public class Filter extends org.sigmah.shared.util.Filter {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -7973838332936879586L;

	@Override
	@XmlTransient
	public Date getMinDate() {
		return super.getMinDate();
	}

	@Override
	@XmlTransient
	public Date getMaxDate() {
		return super.getMaxDate();
	}

	@Override
	@XmlElement
	public DateRange getDateRange() {
		return super.getDateRange();
	}

}
