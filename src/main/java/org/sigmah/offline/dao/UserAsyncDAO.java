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
import org.sigmah.offline.js.Values;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UserAsyncDAO extends AbstractAsyncDAO<UserDTO> {

	private final OrgUnitAsyncDAO orgUnitDAO;

	@Inject
	public UserAsyncDAO(OrgUnitAsyncDAO orgUnitDAO) {
		this.orgUnitDAO = orgUnitDAO;
	}
	
	@Override
	public void saveOrUpdate(UserDTO t, AsyncCallback<UserDTO> callback, Transaction transaction) {
		final ObjectStore userObjectStore = transaction.getObjectStore(Store.USER);
		
		final UserJS userJS = UserJS.toJavaScript(t);
		userObjectStore.put(userJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving user " + userJS.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("User " + userJS.getId() + " has been successfully saved.");
            }
        });
		
		// Saving its org unit
		if(Values.isDefined(userJS, UserDTO.ORG_UNIT)) {
			orgUnitDAO.saveOrUpdate(t.getOrgUnit(), null, transaction);
		}
	}
	
	public void saveOrUpdate(final ListResult<UserDTO> userListResult, final Integer organizationId) {
		if(userListResult != null && userListResult.getList() != null) {
            openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

                @Override
                public void onTransaction(Transaction transaction) {
                    for(final UserDTO userDTO : userListResult.getList()) {
						userDTO.set("organization", organizationId);
						saveOrUpdate(userDTO, null, transaction);
					}
                }
            });
		}
	}

	@Override
	public void get(final int id, final AsyncCallback<UserDTO> callback, final Transaction transaction) {
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
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(final Transaction transaction) {
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

	@Override
	public Store getRequiredStore() {
		return Store.USER;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> list = new ArrayList<BaseAsyncDAO>();
		list.add(orgUnitDAO);
		return list;
	}
}
