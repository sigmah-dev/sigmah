package org.sigmah.offline.indexeddb;

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Define a request to open an IndexedDB database.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface OpenDatabaseRequest<S extends Enum<S> & Schema> {
	/**
	 * Retrieve the opened database instance.
	 * <p/>
	 * Returned value is null during the opening.
	 * 
	 * @return Instance of the opened database or <code>null</code> while loading.
	 */
    Database<S> getResult();
	
	/**
	 * Adds an handler that will be called when the database is opened.
	 * 
	 * @param handler The handler to add.
	 */
    void addSuccessHandler(JavaScriptEvent handler);
	
	/**
	 * Adds a callback that will be called when the database is opened or if an
	 * error occurs during the operation.
	 * 
	 * @param callback Callback to add.
	 */
    void addCallback(AsyncCallback<Request> callback);
	
}
