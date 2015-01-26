package org.sigmah.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Dictionary;

/**
 * <p>
 * Client configuration utility class.
 * </p>
 * <p>
 * This class provides an access to the client "{@code sigmah.extra.nocache.js}" configuration file parameters.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ClientConfiguration {

	private static final boolean DEBUG_ACTIVE_WHEN_SCRIPT;

	private static final int CHUNK_SIZE;
	private static final int CHUNK_SIZE_DEFAULT_VALUE;

	private static final int REPORT_AUTO_SAVE_DELAY;
	private static final int REPORT_AUTO_SAVE_DELAY_DEFAULT_VALUE;

	static {
		final Dictionary extraParameters = Dictionary.getDictionary("ExtraParameters");

		DEBUG_ACTIVE_WHEN_SCRIPT = ClientUtils.isTrue(extraParameters.get("debugActiveWhenScript"));

		CHUNK_SIZE_DEFAULT_VALUE = 2;
		CHUNK_SIZE = ClientUtils.asInt(extraParameters.get("chunkSize"), CHUNK_SIZE_DEFAULT_VALUE);

		REPORT_AUTO_SAVE_DELAY_DEFAULT_VALUE = 120000;
		REPORT_AUTO_SAVE_DELAY = ClientUtils.asInt(extraParameters.get("reportAutoSaveDelay"), REPORT_AUTO_SAVE_DELAY_DEFAULT_VALUE);
	}

	private ClientConfiguration() {
	}

	/**
	 * <p>
	 * Returns if debug is activated.
	 * </p>
	 * <p>
	 * <em>Note: Debug is automatically activated when running GWT hosted mode.</em>
	 * </p>
	 * 
	 * @return {@code true} if debug is activated, {@code false} otherwise.
	 */
	public static boolean isDebugActive() {
		return !GWT.isScript() || DEBUG_ACTIVE_WHEN_SCRIPT;
	}

	/**
	 * Returns the chunks size used in projects grid loading mechanism.
	 * 
	 * @return The chunks size used in projects grid loading mechanism.
	 */
	public static int getChunkSize() {
		return CHUNK_SIZE <= 0 ? CHUNK_SIZE_DEFAULT_VALUE : CHUNK_SIZE;
	}

	/**
	 * Returns the report auto-save delay (in milliseconds).
	 * 
	 * @return The report auto-save delay (in milliseconds).
	 */
	public static int getReportAutoSaveDelay() {
		return REPORT_AUTO_SAVE_DELAY <= 0 ? REPORT_AUTO_SAVE_DELAY_DEFAULT_VALUE : REPORT_AUTO_SAVE_DELAY;
	}

}
