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
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import org.sigmah.shared.util.FileType;

/**
 * File attached to an email.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class EmailAttachment {
	
	/**
	 * Name of the attached file.
	 */
	private final String fileName;
	
	/**
	 * Content of the attached file.
	 */
	private final DataHandler dataHandler;

	/**
	 * Creates a new attachment with the given filename and content.
	 * 
	 * @param fileName
	 *			Name of the file to attach.
	 * @param content 
	 *			Content of the file to attach.
	 */
	public EmailAttachment(final String fileName, final byte[] content) {
		final String contentType = FileType.fromExtension(FileType.getExtension(fileName), FileType._DEFAULT).getContentType();
		
		this.fileName = fileName;
		this.dataHandler = new DataHandler(toDataSource(content, contentType));
	}

	/**
	 * Creates a new attachment with the given filename and input stream.
	 * 
	 * @param fileName
	 *			Name of the file to attach.
	 * @param inputStream 
	 *			Opened input stream of the file to attach.
	 */
	public EmailAttachment(final String fileName, final InputStream inputStream) throws IOException {
		final String contentType = FileType.fromExtension(FileType.getExtension(fileName), FileType._DEFAULT).getContentType();
		
		this.fileName = fileName;
		this.dataHandler = new DataHandler(toDataSource(inputStream, contentType));
	}

	/**
	 * Creates a new <code>MimeBodyPart</code> object with the content of this
	 * attachement.
	 * 
	 * @return a new <code>MimeBodyPart</code> object.
	 * @throws MessagingException If an error occurs while setting the body part.
	 */
	public MimeBodyPart toMimeBodyPart() throws MessagingException {
		final MimeBodyPart attachmentPart = new MimeBodyPart();
		attachmentPart.setDataHandler(dataHandler);
		attachmentPart.setFileName(fileName);
		attachmentPart.setDescription(fileName);
		return attachmentPart;
	}
	
	/**
	 * Creates a new <code>DataSource</code> with the given input stream and 
	 * content type.
	 * 
	 * @param inputStream
	 *			Opened input stream on the content of the file to attach.
	 * @param contentType
	 *			MIME type of the file.
	 * @return A new <code>DataSource</code> object.
	 * @throws IOException If an error occurs while reading the content of the input stream.
	 */
	private DataSource toDataSource(final InputStream inputStream, final String contentType) throws IOException {
		return new ByteArrayDataSource(inputStream, contentType);
	}
	
	/**
	 * Creates a new <code>DataSource</code> with the given bytes and content
	 * type.
	 * 
	 * @param bytes
	 *			Content of the file to attach.
	 * @param contentType
	 *			MIME type of the file.
	 * @return A new <code>DataSource</code> object.
	 */
	private DataSource toDataSource(final byte[] bytes, final String contentType) {
		return new ByteArrayDataSource(bytes, contentType);
	}
	
	/**
	 * Returns the name of this file.
	 * 
	 * @return the name of this file.
	 */
	public String getFileName() {
		return fileName;
	}

}
