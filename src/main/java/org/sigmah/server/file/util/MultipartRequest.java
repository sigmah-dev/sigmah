package org.sigmah.server.file.util;

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

import com.google.gwt.http.client.Response;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.sigmah.server.servlet.base.StatusServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse a multipart request and allow access to its data as a map of <code>String</code> and <code>InputStream</code>.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MultipartRequest {
	private static final Logger LOGGER = LoggerFactory.getLogger(MultipartRequest.class);
	
	private final Map<String, String> properties;
	private final HttpServletRequest request;
	
	public MultipartRequest(HttpServletRequest request) {
		this.request = request;
		this.properties = new HashMap<>();
	}
	
	public void parse(MultipartRequestCallback callback) throws IOException, FileUploadException, StatusServletException {
		if (!ServletFileUpload.isMultipartContent(request)) {
			LOGGER.error("Request content is not multipart.");
			throw new StatusServletException(Response.SC_PRECONDITION_FAILED);
		}
		
		final FileItemIterator iterator = new ServletFileUpload(new DiskFileItemFactory()).getItemIterator(request);
		while (iterator.hasNext()) {
			// Gets the first HTTP request element.
			final FileItemStream item = iterator.next();

			if (item.isFormField()) {
				final String value = Streams.asString(item.openStream(), "UTF-8");
				properties.put(item.getFieldName(), value);
				
			} else if(callback != null) {
				callback.onInputStream(item.openStream(), item.getFieldName(), item.getContentType());
			}
		}
	}
	
	public String getProperty(String property) {
		return properties.get(property);
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
