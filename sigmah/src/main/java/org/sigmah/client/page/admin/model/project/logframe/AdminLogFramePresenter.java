package org.sigmah.client.page.admin.model.project.logframe;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.ModelView;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class AdminLogFramePresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO model;
	
	public static abstract class View extends ModelView {
		public abstract Component getMainPanel();
		public abstract void fillLogFrame(LogFrameModelDTO logFrameModel);
	}
	
	public AdminLogFramePresenter(Dispatcher dispatcher){
		this.view = new AdminLogFrameView(dispatcher, model);
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		
		assert this.model != null;
		view.setProjectModel(model);

		LogFrameModelDTO logFrameModel = model.getLogFrameModelDTO();
		view.fillLogFrame(logFrameModel);
		
		this.model = view.getProjectModel();
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
		this.model = (ProjectModelDTO) model;
	}

	@Override
	public Object getModel() {
		return model;
	}
}
