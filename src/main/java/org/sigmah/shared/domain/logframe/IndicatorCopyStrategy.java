package org.sigmah.shared.domain.logframe;

/**
 * Defines how Indicators referenced within a LogFrame are to be copied.
 * 
 * 
 * @author alexander
 *
 */
public enum IndicatorCopyStrategy {
	/**
	 * Make a new copy of the indicator, but do not link to original
	 */
	DUPLICATE,
	
	/**
	 * Make a new copy of the indicator and link to the original.
	 * (The original is added to the {@code dataSources} set of the copy)
	 */
	DUPLICATE_AND_LINK,
	
	/**
	 * Copy the indicator by reference; only possible when the 
	 * logframe is copied to the same project, to a different amendment, for example. 
	 */
	REFERENCE
}