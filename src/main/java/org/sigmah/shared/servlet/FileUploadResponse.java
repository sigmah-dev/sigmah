package org.sigmah.shared.servlet;

import java.util.Date;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * File upload response handler.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class FileUploadResponse {

	/**
	 * HTTP response separator.
	 */
	private static final String TAG_SEPARATOR = "%~%~%~%";

	// --
	// File id response tags.
	// --

	/**
	 * HTTP response start tag for the response code.
	 */
	private static final String TAG_START_FILE_VERSION = "FV_START:";

	/**
	 * HTTP response end tag for the response code.
	 */
	private static final String TAG_END_FILE_VERSION = "?FV_END";

	// --
	// Monitored point response tags.
	// --

	/**
	 * HTTP response start tag for the monitored point.
	 */
	private static final String TAG_START_MONITORED_POINT = "MPST:";

	/**
	 * HTTP response end tag for the monitored point.
	 */
	private static final String TAG_END_MONITORED_POINT = "?MPEN";

	// --
	// Parsed values.
	// --

	private FileVersionDTO fileVersion;
	private MonitoredPointDTO monitoredPoint;

	/**
	 * Returns the parsed file version.
	 * 
	 * @return The parsed file version, or {@code null}.
	 */
	public FileVersionDTO getFileVersion() {
		return fileVersion;
	}

	/**
	 * Returns the parsed monitored point.
	 * 
	 * @return The parsed monitored point, or {@code null}.
	 */
	public MonitoredPointDTO getMonitoredPoint() {
		return monitoredPoint;
	}

	/**
	 * Serializes the given arguments in order to be sent into an {@code HTTP} response.
	 * 
	 * @param fileVersion
	 *          The file version, may be {@code null}.
	 * @param monitoredPoint
	 *          The monitored point instance, may be {@code null}.
	 * @return The given objects serialized in a {@code String} object.
	 */
	public static String serialize(final FileVersionDTO fileVersion, final MonitoredPointDTO monitoredPoint) {

		final StringBuilder builder = new StringBuilder();

		if (fileVersion != null) {
			builder.append(TAG_START_FILE_VERSION);
			builder.append(fileVersion.getId()).append(TAG_SEPARATOR);
			builder.append(fileVersion.getVersionNumber()).append(TAG_SEPARATOR);
			builder.append(fileVersion.getName()).append(TAG_SEPARATOR);
			builder.append(fileVersion.getExtension()).append(TAG_SEPARATOR);
			builder.append(fileVersion.getSize());
			builder.append(TAG_END_FILE_VERSION);
		}

		if (monitoredPoint != null) {
			builder.append(TAG_START_MONITORED_POINT);
			builder.append(monitoredPoint.getId()).append(TAG_SEPARATOR);
			builder.append(monitoredPoint.getLabel()).append(TAG_SEPARATOR);
			builder.append(monitoredPoint.getExpectedDate().getTime());
			builder.append(TAG_END_MONITORED_POINT);
		}

		return builder.toString();
	}

	/**
	 * Parses the given {@code HTTP} {@code response}.
	 * 
	 * @param response
	 *          The response content.
	 * @return The {@link FileUploadResponse} instance corresponding to the parsed {@code response}.
	 * @throws UnsupportedOperationException
	 *           If the {@code response} does not contain <em>valid</em> data.
	 */
	public static FileUploadResponse parse(final String response) {

		final FileUploadResponse parsing = new FileUploadResponse();

		parsing.fileVersion = parseFileVersion(response);
		parsing.monitoredPoint = parseMonitoredPoint(response);

		return parsing;
	}

	/**
	 * Parses the given HTTP {@code response} to extract a {@link FileVersionDTO} instance if any.
	 * 
	 * @param response
	 *          The HTTP response.
	 * @return The file version instance, or {@code null} if the {@code response} does not contain a file version.
	 * @throws UnsupportedOperationException
	 *           If the {@code response} does not contain <em>valid</em> {@link FileVersionDTO} instance.
	 */
	private static FileVersionDTO parseFileVersion(final String response) {

		if (ClientUtils.isBlank(response)) {
			return null;
		}

		// --
		// Controlling start tag presence.
		// --

		final int beginIndex = response.indexOf(TAG_START_FILE_VERSION);

		if (beginIndex == -1) {
			return null;
		}

		// --
		// Controlling expected end tag presence.
		// --

		final int endIndex = response.lastIndexOf(TAG_END_FILE_VERSION);

		if (endIndex == -1) {
			throw new UnsupportedOperationException("Expected end tag not found into HTTP response.");
		}

		// --
		// Parsing content.
		// --

		try {

			final String content = response.substring(beginIndex + TAG_START_FILE_VERSION.length(), endIndex);
			final String[] tokens = content.split(TAG_SEPARATOR);
			int index = 0;

			final FileVersionDTO fileVersion = new FileVersionDTO();
			fileVersion.setId(Integer.valueOf(tokens[index++]));
			fileVersion.setVersionNumber(Integer.valueOf(tokens[index++]));
			fileVersion.setName(tokens[index++]);
			fileVersion.setExtension(tokens[index++]);
			fileVersion.setSize(Long.valueOf(tokens[index++]));

			return fileVersion;

		} catch (final Exception e) {
			throw new UnsupportedOperationException("Error occures while parsing HTTP response to extract file version.", e);
		}
	}

	/**
	 * Parses the given HTTP {@code response} to extract a {@link MonitoredPointDTO} instance if any.
	 * 
	 * @param response
	 *          The HTTP response.
	 * @return The monitored point, or {@code null} if the {@code response} does not contain a {@link MonitoredPointDTO}
	 *         instance.
	 * @throws UnsupportedOperationException
	 *           If the {@code response} does not contain <em>valid</em> {@link MonitoredPointDTO} instance data.
	 */
	private static MonitoredPointDTO parseMonitoredPoint(final String response) {

		if (ClientUtils.isBlank(response)) {
			return null;
		}

		// --
		// Controlling start tag presence.
		// --

		final int beginIndex = response.indexOf(TAG_START_MONITORED_POINT);

		if (beginIndex == -1) {
			return null;
		}

		// --
		// Controlling expected end tag presence.
		// --

		final int endIndex = response.lastIndexOf(TAG_END_MONITORED_POINT);

		if (endIndex == -1) {
			throw new UnsupportedOperationException("Expected end tag not found into HTTP response.");
		}

		// --
		// Parsing content.
		// --

		try {

			final String content = response.substring(beginIndex + TAG_START_MONITORED_POINT.length(), endIndex);
			final String[] tokens = content.split(TAG_SEPARATOR);
			int index = 0;

			final MonitoredPointDTO point = new MonitoredPointDTO();
			point.setId(Integer.valueOf(tokens[index++]));
			point.setLabel(tokens[index++]);
			point.setExpectedDate(new Date(Long.valueOf(tokens[index++])));

			return point;

		} catch (final Exception e) {
			throw new UnsupportedOperationException("Error occures while parsing HTTP response to extract monitored point.", e);
		}
	}

	/**
	 * Utility class pattern.
	 */
	private FileUploadResponse() {
		// Only provides static methods.
	}

}
