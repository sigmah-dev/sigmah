package org.sigmah.shared.dto.referential;

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

import org.sigmah.shared.command.result.Result;

/**
 * Defines how Indicators referenced within a LogFrame are to be copied.
 * 
 * @author alexander
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum IndicatorCopyStrategy implements Result {
	/**
	 * Make a new copy of the indicator, but do not link to original
	 */
	DUPLICATE,

	/**
	 * Make a new copy of the indicator and link to the original. (The original is added to the {@code dataSources} set of
	 * the copy)
	 */
	DUPLICATE_AND_LINK,

	/**
	 * Copy the indicator by reference; only possible when the logframe is copied to the same project, to a different
	 * amendment, for example.
	 */
	REFERENCE;
}
