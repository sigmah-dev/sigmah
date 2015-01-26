package org.sigmah.client.page.entry;

import org.sigmah.shared.dto.IndicatorDTO;

import com.google.gwt.i18n.client.NumberFormat;

public class IndicatorNumberFormats {

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
	 * 
	 * @param indicator 
	 * @return a NumberFormat object
	 */
	public static NumberFormat forIndicator(IndicatorDTO indicator) {
		if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
			return IndicatorNumberFormats.RATE;
		} else {
			return IndicatorNumberFormats.STOCK;
		}
	}
}
