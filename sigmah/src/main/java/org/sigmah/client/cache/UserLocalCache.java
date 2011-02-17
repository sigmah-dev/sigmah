package org.sigmah.client.cache;

import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.CountryResult;
import org.sigmah.shared.command.result.UserListResult;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.dto.UserDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Stores data widely used on client-side for the current user.
 * 
 * @author tmi
 * 
 */
@Singleton
public class UserLocalCache {

    /**
     * The dispatcher.
     */
    private final Dispatcher dispatcher;

    /**
     * The authentication.
     */
    private final Authentication authentication;

    /**
     * Cache of the countries.
     */
    private LocalCachedCollection<CountryDTO> countries;

    /**
     * Cache of the users (for the current organization only).
     */
    private LocalCachedCollection<UserDTO> users;

    /**
     * Cache of the organization.
     */
    private LocalCachedOrganization organization;

    @Inject
    public UserLocalCache(final Dispatcher dispatcher, final Authentication authentication) {
        this.dispatcher = dispatcher;
        this.authentication = authentication;
        countries = new LocalCachedCollection<CountryDTO>();
        users = new LocalCachedCollection<UserDTO>();
        organization = new LocalCachedOrganization();
    }

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
     * Initializes the local cache.
     */
    public void init() {

        if (Log.isDebugEnabled()) {
            Log.debug("[init] Initializes local cache.");
        }

        // Gets countries list.
        dispatcher.execute(new GetCountries(), null, new AsyncCallback<CountryResult>() {

            @Override
            public void onFailure(Throwable e) {
                Log.error("[init] Error while getting the countries list for the local cache.", e);
                countries.set(null);
            }

            @Override
            public void onSuccess(CountryResult result) {

                final List<CountryDTO> list = result.getData();
                countries.set(list);

                if (Log.isDebugEnabled()) {
                    Log.debug("[init] The local cache of the countries has been set (" + list.size()
                            + " countries cached).");
                }
            }
        });

        // Gets users list.
        dispatcher.execute(new GetUsersByOrganization(authentication.getOrganizationId()), null,
                new AsyncCallback<UserListResult>() {

                    @Override
                    public void onFailure(Throwable e) {
                        Log.error("[init] Error while getting the users list for the local cache.", e);
                        users.set(null);
                    }

                    @Override
                    public void onSuccess(UserListResult result) {

                        final List<UserDTO> list = result.getList();
                        users.set(list);

                        if (Log.isDebugEnabled()) {
                            Log.debug("[init] The cache of the users has been set (" + list.size() + " users cached).");
                        }
                    }
                });

        // Gets the organization.
        dispatcher.execute(new GetOrganization(authentication.getOrganizationId()), null,
                new AsyncCallback<OrganizationDTO>() {

                    @Override
                    public void onFailure(Throwable e) {
                        Log.error("[init] Error while getting the organization for the local cache.", e);
                    }

                    @Override
                    public void onSuccess(OrganizationDTO result) {

                        organization.set(result, authentication.getOrgUnitId());

                        if (Log.isDebugEnabled()) {
                            Log.debug("[init] The cache of the organization has been set.");
                        }
                    }
                });
    }

}
