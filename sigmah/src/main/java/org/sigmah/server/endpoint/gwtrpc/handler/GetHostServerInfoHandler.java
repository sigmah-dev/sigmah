/**
 * 
 */
package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.Properties;

import org.sigmah.shared.command.GetHostServerInfo;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.HostServerInfo;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

/**
 * @author HUZHE
 *
 */
public class GetHostServerInfoHandler implements CommandHandler<GetHostServerInfo>{

	
	//see main/resources/sigmah.properties and org/sigmah/server/ConfigModule
	private final Properties configProperties;
	
	//The key for reading the host url value from src/main/resources/sigmah.properties 
    final public static String KEY_HOST_URL= "host.url";
    
    //The default value if the key above does not exist in src/main/resources/sigmah.properties
    final public static String DEFAULT_HOST_URL= "unknown host url";
	

    @Inject
    public GetHostServerInfoHandler(Properties configProperties) {
        this.configProperties = configProperties;
    }

	
	@Override
	public CommandResult execute(GetHostServerInfo cmd, User user)
			throws CommandException {
		
		//Host url configured in properties file
		HostServerInfo info = new HostServerInfo();
		info.setHostUrl(configProperties.getProperty(KEY_HOST_URL,DEFAULT_HOST_URL));
				
			
		return info;
	}
	
	

}
