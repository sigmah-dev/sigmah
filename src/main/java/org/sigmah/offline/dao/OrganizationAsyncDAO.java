package org.sigmah.offline.dao;

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
import java.util.Collection;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.OrganizationJS;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>OrganizationDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OrganizationAsyncDAO extends AbstractUserDatabaseAsyncDAO<OrganizationDTO, OrganizationJS> {
	
	private final OrgUnitAsyncDAO orgUnitDAO;

	@Inject
	public OrganizationAsyncDAO(OrgUnitAsyncDAO orgUnitDAO) {
		this.orgUnitDAO = orgUnitDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganizationJS toJavaScriptObject(OrganizationDTO t) {
		return OrganizationJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrganizationDTO toJavaObject(OrganizationJS js) {
		return js.toDTO();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(final OrganizationDTO t, AsyncCallback<OrganizationDTO> callback, Transaction<Store> transaction) {
		// Saving organization
		super.saveOrUpdate(t, callback, transaction);
		
		// Saving its root
		final OrgUnitDTO rootOrgUnit = t.getRoot();
		if (rootOrgUnit != null) {
			orgUnitDAO.saveOrUpdate(rootOrgUnit, null, transaction);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<OrganizationDTO> callback, final Transaction<Store> transaction) {
		if(transaction.useObjectFromCache(OrganizationDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore organizationStore = transaction.getObjectStore(Store.ORGANIZATION);

		organizationStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final OrganizationJS organizationJS = request.getResult();
				if(organizationJS != null) {
					final OrganizationDTO organizationDTO = organizationJS.toDTO();
					transaction.getObjectCache().put(id, organizationDTO);

					orgUnitDAO.get(organizationJS.getRoot(), new AsyncCallback<OrgUnitDTO>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(OrgUnitDTO result) {
							organizationDTO.setRoot(result);
							callback.onSuccess(organizationDTO);
						}
					}, transaction);
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.ORGANIZATION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<BaseAsyncDAO<Store>> getDependencies() {
		final ArrayList<BaseAsyncDAO<Store>> list = new ArrayList<BaseAsyncDAO<Store>>();
		list.add(orgUnitDAO);
		return list;
	}
}
