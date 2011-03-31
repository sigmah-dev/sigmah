package org.sigmah.client.page.admin.report;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.result.ReportModelsListResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminReportModelPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO projectModel;
	
	public static abstract class View extends ContentPanel {
		public abstract ListStore<ReportModelDTO> getReportModelsStore();
		public abstract Component getMainPanel();
	}
	
	public AdminReportModelPresenter(Dispatcher dispatcher){
		this.view = new AdminReportModelView(dispatcher);
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		dispatcher.execute(new GetReportModels(), 
        		null,
        		new AsyncCallback<ReportModelsListResult>() {

				@Override
	            public void onFailure(Throwable arg0) {
	                AdminUtil.alertPbmData(alert);
	            }
	
	            @Override
	            public void onSuccess(ReportModelsListResult result) {
	            	view.getReportModelsStore().removeAll();
	            	view.getReportModelsStore().clearFilters();
	                if (result.getList() == null || result.getList().isEmpty()) {
	                	 AdminUtil.alertPbmData(alert);
	                    return;
	                }
	                view.getReportModelsStore().add(result.getList());
	                view.getReportModelsStore().commitChanges();
	            }		
		});
		
		return view.getMainPanel();
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
