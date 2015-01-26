package org.sigmah.server.mail;

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

}
