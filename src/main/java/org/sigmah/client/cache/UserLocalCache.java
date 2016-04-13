package org.sigmah.client.cache;

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

import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.organization.OrganizationDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;

/**
 * Stores data widely used on client-side for the current user.
 *
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @deprecated [TO DELETE] use the command each time it's necessary.
 */
@Singleton
@Deprecated
public class UserLocalCache {

	/**
	 * The dispatcher.
	 */
	@Inject
	private DispatchAsync dispatch;
	
	/**
	 * Implementation of the RPC dispatch service.
	 */
	@Inject
	private LocalDispatchServiceAsync localDispatch;

	/**
	 * The authentication provider.
	 */
	@Inject
	private AuthenticationProvider authenticationProvider;
	
	/**
	 * Cache of the countries.
	 */
	private final LocalCachedCollection<CountryDTO> countries = new LocalCachedCollection<CountryDTO>();

	/**
	 * Cache of the users (for the current organization only).
	 */
	private final LocalCachedCollection<UserDTO> users = new LocalCachedCollection<UserDTO>();

	/**
	 * Cache of the organization.
	 */
	private final LocalCachedOrganization organization = new LocalCachedOrganization();

	/**
	 * Flag set to {@code true} once local client-side cache has been initialized.
	 */
	private boolean initialized;

	/**
	 * Gets the cache of the countries.
	 *
	 * @return The cache of the countries.
	 */
	public LocalCachedCollection<CountryDTO> getCountryCache() {
		return countries;
	}

	/**
	 * Gets the cache of the current organization members.
	 *
	 * @return The cache of the current organization members.
	 */
	public LocalCachedCollection<UserDTO> getUserCache() {
		return users;
	}

	/**
	 * Gets the cache of the current organization.
	 *
	 * @return The cache of the current organization.
	 */
	public LocalCachedOrganization getOrganizationCache() {
		return organization;
	}

	/**
	 * Initializes the local cache.<br>
	 * Does nothing if executed more than once.
	 */
	public void init() {

		if (authenticationProvider.isAnonymous()) {

			if (Log.isDebugEnabled()) {
				Log.debug("[init] Anonymous user ; clearing local cache data.");
			}

			countries.set(null);
			users.set(null);
			organization.set(null, null);

			initialized = false;
			return;
		}

		if (initialized) {
			if (Log.isDebugEnabled()) {
				Log.debug("[init] Local cache has already been initialized ; aborting initialization command.");
			}
			return;
		}

		if (Log.isDebugEnabled()) {
			Log.debug("[init] Initializes local cache.");
		}
		
		localDispatch.execute(new GetCountries(), new AsyncCallback<ListResult<CountryDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				// IndexedDB is unavailable or forbidden.
				loadCountriesFromServer();
			}

			@Override
			public void onSuccess(ListResult<CountryDTO> result) {
				if (result.isEmpty()) {
					loadCountriesFromServer();
				} else {
					countries.set(result.getList());
					
					Log.debug("[init] The local cache of the countries has been set from IndexedDB (" + result.getSize() + " countries cached).");
				}
			}
			
		});

		// Gets users list.
		dispatch.execute(new GetUsersByOrganization(authenticationProvider.get().getOrganizationId(), null), new CommandResultHandler<ListResult<UserDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				Log.error("[init] Error while getting the users list for the local cache.", e);
				users.set(null);
			}

			@Override
			public void onCommandSuccess(final ListResult<UserDTO> result) {

				final List<UserDTO> list = result.getList();
				users.set(list);

				if (Log.isDebugEnabled()) {
					Log.debug("[init] The cache of the users has been set (" + list.size() + " users cached).");
				}
			}
		});

		// Gets the organization.
		refreshOrganization(null);

		initialized = true;
	}

	/**
	 * Refreshes the cached {@code OrganizationDTO} and executes the given {@code callback} once refresh process is
	 * complete.
	 *
	 * @param callback
	 *          If not {@code null}, the callback is executed once {@code OrganizationDTO} has been loaded.
	 */
	public void refreshOrganization(final AsyncCallback<OrganizationDTO> callback) {

		final Integer organizationId = authenticationProvider.get().getOrganizationId();
		final Integer orgUnitId = authenticationProvider.get().getMainOrgUnitId();

		// Gets the organization.
		dispatch.execute(new GetOrganization(OrganizationDTO.Mode.WITH_ROOT, organizationId), new CommandResultHandler<OrganizationDTO>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				Log.error("[init] Error while getting the organization for the local cache.", e);
				if (callback != null) {
					callback.onFailure(e);
				}
			}

			@Override
			public void onCommandSuccess(final OrganizationDTO result) {

				organization.set(result, orgUnitId);

				if (Log.isDebugEnabled()) {
					Log.debug("[init] The cache of the organization has been set.");
				}

				if (callback != null) {
					callback.onSuccess(result);
				}
			}
		});
	}
	
	/**
	 * Send a request to load the country list from the server.
	 */
	private void loadCountriesFromServer() {
		// Gets countries list.
		dispatch.execute(new GetCountries(), new CommandResultHandler<ListResult<CountryDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				Log.error("[init] Error while getting the countries list for the local cache.", e);
				countries.set(null);
			}

			@Override
			public void onCommandSuccess(final ListResult<CountryDTO> result) {

				countries.set(result.getList());

				if (Log.isDebugEnabled()) {
					Log.debug("[init] The local cache of the countries has been set (" + result.getSize() + " countries cached).");
				}
			}
		});
	}

}
