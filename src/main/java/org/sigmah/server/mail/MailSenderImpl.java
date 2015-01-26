package org.sigmah.server.mail;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * Implementation of the mail sender using the Apache Commons library.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class MailSenderImpl implements MailSender {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void send(final Email email) throws EmailException {

		if (email == null || ArrayUtils.isEmpty(email.getToAddresses())) {
			// does nothing.
			throw new EmailException("Email object null or invalid.");
		}

		// Simple email.
		final SimpleEmail simpleEmail = new SimpleEmail();

		// Mail content parameters.
		simpleEmail.setFrom(email.getFromAddress(), email.getFromName());
		for (final String address : email.getToAddresses()) {
			simpleEmail.addTo(address);
		}
		if (ArrayUtils.isNotEmpty(email.getCcAddresses())) {
			for (final String address : email.getCcAddresses()) {
				simpleEmail.addCc(address);
			}
		}
		simpleEmail.setSubject(email.getSubject());
		simpleEmail.setContent(email.getContent(), email.getContentType());

		// Mail sending parameters.
		simpleEmail.setCharset(email.getEncoding());
		simpleEmail.setHostName(email.getHostName());
		simpleEmail.setSmtpPort(email.getSmtpPort());

		// Authentication is needed.
		final String userName = email.getAuthenticationUserName();
		final String password = email.getAuthenticationPassword();
		if (userName != null && password != null) {
			simpleEmail.setAuthentication(userName, password);
		}

		// Sends the mail.
		simpleEmail.send();
	}

}
