package org.sigmah.shared.dto.referential;

import org.sigmah.shared.command.result.Result;

/**
 * Report frequencies enumeration.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public enum ReportFrequency implements Result {

	/**
	 * The report is not bound to a time frame
	 */
	NotDateBound,

	/**
	 * The time frame of the report will be monthly.
	 */
	Monthly,

	/**
	 * The time frame of the report is weekly
	 */
	Weekly,

	/**
	 * The time frame of the report is daily
	 */
	Daily,

	/**
	 * The time frame of the report is to be defined by an arbitrary date range. (These types of reports cannot be
	 * "subscribed" to)
	 */
	Adhoc;

}
