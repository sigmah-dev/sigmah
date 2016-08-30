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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.mail.EmailException;
import org.junit.Assert;

/**
 * Test implementation of the mail sender.
 *
 * Does not send any actual email but test if the given arguments are corrects
 * and display the requested e-mail on the standard output.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DummyMailSender implements MailSender {

	@Override
	public void send(Email email) throws EmailException {
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

	@Override
	public void sendFile(Email email, String fileName, InputStream fileStream) throws EmailException {
		send(email);
	}

	@Override
	public void sendEmailWithMultiAttachmenets(Email email, String[] fileNames, InputStream[] fileStreams) throws EmailException {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

}
