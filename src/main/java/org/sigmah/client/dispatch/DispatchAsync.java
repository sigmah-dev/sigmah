package org.sigmah.client.dispatch;

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

import java.util.Collection;

import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.server.dispatch.Dispatch;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.Result;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * <p>
 * This is an asynchronous equivalent of the {@link Dispatch} interface on the server side.
 * </p>
 * <p>
 * The reason it exists is because GWT currently can't correctly handle having generic method templates in method
 * signatures, for example:
 * 
 * <pre>
 * &lt;C&gt; C create(Class&lt;C&gt; type)
 * </pre>
 * 
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface DispatchAsync {

	/**
	 * Executes asynchronously the given {@code command} and process the given {@code callback} once asynchronous
	 * execution is done.
	 * 
	 * @param command
	 *          The executed command.
	 * @param callback
	 *          The callback executed once asynchronous execution is done.
	 * @param loadables
	 *          (optional) Loadable element(s) to set in <em>loading</em> state during command execution.
	 */
	<C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback, final Loadable... loadables);

	/**
	 * Executes asynchronously the given {@code command} and process the given {@code callback} once asynchronous
	 * execution is done.
	 * 
	 * @param command
	 *          The executed command.
	 * @param callback
	 *          The callback executed once asynchronous execution is done.
	 * @param loadables
	 *          (optional) Loadable element(s) collection to set in <em>loading</em> state during command execution.
	 */
	<C extends Command<R>, R extends Result> void execute(final C command, final AsyncCallback<R> callback, final Collection<Loadable> loadables);

}
