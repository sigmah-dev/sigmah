package org.sigmah.server.domain.util;

/**
 * Aggregation methods enumeration.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum AggregationMethod {

	Sum(0),
	Average(1),
	SiteCount(2),
	Multinomial(3);

	private final int code;

	private AggregationMethod(final int code) {
		this.code = code;
	}

	public final Integer code() {
		return code;
	}

}
