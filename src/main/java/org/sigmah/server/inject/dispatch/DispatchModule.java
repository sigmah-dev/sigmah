package org.sigmah.server.inject.dispatch;

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

import org.sigmah.server.dispatch.CommandHandlerRegistry;
import org.sigmah.server.dispatch.Dispatch;
import org.sigmah.server.dispatch.impl.LazyCommandHandlerRegistry;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * This module will configure the implementation for the {@link Dispatch} and {@link CommandHandlerRegistry} interfaces.
 * If you want to override the defaults ({@link GuiceDispatch} and {@link DefaultCommandHandlerRegistry}, respectively),
 * pass the override values into the constructor for this module and ensure it is installed <b>before</b> any
 * {@link AbstractCommandHandlerModule} instances.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
final class DispatchModule extends AbstractModule {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(DispatchModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing dispatch module.");
		}

		bind(CommandHandlerRegistry.class).to(LazyCommandHandlerRegistry.class).in(Singleton.class);
		bind(Dispatch.class).to(UserDispatch.class).in(Singleton.class);

		// This will bind registered handlers to the registry.
		requestStaticInjection(CommandHandlerLinker.class);
	}

	/**
	 * Override so that only one instance of this class will ever be installed in an {@link Injector}.
	 */
	@Override
	public boolean equals(Object obj) {
		return obj instanceof DispatchModule;
	}

	/**
	 * Override so that only one instance of this class will ever be installed in an {@link Injector}.
	 */
	@Override
	public int hashCode() {
		return DispatchModule.class.hashCode();
	}

}
