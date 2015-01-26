package org.sigmah.server.handler;

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
