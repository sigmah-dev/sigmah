package org.sigmah.client.util;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.util.EntityConstants;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.http.client.URL;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Client-side utility methods.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class ClientUtils {

	/**
	 * Provides only static methods.
	 */
	private ClientUtils() {
		// Utility class.
	}

	/**
	 * Email client regular expression.
	 */
	public static final RegExp EMAIL_CLIENT_REGEXP = RegExp.compile(EntityConstants.EMAIL_REGULAR_EXPRESSION);

	// -------------------------------------------------------------------------------
	//
	// NAVIGATOR UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns the current user agent.
	 * 
	 * @return The current user agent.
	 */
	public static String getUserAgent() {
		return Window.Navigator.getUserAgent();
	}

	/**
	 * The regular expression to extract the IE version from the navigator's app version.
	 */
	private static final RegExp ieAppVersionExp = RegExp.compile("MSIE (.+?);");

	/**
	 * Returns if the current navigator is a version of Microsoft Internet Explorer.
	 * 
	 * @return If the current navigator is a version of Microsoft Internet Explorer.
	 */
	public static boolean isIE() {
		return isIE(null);
	}

	/**
	 * Returns if the current navigator is Microsoft Internet Explorer 6.
	 * 
	 * @return If the current navigator is Microsoft Internet Explorer 6.
	 */
	public static boolean isIE6() {
		return isIE("6.0");
	}

	/**
	 * Returns if the current navigator is Microsoft Internet Explorer 7.
	 * 
	 * @return If the current navigator is Microsoft Internet Explorer 7.
	 */
	public static boolean isIE7() {
		return isIE("7.0");
	}

	/**
	 * Returns if the current navigator is Microsoft Internet Explorer 8.
	 * 
	 * @return If the current navigator is Microsoft Internet Explorer 8.
	 */
	public static boolean isIE8() {
		return isIE("8.0");
	}

	/**
	 * Returns if the current navigator is Microsoft Internet Explorer 9.
	 * 
	 * @return If the current navigator is Microsoft Internet Explorer 9.
	 */
	public static boolean isIE9() {
		return isIE("9.0");
	}

	/**
	 * Returns if the current navigator is Microsoft Internet Explorer 10.
	 * 
	 * @return If the current navigator is Microsoft Internet Explorer 10.
	 */
	public static boolean isIE10() {
		return isIE("10.0");
	}

	/**
	 * Returns if the current navigator is Microsoft Internet Explorer 11.
	 * 
	 * @return If the current navigator is Microsoft Internet Explorer 11.
	 */
	public static boolean isIE11() {
		return isIE("11.0");
	}

	/**
	 * Returns if the current navigator is the given version of Microsoft Internet Explorer.
	 * 
	 * @param version
	 *          The expected version. If the version is <code>null</code>, then this method return is the navigator is one
	 *          of the versions of Microsoft Internet Explorer.
	 * @return If the current navigator is the given version of Microsoft Internet Explorer.
	 */
	public static boolean isIE(String version) {

		boolean isIE = "Microsoft Internet Explorer".equals(Window.Navigator.getAppName());

		if (isIE && version != null) {

			final MatchResult result = ieAppVersionExp.exec(Window.Navigator.getAppVersion());

			if (result != null) {
				isIE = version.equals(result.getGroup(1));
			} else {
				isIE = false;
			}

		}

		return isIE;
	}

	/**
	 * Returns if the current navigator is Mozilla <b>Firefox</b>.
	 * 
	 * @return {@code true} if the current navigator is Firefox.
	 */
	public static boolean isFF() {
		return Window.Navigator.getUserAgent().contains("Firefox");
	}

	/**
	 * Returns if the current navigator is Apple <b>Safari</b>.
	 * 
	 * @return {@code true} if the current navigator is Safari.
	 */
	public static boolean isSafari() {
		return Window.Navigator.getUserAgent().contains("Safari");
	}

	/**
	 * Returns if the current operating system is Apple Macintosh.
	 * 
	 * @return If the current operating system is Apple Macintosh.
	 */
	public static boolean isMac() {

		final String platform = Window.Navigator.getPlatform();
		return platform != null && platform.toUpperCase().contains("MAC");

	}

	/**
	 * Returns if the current operating system is Linux.
	 * 
	 * @return If the current operating system is Linux.
	 */
	public static boolean isLinux() {

		final String platform = Window.Navigator.getPlatform();
		return platform != null && platform.toUpperCase().contains("LINUX");

	}

	/**
	 * Returns if the current operating system is Microsoft Windows.
	 * 
	 * @return If the current operating system is Microsoft Windows.
	 */
	public static boolean isWindows() {

		final String platform = Window.Navigator.getPlatform();
		return platform != null && platform.toUpperCase().contains("WIN");

	}

	// -------------------------------------------------------------------------------
	//
	// STRING UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns if the given {@code value} is blank.
	 * 
	 * <pre>
	 * isBlank(null)    → true
	 * isBlank("")      → true
	 * isBlank(" ")     → true
	 * isBlank(" test") → false
	 * isBlank(" a ")   → false
	 * </pre>
	 * 
	 * @param value
	 *          The value to test.
	 * @return {@code true} if the given {@code value} is {@code null} or {@code empty}.
	 */
	public static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * Returns if the given {@code value} is not blank.
	 * 
	 * <pre>
	 * isNotBlank(null)    → false
	 * isNotBlank("")      → false
	 * isNotBlank(" ")     → false
	 * isNotBlank(" test") → true
	 * isNotBlank(" a ")   → true
	 * </pre>
	 * 
	 * @param value
	 *          The value to test.
	 * @return {@code true} if the given {@code value} is not {@code null} and not {@code empty}.
	 */
	public static boolean isNotBlank(String value) {
		return !isBlank(value);
	}

	/**
	 * Trims the given {@code value}.
	 * 
	 * @param value
	 *          The value to trim.
	 * @return the given {@code value} trimmed or {@code null}.
	 */
	public static String trim(String value) {
		if (value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * Trims the given {@code value}, returns {@code empty} if {@code null}.
	 * 
	 * @param value
	 *          The value to trim.
	 * @return the given {@code value} trimmed or {@code empty} if {@code null}.
	 */
	public static String trimToEmpty(String value) {
		if (isBlank(value)) {
			return "";
		}
		return value.trim();
	}

	/**
	 * Returns {@code true} if the given {@code value} contains the given {@code pattern}.
	 * 
	 * @param value
	 *          The value to test.
	 * @param pattern
	 *          The pattern to find.
	 * @return {@code true} if the given {@code value} contains the given {@code pattern}.
	 */
	public static boolean contains(String value, String pattern) {
		if (isBlank(value) || isBlank(pattern)) {
			return false;
		}

		return value.contains(pattern);
	}

	/**
	 * Returns {@code true} if the given {@code values} contains the given {@code pattern}.
	 * 
	 * @param values
	 *          The values to test.
	 * @param pattern
	 *          The pattern to find.
	 * @return {@code true} if the given {@code values} contains the given {@code pattern}.
	 */
	public static boolean contains(String[] values, String pattern) {
		if (isBlank(pattern) || isEmpty(values)) {
			return false;
		}
		for (final String value : values) {
			if (value != null && value.trim().equalsIgnoreCase(pattern.trim())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Compares the given two values, returning {@code true} if they are equal.
	 * </p>
	 * <p>
	 * {@code null}s are handled without exceptions. Two {@code null} references are considered to be equal. The
	 * comparison is case sensitive.
	 * </p>
	 * 
	 * <pre>
	 * equals(null, null)   = true
	 * equals(null, "abc")  = false
	 * equals("abc", null)  = false
	 * equals("abc", "abc") = true
	 * equals("abc", "ABC") = false
	 * </pre>
	 * 
	 * @param value1
	 *          The first string value, may be null.
	 * @param value2
	 *          The second string value, may be null.
	 * @return {@code true} if the given values are equal, case sensitive, or both {@code null}.
	 */
	public static boolean equals(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		return value1 != null && value1.equals(value2);
	}

	/**
	 * <p>
	 * Compares the given two values, returning {@code true} if they are equal.
	 * </p>
	 * <p>
	 * {@code null}s are handled without exceptions. Two {@code null} references are considered to be equal. The
	 * comparison is case <b>insensitive</b>.
	 * </p>
	 * 
	 * <pre>
	 * equals(null, null)   = true
	 * equals(null, "abc")  = false
	 * equals("abc", null)  = false
	 * equals("abc", "abc") = true
	 * equals("abc", "ABC") = true
	 * </pre>
	 * 
	 * @param value1
	 *          The first string value, may be null.
	 * @param value2
	 *          The second string value, may be null.
	 * @return {@code true} if the given values are equal, case <b>insensitive</b>, or both {@code null}.
	 */
	public static boolean equalsIgnoreCase(String value1, String value2) {
		if (value1 == null && value2 == null) {
			return true;
		}

		return value1 != null && value1.equalsIgnoreCase(value2);
	}

	/**
	 * <p>
	 * Converts the given value to upper case.
	 * </p>
	 * 
	 * <pre>
	 * toUpperCase(null)    = null
	 * toUpperCase("")      = ""
	 * toUpperCase("aBc")   = "ABC"
	 * toUpperCase("ABC")   = "ABC"
	 * toUpperCase(" abc ") = "ABC"
	 * </pre>
	 * 
	 * @param value
	 *          The string value, may be null.
	 * @return the given {@code value} converted to upper case or {@code null}.
	 */
	public static String toUpperCase(String value) {
		if (value == null) {
			return null;
		}

		return value.trim().toUpperCase();
	}

	/**
	 * <p>
	 * Converts the given value to lower case.
	 * </p>
	 * 
	 * <pre>
	 * toLowerCase(null)    = null
	 * toLowerCase("")      = ""
	 * toLowerCase("aBc")   = "abc"
	 * toLowerCase("abc")   = "abc"
	 * toLowerCase(" ABC ") = "abc"
	 * </pre>
	 * 
	 * @param value
	 *          The string value, may be null.
	 * @return the given {@code value} converted to lower case or {@code null}.
	 */
	public static String toLowerCase(String value) {
		if (value == null) {
			return null;
		}

		return value.trim().toLowerCase();
	}

	/**
	 * Puts the first text's character to its upper case value.
	 * The rest of the {@code text} is not modified.
	 * 
	 * @param text
	 *          The initial text.
	 * @return The first character uppered text.
	 */
	public static final String upperFirst(String text) {
		if (isBlank(text)) {
			return text;
		}
		text = text.trim();
		return text.substring(0, 1).toUpperCase() + text.substring(1);
	}

	/**
	 * <p>
	 * Converts the given {@code name} with proper name format.
	 * </p>
	 * 
	 * <pre>
	 * formatName(null)          = null
	 * formatName("")            = ""
	 * formatName("jean-luc")    = "Jean-Luc"
	 * formatName("jean claude") = "Jean Claude"
	 * formatName("bernard")     = "Bernard"
	 * formatName("BERNARD")     = "Bernard"
	 * formatName(" denis ")     = "Denis"
	 * </pre>
	 * 
	 * @param name
	 *          The name value, may be {@code null}.
	 * @return the given {@code name} formatted or {@code null}.
	 */
	public static String formatName(String name) {
		if (name == null) {
			return null;
		}

		// Space must be the last one !
		final Character[] splitters = {
																		'-',
																		' '
		};

		name = name.toLowerCase().trim();
		for (final char splitter : splitters) {
			name = formatWords(name.split("[" + splitter + "]"), splitter);
		}

		return name;
	}

	/**
	 * Formats the given {@code words} and creates a new {@code String} which parts are separated by given
	 * {@code splitChar}.
	 * 
	 * @param words
	 *          The words.
	 * @param splitChar
	 *          The split character used to generate {@code words} array.
	 * @return the generated string.
	 */
	private static String formatWords(final String[] words, char splitChar) {
		if (isEmpty(words)) {
			return "";
		}

		final StringBuilder builder = new StringBuilder();

		for (String word : words) {

			if (isBlank(word)) {
				continue;
			}

			word = trimToEmpty(word);

			if ("de".equals(word)) {
				// Nothing to do here.

			} else if (word.startsWith("d'")) {
				word = word.substring(0, 2) + String.valueOf(word.charAt(2)).toUpperCase() + word.substring(3);

			} else {
				word = String.valueOf(word.charAt(0)).toUpperCase() + word.substring(1);
			}

			builder.append(word).append(splitChar);
		}

		if (builder.length() > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}

		return builder.toString();
	}

	/**
	 * Replaces the special characters into the given {@code text} by a corresponding value.
	 * <ul>
	 * <li>{@code spaces : $ % ' " \ / * + = - ~ , ; ! ?} are replaced by {@code _}</li>
	 * <li>{@code éèêë€} are replaced by {@code e}</li>
	 * <li>{@code ÈÉÊË} are replaced by {@code E}</li>
	 * <li>{@code àâä@} are replaced by {@code a}</li>
	 * <li>{@code ÀÁÂÄÅ} are replaced by {@code A}</li>
	 * <li>{@code òóôõö} are replaced by {@code o}</li>
	 * <li>{@code ÒÓÔÕÖ} are replaced by {@code O}</li>
	 * <li>{@code îï} are replaced by {@code i}</li>
	 * <li>{@code úùûü} are replaced by {@code u}</li>
	 * <li>{@code ÙÚÛÜ} are replaced by {@code U}</li>
	 * <li>{@code ç} are replaced by {@code c}</li>
	 * <li>{@code Ç} are replaced by {@code C}</li>
	 * </ul>
	 * 
	 * @param text
	 *          The text.
	 * @return the given {@code text} with special characters replaced.
	 */
	public static final String removeSpecialChars(String text) {
		if (isBlank(text)) {
			return text;
		}
		text = text.replaceAll("[\\s\\:\\$%\'\"\\\\/\\*\\+=\\-|~\\,\\;\\!\\?]", "_");
		text = text.replaceAll("[éèêë€]", "e");
		text = text.replaceAll("[ÈÉÊË]", "E");
		text = text.replaceAll("[àâä@]", "a");
		text = text.replaceAll("[ÀÁÂÄÅ]", "A");
		text = text.replaceAll("[òóôõö]", "o");
		text = text.replaceAll("[ÒÓÔÕÖ]", "O");
		text = text.replaceAll("[îï]", "i");
		text = text.replaceAll("[úùûü]", "u");
		text = text.replaceAll("[ÙÚÛÜ]", "U");
		text = text.replaceAll("[ç]", "c");
		text = text.replaceAll("[Ç]", "C");
		return text;
	}

	/**
	 * Gets a CharSequence length or {@code 0} if the CharSequence is {@code null}.
	 * 
	 * @param cs
	 *          a CharSequence or {@code null}
	 * @return CharSequence length or {@code 0} if the CharSequence is {@code null}.
	 */
	public static int length(CharSequence cs) {
		return cs == null ? 0 : cs.length();
	}

	/**
	 * <p>
	 * Gets a substring from the specified String avoiding exceptions.
	 * </p>
	 * <p>
	 * A negative start position can be used to start/end {@code n} characters from the end of the String.
	 * </p>
	 * <p>
	 * The returned substring starts with the character in the {@code start} position and ends before the {@code end}
	 * position. All position counting is zero-based -- i.e., to start at the beginning of the string use
	 * {@code start = 0}. Negative start and end positions can be used to specify offsets relative to the end of the
	 * String.
	 * </p>
	 * <p>
	 * If {@code start} is not strictly to the left of {@code end}, "" is returned.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.substring(null, *, *)    = null
	 * StringUtils.substring("", * ,  *)    = "";
	 * StringUtils.substring("abc", 0, 2)   = "ab"
	 * StringUtils.substring("abc", 2, 0)   = ""
	 * StringUtils.substring("abc", 2, 4)   = "c"
	 * StringUtils.substring("abc", 4, 6)   = ""
	 * StringUtils.substring("abc", 2, 2)   = ""
	 * StringUtils.substring("abc", -2, -1) = "b"
	 * StringUtils.substring("abc", -4, 2)  = "ab"
	 * </pre>
	 * 
	 * @param str
	 *          the String to get the substring from, may be null
	 * @param start
	 *          the position to start from, negative means count back from the end of the String by this many characters
	 * @param end
	 *          the position to end at (exclusive), negative means count back from the end of the String by this many
	 *          characters
	 * @return substring from start position to end position, {@code null} if null String input
	 */
	public static String substring(final String str, int start, int end) {
		if (str == null) {
			return null;
		}

		// handle negatives
		if (end < 0) {
			end = str.length() + end; // remember end is negative
		}
		if (start < 0) {
			start = str.length() + start; // remember start is negative
		}

		// check length next
		if (end > str.length()) {
			end = str.length();
		}

		// if start is greater than end, return ""
		if (start > end) {
			return "";
		}

		if (start < 0) {
			start = 0;
		}
		if (end < 0) {
			end = 0;
		}

		return str.substring(start, end);
	}

	/**
	 * <p>
	 * Capitalizes a String changing the first letter to title case case as per {@link Character#toUpperCase(char)}. No
	 * other letters are changed.
	 * </p>
	 * <p>
	 * A {@code null} input String returns {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.capitalize(null)  = null
	 * StringUtils.capitalize("")    = ""
	 * StringUtils.capitalize("cat") = "Cat"
	 * StringUtils.capitalize("cAt") = "CAt"
	 * </pre>
	 * 
	 * @param str
	 *          the String to capitalize, may be null
	 * @return the capitalized String, {@code null} if null String input
	 * @see #uncapitalize(String)
	 */
	public static String capitalize(final String str) {

		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}

		final char firstChar = str.charAt(0);

		if (Character.isUpperCase(firstChar)) {
			// already capitalized
			return str;
		}

		return new StringBuilder(strLen).append(Character.toUpperCase(firstChar)).append(str.substring(1)).toString();

	}

	/**
	 * <p>
	 * Uncapitalizes a String changing the first letter to title case as per {@link Character#toLowerCase(char)}. No other
	 * letters are changed.
	 * </p>
	 * <p>
	 * A {@code null} input String returns {@code null}.
	 * </p>
	 * 
	 * <pre>
	 * StringUtils.uncapitalize(null)  = null
	 * StringUtils.uncapitalize("")    = ""
	 * StringUtils.uncapitalize("Cat") = "cat"
	 * StringUtils.uncapitalize("CAT") = "cAT"
	 * </pre>
	 * 
	 * @param str
	 *          the String to uncapitalize, may be null
	 * @return the uncapitalized String, {@code null} if null String input
	 * @see #capitalize(String)
	 */
	public static String uncapitalize(final String str) {

		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return str;
		}

		char firstChar = str.charAt(0);
		if (Character.isLowerCase(firstChar)) {
			// already uncapitalized
			return str;
		}

		return new StringBuilder(strLen).append(Character.toLowerCase(firstChar)).append(str.substring(1)).toString();

	}

	/**
	 * Abbreviates the given {@code value} to {@code max} characters (if necessary).<br>
	 * If the {@code value} is abbreviated, the result ends with "{@code ...}".
	 * 
	 * <pre>
	 * abbreviate("my value", 150) → "my value"
	 * abbreviate("my value", 5) → "my va..."
	 * abbreviate("my value", 0) → "..."
	 * </pre>
	 * 
	 * @param value
	 *          The value.
	 * @param max
	 *          The max value.
	 * @return The value abbreviated (if necessary).
	 */
	public static String abbreviate(final String value, int max) {
		if (value == null) {
			return null;
		}
		if (max < 0) {
			max = 0;
		}
		return value.length() > max ? value.substring(0, max) + "..." : value;
	}

	/**
	 * Adds a colon to the given label.
	 * 
	 * @param label
	 *          The label.
	 * @return The label with a proper I18N colon.
	 */
	public static String colon(String label) {
		return label != null ? label + I18N.CONSTANTS.form_label_separator() : null;
	}

	/**
	 * Removes the last {@code suffix} of the given {@code value}.
	 * 
	 * <pre>
	 * removeLastSuffix("a blue dog", null) → "a blue dog"
	 * removeLastSuffix("a blue dog", "") → "a blue dog"
	 * removeLastSuffix(null, *) → null
	 * removeLastSuffix("", *) → ""
	 * removeLastSuffix("a blue dog", "fog") → "a blue dog"
	 * removeLastSuffix("a blue dog", "og") → "a blue d"
	 * removeLastSuffix("a blue dog", "dog") → "a blue "
	 * removeLastSuffix("a blue dog", "a blue dog") → ""
	 * </pre>
	 * 
	 * @param value
	 *          The value.
	 * @param suffix
	 *          The suffix.
	 * @return The given {@code value} without the last {@code suffix}.
	 */
	public static String removeLastSuffix(final String value, final String suffix) {
		if (value == null) {
			return null;
		}

		if (suffix == null || value.length() == 0 || !value.endsWith(suffix)) {
			return value;
		}

		return value.substring(0, value.length() - suffix.length());
	}

	// -------------------------------------------------------------------------------
	//
	// BOOLEAN UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns {@code true} if the given string is a valid <strong>true</strong> boolean value.
	 * 
	 * <pre>
	 * isTrue(null)   → false
	 * isTrue("true") → true
	 * isTrue("TRUE") → true
	 * isTrue("on")   → true
	 * isTrue("ON")   → true
	 * isTrue("1")    → true
	 * isTrue(" 1 ")  → true
	 * </pre>
	 * 
	 * @param value
	 *          The string value.
	 * @return {@code true} if the given string is a valid <strong>true</strong> boolean value.
	 */
	public static boolean isTrue(String value) {
		return value != null && ("TRUE".equalsIgnoreCase(value.trim()) || "ON".equalsIgnoreCase(value.trim()) || "1".equals(value.trim()));
	}

	/**
	 * Returns {@code true} if the given {@link Boolean} value is a {@code true} boolean value.
	 * 
	 * <pre>
	 * isTrue(null)  → false
	 * isTrue(false) → false
	 * isTrue(true)  → true
	 * </pre>
	 * 
	 * @param value
	 *          The {@link Boolean} value.
	 * @return {@code true} if the given value is a {@code true} boolean value.
	 */
	public static boolean isTrue(Boolean value) {
		return value != null && value.booleanValue();
	}

	/**
	 * Returns {@code false} if the given {@link Boolean} value is a {@code true} boolean value.
	 * 
	 * <pre>
	 * isNotTrue(null)  → true
	 * isNotTrue(false) → true
	 * isNotTrue(true)  → false
	 * </pre>
	 * 
	 * @param value
	 *          The {@link Boolean} value.
	 * @return {@code false} if the given value is a {@code true} boolean value.
	 */
	public static boolean isNotTrue(Boolean value) {
		return !isTrue(value);
	}

	/**
	 * Returns {@code true} if the given {@link Object} value is a {@code true} boolean value.
	 * 
	 * <pre>
	 * isTrue(null)                → false
	 * isTrue(true)                → true
	 * isTrue(false)               → true
	 * isTrue(new String("true"))  → true
	 * isTrue(new String("TRUE"))  → true
	 * isTrue(new String("false")) → false
	 * isTrue(new String("1"))     → true
	 * isTrue(new String(" 1 "))   → true
	 * isTrue(new String("ON"))    → true
	 * isTrue(new String("on"))    → true
	 * isTrue(new String(""))      → false
	 * isTrue(new String("abc"))   → false
	 * isTrue(new Boolean(true))   → true
	 * isTrue(new Boolean(false))  → false
	 * isTrue(new Integer(0))      → false
	 * isTrue(new Integer(1))      → true
	 * </pre>
	 * 
	 * @param value
	 *          The {@link Object} value.
	 * @return {@code true} if the given value is a {@code true} boolean value.
	 * @see #isTrue(String)
	 */
	public static boolean isTrue(Object value) {
		return isTrue(String.valueOf(value));
	}

	// -------------------------------------------------------------------------------
	//
	// NUMBER UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns the integer corresponding to the given string {@code value}.
	 * 
	 * @param value
	 *          The {@link String} value.
	 * @return the integer corresponding to the given string {@code value} or {@code null} if the {@code value} does not
	 *         contain a parsable integer.
	 */
	public static Integer asInt(String value) {
		return asInt(value, null);
	}

	/**
	 * Returns the integer corresponding to the given string {@code value}.
	 * 
	 * @param value
	 *          The {@link String} value.
	 * @param defaultValue
	 *          The default value returned by the method if the given {@code value} is not a valid integer.
	 * @return the integer corresponding to the given string {@code value} or {@code defaultValue} if the {@code value}
	 *         does not contain a parsable integer.
	 */
	public static Integer asInt(String value, Integer defaultValue) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns the {@code long} corresponding to the given string {@code value}.
	 * 
	 * @param value
	 *          The {@link String} value.
	 * @return the integer corresponding to the given string {@code value} or {@code null} if the {@code value} does not
	 *         contain a parsable {@code long}.
	 */
	public static Long asLong(String value) {
		return asLong(value, null);
	}

	/**
	 * Returns the {@code long} corresponding to the given string {@code value}.
	 * 
	 * @param value
	 *          The {@link String} value.
	 * @param defaultValue
	 *          The default value returned by the method if the given {@code value} is not a valid {@code long}.
	 * @return the integer corresponding to the given string {@code value} or {@code defaultValue} if the {@code value}
	 *         does not contain a parsable {@code long}.
	 */
	public static Long asLong(String value, Long defaultValue) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	/**
	 * Returns the given {@code number} corresponding {@code Integer} value.<br>
	 * If the {@code number} is {@code null}, the method returns {@code null}.
	 * 
	 * @param number
	 *          The number, may be {@code null}.
	 * @return The given {@code number} corresponding {@code Integer} value, or {@code null}.
	 */
	public static Integer getInteger(final Number number) {
		return number != null ? number.intValue() : null;
	}

	/**
	 * Returns the given {@code number} corresponding {@code Long} value.<br>
	 * If the {@code number} is {@code null}, the method returns {@code null}.
	 * 
	 * @param number
	 *          The number, may be {@code null}.
	 * @return The given {@code number} corresponding {@code Long} value, or {@code null}.
	 */
	public static Long getLong(final Number number) {
		return number != null ? number.longValue() : null;
	}

	// -------------------------------------------------------------------------------
	//
	// DATE UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns the given {@code date} corresponding timestamp (in ms).<br>
	 * If the {@code date} is {@code null}, the method returns {@code null}.
	 * 
	 * @param date
	 *          The date, may be {@code null}.
	 * @return The given {@code date} corresponding timestamp (in ms), or {@code null}.
	 */
	public static Long getTimestamp(final Date date) {
		return date != null ? date.getTime() : null;
	}

	// -------------------------------------------------------------------------------
	//
	// COLLECTION UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns if the given {@code collection} is {@code null} or empty.
	 * 
	 * @param collection
	 *          The collection to test.
	 * @return {@code true} if the given {@code collection} is {@code null} or {@code empty}.
	 */
	public static <C extends Collection<?>> boolean isEmpty(C collection) {
		return collection == null || collection.isEmpty();
	}

	/**
	 * Returns if the given {@code collection} is not {@code null} and not empty.
	 * 
	 * @param collection
	 *          The collection to test.
	 * @return {@code true} if the given {@code collection} is not {@code null} and not {@code empty}.
	 */
	public static <C extends Collection<?>> boolean isNotEmpty(C collection) {
		return !isEmpty(collection);
	}

	/**
	 * Returns if the given {@code array} is {@code null} or empty.
	 * 
	 * @param array
	 *          The array to test.
	 * @return {@code true} if the given {@code array} is {@code null} or {@code empty}.
	 */
	public static <T extends Object> boolean isEmpty(T[] array) {
		return array == null || array.length == 0;
	}

	/**
	 * Returns if the given {@code array} is {@code null} or empty.
	 * 
	 * @param array
	 *          The array to test.
	 * @return {@code true} if the given {@code array} is {@code null} or {@code empty}.
	 */
	public static <T extends Object> boolean isNotEmpty(T[] array) {
		return !isEmpty(array);
	}

	/**
	 * Returns if the given {@code map} is {@code null} or empty.
	 * 
	 * @param map
	 *          The map to test.
	 * @return {@code true} if the given {@code map} is {@code null} or {@code empty}.
	 */
	public static <M extends Map<?, ?>> boolean isEmpty(M map) {
		return map == null || map.isEmpty();
	}

	/**
	 * Returns if the given {@code map} is not {@code null} and not empty.
	 * 
	 * @param map
	 *          The map to test.
	 * @return {@code true} if the given {@code map} is not {@code null} and not {@code empty}.
	 */
	public static <M extends Map<?, ?>> boolean isNotEmpty(M map) {
		return !isEmpty(map);
	}

	/**
	 * Returns the collection corresponding to the given {@code array}.
	 * 
	 * @param array
	 *          The array to convert.
	 * @return the collection corresponding to the given {@code array} or {@code null} if the {@code array} is
	 *         {@code null}.
	 */
	public static <T> List<T> toList(T[] array) {
		if (array == null) {
			return null;
		}
		return Arrays.asList(array);
	}

	/**
	 * Returns the collection corresponding to the given {@code value}.
	 * 
	 * @param value
	 *          The value to convert.
	 * @return the collection corresponding to the given nullable {@code value}.
	 */
	public static <T> List<T> toList(T value) {
		return Arrays.asList(toArray(value));
	}

	/**
	 * Utility method returning the intersection between the given sets (i.e. elements present among both sets).
	 * 
	 * @param firstSet
	 *          The first set.
	 * @param secondSet
	 *          The second set.
	 * @return The intersection between the given sets (i.e. elements present among both sets).
	 *         Never returns {@code null} ; if no intersection, returns empty set.
	 */
	public static <T> Set<T> intersect(final Set<T> firstSet, final Set<T> secondSet) {

		if (isEmpty(firstSet)) {
			return secondSet == null ? new HashSet<T>(0) : secondSet;
		}
		if (isEmpty(secondSet)) {
			return firstSet;
		}

		firstSet.retainAll(secondSet);

		return firstSet;
	}

	/**
	 * Concatenates all the given {@code collections} together (order is conserved).
	 * 
	 * <pre>
	 * concat({4,5}, {1,6}, {7,9}) → {4,5,1,6,7,9}
	 * concat({4,5}, null, {7,9}) → {4,5,7,9}
	 * </pre>
	 * 
	 * @param collections
	 *          The collection(s).
	 * @return The given {@code collections} concatenated together.
	 */
	@SafeVarargs
	public static <T> List<T> concat(final Collection<T>... collections) {

		final List<T> concatenation = new ArrayList<T>();

		for (final Collection<T> collection : collections) {
			if (collection != null) {
				concatenation.addAll(collection);
			}
		}

		return concatenation;
	}

	// -------------------------------------------------------------------------------
	//
	// WIDGET UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Sets the given {@code isWidget} display style property (either {@link Display#BLOCK} or {@link Display#NONE}).
	 * 
	 * @param isWidget
	 *          The widget.
	 * @param visible
	 *          {@code true} to set the {@code isWidget} visible by applying the given {@code display} to its style,
	 *          {@code false} to hide it.
	 */
	public static <W extends IsWidget> void setDisplay(W isWidget, boolean visible) {
		setDisplay(isWidget, visible ? Display.BLOCK : Display.NONE);
	}

	/**
	 * Sets the given {@code isWidget} with the given {@code display} style property.
	 * 
	 * @param isWidget
	 *          The widget.
	 * @param display
	 *          The display type.
	 * @param visible
	 *          {@code true} to set the {@code isWidget} visible by applying the given {@code display} to its style,
	 *          {@code false} to hide it with {@code Display.NONE}.
	 */
	public static <W extends IsWidget> void setDisplay(W isWidget, final Display display, boolean visible) {
		setDisplay(isWidget, visible ? display : Display.NONE);
	}

	/**
	 * Sets the display style property to all given {@link IsWidget}.
	 * 
	 * @param display
	 *          The display style property.
	 * @param isWidgets
	 *          The Widgets.
	 */
	public static void setDisplay(final Display display, IsWidget... isWidgets) {
		if (isEmpty(isWidgets)) {
			return;
		}

		for (final IsWidget widget : isWidgets) {
			setDisplay(widget, display);
		}
	}

	/**
	 * Sets the given {@code isWidget} display style property.
	 * 
	 * @param isWidget
	 *          The widget.
	 * @param display
	 *          The display style property. Is the given value is {@code null}, the {@code display} style property is
	 *          cleared.
	 */
	public static <W extends IsWidget> void setDisplay(W isWidget, Display display) {
		if (isWidget == null) {
			return;
		}

		final Widget widget = isWidget.asWidget();
		if (widget == null) {
			return;
		}

		if (display == null) {
			widget.getElement().getStyle().clearDisplay();
		} else {
			widget.getElement().getStyle().setDisplay(display);
		}
	}

	/**
	 * Adds the given {@code addedStyle} to the given {@code isWidget} and removes the given {@code removedStyles} from
	 * the given {@code isWidget}.
	 * 
	 * @param isWidget
	 *          The widget.
	 * @param addedStyle
	 *          The added style name (if not {@code null} or empty).
	 * @param removedStyles
	 *          The removed style names (if not {@code null} or empty).
	 */
	public static <W extends IsWidget> void addAndRemoveStyles(W isWidget, String addedStyle, String... removedStyles) {
		if (isWidget == null) {
			return;
		}

		final Widget widget = isWidget.asWidget();
		if (widget == null) {
			return;
		}

		if (isNotEmpty(removedStyles)) {
			// Removing style names.
			for (final String removedStyle : removedStyles) {
				if (isBlank(removedStyle)) {
					continue;
				}
				widget.removeStyleName(removedStyle);
			}
		}

		if (isNotBlank(addedStyle)) {
			widget.addStyleName(addedStyle);
		}
	}

	/**
	 * Safe access to the given {@code SimpleComboBox} <em>simple</em> value.<br>
	 * Indeed, GXT may throw a {@code ClassCastException} if the value of a {@code SimpleComboBox} is {@code null} and not
	 * of type {@code String}.
	 * 
	 * @param field
	 *          The {@code SimpleComboBox} field.
	 * @return The simple value or {@code null}.
	 */
	public static <T> T getSimpleValue(final SimpleComboBox<T> field) {
		return field != null && field.getValue() != null ? field.getValue().getValue() : null;
	}

	// -------------------------------------------------------------------------------
	//
	// URL UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------

	/**
	 * Returns the current application URL (with current optional parameters).
	 * 
	 * @return the current application URL (with current optional parameters).
	 */
	public static String getApplicationUrl() {
		return getApplicationUrl(true, null);
	}

	/**
	 * Returns the current application URL.
	 * 
	 * @param withParameters
	 *          {@code true} to retrieve existing current URL parameters, {@code false} to forget them.
	 * @return the current application URL (with current optional parameters).
	 */
	public static String getApplicationUrl(boolean withParameters) {
		return getApplicationUrl(withParameters, null);
	}

	/**
	 * Returns the current application URL (with current optional parameters) with the given additional parameter
	 * {@code paramName}.
	 * 
	 * @param withParameters
	 *          {@code true} to retrieve existing current URL parameters, {@code false} to forget them.
	 * @param paramName
	 *          The parameter name.
	 * @param paramValues
	 *          The parameter value(s).
	 * @return the current application URL (with current optional parameters) with the given additional parameter
	 *         {@code paramName}.
	 */
	public static String getApplicationUrl(boolean withParameters, String paramName, String... paramValues) {

		final UrlBuilder urlBuilder = new UrlBuilder();

		urlBuilder.setProtocol(Location.getProtocol());
		urlBuilder.setHost(Location.getHost());
		final Integer port = asInt(Location.getPort());
		if (port != null) {
			urlBuilder.setPort(port);
		}
		urlBuilder.setPath(Location.getPath());

		if (Location.getParameterMap() != null) {
			// "?gwt.codesvr=127.0.0.1:9997" for example.
			for (final Entry<String, List<String>> param : Location.getParameterMap().entrySet()) {

				if ("gwt.codesvr".equalsIgnoreCase(param.getKey()) && isNotEmpty(param.getValue())) {
					// Hosted mode parameter exception.
					urlBuilder.setParameter(param.getKey(), param.getValue().get(0));

				} else if (withParameters) {
					final String[] values = param.getValue() != null ? param.getValue().toArray(new String[param.getValue().size()]) : null;
					urlBuilder.setParameter(param.getKey(), values);
				}
			}
		}

		if (isNotBlank(paramName) && paramValues != null) {
			urlBuilder.setParameter(paramName, paramValues);
		}

		return urlBuilder.buildString();
	}

	/**
	 * Appends an additional path to the application URL.
	 * 
	 * @param additionalPath
	 *          The additional path to append to the application URL.
	 * @return the new URL.
	 */
	public static String appendToApplicationUrl(String additionalPath) {

		final UrlBuilder urlBuilder = new UrlBuilder();

		urlBuilder.setProtocol(Location.getProtocol());
		urlBuilder.setHost(Location.getHost());
		final Integer port = asInt(Location.getPort());
		if (port != null) {
			urlBuilder.setPort(port);
		}
		urlBuilder.setPath(Location.getPath() + additionalPath);

		return urlBuilder.buildString();

	}

	/**
	 * Gets the current date.
	 * 
	 * @return The current date.
	 */
	public static Date now() {
		return new Date();
	}

	/**
	 * Formats the given {@code date} using the default pattern {@code dd/MM/yyyy}.
	 * <p>
	 * <b><em>Warning : on <u>server</u> side, the method returns the {@code date} time (in milliseconds).</em></b>
	 * </p>
	 * 
	 * @param date
	 *          The date to format.
	 * @return the formatted date or {@code null} if the given {@code date} is {@code null}.
	 */
	public static String formatDate(final Date date) {
		return formatDate(date, null);
	}

	/**
	 * Formats the given {@code date} using the given pattern {@code pattern}.
	 * <p>
	 * <b><em>Warning : on <u>server</u> side, the method returns the {@code date} time (in milliseconds).</em></b>
	 * </p>
	 * 
	 * @param date
	 *          The date to format.
	 * @param pattern
	 *          The date pattern (default pattern {@code dd/MM/yyyy} is used if {@code null} or empty).
	 * @return the formatted date or {@code null} if the given {@code date} is {@code null}.
	 * @throws IllegalArgumentException
	 *           If the given {@code pattern} cannot be parsed.
	 */
	public static String formatDate(final Date date, final String pattern) {
		if (date == null) {
			return null;
		}
		if (GWT.isClient()) {
			return DateTimeFormat.getFormat(isBlank(pattern) ? "dd/MM/yyyy" : pattern).format(date);
		} else {
			// Cannot use 'DateTimeFormat' on server-side.
			return String.valueOf(date.getTime());
		}
	}

	/**
	 * Creates a type-safe generic array.
	 * 
	 * @param <T>
	 *          The array's element type.
	 * @param items
	 *          The varargs array items, null allowed.
	 * @return The array, not null unless a null array is passed in
	 * @see org.apache.commons.lang3.ArrayUtils#toArray(Object...)
	 */
	@SafeVarargs
	public static <T> T[] toArray(final T... items) {
		return items;
	}

	/**
	 * Fires a standard window open request from the given url.
	 * 
	 * @param url
	 *          The URL evaluated by the window open request.
	 */
	public static void openWindow(String url) {
		openWindow(url, false);
	}

	/**
	 * Fires a featured or not window open request from the given url with eventual default features. This method's
	 * features are IE10 complient.<br>
	 * <i> Note that Miscrosoft does not support window name as a second parameter of the native {@code Window.open}
	 * method. Can bu used : "_blank", "_media", "_parent", "_search", "_self", "_top".</i>.
	 * 
	 * @param url
	 *          The URL evaluated by the window open request.
	 * @param featured
	 *          If the features must be passed in the method and the zoom parameter appended in url at a 100% fit value.
	 */
	public static void openWindow(String url, final boolean featured) {
		openWindow(url, featured, 0, 0);
	}

	/**
	 * Open a window from the given url, sized with the given pixel width and height. This method's features are IE10
	 * complient.<br>
	 * <i> Note that Miscrosoft does not support window name as a second parameter of the native {@code Window.open}
	 * method. Can bu used : "_blank", "_media", "_parent", "_search", "_self", "_top".</i>.
	 * 
	 * @param url
	 *          The URL evaluated by the window open request.
	 * @param featured
	 *          If the features must be passed in the method and the zoom parameter appended in url at a 100% fit value.
	 * @param widthPx
	 *          The window's width feature in pixels (0 or negative value for default).<br>
	 *          Should be lower than current screen width.
	 * @param heightPx
	 *          The window's height feature in pixels (0 or negative value for default).<br>
	 *          Should be lower than current screen height.
	 */
	public static void openWindow(String url, final boolean featured, int widthPx, int heightPx) {

		final int screenWidth = screenWidth();
		final int screenHeight = screenHeight();

		// Build the eventual features string.
		final StringBuilder featuresBuilder;
		if (featured) {
			featuresBuilder = new StringBuilder();
			featuresBuilder.append("resizable=yes,scrollbars=yes,menubar=no,status=no,location=no,titlebar=no,toolbar=no");
			if (widthPx > 0) {
				if (widthPx < screenWidth) {
					featuresBuilder.append(',');
					featuresBuilder.append("left=");
					featuresBuilder.append((screenWidth - widthPx) / 2);
				} else {
					widthPx = screenWidth;
				}
				featuresBuilder.append(',');
				featuresBuilder.append("width=");
				featuresBuilder.append(widthPx);
			}
			if (heightPx > 0) {
				if (heightPx < screenHeight) {
					featuresBuilder.append(',');
					featuresBuilder.append("top=");
					featuresBuilder.append((screenHeight - heightPx) / 2);
				} else {
					heightPx = screenHeight;
				}
				featuresBuilder.append(',');
				featuresBuilder.append("height=");
				featuresBuilder.append(heightPx);
			}
		} else {
			featuresBuilder = new StringBuilder(0);
		}

		// Check the URL, eventually configure zoom and cache bypass it.
		if (!url.contains("rand=") && !url.contains("r=")) {
			url += (url.indexOf('?') == -1) ? '?' : '&';
			url += "rand=" + Math.random();
		}
		if (featured) {
			url += "#zoom=100";
		}

		// Javascript native call of window opening.
		// FIXME Check how to prevent IE10 from closing window and suggesting open, save in a bottom popin if featured.
		Window.open(url, "", featuresBuilder.toString());

	}

	/**
	 * Returns the {@code window.screen.width} property value.
	 * 
	 * @return The {@code window.screen.width} property value (in pixels).
	 */
	public static native int screenWidth() /*-{
		return $wnd.screen.width;
	}-*/;

	/**
	 * Returns the {@code window.screen.height} property value.
	 * 
	 * @return The {@code window.screen.height} property value (in pixels).
	 */
	public static native int screenHeight() /*-{
		return $wnd.screen.height;
	}-*/;

	/**
	 * Calculates the absolute offset (left, top) of the given {@code element} relatively to root <em>body</em> element.
	 * Automatically includes parent(s) offset(s).
	 * 
	 * @param element
	 *          The DOM element.
	 * @return The calculated absolute offset (left, top) of the given {@code element} relatively to root <em>body</em>
	 *         element.
	 */
	public static Pair<Integer, Integer> getAbsoluteOffset(final Element element) {
		return getAbsoluteOffset(element, 0, 0);
	}

	/**
	 * Recursive method used by {@link #getAbsoluteOffset(Element)}.
	 * 
	 * @param element
	 *          The current DOM element.
	 * @param offsetLeft
	 *          The current calculated absolute left offset.
	 * @param offsetTop
	 *          The current calculated absolute right offset.
	 * @return The calculated absolute offset (left, top) of an element relatively to root <em>body</em> element.
	 * @see #getAbsoluteOffset(Element)
	 */
	private static Pair<Integer, Integer> getAbsoluteOffset(final Element element, final int offsetLeft, final int offsetTop) {
		if (element == null) {
			return new Pair<Integer, Integer>(offsetLeft, offsetTop);
		} else {
			return getAbsoluteOffset(element.getOffsetParent(), offsetLeft + element.getOffsetLeft(), offsetTop + element.getOffsetTop());
		}
	}

	/**
	 * Sends the browser a new {@code mailto} request with the given subject and mail addresses.
	 * 
	 * @param subject
	 *          The mail's subject (can be null).
	 * @param body
	 *          The mail's body (can be null).
	 * @param addresses
	 *          The mail's addresses collection.
	 */
	public static final void mailTo(final String subject, final String body, final Collection<String> addresses) {

		if (isEmpty(addresses)) {
			return;
		}

		// Filters mails addresses.
		final List<String> filteredMails = new ArrayList<String>(addresses.size());
		for (final String address : addresses) {

			if (isNotBlank(address) && EMAIL_CLIENT_REGEXP.test(address.trim())) {
				filteredMails.add(address);
			}
		}
		if (isEmpty(filteredMails)) {
			return;
		}
		Collections.sort(filteredMails);

		// Builds mailto link.
		final StringBuilder mailTos = new StringBuilder("mailto:");
		for (final String mail : filteredMails) {
			mailTos.append(mail);
			mailTos.append(";");
		}
		mailTos.deleteCharAt(mailTos.length() - 1);
		if (isNotBlank(subject)) {
			mailTos.append("?subject=");
			mailTos.append(subject);
		}
		if (isNotBlank(body)) {
			mailTos.append((isNotBlank(subject) ? "&" : "?") + "body=");
			mailTos.append(body);
		}

		Window.Location.assign(mailTos.toString());

	}

	/**
	 * Sends the browser a new {@code mailto} request with the given subject and mail addresses.
	 * 
	 * @param subject
	 *          The mail's subject (can be null).
	 * @param body
	 *          The mail's body (can be null).
	 * @param addresses
	 *          The mail's addresses items.
	 */
	public static final void mailTo(final String subject, final String body, final String... addresses) {
		if (isEmpty(addresses)) {
			return;
		}
		mailTo(subject, body, Arrays.asList(addresses));
	}

	/**
	 * Calculates the ratio dimensions for the given arguments.
	 * 
	 * @param width
	 *          The current width.
	 * @param height
	 *          The current height.
	 * @param maxWidth
	 *          The max width.
	 * @param maxHeight
	 *          The max height.
	 * @return The ratio dimensions (width ; height).
	 */
	public static final Pair<Integer, Integer> ratio(final int width, final int height, final int maxWidth, final int maxHeight) {

		float scale = Math.min(maxWidth * 1.0f / width, maxHeight * 1.0f / height);

		return new Pair<Integer, Integer>((int) (width * scale), (int) (height * scale));
	}

	/**
	 * Deletes the {@code <pre>} tags from the given {@code value}.
	 * 
	 * <pre>
	 * deletePreTags("&lt;pre&gt;my value&lt;/pre&gt;") → "my value"
	 * </pre>
	 * 
	 * @param value
	 *          The value.
	 * @return The value without {@code <pre>} tags.
	 */
	public static final String deletePreTags(final String value) {

		if (isBlank(value)) {
			return value;
		}

		return value.replace("<pre>", "").replace("</pre>", "");
	}

	/**
	 * <p>
	 * Launches the given {@code downloadUrl} corresponding download action (simple {@code GET} access on the URL).
	 * </p>
	 * <p>
	 * In <em>script</em> mode, assigns window location to the given {@code downloadUrl}.<br>
	 * In <em>hosted</em> (dev) mode, opens a new window to avoid loosing the entire JS application context in case of
	 * error.
	 * </p>
	 * 
	 * @param downloadUrl
	 *          The download URL.
	 * @throws IllegalArgumentException
	 *           If the given {@code downloadUrl} is blank.
	 */
	public static final void launchDownload(final String downloadUrl) {

		if (isBlank(downloadUrl)) {
			throw new IllegalArgumentException("Invalid download URL '" + downloadUrl + "'.");
		}

		if (GWT.isScript()) {
			// Production mode: switch to new page.
			Window.Location.assign(downloadUrl);

		} else {
			// Hosted mode: avoid loosing the entire JS application context in case of error.
			openWindow(downloadUrl);
		}
	}

	/**
	 * Extracts the {@link Integer} ids of the given {@code entityDTOs}.<br>
	 * If the given {@code entityDTOs} is {@code null}, the method returns {@code null}.
	 * 
	 * @param entityDTOs
	 *          The collection of {@link EntityDTO} with {@link Integer} id type.<br>
	 *          Ignores {@code null} values.
	 * @return The given {@code entityDTOs} corresponding ids.
	 */
	public static <E extends EntityDTO<Integer>> List<Integer> getEntityIds(final Collection<E> entityDTOs) {

		if (entityDTOs == null) {
			return null;
		}

		final List<Integer> ids = new ArrayList<Integer>();

		for (final E entity : entityDTOs) {
			if (entity != null) {
				ids.add(entity.getId());
			}
		}

		return ids;
	}

}
