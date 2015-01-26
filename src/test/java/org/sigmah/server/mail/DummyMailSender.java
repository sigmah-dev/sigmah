package org.sigmah.server.mail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.mail.EmailException;
import org.junit.Assert;

/**
 * Test implementation of the mail sender.
 * <p/>
 * Does not send any actual email but test if the given arguments are corrects
 * and display the requested e-mail on the standard output.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DummyMailSender implements MailSender {

	@Override
	public void send(Email email) throws Exception {
		if (email == null || ArrayUtils.isEmpty(email.getToAddresses())) {
			// does nothing.
			throw new EmailException("Email object null or invalid.");
		}
		
		Assert.assertNotNull(email.getFromAddress());
		Assert.assertFalse(email.getFromAddress().trim().isEmpty());
		
		Assert.assertNotNull(email.getSubject());
		Assert.assertFalse(email.getSubject().isEmpty());
		
		Assert.assertNotNull(email.getContent());
		Assert.assertFalse(email.getContent().isEmpty());
		
		Assert.assertNotNull(email.getContentType());
		Assert.assertFalse(email.getContentType().isEmpty());
		
		Assert.assertNotNull(email.getEncoding());
		Assert.assertFalse(email.getEncoding().isEmpty());
		
		// Building the output
		System.out.println("From: \"" + email.getFromName()+ "\" <" + email.getFromAddress() + '>');

		final StringBuilder toAddressesBuilder = new StringBuilder();
		for (final String address : email.getToAddresses()) {
			toAddressesBuilder.append(address).append(", ");
		}
		toAddressesBuilder.setLength(toAddressesBuilder.length() - 2);
		System.out.println("To: " + toAddressesBuilder);
		
		if (ArrayUtils.isNotEmpty(email.getCcAddresses())) {
			final StringBuilder ccAddressesBuilder = new StringBuilder();
			for (final String address : email.getCcAddresses()) {
				ccAddressesBuilder.append(address).append(", ");
			}
			ccAddressesBuilder.setLength(toAddressesBuilder.length() - 2);
			System.out.println("Cc: " + ccAddressesBuilder);
		}
		
		System.out.println("Subject: " + email.getSubject());
		final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
		System.out.println("Date: " + dateFormat.format(new Date()));
		System.out.println("Content-Type: " + email.getContentType() + ";charset=" + email.getEncoding());
		
		System.out.println();
		System.out.println(email.getContent());
	}
	
}
