package org.sigmah.client.page.admin.model.common.importation;

import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.ModelView;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.result.ImportationSchemeModelListResult;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminImportationSchemeModelsPresenter implements AdminModelSubPresenter {
	private final View view;
	private EntityDTO model;
	private Dispatcher dispatcher;

	public static abstract class View extends ModelView {

		public abstract Component getMainPanel();

		public abstract ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore();

		public abstract ListStore<VariableFlexibleElementDTO> getVariableFlexibleElementsStore();

		public abstract void showNewVariableFlexibleElementForm(Boolean forKey);

		public abstract void showNewImportationSchemeModelForm(Boolean forKey);

		public abstract List<ImportationSchemeModelDTO> getImportationSchemeModelsSelection();

		public abstract List<VariableFlexibleElementDTO> getVariableFlexibleElementsSelection();

		public abstract MaskingAsyncMonitor getVariableFlexibleElementsLoadingMonitor();

		public abstract MaskingAsyncMonitor getImportationSchemeModelsLoadingMonitor();

		public abstract void confirmDeleteSelected(ConfirmCallback confirmCallback);
	}

	public AdminImportationSchemeModelsPresenter(Dispatcher dispatcher) {
		this.view = new AdminImportationSchemeModelsView(dispatcher);
		this.dispatcher = dispatcher;
	}

	@Override
	public void setCurrentState(AdminPageState currentState) {

	}

	@Override
	public Component getView() {
		assert this.model != null;
		GetImportationSchemeModels cmd = new GetImportationSchemeModels();
		if (model instanceof ProjectModelDTO) {
			view.setProjectModel((ProjectModelDTO) model);
			cmd.setProjectModelId(Long.valueOf(model.getId()));

		} else {
			view.setOrgUnitModel((OrgUnitModelDTO) model);
			cmd.setOrgUnitModelId(Long.valueOf(model.getId()));
		}
		dispatcher.execute(cmd, null, new AsyncCallback<ImportationSchemeModelListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ImportationSchemeModelListResult result) {
				view.getImportationSchemeModelsStore().removeAll();
				view.getImportationSchemeModelsStore().clearFilters();
				if (result.getList() != null && !result.getList().isEmpty()) {
					view.getImportationSchemeModelsStore().add(result.getList());
					view.getImportationSchemeModelsStore().commitChanges();
				}
			}
		});
		return view.getMainPanel();
	}

	@Override
	public void discardView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasValueChanged() {
		return false;
	}

	@Override
	public void forgetAllChangedValues() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setModel(Object model) {
		this.model = (EntityDTO) model;
	}

	@Override
	public Object getModel() {
		return model;
	}

}
