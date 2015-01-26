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
import org.sigmah.shared.command.GetImportationSchemeModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ImportationSchemeModelListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Actions performed on importation schemes
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * 
 */
public class AdminImportSchemeActionListener implements ActionListener {
	private final Dispatcher dispatcher;
	private final View view;
	private ImportationSchemeDTO schema;

	public AdminImportSchemeActionListener(View view, Dispatcher dispatcher, ImportationSchemeDTO schema) {
		this.dispatcher = dispatcher;
		this.view = view;
		this.schema = schema;
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
			view.confirmDeleteSchemesSelected(new ConfirmCallback() {

				@Override
				public void confirmed() {
					onDeleteConfirmed(view.getSchemesSelection());
				}
			});
		}

	}

	private void onEdit() {
		buildWindow("");
	}

	private void onAdd() {
		buildWindow("");
	}

	/**
	 * Action to perform when the user has confirmed the deletion
	 * 
	 * @param importationSchemesSelection
	 *            selected importation schemes
	 */
	protected void onDeleteConfirmed(final List<ImportationSchemeDTO> importationSchemesSelection) {

		final GetImportationSchemeModels cmdGet = new GetImportationSchemeModels();
		dispatcher.execute(cmdGet, view.getSchemesLoadingMonitor(),
		                new AsyncCallback<ImportationSchemeModelListResult>() {

			                @Override
			                public void onFailure(Throwable arg0) {
				                // TODO Auto-generated method stub

			                }

			                @Override
			                public void onSuccess(ImportationSchemeModelListResult result) {
				                String modelNames = "";
				                String notDeleted = "";
				                List<ImportationSchemeModelDTO> importSchemeModels = result.getList();
				                for (ImportationSchemeModelDTO schemeModel : importSchemeModels) {
					                if (importationSchemesSelection.contains(schemeModel.getImportationSchemeDTO())) {
						                importationSchemesSelection.remove(schemeModel.getImportationSchemeDTO());
						                notDeleted += schemeModel.getImportationSchemeDTO().getName() + ", ";
						                if (schemeModel.getProjectModelDTO() != null) {
							                modelNames += schemeModel.getProjectModelDTO().getName() + "(P), ";
						                } else {
							                modelNames += schemeModel.getOrgUnitModelDTO().getName() + "(O), ";
						                }
					                }
				                }
				                if (importationSchemesSelection.size() == 0) {
					                MessageBox.alert(I18N.CONSTANTS.deletionError(),
					                                I18N.MESSAGES.adminImportationSchemesWarnModelsLinked(modelNames),
					                                null);
				                } else {
					                final List<Long> schemaIdsToDelete = new ArrayList<Long>();
					                if (importationSchemesSelection != null && importationSchemesSelection.size() != 0) {
						                for (ImportationSchemeDTO schemaToDelete : importationSchemesSelection) {
							                schemaIdsToDelete.add(Long.valueOf(schemaToDelete.getId()));
						                }
						                final DeleteImportationSchemes cmdDelete = new DeleteImportationSchemes();
						                cmdDelete.setSchemaIdsList(schemaIdsToDelete);
						                dispatcher.execute(cmdDelete, view.getSchemesLoadingMonitor(),
						                                new AsyncCallback<VoidResult>() {

							                                @Override
							                                public void onFailure(Throwable caught) {
								                                // TODO
								                                // Auto-generated
								                                // method stub

							                                }

							                                @Override
							                                public void onSuccess(VoidResult result) {
								                                Notification.show(
								                                                I18N.CONSTANTS.infoConfirmation(),
								                                                I18N.CONSTANTS.adminImportationSchemesDeleteConfirm());
								                                for (ImportationSchemeDTO importationSchemeDTO : importationSchemesSelection) {
									                                view.getSchemesStore().remove(importationSchemeDTO);
								                                }
							                                }
						                                });
					                }
				                }
			                }

		                });

	}

	private void buildWindow(String title) {
		final Window window = new Window();
		window.setHeading(I18N.CONSTANTS.addItem());
		window.setSize(600, 250);
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());

		final ImportationSchemeForm form = new ImportationSchemeForm(dispatcher, view.getSchemesLoadingMonitor(),
		                new AsyncCallback<CreateResult>() {

			                @Override
			                public void onSuccess(CreateResult result) {
				                Notification.show(I18N.CONSTANTS.infoConfirmation(),
				                                I18N.CONSTANTS.adminImportationSchemeUpdateConfirm());
				                window.hide();
				                ImportationSchemeDTO schemaUpdated = (ImportationSchemeDTO) result.getEntity();
				                if (schema.getId() > 0) {
					                view.getSchemesStore().remove(schema);
				                }
				                view.getSchemesStore().add(schemaUpdated);
				                view.getSchemesStore().commitChanges();
			                }

			                @Override
			                public void onFailure(Throwable caught) {
				                window.hide();

			                }
		                }, schema);
		window.add(form);
		window.show();
	}

}
