package org.sigmah.server.servlet;

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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sigmah.linker.Manifest;
import org.sigmah.server.inject.ServletModule;
import org.sigmah.server.util.Languages;
import org.sigmah.shared.Language;

import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parse the user-agent to determine which manifest is the best match for the
 * user.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ManifestServlet extends HttpServlet {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ManifestServlet.class);
	
	private static final String PATH_OF_THIS_CLASS;
	private static final String USER_AGENT_HEADER = "user-agent";
	private static final String MANIFEST_MIME_TYPE = "text/cache-manifest";
	private static final String MANIFEST_EXTENSION = ".manifest";
	private static final String ENCODING = "UTF-8";
	
	static {
		final String name = ManifestServlet.class.getName();
		PATH_OF_THIS_CLASS = '/' + name.replace('.', '/') + ".class";
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		final String userAgent = req.getHeader(USER_AGENT_HEADER);
		final Language language = Languages.getLanguage(req);
		final String permutation = getPermutation(userAgent.toLowerCase());
		
		final Manifest manifest = new Manifest(permutation, language);
		LOGGER.info("Requesting HTML5 manifest for " + manifest);
		
		resp.setContentType(MANIFEST_MIME_TYPE);
		resp.setCharacterEncoding(ENCODING);
		
		File manifestFile = null;
		try {
			final URL resourceUrl = ManifestServlet.class.getResource(PATH_OF_THIS_CLASS);
			final File gwtXmlFile = new File(resourceUrl.toURI());
			final File rootFolder = gwtXmlFile // ManifestServlet.class
					.getParentFile() // servlet
					.getParentFile() // server
					.getParentFile() // sigmah
					.getParentFile() // org
					.getParentFile() // classes
					.getParentFile() // WEB-INF
					.getParentFile(); // root
			final File jsFolder = new File(rootFolder, ServletModule.ENDPOINT);
			manifestFile = new File(jsFolder, manifest.toFileName());
			
			if(!manifestFile.exists()) {
				final Manifest defaultLocaleAlternative = new Manifest(permutation, (String)null);
				manifestFile = new File(jsFolder, defaultLocaleAlternative.toFileName());
				
				if(!manifestFile.exists()) {
					final Manifest defaultUserAgentAlternative = new Manifest(null, language);
					manifestFile = new File(jsFolder, defaultUserAgentAlternative.toFileName());
					
					if(!manifestFile.exists()) {
						final Manifest defaultAlternative = new Manifest();
						manifestFile = new File(jsFolder, defaultAlternative.toFileName());
					}
				}
			}
			
		} catch (URISyntaxException ex) {
			LOGGER.error("The path to this class ('" + PATH_OF_THIS_CLASS + "') could not be transformed to a valid URI.", ex);
		}
		
		if(manifestFile == null || !manifestFile.exists()) {
			throw new FileNotFoundException(permutation + MANIFEST_EXTENSION);
		}
		
		try (FileInputStream inputStream = new FileInputStream(manifestFile); OutputStream outputStream = resp.getOutputStream()) {
			final byte[] cache = new byte[1024];
			int length = inputStream.read(cache);
			while(length != -1) {
				outputStream.write(cache, 0, length);
				length = inputStream.read(cache);
			}
		}
	}
	
	/**
	 * This method is based on the one used by GWT when choosing which
	 * permutation to use.
	 * 
	 * @param userAgent User-agent of the web browser.
	 * @return Permutation associated with the given browser.
	 * @throws UnsupportedOperationException If the given web browser is not supported.
	 */
	private String getPermutation(String userAgent) {
		if (userAgent.contains("opera")) {
			return "opera";
		} else if (userAgent.contains("webkit")) {
			return "safari";
		} else if (userAgent.contains("trident/")) {
			final int versionStart = userAgent.indexOf("trident/");
			final int versionEnd = userAgent.indexOf('.', versionStart);
			final int version = Integer.parseInt(userAgent.substring(versionStart + "trident/".length(), versionEnd));
			
			if(version < 4) {
				return "ie6";
			} else if(version == 4) {
				return "ie8";
			} else {
				return "ie9";
			}
		} else if (userAgent.contains("gecko")) {
			return "gecko1_8";
		}
		throw new UnsupportedOperationException("Web browser is unsupported: " + userAgent);
	}
	
}
