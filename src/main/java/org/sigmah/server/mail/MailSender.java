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
	/**
	 * Sends the given email with a many files as attached content
	 * @param email 
	 *			Email to send.
	 * @param fileNames
	 *			Array of files names
	 * @param fileStreams
	 *			Array of conntent of attachement
	 * @throws EmailException 
	 */
	public void sendEmailWithMultiAttachmenets(Email email, String[] fileNames, InputStream[] fileStreams) throws EmailException;
}
