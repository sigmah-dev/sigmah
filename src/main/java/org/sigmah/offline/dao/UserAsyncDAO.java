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
import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.sigmah.offline.indexeddb.*;
import org.sigmah.offline.js.UserJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;

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
	public void saveOrUpdate(UserDTO userDTO, AsyncCallback<UserDTO> callback, Transaction<Store> transaction) {
		super.saveOrUpdate(userDTO, callback, transaction);
		
		// Saving its org unit

		if (userDTO.getMainOrgUnit() != null) {
			orgUnitDAO.saveOrUpdate(userDTO.getMainOrgUnit(), null, transaction);
		}
		if (userDTO.getSecondaryOrgUnits() != null) {
			for (OrgUnitDTO orgUnit : userDTO.getSecondaryOrgUnits()) {
				orgUnitDAO.saveOrUpdate(orgUnit, null, transaction);
			}
		}
	}
	
	/**
	 * Open a new transaction and save or replace the given objects.
	 * 
	 * @param userListResult
	 *			<code>ListResult</code> of the <code>UserDTO</code> objects to save or update.
	 * @param organizationId 
	 *			Identifier of the parent organization.
	 */
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

					orgUnitDAO.get(userJS.getMainOrgUnit(), new AsyncCallback<OrgUnitDTO>() {
						@Override
						public void onFailure(Throwable caught) {
							Log.error("Error while retrieving org unit " + userJS.getMainOrgUnit() + " for user " + userJS.getId());
						}

						@Override
						public void onSuccess(OrgUnitDTO result) {
							userDTO.setMainOrgUnit(result);
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
							
							if (userJS.hasMainOrgUnit()) {
								orgUnitDAO.get(userJS.getMainOrgUnit(), new AsyncCallback<OrgUnitDTO>() {
									@Override
									public void onFailure(Throwable caught) {
										Log.error("Error while retrieving org unit " + userJS.getMainOrgUnit() + " for user " + userJS.getId());
									}

									@Override
									public void onSuccess(OrgUnitDTO result) {
										userDTO.setMainOrgUnit(result);
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

	public void getByOrgUnit(final int orgUnitId, final AsyncCallback<ListResult<UserDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(final Transaction<Store> transaction) {
				final ObjectStore userObjectStore = transaction.getObjectStore(Store.USER);
				final Index orgUnitIndex = userObjectStore.index("orgUnit");
				final OpenCursorRequest openCursorRequest = orgUnitIndex.openCursor(IDBKeyRange.only(orgUnitId));

				final ArrayList<UserDTO> users = new ArrayList<UserDTO>();

				openCursorRequest.addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request request) {
						final Cursor cursor = openCursorRequest.getResult();
						if (cursor != null) {
							final UserJS userJS = (UserJS) cursor.getValue();
							users.add(userJS.toDTO());
							cursor.next();

						} else {
							callback.onSuccess(new ListResult<UserDTO>(users));
						}
					}
				});
			}
		});
	}

	public void getByOrgUnits(final Set<Integer> orgUnitIds, final AsyncCallback<ListResult<UserDTO>> callback) {
		final List<UserDTO> users = new ArrayList<UserDTO>();
		// `i` needs to be final as it is accessed asynchronously
		final int[] i = {0};
		for (final Integer orgUnitId : orgUnitIds) {
			getByOrgUnit(orgUnitId, new AsyncCallback<ListResult<UserDTO>>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(ListResult<UserDTO> userDTOListResult) {
					users.addAll(userDTOListResult.getData());
					i[0]++;

					if (i[0] == orgUnitIds.size()) {
						callback.onSuccess(new ListResult<UserDTO>(users));
					}
				}
			});
		}

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
