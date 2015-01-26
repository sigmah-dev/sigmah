package org.sigmah.client.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTOLight;
import org.sigmah.shared.dto.OrganizationDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

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

        private final int id;
        private final AsyncCallback<OrgUnitDTOLight> callback;

        private OrgUnitAsyncCallback(int id, AsyncCallback<OrgUnitDTOLight> callback) {
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
    private int currentUserOrgUnitId;

    /**
     * Cache of the organizational units (for the current user only).
     */
    private final HashMap<Integer, OrgUnitDTOLight> mapOrgUnits;

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
        mapOrgUnits = new HashMap<Integer, OrgUnitDTOLight>();
        hasBeenSet = false;
        queueOrganization = new ArrayList<OrganizationAsyncCallback>();
        queueOrgUnit = new ArrayList<OrgUnitAsyncCallback>();
        queueUserOrgUnit = new ArrayList<OrgUnitAsyncCallback>();
    }

    /**
     * Gets the organization for the current user. If the cache isn't available
     * immediately, the callback will be called after the cache has been set by
     * the first server call.
     * 
     * @param callback
     *            The callback.
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
     * @return The organization if the cache has been set, <code>null</code>
     *         otherwise.
     */
    public OrganizationDTO getOrganization() {
        return organization;
    }

    /**
     * Gets the root organizational unit for the current user. If the cache
     * isn't available immediately, the callback will be called after the cache
     * has been set by the first server call.
     * 
     * @param callback
     *            The callback.
     */
    public void get(AsyncCallback<OrgUnitDTOLight> callback) {

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
     * Tries to get the root organizational unit of the current user without
     * waiting.
     * 
     * @return The organizational unit if the cache has been set,
     *         <code>null</code> otherwise.
     */
    public OrgUnitDTOLight get() {
        return get(currentUserOrgUnitId);
    }

    /**
     * Gets the an organizational unit. If the cache isn't available
     * immediately, the callback will be called after the cache has been set by
     * the first server call.
     * 
     * @param id
     *            The id.
     * @param callback
     *            The callback.
     */
    public void get(int id, AsyncCallback<OrgUnitDTOLight> callback) {

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
     *            The id.
     * @return The organizational unit if the cache has been set,
     *         <code>null</code> otherwise.
     */
    public OrgUnitDTOLight get(int id) {
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
     *            The root organizational unit for the current user.
     * @param organization
     *            The organization.
     */
    protected void set(OrganizationDTO organization, int currentUserOrgUnitId) {

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
     *            The map.
     * @param root
     *            The root organizational unit.
     */
    private static void crawlOrgUnit(Map<Integer, OrgUnitDTOLight> map, OrgUnitDTO root) {
        if (root != null) {
            map.put(root.getId(), root.light());
            if (root.getChildren() != null) {
                for (final OrgUnitDTO child : root.getChildren()) {
                    if (child.getDeleted() == null) {
                        crawlOrgUnit(map, child);
                    }
                }
            }
        }
    }
}
