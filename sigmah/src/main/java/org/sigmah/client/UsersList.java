package org.sigmah.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.sigmah.shared.dto.UserDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * Utility class to cache the users list of an organization on the client-side.
 * 
 * @author tmi
 * 
 */
@Singleton
public class UsersList {

    private static final class UserAsyncCallback {

        private final int id;
        private final AsyncCallback<UserDTO> callback;

        private UserAsyncCallback(int id, AsyncCallback<UserDTO> callback) {
            this.id = id;
            this.callback = callback;
        }
    }

    private static final class UsersAsyncCallback {

        private final AsyncCallback<List<UserDTO>> callback;

        private UsersAsyncCallback(AsyncCallback<List<UserDTO>> callback) {
            this.callback = callback;
        }
    }

    /**
     * Users list.
     */
    private final HashMap<Integer, UserDTO> usersMap;
    private final ArrayList<UserDTO> usersList;
    private boolean usersHaveBeenSet;

    private final ArrayList<UserAsyncCallback> queueUser;
    private final ArrayList<UsersAsyncCallback> queueUsers;

    public UsersList() {
        usersMap = new HashMap<Integer, UserDTO>();
        usersList = new ArrayList<UserDTO>();
        usersHaveBeenSet = false;
        queueUser = new ArrayList<UserAsyncCallback>();
        queueUsers = new ArrayList<UsersAsyncCallback>();
    }

    private UserDTO getInternalUser(int id) {
        return usersMap.get(id);
    }

    private List<UserDTO> getInternalUsers() {
        return usersList;
    }

    /**
     * Tries to get a user without waiting.
     * 
     * @param id
     *            The user id.
     * @return The user if the cache has been set, <code>null</code> otherwise.
     */
    public UserDTO getUser(int id) {

        if (usersHaveBeenSet) {
            return getInternalUser(id);
        } else {
            return null;
        }
    }

    /**
     * Gets the user with the given id. If the users list isn't available
     * immediately, the callback will be called after the list has been set by
     * the first server call.
     * 
     * @param id
     *            The user id.
     * @param callback
     *            The callback.
     */
    public void getUser(int id, AsyncCallback<UserDTO> callback) {

        // If the users list is available, returns the user immediately.
        if (usersHaveBeenSet) {
            callback.onSuccess(getInternalUser(id));
        }
        // Else put the callback in queue to be called later.
        else {
            queueUser.add(new UserAsyncCallback(id, callback));
        }
    }

    /**
     * Gets the users list with the given id. If the users list isn't available
     * immediately, the callback will be called after the list has been set by
     * the first server call.
     * 
     * @param callback
     *            The callback.
     */
    public void getUsers(AsyncCallback<List<UserDTO>> callback) {

        // If the users list is available, returns the list immediately.
        if (usersHaveBeenSet) {
            callback.onSuccess(getInternalUsers());
        }
        // Else put the callback in queue to be called later.
        else {
            queueUsers.add(new UsersAsyncCallback(callback));
        }
    }

    /**
     * Sets the users list and call all waiting jobs.
     * 
     * @param users
     *            The users list.
     */
    public void setCountries(List<UserDTO> users) {

        usersList.addAll(users);
        for (final UserDTO user : users) {
            this.usersMap.put(user.getId(), user);
        }

        for (final UserAsyncCallback pair : queueUser) {
            pair.callback.onSuccess(getInternalUser(pair.id));
        }

        for (final UsersAsyncCallback pair : queueUsers) {
            pair.callback.onSuccess(getInternalUsers());
        }

        // Clears the queues.
        queueUser.clear();
        queueUsers.clear();

        usersHaveBeenSet = true;
    }
}
