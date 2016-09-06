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
 * Empty request. No operation will be done.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class NoopDatabaseRequest<S extends Enum<S> & Schema> implements OpenDatabaseRequest<S> {

	/**
	 * {@inheritDoc}
	 */
    @Override
    public Database<S> getResult() {
        return null;
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void addSuccessHandler(final JavaScriptEvent<?> handler) {
		// No operation.
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void addCallback(final AsyncCallback<Request> callback) {
		// No operation.
    }
    
}
