package org.sigmah.client.ui.presenter.contact.dashboardlist;

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

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.dispatch.monitor.ProgressMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.view.contact.dashboardlist.DashboardContact;
import org.sigmah.shared.command.GetContactHistory;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;

/**
 * Represents a worker which get contacts chunk by chunk.
 */
final class GetContactsWorker {

	/**
	 * Receives the worker events.
	 */
	public static interface WorkerListener {

		/**
		 * Method called if a server error occurs.
		 *
		 * @param error
		 *          The error.
		 */
		void serverError(Throwable error);

		/**
		 * Method called when a chunk is retrieved.
		 *
		 * @param contact
		 *          The chunk.
		 */
		void chunkRetrieved(DashboardContact contact);

		/**
		 * Method called after the last chunk has been retrieved.
		 */
		void ended();

	}

	/**
	 * The dispatcher.
	 */
	private final DispatchAsync dispatch;

	/**
	 * The {@link GetContacts} command to execute.
	 */
	private final GetContacts cmd;

	/**
	 * The component to mask while the worker runs.
	 */
	private final Component component;

	/**
	 * Listeners.
	 */
	private final ArrayList<WorkerListener> listeners;

	/**
	 * The list of contacts to retrieve.
	 */
	private List<ContactDTO> contactsList;

	/**
	 * The number of contacts to retrieve.
	 */
	private int contactsSize;

	/**
	 * The async monitor.
	 */
	private ProgressMask monitor;

	/**
	 * Builds a new worker.
	 *
	 * @param dispatch
	 *          The dispatcher.
	 * @param cmd
	 *          The {@link GetContacts} command to execute.
	 * @param component
	 *          The component to mask while the worker runs.
	 */
	public GetContactsWorker(final DispatchAsync dispatch, final GetContacts cmd, final Component component) {
		assert dispatch != null;
		assert cmd != null;
		assert component != null;
		this.dispatch = dispatch;
		this.cmd = cmd;
		this.component = component;
		this.listeners = new ArrayList<WorkerListener>();
	}

	/**
	 * Adds a listener to this worker.
	 * 
	 * @param listener
	 *          The new listener.
	 */
	public void addWorkerListener(final WorkerListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Runs the worker.
	 */
	public void run() {

		monitor = new ProgressMask(component, I18N.CONSTANTS.refreshContactListContactLoaded());

		// First call to get the list of contacts.

		dispatch.execute(cmd, new CommandResultHandler<ListResult<ContactDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("[GetContacts command] Error while getting contacts.", e);
				}
				monitor.initCounter(0);
				fireServerError(e);
			}

			@Override
			public void onCommandSuccess(final ListResult<ContactDTO> result) {

				// Retrieves contacts by chunks.
				if (!result.isEmpty()) {
					contactsList = result.getList();
					contactsSize = contactsList.size();
					monitor.initCounter(contactsSize);
					chunk();

				} else {
					monitor.initCounter(0);
				}
			}
		}, new LoadingMask(component));
	}

	private void chunk() {

		// No more contact to get.
		if (contactsList.isEmpty()) {
			fireEnded();
			return;
		}

		// Store the next ids to retrieve.
		final ContactDTO nextContact = contactsList.remove(0);

		// Retrieves these contacts.
		dispatch.execute(new GetContactHistory(nextContact.getId()), new CommandResultHandler<ListResult<ContactHistory>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("[GetContactHistory command] Error while getting contact history.", e);
				}
				monitor.increment(contactsSize);
				fireServerError(e);
			}

			@Override
			public void onCommandSuccess(final ListResult<ContactHistory> result) {

				// Updates the monitor.
				monitor.increment(1);

				// Fires event.
				ContactHistory lastChange = result.isEmpty() ? new ContactHistory() : result.getList().get(0);
				DashboardContact contact = new DashboardContact(nextContact, lastChange);
				fireChunkRetrieved(contact);

				// Next chunk.
				chunk();
			}
		}, monitor);
	}

	/**
	 * Method called if a server error occurs.
	 * 
	 * @param error
	 *          The error.
	 */
	protected void fireServerError(final Throwable error) {
		for (final WorkerListener listener : listeners) {
			listener.serverError(error);
		}
	}

	/**
	 * Method called when a chunk is retrieved.
	 * 
	 * @param contact
	 *          The chunk.
	 */
	protected void fireChunkRetrieved(final DashboardContact contact) {
		for (final WorkerListener listener : listeners) {
			listener.chunkRetrieved(contact);
		}
	}

	/**
	 * Method called after the last chunk has been retrieved.
	 */
	protected void fireEnded() {
		for (final WorkerListener listener : listeners) {
			listener.ended();
		}
	}
}
