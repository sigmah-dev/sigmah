package org.sigmah.server.mail;

import java.io.InputStream;
import org.apache.commons.mail.EmailException;

/**
 * Defines a mail sender.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface MailSender {

	/**
	 * Sends the given email.
	 * 
	 * @param email
	 *          The email.
	 * @throws EmailException
	 *           If an error occurs during the sending.
	 */
	void send(final Email email) throws EmailException;

	/**
	 * Sends the given email with a file as attached content.
	 * 
	 * @param email
	 *			Email to send.
	 * @param fileName
	 *			Name of the attachment.
	 * @param fileStream
	 *			Content of the attachment.
	 * @throws EmailException 
	 */
	void sendFile(final Email email, String fileName, InputStream fileStream) throws EmailException;
	
}
