package org.sigmah.server.mail;

/**
 * Defines a mail sender.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface MailSender {

	/**
	 * Sends the given email.
	 * 
	 * @param email
	 *          The email.
	 * @throws Exception
	 *           If an error occurs during the sending.
	 */
	void send(final Email email) throws Exception;

}
