package org.sigmah.client.page.admin.importation;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.importation.AdminImportPresenter.View;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeleteImportationSchemes;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Action performed on variables
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class AdminImportVariableActionListener implements ActionListener {
	private final Dispatcher dispatcher;
	private final View view;
	private VariableDTO variable;
	private Integer schemaId;
	private ImportationSchemeImportType type;

	public AdminImportVariableActionListener(View view, Dispatcher dispatcher, VariableDTO variable,
	                ImportationSchemeDTO importationScheme) {
		this.dispatcher = dispatcher;
		this.view = view;
		this.variable = variable;
		if (importationScheme != null) {
			this.schemaId = importationScheme.getId();
			this.type = importationScheme.getImportType();
		}
	}

	@Override
	public void onUIAction(String actionId) {
		if (UIActions.add.equals(actionId)) {
			onAdd();
		}
		if (UIActions.edit.equals(actionId)) {
			onEdit();
		}
		if (UIActions.delete.equals(actionId)) {
			view.confirmDeleteVariablesSelected(new ConfirmCallback() {

				@Override
				public void confirmed() {
					onDeleteConfirmed(view.getVariablesSelection());
				}
			});
		}

	}

	/**
	 * Action performed when the user confirmed the deletion
	 * 
	 * @param variablesSelection
	 *            variables selected
	 */
	protected void onDeleteConfirmed(final List<VariableDTO> variablesSelection) {
		List<Long> varIdsToDelete = new ArrayList<Long>();
		if (variablesSelection != null && variablesSelection.size() != 0) {
			for (VariableDTO varToDelete : variablesSelection) {
				varIdsToDelete.add(Long.valueOf(varToDelete.getId()));
			}
		}
		DeleteImportationSchemes cmd = new DeleteImportationSchemes();
		cmd.setVariableIdsList(varIdsToDelete);
		dispatcher.execute(cmd, view.getVariablesLoadingMonitor(), new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(VoidResult result) {
				Notification.show(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminVariableDeleteConfirm());
				for (VariableDTO variableDTO : variablesSelection) {
					view.getVariablesStore().remove(variableDTO);
				}
			}
		});

	}

	private void onEdit() {
		buildWindow(I18N.CONSTANTS.adminImportVariable(), I18N.CONSTANTS.adminVariableUpdateConfirm());

	}

	private void onAdd() {
		buildWindow(I18N.CONSTANTS.adminImportVariable(), I18N.CONSTANTS.adminVariableAddConfirm());
	}

	/**
	 * 
	 * @param title
	 *            title of the window
	 * @param confirmMessage
	 *            Message to display when the action has been successfully
	 *            executed
	 */
	private void buildWindow(String title, final String confirmMessage) {
		final Window window = new Window();
		window.setTitle(title);
		window.setSize(400, 200);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());
		final VariableForm variableForm = new VariableForm(dispatcher, view.getVariablesLoadingMonitor(),
		                new AsyncCallback<CreateResult>() {

			                @Override
			                public void onSuccess(CreateResult result) {
				                Notification.show(I18N.CONSTANTS.infoConfirmation(), confirmMessage);
				                window.hide();
				                view.getVariablesStore().removeAll();
				                view.getVariablesStore().clearFilters();
				                ImportationSchemeDTO schemaDTOUpdated = (ImportationSchemeDTO) result.getEntity();
				                view.getSchemesStore().update(schemaDTOUpdated);
				                view.getSchemesStore().commitChanges();
				                view.getVariablesStore().add(schemaDTOUpdated.getVariablesDTO());
				                view.getVariablesStore().commitChanges();
			                }

			                @Override
			                public void onFailure(Throwable caught) {
				                window.hide();

			                }
		                }, schemaId, variable, type);
		window.add(variableForm);
		window.show();
	}

}
