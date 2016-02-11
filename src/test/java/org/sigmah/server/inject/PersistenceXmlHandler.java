package org.sigmah.server.inject;

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
