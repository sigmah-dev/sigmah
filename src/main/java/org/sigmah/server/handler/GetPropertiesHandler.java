package org.sigmah.server.handler;

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

import java.util.HashMap;

import org.sigmah.server.conf.Properties;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProperties;
import org.sigmah.shared.command.result.MapResult;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.dispatch.CommandException;

import com.google.inject.Inject;

/**
 * Gets application properties.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class GetPropertiesHandler extends AbstractCommandHandler<GetProperties, MapResult<PropertyKey, String>> {

	private final Properties properties;

	@Inject
	public GetPropertiesHandler(Properties properties) {
		this.properties = properties;
	}

	/**
	 * {@inheritDoc}
	 */
	public MapResult<PropertyKey, String> execute(final GetProperties cmd, final UserExecutionContext context) throws CommandException {

		final MapResult<PropertyKey, String> map = new MapResult<PropertyKey, String>(new HashMap<PropertyKey, String>());

		if (cmd.getKeys() != null && cmd.getKeys().length > 0) {
			for (final PropertyKey key : cmd.getKeys()) {
				map.getMap().put(key, properties.getProperty(key));
			}
		}

		return map;

	}

}
