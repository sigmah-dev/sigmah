package org.sigmah.server.mail;

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


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.conf.PropertyName;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Defines the mail service.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class ModelMailService implements MailService {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ModelMailService.class);

	// Content replacements.
	private static final String TAG_START = "{{";
	private static final String TAG_END = "}}";

	/**
	 * Mail sender.
	 */
	private final MailSender sender;

	/**
	 * Injected application properties.
	 */
	private final Properties properties;

	/**
	 * Injected {@code i18nServer} service.
	 */
	private final I18nServer i18nServer;

	@Inject
	public ModelMailService(MailSender sender, Properties properties, I18nServer i18nServer) {
		this.sender = sender;
		this.properties = properties;
		this.i18nServer = i18nServer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean send(final EmailType type, final Map<EmailKey, String> parameters, final Language language, final String... to) {
		return send(type, parameters, language, to, (String[]) null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean send(final EmailType type, final Map<EmailKey, String> parameters, final Language language, final String[] to, final String... cc) {
		return send(type, parameters, null, null, language, to, cc);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean send(final EmailType type, final Map<EmailKey, String> parameters, final String fileName, final InputStream fileStream, final Language language, final String[] to, final String... cc) {

		// Checking that the type is not null.
		if (type == null) {
			if (LOG.isWarnEnabled()) {
				LOG.warn("The email model type is null, interrupting email sending.");
			}
			return false;
		}

		// Retrieving the mail model parts.
		final String subject = i18nServer.t(language, PropertyName.n(PropertyName.PREFIX_MAIL_MODEL, type.getPropertyName(), "subject"));
		final String content = i18nServer.t(language, PropertyName.n(PropertyName.PREFIX_MAIL_MODEL, type.getPropertyName(), "content"));
		final String header = i18nServer.t(language, PropertyName.n(PropertyName.PREFIX_MAIL_MODEL, "header"));
		final String footer = i18nServer.t(language, PropertyName.n(PropertyName.PREFIX_MAIL_MODEL, "footer"));

		final String username = properties.getProperty(PropertyKey.MAIL_AUTH_USERNAME);
		final String password = properties.getProperty(PropertyKey.MAIL_AUTH_PASSWORD);

		// Building the email.
		final Email email = new Email();

		email.setFromAddress(properties.getProperty(PropertyKey.MAIL_FROM_ADDRESS));
		email.setFromName(properties.getProperty(PropertyKey.MAIL_FROM_NAME));

		email.setToAddresses(to);
		email.setCcAddresses(cc);

		email.setSubject(getReplacedString(subject, parameters));
		email.setContent(header + getReplacedString(content, parameters) + footer);
		email.setContentType(properties.getProperty(PropertyKey.MAIL_CONTENT_TYPE));

		email.setHostName(properties.getProperty(PropertyKey.MAIL_HOSTNAME));
		email.setSmtpPort(properties.getIntegerProperty(PropertyKey.MAIL_PORT));
		email.setEncoding(properties.getProperty(PropertyKey.MAIL_ENCODING));
		email.setAuthenticationUserName(StringUtils.isBlank(username) ? null : username);
		email.setAuthenticationPassword(StringUtils.isBlank(password) ? null : password);

		// Sending the email.
		if (LOG.isInfoEnabled()) {
			LOG.info("Sending an email to '{}'.", Arrays.toString(email.getToAddresses()));
		}

		try {

			if(fileName == null || fileStream == null) {
				sender.send(email);
			} else {
				sender.sendWithAttachments(email, new EmailAttachment(fileName, fileStream));
			}

			if (LOG.isInfoEnabled()) {
				LOG.info("Email successfully sent.");
			}

			return true;

		} catch (Exception e) {
			if (LOG.isInfoEnabled()) {
				LOG.info("Email sending failed.", e);
			}
			return false;
		}
	}

	/**
	 * Replaces all the parameters in the source string and returns it.
	 * 
	 * @param source
	 *          The source string.
	 * @param parameters
	 *          The parameters.
	 * @return The replaced string.
	 */
	private static String getReplacedString(String source, Map<EmailKey, String> parameters) {

		if (source == null) {
			return "";
		}

		if (parameters != null) {
			for (final Map.Entry<EmailKey, String> entry : parameters.entrySet()) {
				source = replaceTag(source, entry.getKey().getKey(), entry.getValue());
			}
		}

		return source;
	}

	/**
	 * Replace a tag with the given replacing value in the source string.
	 * 
	 * @param source
	 *          The source string.
	 * @param tagName
	 *          The tag name.
	 * @param replacedValue
	 *          The replacing value.
	 * @return The replaced string.
	 */
	private static String replaceTag(String source, String tagName, String replacedValue) {
		return source.replaceAll(Pattern.quote(TAG_START + tagName + TAG_END), Matcher.quoteReplacement(replacedValue));
	}

}
