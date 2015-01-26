package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.CategoryTypeJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class CategoryTypeAsyncDAO extends AbstractAsyncDAO<CategoryTypeDTO> {
	
	private final CategoryElementAsyncDAO categoryElementAsyncDAO;

	@Inject
	public CategoryTypeAsyncDAO(CategoryElementAsyncDAO categoryElementAsyncDAO) {
		this.categoryElementAsyncDAO = categoryElementAsyncDAO;
	}

	public void saveOrUpdate(final ListResult<CategoryTypeDTO> categoriesListResult) {
		if(categoriesListResult != null && categoriesListResult.getList() != null) {
            openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

                @Override
                public void onTransaction(Transaction transaction) {
                    for(final CategoryTypeDTO categoryTypeDTO : categoriesListResult.getList()) {
						saveOrUpdate(categoryTypeDTO, null, transaction);
					}
                }
            });
		}
	}
	
	@Override
	public void saveOrUpdate(final CategoryTypeDTO t, AsyncCallback<CategoryTypeDTO> callback, Transaction transaction) {
		final ObjectStore categoryTypeStore = transaction.getObjectStore(Store.CATEGORY_TYPE);
		
		final CategoryTypeJS categoryTypeJS = CategoryTypeJS.toJavaScript(t);
		categoryTypeStore.put(categoryTypeJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving category type " + t.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.info("Category type " + t.getId() + " has been successfully saved.");
            }
        });
        
		// Saving category elements
		if(t.getCategoryElementsDTO() != null) {
			for(final CategoryElementDTO categoryElementDTO : t.getCategoryElementsDTO()) {
				categoryElementAsyncDAO.saveOrUpdate(categoryElementDTO, null, transaction);
			}
		}
	}

	@Override
	public void get(int id, final AsyncCallback<CategoryTypeDTO> callback, final Transaction transaction) {
		final ObjectStore categoryTypeStore = transaction.getObjectStore(Store.CATEGORY_TYPE);

		categoryTypeStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final CategoryTypeJS categoryTypeJS = (CategoryTypeJS) request.getResult();
				if(categoryTypeJS != null) {
					final CategoryTypeDTO categoryTypeDTO = categoryTypeJS.toDTO();

					final RequestManager<CategoryTypeDTO> requestManager = new RequestManager<CategoryTypeDTO>(categoryTypeDTO, callback);
					loadCategoryElements(categoryTypeJS, categoryTypeDTO, requestManager, transaction);
					requestManager.ready();
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}
	
	public void getAll(final AsyncCallback<ListResult<CategoryTypeDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(final Transaction transaction) {
                final ArrayList<CategoryTypeDTO> categories = new ArrayList<CategoryTypeDTO>();
				final ListResult<CategoryTypeDTO> result = new ListResult<CategoryTypeDTO>(categories);
				final RequestManager<ListResult<CategoryTypeDTO>> requestManager = new RequestManager<ListResult<CategoryTypeDTO>>(result, callback);
				
				final ObjectStore categoryTypeStore = transaction.getObjectStore(Store.CATEGORY_TYPE);
				final OpenCursorRequest cursorRequest = categoryTypeStore.openCursor();
                
                cursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request result) {
                        final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final CategoryTypeJS categoryTypeJS = (CategoryTypeJS) cursor.getValue();
							final CategoryTypeDTO categoryTypeDTO = categoryTypeJS.toDTO();
							categories.add(categoryTypeDTO);
							
							loadCategoryElements(categoryTypeJS, categoryTypeDTO, requestManager, transaction);
							
							cursor.next();
							
						} else {
							requestManager.ready();
						}
                    }
                });
            }
        });
	}
	
	private <M> void loadCategoryElements(final CategoryTypeJS categoryTypeJS, final CategoryTypeDTO categoryTypeDTO, 
			final RequestManager<M> requestManager, final Transaction transaction) {
		if(categoryTypeJS.getCategoryElements() != null) {
			categoryTypeDTO.setCategoryElementsDTO(new ArrayList<CategoryElementDTO>());

			final JsArrayInteger categoryElements = categoryTypeJS.getCategoryElements();
			final int size = categoryElements.length();

			for(int index = 0; index < size; index++) {
				categoryElementAsyncDAO.get(categoryElements.get(index), new RequestManagerCallback<M, CategoryElementDTO>(requestManager) {
					@Override
					public void onRequestSuccess(CategoryElementDTO result) {
						result.setParentCategoryDTO(categoryTypeDTO);
						categoryTypeDTO.getCategoryElementsDTO().add(result);
					}
				}, transaction);
			}
		}
	}

	@Override
	public Store getRequiredStore() {
		return Store.CATEGORY_TYPE;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> list = new ArrayList<BaseAsyncDAO>();
		list.add(categoryElementAsyncDAO);
		return list;
	}
}
