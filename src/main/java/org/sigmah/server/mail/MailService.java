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


import java.io.InputStream;
import java.util.Map;

import org.sigmah.shared.Language;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailType;

/**
 * Defines the mail service.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface MailService {

	/**
	 * Sends an email built from the given model and saves it.
	 * 
	 * @param type
	 *          The email model.
	 * @param parameters
	 *          The parameters to replace in the model.
	 * @param language
	 *          The mail language.
	 * @param to
	 *          The TO address(es).
	 * @return {@code true} if the email has successfully been sent, {@code false} otherwise.
	 */
	boolean send(EmailType type, Map<EmailKey, String> parameters, Language language, String... to);

	/**
	 * Sends an email built from the given model and saves it.
	 * 
	 * @param type
	 *          The email model.
	 * @param parameters
	 *          The parameters to replace in the model.
	 * @param language
	 *          The mail language.
	 * @param to
	 *          The TO address(es).
	 * @param cc
	 *          The CC address(es).
	 * @return {@code true} if the email has successfully been sent, {@code false} otherwise.
	 */
	boolean send(EmailType type, Map<EmailKey, String> parameters, Language language, String[] to, String... cc);

	/**
	 * Sends an email built from the given model and saves it.
	 * 
	 * @param type
	 *          The email model.
	 * @param parameters
	 *          The parameters to replace in the model.
	 * @param fileName
	 *			Name of the attached file.
	 * @param fileStream
	 *			Content of the attached file.
	 * @param language
	 *          The mail language.
	 * @param to
	 *          The TO address(es).
	 * @param cc
	 *          The CC address(es).
	 * @return {@code true} if the email has successfully been sent, {@code false} otherwise.
	 */
	boolean send(EmailType type, Map<EmailKey, String> parameters, final String fileName, final InputStream fileStream, Language language, String[] to, String... cc);

}
