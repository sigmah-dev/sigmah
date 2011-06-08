package org.sigmah.client.page.admin.model.project.phase;

import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.ModelView;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;

public class AdminPhasesPresenter implements AdminModelSubPresenter {

	private final View view;
	private ProjectModelDTO model;
	
	public static abstract class View extends ModelView {
		public abstract Component getMainPanel();
		public abstract ListStore<PhaseModelDTO> getPhaseStore();
		public abstract Map<String, PhaseModelDTO> getPhases();
		public abstract List<String> getSuccessorsPhases();
	}
	
	public AdminPhasesPresenter(Dispatcher dispatcher){
		this.view = new AdminPhasesView(dispatcher);
	}

	@Override
	public Component getView() {
		
		assert this.model != null;
		view.setProjectModel(model);

		view.getPhaseStore().add(model.getPhaseModelsDTO());
		view.getPhaseStore().commitChanges();
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
