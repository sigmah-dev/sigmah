package org.sigmah.client.page.admin.model.project.phase;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class AdminPhasesPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO model;
	
	public static abstract class View extends ContentPanel {
		public abstract Component getMainPanel();
		public abstract ListStore<PhaseModelDTO> getPhaseStore();
		public abstract void setModel(ProjectModelDTO model);
		public abstract ProjectModelDTO getModel();
	}
	
	public AdminPhasesPresenter(Dispatcher dispatcher){
		this.view = new AdminPhasesView(dispatcher);
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		
		assert this.model != null;
		view.setModel(model);
		Log.debug("Number of phases " + model.getPhaseModelsDTO().size());
		view.getPhaseStore().add(model.getPhaseModelsDTO());
		view.getPhaseStore().commitChanges();
		this.model = view.getModel();
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
