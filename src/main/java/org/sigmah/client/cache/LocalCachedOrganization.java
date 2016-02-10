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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Local cached organization.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LocalCachedOrganization {

	/**
	 * Job to get the organization.
	 * 
	 * @author tmi
	 */
	private static final class OrganizationAsyncCallback {

		private final AsyncCallback<OrganizationDTO> callback;

		private OrganizationAsyncCallback(AsyncCallback<OrganizationDTO> callback) {
			this.callback = callback;
		}
	}

	/**
	 * Job to get an organizational unit.
	 * 
	 * @author tmi
	 */
	private static final class OrgUnitAsyncCallback {

		private final Integer id;
		private final AsyncCallback<OrgUnitDTO> callback;

		private OrgUnitAsyncCallback(Integer id, AsyncCallback<OrgUnitDTO> callback) {
			this.id = id;
			this.callback = callback;
		}
	}

	/**
	 * Cache of the organization.
	 */
	private OrganizationDTO organization;

	/**
	 * The current user root organizational unit id.
	 */
	private Integer currentUserOrgUnitId;

	/**
	 * Cache of the organizational units (for the current user only).
	 */
	private final HashMap<Integer, OrgUnitDTO> mapOrgUnits;

	/**
	 * If the cache has been set.
	 */
	private boolean hasBeenSet;

	/**
	 * Waiting jobs to get the organization instance.
	 */
	private final ArrayList<OrganizationAsyncCallback> queueOrganization;

	/**
	 * Waiting jobs to get an org unit instance.
	 */
	private final ArrayList<OrgUnitAsyncCallback> queueOrgUnit;

	/**
	 * Waiting jobs to get the root org unit instance for the current user.
	 */
	private final ArrayList<OrgUnitAsyncCallback> queueUserOrgUnit;

	public LocalCachedOrganization() {
		mapOrgUnits = new HashMap<Integer, OrgUnitDTO>();
		hasBeenSet = false;
		queueOrganization = new ArrayList<OrganizationAsyncCallback>();
		queueOrgUnit = new ArrayList<OrgUnitAsyncCallback>();
		queueUserOrgUnit = new ArrayList<OrgUnitAsyncCallback>();
	}

	/**
	 * Gets the organization for the current user. If the cache isn't available immediately, the callback will be called
	 * after the cache has been set by the first server call.
	 * 
	 * @param callback
	 *          The callback.
	 */
	public void getOrganization(AsyncCallback<OrganizationDTO> callback) {

		// If the cache is available, returns it immediately.
		if (hasBeenSet) {
			callback.onSuccess(organization);
		}
		// Else put the callback in queue to be called later.
		else {
			queueOrganization.add(new OrganizationAsyncCallback(callback));
		}
	}

	/**
	 * Tries to get the organization of the current user without waiting.
	 * 
	 * @return The organization if the cache has been set, <code>null</code> otherwise.
	 */
	public OrganizationDTO getOrganization() {
		return organization;
	}

	/**
	 * Gets the root organizational unit for the current user. If the cache isn't available immediately, the callback will
	 * be called after the cache has been set by the first server call.
	 * 
	 * @param callback
	 *          The callback.
	 */
	public void get(AsyncCallback<OrgUnitDTO> callback) {

		// If the cache is available, returns it immediately.
		if (hasBeenSet) {
			callback.onSuccess(mapOrgUnits.get(currentUserOrgUnitId));
		}
		// Else put the callback in queue to be called later.
		else {
			queueUserOrgUnit.add(new OrgUnitAsyncCallback(currentUserOrgUnitId, callback));
		}
	}

	/**
	 * Tries to get the root organizational unit of the current user without waiting.
	 * 
	 * @return The organizational unit if the cache has been set, <code>null</code> otherwise.
	 */
	public OrgUnitDTO get() {
		return get(currentUserOrgUnitId);
	}

	/**
	 * Gets the an organizational unit. If the cache isn't available immediately, the callback will be called after the
	 * cache has been set by the first server call.
	 * 
	 * @param id
	 *          The id.
	 * @param callback
	 *          The callback.
	 */
	public void get(Integer id, AsyncCallback<OrgUnitDTO> callback) {
		
		// If the requested id is null, returns immediatly a null result.
		if(id == null) {
			callback.onSuccess(null);
		}

		// If the cache is available, returns it immediately.
		if (hasBeenSet) {
			callback.onSuccess(mapOrgUnits.get(id));
		}
		// Else put the callback in queue to be called later.
		else {
			queueOrgUnit.add(new OrgUnitAsyncCallback(id, callback));
		}
	}

	/**
	 * Tries to get an organizational unit without waiting.
	 * 
	 * @param id
	 *          The id.
	 * @return The organizational unit if the cache has been set, <code>null</code> otherwise.
	 */
	public OrgUnitDTO get(Integer id) {
		if (hasBeenSet) {
			return mapOrgUnits.get(id);
		} else {
			return null;
		}
	}

	/**
	 * Sets the organization and call all waiting jobs.
	 * 
	 * @param currentUserOrgUnitId
	 *          The root organizational unit for the current user.
	 * @param organization
	 *          The organization.
	 */
	protected void set(OrganizationDTO organization, Integer currentUserOrgUnitId) {

		// // This method is called once.
		// if (hasBeenSet) {
		// return;
		// }

		this.currentUserOrgUnitId = currentUserOrgUnitId;
		this.organization = organization;

		// Stores entities.
		if (organization != null) {
			crawlOrgUnit(mapOrgUnits, organization.getRoot());
		}

		// Calls the waiting jobs.
		for (final OrganizationAsyncCallback job : queueOrganization) {
			job.callback.onSuccess(organization);
		}

		for (final OrgUnitAsyncCallback job : queueOrgUnit) {
			job.callback.onSuccess(mapOrgUnits.get(job.id));
		}

		for (final OrgUnitAsyncCallback job : queueUserOrgUnit) {
			job.callback.onSuccess(mapOrgUnits.get(currentUserOrgUnitId));
		}

		// Clears the queues.
		queueOrganization.clear();
		queueOrgUnit.clear();
		queueUserOrgUnit.clear();

		hasBeenSet = true;
	}

	/**
	 * Crawls among the organizational units tree and store them in the map.
	 * 
	 * @param map
	 *          The map.
	 * @param root
	 *          The root organizational unit.
	 */
	private static void crawlOrgUnit(Map<Integer, OrgUnitDTO> map, OrgUnitDTO root) {

		if (root == null) {
			return;
		}

		map.put(root.getId(), root);

		if (root.getChildren() == null) {
			return;
		}

		for (final OrgUnitDTO child : root.getChildrenOrgUnits()) {
			if (child.getDeleted() == null) {
				crawlOrgUnit(map, child);
			}
		}
	}

}
