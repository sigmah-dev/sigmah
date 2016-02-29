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


import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.sigmah.shared.util.FileType;

/**
 * Implementation of the mail sender using the Apache Commons library.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MailSenderImpl implements MailSender {
	
	private static final String MAIL_TRANSPORT_PROTOCOL = "mail.transport.protocol";
	
	private static final String MAIL_SMTP_HOST = "mail.smtp.host";
	
	private static final String MAIL_SMTP_PORT = "mail.smtp.port";
	
	private static final String TRANSPORT_PROTOCOL = "smtp";

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

	@Override
	public void sendFile(Email email, String fileName, InputStream fileStream) throws EmailException {
        final String user = email.getAuthenticationUserName();
        final String password = email.getAuthenticationPassword();
        
		final Properties properties = new Properties();
		properties.setProperty(MAIL_TRANSPORT_PROTOCOL, TRANSPORT_PROTOCOL);
		properties.setProperty(MAIL_SMTP_HOST, email.getHostName());
		properties.setProperty(MAIL_SMTP_PORT, Integer.toString(email.getSmtpPort()));
        
		final StringBuilder toBuilder = new StringBuilder();
		for(final String to : email.getToAddresses()) {
			if(toBuilder.length() > 0) {
				toBuilder.append(',');
			}
			toBuilder.append(to);
		}

		final StringBuilder ccBuilder = new StringBuilder();
		if(email.getCcAddresses().length > 0) {
			for(final String cc : email.getCcAddresses()) {
				if(ccBuilder.length() > 0) {
					ccBuilder.append(',');
				}
				ccBuilder.append(cc);
			}
		}
		
        final Session session = javax.mail.Session.getInstance(properties);
		try {
			final DataSource attachment = new ByteArrayDataSource(fileStream, 
			FileType.fromExtension(FileType.getExtension(fileName), FileType._DEFAULT).getContentType());
			
			final Transport transport = session.getTransport();

			if(password != null) {
				transport.connect(user, password);
			} else {
				transport.connect();
			}
			
			final MimeMessage message = new MimeMessage(session);
			
			// Configures the headers.
			message.setFrom(new InternetAddress(email.getFromAddress(), false));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toBuilder.toString(), false));
			if(email.getCcAddresses().length > 0) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccBuilder.toString(), false));
			}

			message.setSubject(email.getSubject(), email.getEncoding());

			// Html body part.
			final MimeMultipart textMultipart = new MimeMultipart("alternative");

			final MimeBodyPart htmlBodyPart = new MimeBodyPart();
			htmlBodyPart.setContent(email.getContent(), "text/html; charset=\"" + email.getEncoding() + "\"");
			textMultipart.addBodyPart(htmlBodyPart);
			
			final MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setContent(textMultipart);				

			// Attachment body part.
			final MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.setDataHandler(new DataHandler(attachment));
			attachmentPart.setFileName(fileName);
			attachmentPart.setDescription(fileName);

			// Mail multipart content.
			final MimeMultipart contentMultipart = new MimeMultipart("related");
			contentMultipart.addBodyPart(textBodyPart);
			contentMultipart.addBodyPart(attachmentPart);

			message.setContent(contentMultipart);
			message.saveChanges();

			// Sends the mail.
			transport.sendMessage(message, message.getAllRecipients());
			
		} catch (UnsupportedEncodingException ex) {
			throw new EmailException("An error occured while encoding the mail content to '" + email.getEncoding() + "'.", ex);
			
		} catch(IOException ex) {
			throw new EmailException("An error occured while reading the attachment of an email.", ex);
			
		} catch (MessagingException ex) {
			throw new EmailException("An error occured while sending an email.", ex);
		}
	}
	
	
	
	public void sendEmailWithMultiAttachmenets(Email email, String[] fileNames, InputStream[] fileStreams) throws EmailException {
        final String user = email.getAuthenticationUserName();
        final String password = email.getAuthenticationPassword();
        
		final Properties properties = new Properties();
		properties.setProperty(MAIL_TRANSPORT_PROTOCOL, TRANSPORT_PROTOCOL);
		properties.setProperty(MAIL_SMTP_HOST, email.getHostName());
		properties.setProperty(MAIL_SMTP_PORT, Integer.toString(email.getSmtpPort()));
        
		final StringBuilder toBuilder = new StringBuilder();
		for(final String to : email.getToAddresses()) {
			if(toBuilder.length() > 0) {
				toBuilder.append(',');
			}
			toBuilder.append(to);
		}

		final StringBuilder ccBuilder = new StringBuilder();
		if(email.getCcAddresses().length > 0) {
			for(final String cc : email.getCcAddresses()) {
				if(ccBuilder.length() > 0) {
					ccBuilder.append(',');
				}
				ccBuilder.append(cc);
			}
		}
		
        final Session session = javax.mail.Session.getInstance(properties);
		try {
			final Transport transport = session.getTransport();

			if(password != null) {
				transport.connect(user, password);
			} else {
				transport.connect();
			}
			
			final MimeMessage message = new MimeMessage(session);
			
			// Configures the headers.
			message.setFrom(new InternetAddress(email.getFromAddress(), false));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toBuilder.toString(), false));
			if(email.getCcAddresses().length > 0) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccBuilder.toString(), false));
			}
			message.setSubject(email.getSubject(), email.getEncoding());

			// Html body part.
			final MimeMultipart textMultipart = new MimeMultipart("alternative");

			final MimeBodyPart htmlBodyPart = new MimeBodyPart();
			htmlBodyPart.setContent(email.getContent(), "text/html; charset=\"" + email.getEncoding() + "\"");
			textMultipart.addBodyPart(htmlBodyPart);
			
			final MimeBodyPart textBodyPart = new MimeBodyPart();
			textBodyPart.setContent(textMultipart);		
			
			// Mail multipart content.
			final MimeMultipart contentMultipart = new MimeMultipart("related");
			contentMultipart.addBodyPart(textBodyPart);
			
			for(int i=0;i<fileNames.length;i++){
				addAttachment(contentMultipart,fileNames[i],fileStreams[i]);
			}
			message.setContent(contentMultipart);
			message.saveChanges();
			// Sends the mail.
			transport.sendMessage(message, message.getAllRecipients());
			
		} catch (UnsupportedEncodingException ex) {
			throw new EmailException("An error occured while encoding the mail content to '" + email.getEncoding() + "'.", ex);
			
		} catch(IOException ex) {
			throw new EmailException("An error occured while reading the attachment of an email.", ex);
			
		} catch (MessagingException ex) {
			throw new EmailException("An error occured while sending an email.", ex);
		}
	}
	
	public void addAttachment(MimeMultipart contentMultipart, String fileName, InputStream fileStream) throws MessagingException,IOException{        
			
			
			final DataSource attachment = new ByteArrayDataSource(fileStream,FileType.fromExtension(FileType.getExtension(fileName), FileType._DEFAULT).getContentType()); 			
			// Attachment body part.
			final MimeBodyPart attachmentPart = new MimeBodyPart();
			attachmentPart.setDataHandler(new DataHandler(attachment));
			attachmentPart.setFileName(fileName);
			attachmentPart.setDescription(fileName);
			contentMultipart.addBodyPart(attachmentPart);
			
		
	}
	
	

}
