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
import java.util.ArrayList;
import java.util.List;
import org.sigmah.offline.event.JavaScriptEvent;

/**
 * Open request done on an already opened database.
 *
 * Reuse the given database instance instead of creating a new connection.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <S> Type defining the schema of the database.
 */
public class AlreadyOpenedDatabaseRequest<S extends Enum<S> & Schema> implements OpenDatabaseRequest<S> {
    
    private final List<JavaScriptEvent<?>> handlers = new ArrayList<JavaScriptEvent<?>>();
    private Database<S> result;
    
	/**
	 * Creates an empty request. This is useful if an open request is pending.
	 */
    public AlreadyOpenedDatabaseRequest() {
    }

	/**
	 * Creates a request that will immediatly returns the given database.
	 * 
	 * @param database An already opened database.
	 */
    public AlreadyOpenedDatabaseRequest(Database<S> database) {
        this.result = database;
    }
    
	/**
	 * {@inheritDoc}
	 */
    @Override
    public Database<S> getResult() {
        return result;
    }

	/**
	 * Defines the database to use. 
	 * This will trigger the success handler.
	 * 
	 * @param result An already opened database.
	 */
    public void setResult(Database<S> result) {
        this.result = result;
        for(final JavaScriptEvent<?> handler : handlers) {
            handler.onEvent(null);
        }
    }
    
	/**
	 * {@inheritDoc}
	 */
    @Override
    public void addSuccessHandler(JavaScriptEvent<?> handler) {
        if(result != null) {
            handler.onEvent(null);
        } else {
            handlers.add(handler);
        }
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void addCallback(AsyncCallback<Request> callback) {
        throw new UnsupportedOperationException("Not supported.");
    }
    
}
