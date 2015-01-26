package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.MapResult;
import org.sigmah.shared.conf.PropertyKey;

/**
 * Gets application properties.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class GetProperties extends AbstractCommand<MapResult<PropertyKey, String>> {

	private PropertyKey[] keys;

	public GetProperties() {
		// Serialization.
	}

	public GetProperties(PropertyKey... keys) {
		this.keys = keys;
	}

	public PropertyKey[] getKeys() {
		return keys;
	}

}
