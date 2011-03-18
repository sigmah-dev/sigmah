package org.sigmah.client.page.admin.model.common.element;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;

public class AdminFlexibleElementsPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO model;
	
	public static abstract class View extends ContentPanel {
		public abstract Component getMainPanel();
		public abstract ListStore<FlexibleElementDTO> getFieldsStore();
		public abstract void setModel(ProjectModelDTO model);
		public abstract ProjectModelDTO getModel();
	}
	
	public AdminFlexibleElementsPresenter(Dispatcher dispatcher){
		this.view = new AdminFlexibleElementsView(dispatcher);
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		
		assert this.model != null;
		view.setModel(model);
		view.getFieldsStore().add(model.getAllElements());
		view.getFieldsStore().commitChanges();
		this.model = view.getModel();
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
		this.model = (ProjectModelDTO) model;
	}

	@Override
	public Object getModel() {
		return model;
	}
}
