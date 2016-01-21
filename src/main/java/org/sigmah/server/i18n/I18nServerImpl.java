package org.sigmah.server.i18n;

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


import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.conf.ReloadableProperties;
import org.sigmah.server.util.Languages;
import org.sigmah.shared.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation for {@link I18nServer}
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class I18nServerImpl implements I18nServer {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ReloadableProperties.class);

	/**
	 * Properties files extension.
	 */
	private static final String PROPERTIES_FILE_EXTENSION = ".properties";

	/**
	 * <p>
	 * i18n files encoding charset.
	 * </p>
	 * See {@code http://www.gwtproject.org/doc/latest/DevGuideI18n.html#DevGuidePropertiesFiles}.
	 */
	private static final Charset I18N_FILE_ENCODING = StandardCharsets.UTF_8;

	/**
	 * The start tag for I18N message property parameter.
	 */
	private static final String PARAM_TAG_START = "{";

	/**
	 * The end tag for I18N message property parameter.
	 */
	private static final String PARAM_TAG_END = "}";

	/**
	 * The list of the properties file to load.
	 */
	private static final List<String> propertiesFiles = new ArrayList<String>();

	/**
	 * Properties map: one {@code java.util.Properties} for each language.
	 */
	private static final Map<Language, java.util.Properties> propertiesMap = new EnumMap<Language, java.util.Properties>(Language.class);

	public I18nServerImpl() {
		// Initialize the properties files list.
		propertiesFiles.add("org/sigmah/client/i18n/UIConstants");
		propertiesFiles.add("org/sigmah/client/i18n/UIMessages");
		propertiesFiles.add("org/sigmah/server/mail/model/MailModels");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String t(final Language language, final String key) {
		return t(language, key, (Object[]) null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String t(final Language language, final String key, final Object... parameters) {

		if (StringUtils.isBlank(key)) {
			return null;
		}

		// Loading the properties files for the language if necessary
		ensurePropertiesLoaded(language);

		String propertyValue = propertiesMap.get(Languages.notNull(language)).getProperty(key);
		
		if(propertyValue == null) {
			ensurePropertiesLoaded(Languages.DEFAULT_LANGUAGE);
			propertyValue = propertiesMap.get(Languages.DEFAULT_LANGUAGE).getProperty(key);
		}

		if (propertyValue != null && ArrayUtils.isNotEmpty(parameters)) {
			return buildMessage(propertyValue, parameters);
		} else {
			return propertyValue;
		}
	}
	
	/**
	 * Load the properties for the given language if not loaded yet.
	 * 
	 * @param language Language to load.
	 */
	private static void ensurePropertiesLoaded(Language language) {
		if (propertiesMap.get(Languages.notNull(language)) == null) {
			try {

				loadProperties(Languages.notNull(language));

			} catch (final IOException e) {
				// If the properties files cannot be loaded properly, returning null value
			}
		}
	}

	/**
	 * Load the properties files for a given {@code language} code.
	 * 
	 * @param language
	 *          The language.
	 * @throws IOException
	 *           If an error occurs while loading the properties files.
	 */
	private static void loadProperties(final Language language) throws IOException {

		final java.util.Properties properties = new java.util.Properties();

		// Read properties from the properties files.
		for (final String file : propertiesFiles) {

			// Determining the properties filename using the language code.
			final String fullFilename = file + Languages.getFileSuffix(language, PROPERTIES_FILE_EXTENSION);

			try {

				if (LOG.isInfoEnabled()) {
					LOG.info("Loading properties from file '{}'.", fullFilename);
				}

				// Loading the properties file.
				properties.load(new InputStreamReader(Properties.class.getClassLoader().getResourceAsStream(fullFilename), I18N_FILE_ENCODING));

			} catch (final IOException e) {
				if (LOG.isErrorEnabled()) {
					LOG.error("Properties loading failure with file '" + fullFilename + "'.", e);
				}
				throw e;
			}
		}

		propertiesMap.put(language, properties);
	}
	
	/**
	 * Builds the property value's message from given parameters.
	 * 
	 * @param propertyValue
	 *          The value issued of a message property key.
	 * @param params
	 *          The parameters object values that are supposed to fit the message content.
	 * @return The built message.
	 */
	private static final String buildMessage(String propertyValue, final Object... params) {

		propertyValue = propertyValue.replaceAll(Pattern.quote("''"), "'");

		if (ArrayUtils.isNotEmpty(params)) {

			int index = 0;
			String regex = PARAM_TAG_START + index + PARAM_TAG_END;

			while (StringUtils.contains(propertyValue, regex)) {
				if (index < params.length) {
					propertyValue = propertyValue.replaceAll(Pattern.quote(regex), String.valueOf(params[index]));
				}
				index++;
				regex = PARAM_TAG_START + index + PARAM_TAG_END;
			}
		}

		return propertyValue;
	}

}
