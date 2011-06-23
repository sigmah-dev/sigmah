package org.sigmah.server.endpoint.account;

import com.google.inject.servlet.ServletModule;

/** 
 * Defines web services that can be used to register new users /
 * organizations.
 * 
 * @author alexander
 *
 */
public class AccountModule extends ServletModule {

	@Override
	protected void configureServlets() {
		serve(SignupServlet.END_POINT).with(SignupServlet.class);
	}

	
}
