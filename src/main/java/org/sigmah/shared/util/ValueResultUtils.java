package org.sigmah.shared.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.shared.dto.base.EntityDTO;

/**
 * Utility class used to manipulate the values of the flexible elements.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class ValueResultUtils {

	public static final String DEFAULT_VALUE_SEPARATOR = "~";

	public static final String BUDGET_VALUE_SEPARATOR = "%";

	/**
	 * Provides only static methods.
	 */
	private ValueResultUtils() {
		// Provides only static methods.
	}

	/**
	 * Split a list of values (manages entities with Integer type id).
	 * 
	 * @param values
	 *          The values list as a single string.
	 * @return The values.
	 */
	public static List<Integer> splitValuesAsInteger(Serializable values) {

		final ArrayList<Integer> list = new ArrayList<Integer>();

		try {
			if (values != null) {

				final String valuesAsString = (String) values;

				final String[] split = valuesAsString.trim().split(DEFAULT_VALUE_SEPARATOR);

				if (split != null && split.length != 0) {
					for (final String value : split) {
						if (value != null) {
							list.add(Integer.valueOf(value));
						}
					}
				}
			}
		} catch (ClassCastException e) {
			// digest exception.
		}

		return list;
	}

	/**
	 * Merges a list of values as a single string.<br/>
	 * <br/>
	 * The <code>null</code> values are ignored.
	 * 
	 * @param <T>
	 *          The type of the values.
	 * @param values
	 *          The values list.
	 * @return The values list as a single string.
	 */
	public static <T extends EntityDTO<?>> String mergeValues(List<T> values) {

		final StringBuilder sb = new StringBuilder();

		if (values != null && values.size() > 0) {
			for (int i = 0; i < values.size(); i++) {

				final T value = values.get(i);

				if (value != null) {
					sb.append(String.valueOf(value.getId()));
					if (i < values.size() - 1) {
						sb.append(DEFAULT_VALUE_SEPARATOR);
					}
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Merges a list of elements as a single string.<br/>
	 * <br/>
	 * The <code>null</code> values are ignored.
	 * 
	 * @param <T>
	 *          The type of the elements.
	 * @param values
	 *          The values list.
	 * @return The values list as a single string.
	 */
	public static <T extends Serializable> String mergeElements(List<T> values) {

		final StringBuilder sb = new StringBuilder();

		if (values != null && values.size() > 0) {
			for (int i = 0; i < values.size(); i++) {

				final T value = values.get(i);

				if (value != null) {
					sb.append(String.valueOf(value));
					if (i < values.size() - 1) {
						sb.append(DEFAULT_VALUE_SEPARATOR);
					}
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Merges a map into a single string <br/>
	 * The <code>null</code> values are ignored.
	 * 
	 * @param values
	 *          The values map
	 * @return The map as a single string
	 */
	public static <T extends EntityDTO<?>, E extends Serializable> String mergeElements(Map<T, E> values) {

		final StringBuilder sb = new StringBuilder();

		if (values != null && values.size() > 0) {
			int i = 0;
			for (T key : values.keySet()) {
				final E value = values.get(key);

				if (value != null) {
					sb.append(key.getId());
					sb.append(BUDGET_VALUE_SEPARATOR);
					sb.append(String.valueOf(value));
					if (i < values.size() - 1) {
						sb.append(DEFAULT_VALUE_SEPARATOR);
					}
				}
				i++;
			}
		}

		return sb.toString();
	}

	/**
	 * Merges a list of elements as a single string.<br/>
	 * <br/>
	 * The <code>null</code> values are ignored.
	 * 
	 * @param <T>
	 *          The type of the elements.
	 * @param values
	 *          The values list.
	 * @return The values list as a single string.
	 */
	@SafeVarargs
	public static <T extends Serializable> String mergeElements(T... values) {

		final StringBuilder sb = new StringBuilder();

		if (values != null && values.length > 0) {
			for (int i = 0; i < values.length; i++) {

				final T value = values[i];

				if (value != null) {
					sb.append(String.valueOf(value));
					if (i < values.length - 1) {
						sb.append(DEFAULT_VALUE_SEPARATOR);
					}
				}
			}
		}

		return sb.toString();
	}

	/**
	 * Split a list of values.
	 * 
	 * @param values
	 *          The values list as a single string.
	 * @return The values.
	 */
	public static List<String> splitElements(String values) {

		final ArrayList<String> list = new ArrayList<String>();

		try {
			if (values != null) {

				final String[] split = values.trim().split(DEFAULT_VALUE_SEPARATOR);

				if (split != null && split.length != 0) {
					for (final String value : split) {
						if (value != null) {
							list.add(String.valueOf(value));
						}
					}
				}
			}
		} catch (ClassCastException e) {
			// digest exception.
		}

		return list;
	}

	public static Map<Integer, String> splitMapElements(String values) {
		Map<Integer, String> returnedMap = new HashMap<Integer, String>();

		try {
			if (values != null) {

				final String[] split = values.trim().split(DEFAULT_VALUE_SEPARATOR);

				if (split != null && split.length != 0) {
					for (final String value : split) {
						final String[] splited = value.trim().split(BUDGET_VALUE_SEPARATOR);
						if (splited.length == 2 && splited[0] != null && splited[1] != null) {
							returnedMap.put(Integer.valueOf(splited[0]), String.valueOf(splited[1]));
						}
					}
				}
			}
		} catch (ClassCastException e) {
			// digest exception.
		}
		return returnedMap;
	}
	
	/**
	 * Removes the folder "C:\fakepath\" (used by Webkit browsers to hide the real path of the file).
	 * Also replaces characters that can't be used in Windows filenames by an underscore.
	 * 
	 * @param fileName
	 *            name to validate
	 * @return string the name validated
	 */
	public static String normalizeFileName(String fileName) {
		if(fileName != null) {
			return fileName.replaceFirst("[cC]:\\\\fakepath\\\\", "").replaceAll("[\\/:*?\"<>|]", "_");
		} else {
			return "";
		}
	}

}
