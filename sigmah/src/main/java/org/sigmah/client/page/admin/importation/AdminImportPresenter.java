package org.sigmah.client.page.admin.importation;

import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminSubPresenter;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.ImportationSchemeListResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

public class AdminImportPresenter implements AdminSubPresenter {
	private final View view;
	private final Dispatcher dispatcher;

	@ImplementedBy(AdminImportView.class)
	public static abstract class View extends ContentPanel {

		public abstract ListStore<VariableDTO> getVariablesStore();

		public abstract ListStore<ImportationSchemeDTO> getSchemasStore();

		public abstract void confirmDeleteSchemasSelected(ConfirmCallback confirmCallback);

		public abstract void confirmDeleteVariablesSelected(ConfirmCallback confirmCallback);

		public abstract List<VariableDTO> getVariablesSelection();

		public abstract List<ImportationSchemeDTO> getSchemasSelection();

		public abstract Component getMainPanel();

		public abstract MaskingAsyncMonitor getVariablesLoadingMonitor();

		public abstract MaskingAsyncMonitor getSchemasLoadingMonitor();

	}

	public AdminImportPresenter(Dispatcher dispatcher) {
		this.view = new AdminImportView(dispatcher);
		this.dispatcher = dispatcher;
	}

	@Override
	public Component getView() {
		dispatcher.execute(new GetImportationSchemes(), null, new AsyncCallback<ImportationSchemeListResult>() {

			@Override
			public void onFailure(Throwable arg0) {
				AdminUtil.alertPbmData(false);
			}

			@Override
			public void onSuccess(ImportationSchemeListResult result) {
				view.getSchemasStore().removeAll();
				view.getSchemasStore().clearFilters();
				if (result.getList() != null && !result.getList().isEmpty()) {
					view.getSchemasStore().add(result.getList());
					view.getSchemasStore().commitChanges();
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
	public void setCurrentState(AdminPageState currentState) {
		// TODO Auto-generated method stub

	}

}
