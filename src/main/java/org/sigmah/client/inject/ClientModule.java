package org.sigmah.client.inject;

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
		
		//possibly later, also other search related classes, like results view
		//bind(SearchPresenter.class).in(Singleton.class);
		
	}

}
