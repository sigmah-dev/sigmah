package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.UserJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>UserDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UserAsyncDAO extends AbstractUserDatabaseAsyncDAO<UserDTO, UserJS> {

	private final OrgUnitAsyncDAO orgUnitDAO;

	@Inject
	public UserAsyncDAO(OrgUnitAsyncDAO orgUnitDAO) {
		this.orgUnitDAO = orgUnitDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(UserDTO t, AsyncCallback<UserDTO> callback, Transaction<Store> transaction) {
		super.saveOrUpdate(t, callback, transaction);
		
		// Saving its org unit
		if (t.getOrgUnit() != null) {
			orgUnitDAO.saveOrUpdate(t.getOrgUnit(), null, transaction);
		}
	}
	
	public void saveAll(final ListResult<UserDTO> userListResult, final Integer organizationId) {
		if(userListResult != null && userListResult.getList() != null) {
            openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

                @Override
                public void onTransaction(Transaction<Store> transaction) {
                    for(final UserDTO userDTO : userListResult.getList()) {
						userDTO.set("organization", organizationId);
						saveOrUpdate(userDTO, null, transaction);
					}
                }
            });
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<UserDTO> callback, final Transaction<Store> transaction) {
		if(transaction.useObjectFromCache(UserDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore userObjectStore = transaction.getObjectStore(Store.USER);
		
		userObjectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final UserJS userJS = request.getResult();
				if(userJS != null) {
					final UserDTO userDTO = userJS.toDTO();
					transaction.getObjectCache().put(id, userDTO);

					orgUnitDAO.get(userJS.getOrgUnit(), new AsyncCallback<OrgUnitDTO>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.error("Error while retrieving org unit " + userJS.getOrgUnit() + " for user " + userJS.getId());
						}

						@Override
						public void onSuccess(OrgUnitDTO result) {
							userDTO.setOrgUnit(result);
							callback.onSuccess(userDTO);
						}
					}, transaction);
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}
	
	public void getByOrganization(final int organizationId, final AsyncCallback<ListResult<UserDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(final Transaction<Store> transaction) {
                final ObjectStore userObjectStore = transaction.getObjectStore(Store.USER);
				final Index organizationIndex = userObjectStore.index("organization");
				final OpenCursorRequest openCursorRequest = organizationIndex.openCursor(IDBKeyRange.only(organizationId));
				
				final ArrayList<UserDTO> users = new ArrayList<UserDTO>();
				
                openCursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = openCursorRequest.getResult();
						if(cursor != null) {
							final UserJS userJS = (UserJS) cursor.getValue();
							final UserDTO userDTO = userJS.toDTO();
							
							if(userJS.hasOrgUnit()) {
								orgUnitDAO.get(userJS.getOrgUnit(), new AsyncCallback<OrgUnitDTO>() {
									@Override
									public void onFailure(Throwable caught) {
										Log.error("Error while retrieving org unit " + userJS.getOrgUnit() + " for user " + userJS.getId());
									}

									@Override
									public void onSuccess(OrgUnitDTO result) {
										userDTO.setOrgUnit(result);
									}
								}, transaction);
							}
							
							users.add(userDTO);
							cursor.next();
							
						} else {
							callback.onSuccess(new ListResult<UserDTO>(users));
						}
                    }
                });
            }
        });
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.USER;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserJS toJavaScriptObject(UserDTO t) {
		return UserJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserDTO toJavaObject(UserJS js) {
		return js.toDTO();
	}
	
}
