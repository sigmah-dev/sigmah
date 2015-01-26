package org.sigmah.client.page.admin.importation;

import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminSubPresenter;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.ImportationSchemeListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

public class AdminImportPresenter implements AdminSubPresenter {
	private final View view;
	private final Dispatcher dispatcher;

	@ImplementedBy(AdminImportView.class)
	public static abstract class View extends ContentPanel {

		public abstract ListStore<VariableDTO> getVariablesStore();

		public abstract ListStore<ImportationSchemeDTO> getSchemesStore();

		public abstract void confirmDeleteSchemesSelected(ConfirmCallback confirmCallback);

		public abstract void confirmDeleteVariablesSelected(ConfirmCallback confirmCallback);

		public abstract List<VariableDTO> getVariablesSelection();

		public abstract List<ImportationSchemeDTO> getSchemesSelection();

		public abstract Component getMainPanel();

		public abstract MaskingAsyncMonitor getVariablesLoadingMonitor();

		public abstract MaskingAsyncMonitor getSchemesLoadingMonitor();
		
		public abstract Button getAddVariableButton();

		public abstract Button getDeleteVariableButton();

		public abstract Button getDeleteSchemeButton();

		public abstract Button getAddSchemeButton() ;
		
		public abstract Button getSaveSheetNameFirstRowButton();
		
		public abstract ImportationSchemeDTO getCurrentSchemeDTO();

		public abstract void setCurrentSchemeDTO(ImportationSchemeDTO currentSchema);

		public abstract Grid<ImportationSchemeDTO> getSchemesGrid();

		public abstract Grid<VariableDTO> getVariablesGrid();

	}

	public AdminImportPresenter(Dispatcher dispatcher) {
		this.view = new AdminImportView(dispatcher);
		addListeners();
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		dispatcher.execute(new GetImportationSchemes(), view.getSchemesLoadingMonitor() , new AsyncCallback<ImportationSchemeListResult>() {

			@Override
			public void onFailure(Throwable arg0) {
				AdminUtil.alertPbmData(false);
			}

			@Override
			public void onSuccess(ImportationSchemeListResult result) {
				view.getSchemesStore().removeAll();
				view.getSchemesStore().clearFilters();
				if (result.getList() != null && !result.getList().isEmpty()) {
					view.getSchemesStore().add(result.getList());
					view.getSchemesStore().commitChanges();
				}

			}
		});
		return view.getMainPanel();
	}
	
	public void addListeners() {
		view.getAddSchemeButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminImportSchemeActionListener actionListener = new AdminImportSchemeActionListener(
				                view, dispatcher, new ImportationSchemeDTO());
				actionListener.onUIAction(UIActions.add);
			}

		});
		
		view.getDeleteSchemeButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				AdminImportSchemeActionListener actionListener = new AdminImportSchemeActionListener(
								view, dispatcher, null);
				actionListener.onUIAction(UIActions.delete);

			}
		});
	
		view.getDeleteVariableButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				AdminImportVariableActionListener actionListener = new AdminImportVariableActionListener(
								view, dispatcher, null, null);
				actionListener.onUIAction(UIActions.delete);

			}
		});
		
		
		view.getAddVariableButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				AdminImportVariableActionListener actionListener = new AdminImportVariableActionListener(
								view, dispatcher, new VariableDTO(), view.getCurrentSchemeDTO());
				actionListener.onUIAction(UIActions.add);
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
	public void setCurrentState(AdminPageState currentState) {
		// TODO Auto-generated method stub

	}

}
