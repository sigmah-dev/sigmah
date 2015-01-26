package org.sigmah.client.page.admin.model.common.importation;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.ModelView;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeleteImportationSchemeModels;
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.result.ImportationSchemeModelListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
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

		public abstract void confirmDeleteSchemeModelsSelected(ConfirmCallback confirmCallback);
		
		public abstract void confirmDeleteVariableFlexibleElementsSelected(ConfirmCallback confirmCallback);
		
		public abstract Button getAddImportationSchemeModelButton();

		public abstract Button getDeleteVariableFlexibleElementButton() ;

		public abstract Button getDeleteImportationSchemeModelButton();

		public abstract Button getAddVariableFlexibleElementButton();
		
		public abstract ImportationSchemeModelDTO getCurrentImportationSchemeModelDTO();
		
		public abstract void setCurrentImportationSchemeModelDTO(ImportationSchemeModelDTO currentImportationSchemeModel);
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
		addListeners();
		return view.getMainPanel();
	}
	
	private void addListeners(){
		view.getDeleteVariableFlexibleElementButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.confirmDeleteVariableFlexibleElementsSelected(new ConfirmCallback() {

					@Override
					public void confirmed() {
						deleteVariableFlexibleElements();
					}
				});

			}
		});
		
		view.getDeleteImportationSchemeModelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.confirmDeleteSchemeModelsSelected(new ConfirmCallback() {

					@Override
					public void confirmed() {
						deleteImportationSchemeModels();
					}
				});
			}
		});
		
		view.getAddVariableFlexibleElementButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				view.showNewVariableFlexibleElementForm(false);
			}
		});
		
		view.getAddImportationSchemeModelButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				view.showNewImportationSchemeModelForm(true);
			}

		});
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

	protected void deleteVariableFlexibleElements() {
		List<Long> variableFlexibleElementIdsToDelete = new ArrayList<Long>();
		final List<VariableFlexibleElementDTO> variableFlexibleElementsSelection = view.getVariableFlexibleElementsSelection();
		for (VariableFlexibleElementDTO variableFlexibleElement : variableFlexibleElementsSelection) {
			variableFlexibleElementIdsToDelete.add(Long.valueOf(variableFlexibleElement.getId()));
		}
		final DeleteImportationSchemeModels cmdDelete = new DeleteImportationSchemeModels();
		cmdDelete.setVariableFlexibleElemementIdsList(variableFlexibleElementIdsToDelete);
		dispatcher.execute(cmdDelete, view.getVariableFlexibleElementsLoadingMonitor(), new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
				// Auto-generated
				// method stub

			}

			@Override
			public void onSuccess(VoidResult result) {
				Notification.show(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminVariableDeleteConfirm());
				for (VariableFlexibleElementDTO variableFlexibleElement : variableFlexibleElementsSelection) {
					view.getVariableFlexibleElementsStore().remove(variableFlexibleElement);
					view.getCurrentImportationSchemeModelDTO().getVariableFlexibleElementsDTO().remove(variableFlexibleElement);
					
				}
			}
		});

	}
	
	protected void deleteImportationSchemeModels() {
		List<Long> schemeModelIdsToDelete = new ArrayList<Long>();
		final List<ImportationSchemeModelDTO> importationSchemeSelection = view.getImportationSchemeModelsSelection();
		for (ImportationSchemeModelDTO importationSchemeModel : importationSchemeSelection) {
			schemeModelIdsToDelete.add(Long.valueOf(importationSchemeModel.getId()));
		}
		final DeleteImportationSchemeModels cmdDelete = new DeleteImportationSchemeModels();
		cmdDelete.setImportationSchemeIdsList(schemeModelIdsToDelete);
		dispatcher.execute(cmdDelete, view.getImportationSchemeModelsLoadingMonitor(), new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO
				// Auto-generated
				// method stub

			}

			@Override
			public void onSuccess(VoidResult result) {
				Notification.show(I18N.CONSTANTS.infoConfirmation(),
				                I18N.CONSTANTS.adminImportationSchemesDeleteConfirm());
				for (ImportationSchemeModelDTO importationSchemeModelDTO : importationSchemeSelection) {
					view.getImportationSchemeModelsStore().remove(importationSchemeModelDTO);
				}
			}
		});

	}
}
