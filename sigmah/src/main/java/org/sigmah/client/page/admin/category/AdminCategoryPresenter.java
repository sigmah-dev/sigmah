package org.sigmah.client.page.admin.category;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.result.CategoriesListResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminCategoryPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO projectModel;
	
	public static abstract class View extends ContentPanel {
		public abstract ListStore<CategoryTypeDTO> getCategoriesStore();
		public abstract Component getMainPanel();
		public abstract MaskingAsyncMonitor getReportModelsLoadingMonitor();
	}
	
	public AdminCategoryPresenter(Dispatcher dispatcher){
		this.view = new AdminCategoryView(dispatcher);
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		dispatcher.execute(new GetCategories(), 
        		null,
        		new AsyncCallback<CategoriesListResult>() {

				@Override
	            public void onFailure(Throwable arg0) {
	                AdminUtil.alertPbmData(alert);
	            }
	
	            @Override
	            public void onSuccess(CategoriesListResult result) {
	            	view.getCategoriesStore().removeAll();
	            	view.getCategoriesStore().clearFilters();
	                if (result.getList() == null || result.getList().isEmpty()) {
	                	AdminUtil.alertPbmData(alert);
	                    return;
	                }
	                view.getCategoriesStore().add(result.getList());
	                view.getCategoriesStore().commitChanges();
	            }		
		});
		
		return view.getMainPanel();
	}

	public static void refreshCategoryTypePanel(Dispatcher dispatcher, final View view){
		dispatcher.execute(new GetCategories(), 
				view.getReportModelsLoadingMonitor(),
        		new AsyncCallback<CategoriesListResult>() {
        	@Override
            public void onFailure(Throwable arg0) {
        		AdminUtil.alertPbmData(alert);
            }

            @Override
            public void onSuccess(CategoriesListResult result) {
            	if (result.getList() != null && !result.getList().isEmpty()) {
            		view.getCategoriesStore().removeAll();
            		view.getCategoriesStore().add(result.getList());
                	view.getCategoriesStore().commitChanges();
            	}
            	
            }
        });
	}
	
	@Override
	public void discardView() {
	}

	@Override
	public void viewDidAppear() {
	}

	@Override
	public void setCurrentState(AdminPageState currentState) {
	}

	@Override
	public void setModel(Object model) {
		projectModel = (ProjectModelDTO)model;
	}

	@Override
	public Object getModel() {
		return projectModel;
	}
}
