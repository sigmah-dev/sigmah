package org.sigmah.client.inject;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.ExceptionHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.EventBusImpl;
import org.sigmah.client.page.PageManager;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.security.SecureDispatchAsync;
import org.sigmah.client.security.SecureExceptionHandler;
import org.sigmah.client.ui.theme.SigmahTheme;
import org.sigmah.client.ui.theme.Theme;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import org.sigmah.shared.dto.pivot.content.GXTStateManager;
import org.sigmah.shared.dto.pivot.content.IStateManager;

/**
 * GIN module to bind presenters and views.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ClientModule extends AbstractGinModule {

	@Override
	protected void configure() {

		// Navigation.
		bind(EventBus.class).to(EventBusImpl.class).in(Singleton.class);
		bind(PageManager.class).in(Singleton.class);

		// Theming.
		bind(Theme.class).to(SigmahTheme.class).in(Singleton.class);

		// Presenters rely on "@ImplementedBy" annotation on their view interface.

		// Dispatch & security.
		bind(AuthenticationProvider.class).in(Singleton.class);
		bind(ExceptionHandler.class).to(SecureExceptionHandler.class).in(Singleton.class);
		bind(DispatchAsync.class).to(SecureDispatchAsync.class).in(Singleton.class);

		// StateManager (for indicators).
		bind(IStateManager.class).to(GXTStateManager.class);
		
	}

}
