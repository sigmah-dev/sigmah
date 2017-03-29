package org.sigmah.client.inject;

/**
 * GIN module to bind presenters and views.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ClientModule {

	
	protected void configure() {

		// Navigation.
//		bind(EventBus.class).to(EventBusImpl.class).in(Singleton.class);
//		bind(PageManager.class).in(Singleton.class);
//
//		// Theming.
//		bind(Theme.class).to(SigmahTheme.class).in(Singleton.class);
//
//		// Presenters rely on "@ImplementedBy" annotation on their view interface.
//
//		// Dispatch & security.
//		bind(AuthenticationProvider.class).in(Singleton.class);
//		bind(ExceptionHandler.class).to(SecureExceptionHandler.class).in(Singleton.class);
//		bind(DispatchAsync.class).to(SecureDispatchAsync.class).in(Singleton.class);
//
//		// StateManager (for indicators).
//		bind(IStateManager.class).to(GXTStateManager.class);
		
	}

}
