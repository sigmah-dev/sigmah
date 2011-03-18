package org.sigmah.client.page.admin.model.common.report;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.result.ReportModelsListResult;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminReportModelPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	
	public static abstract class View extends ContentPanel {
		public abstract TreeStore<ReportModelDTO> getReportModelsStore();
		public abstract Component getMainPanel();
	}
	
	public AdminReportModelPresenter(Dispatcher dispatcher){
		this.view = new AdminReportModelView();
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		Log.debug("getting report view");
		dispatcher.execute(new GetReportModels(), 
        		null,
        		new AsyncCallback<ReportModelsListResult>() {

				@Override
	            public void onFailure(Throwable arg0) {
					Log.debug("A complete failure");
	                alertPbmData();
	            }
	
	            @Override
	            public void onSuccess(ReportModelsListResult result) {
	            	view.getReportModelsStore().removeAll();
	            	view.getReportModelsStore().clearFilters();
	                if (result.getList() == null || result.getList().isEmpty()) {
	                	Log.debug("Not quite a Success");
	                    alertPbmData();
	                    return;
	                }
	                view.getReportModelsStore().add(result.getList(), true);
	                view.getReportModelsStore().commitChanges();
	            }		
		});
		
		return view.getMainPanel();
	}
	
	private static void alertPbmData() {
        if (alert)
            return;
        alert = true;
        MessageBox.alert(I18N.CONSTANTS.adminUsers(), I18N.CONSTANTS.adminProblemLoading(), null);
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getModel() {
		// TODO Auto-generated method stub
		return null;
	}
}
