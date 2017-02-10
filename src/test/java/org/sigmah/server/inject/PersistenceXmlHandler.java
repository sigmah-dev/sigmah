package org.sigmah.server.inject;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2017 Groupe URD
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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler to retrieve the database configuration from a 
 * <code>persistence.xml</code> file.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PersistenceXmlHandler extends DefaultHandler {
	
	/**
	 * Class name of the SQL driver.
	 */
	private String driverClass;
	
	/**
	 * JDBC URL to connect to the database.
	 */
	private String connectionUrl;
	
	/**
	 * Username to connect to the database.
	 */
	private String username;
	
	/**
	 * Password to connect to the database.
	 */
	private String password;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startDocument() throws SAXException {
		driverClass = null;
		connectionUrl = null;
		username = null;
		password = null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		if ("property".equals(qName)) {
			final String name = attributes.getValue("name");
			final String value = attributes.getValue("value");

			if (name != null) switch (name) {
			case "hibernate.connection.driver_class":
				driverClass = value;
				try {
					Class.forName(value);
				} catch (ClassNotFoundException ex) {
					throw new SAXException("SQL driver '" + value + "' could not be loaded.", ex);
				}
				break;
			case "hibernate.connection.url":
				connectionUrl = value;
				break;
			case "hibernate.connection.username":
				username = value;
				break;
			case "hibernate.connection.password":
				password = value;
				break;
			default:
				break;
			}
		}
	}

	public String getDriverClass() {
		return driverClass;
	}

	public String getConnectionUrl() {
		return connectionUrl;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
	
}
