package org.sigmah.client.ui.presenter.project.indicator;

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
