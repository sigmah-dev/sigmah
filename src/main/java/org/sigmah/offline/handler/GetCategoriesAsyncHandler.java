package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.CategoryTypeAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetCategoriesHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetCategoriesAsyncHandler implements AsyncCommandHandler<GetCategories, ListResult<CategoryTypeDTO>>, DispatchListener<GetCategories, ListResult<CategoryTypeDTO>> {

	private final CategoryTypeAsyncDAO categoryTypeAsyncDAO;

	@Inject
	public GetCategoriesAsyncHandler(CategoryTypeAsyncDAO categoryTypeAsyncDAO) {
		this.categoryTypeAsyncDAO = categoryTypeAsyncDAO;
	}
	
	@Override
	public void execute(GetCategories command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<CategoryTypeDTO>> callback) {
		categoryTypeAsyncDAO.getAll(callback);
	}

	@Override
	public void onSuccess(GetCategories command, ListResult<CategoryTypeDTO> result, Authentication authentication) {
		categoryTypeAsyncDAO.saveOrUpdate(result);
	}
	
}
