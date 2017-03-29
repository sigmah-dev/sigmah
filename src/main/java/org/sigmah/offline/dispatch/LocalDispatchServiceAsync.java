package org.sigmah.offline.dispatch;

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
import java.util.Map;

import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.security.SecureDispatchAsync;
import org.sigmah.client.security.SecureDispatchServiceAsync;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Result;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * Dispatches commands to local handlers. Primarily used when offline but can
 * be used to cache ressources.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class LocalDispatchServiceAsync implements SecureDispatchServiceAsync {
	
	public static final String LAST_USER_ITEM = "sigmah.last-user";
	
	/**
	 * Map associating a command type to its offline handler.
	 */
    private final Map<Class, AsyncCommandHandler> registry;
	/**
	 * Provides information about the current user.
	 */
	private final AuthenticationProvider authenticationProvider;
    
    public LocalDispatchServiceAsync(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
		this.registry = new HashMap<Class, AsyncCommandHandler>();
    }

    public <C extends Command<R>, R extends Result> void registerHandler(Class<C> commandClass, AsyncCommandHandler<C,R> handler) {
        registry.put(commandClass, handler);
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <C extends Command<R>, R extends Result> void execute(SecureDispatchAsync.CommandExecution<C, R> commandExecution, AsyncCallback<Result> callback) {
		final C command = commandExecution.getCommand();
		Log.info("Local " + command.getClass().getName() + " is pending...");
		
		final AsyncCommandHandler handler = getHandler(command);
		
		if(handler == null) {
			callback.onFailure(new UnavailableCommandException("No handler is registered for command '" + command.getClass().getName() + "'."));
			
		} else {
			final OfflineExecutionContext executionContext = new OfflineExecutionContext(getAuthentication());
			handler.execute(command, executionContext, callback);
		}
	}
	
	/**
	 * Executes the given {@code commandExecution} corresponding {@link Command} and executes the given {@code callback}
	 * once command has been processed.
	 * 
	 * This method is thought to be executable online.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @param command
	 *          The {@link Command} to execute.
	 * @param callback
	 *          The callback executed once command has been processed. 
	 */
	public <C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback) {
		getHandler(command).execute(command, new OfflineExecutionContext(getAuthentication()), callback);
	}
    
	/**
	 * Retrieves the handler associated with the given command.
	 * 
	 * @param <C> Command type.
	 * @param <R> Result type.
	 * @param c Command to execute.
	 * @return The associated handler or <code>null</code> if not found.
	 */
    private <C extends Command<R>, R extends Result> AsyncCommandHandler<C, R> getHandler(C c) {
        final AsyncCommandHandler<C, R> handler = registry.get(c.getClass());
        return handler;
    }
	
	/**
	 * Retrieves the current authentication.
	 * 
	 * @return The current authentication.
	 */
	private Authentication getAuthentication() {
		final Authentication authentication = authenticationProvider.get();
			
		if (authentication.getUserEmail() == null) {
			// Search the last logged user in the users database
			final Storage storage = Storage.getLocalStorageIfSupported();
			final String email = storage.getItem(LAST_USER_ITEM);

			authentication.setUserEmail(email);
		}

		return authentication;
	}
	
}
