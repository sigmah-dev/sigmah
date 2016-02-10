package org.sigmah.client.ui.presenter.project.indicator;

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

import com.google.gwt.i18n.client.NumberFormat;
import org.sigmah.shared.dto.IndicatorDTO;

/**
 * Utility object that returns an appropriated number formats according to
 * the given indicator.
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public final class IndicatorNumberFormats {
	
	/**
	 * Standard number format for Stock indicators (Sums)
	 */
	public static final NumberFormat STOCK = NumberFormat.getFormat("#,###");

	/**
	 * Standard number format for Rate indicators (averages)
	 */
	public static final NumberFormat RATE = NumberFormat.getFormat("0.00");
	
	/**
	 * Returns an appropriate NumberFormat for the given indicator
	 * @param indicator
	 * @return 
	 */
	public static NumberFormat getNumberFormat(IndicatorDTO indicator) {
		if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
			return IndicatorNumberFormats.RATE;
		} else {
			return IndicatorNumberFormats.STOCK;
		}
	}
}
