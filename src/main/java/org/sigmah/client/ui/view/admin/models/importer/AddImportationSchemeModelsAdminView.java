package org.sigmah.client.ui.view.admin.models.importer;

import javax.inject.Inject;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.importer.AddImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class AddImportationSchemeModelsAdminView extends AbstractPopupView<PopupWidget> implements AddImportationSchemeModelsAdminPresenter.View {

	private FormPanel mainPanel;
	private ComboBox<ImportationSchemeDTO> schemasCombo;
	private Button submitButton;
	private ListStore<ImportationSchemeDTO> schemasStore;

	@Inject
	protected AddImportationSchemeModelsAdminView() {
		super(new PopupWidget(true), 500);
	}

	@Override
	public void initialize() {

		mainPanel = Forms.panel();

		schemasCombo = Forms.combobox(I18N.CONSTANTS.adminImportationScheme(), true, EntityDTO.ID, ImportationSchemeDTO.NAME);
		schemasCombo.setFireChangeEventOnSetValue(true);

		schemasStore = new ListStore<ImportationSchemeDTO>();
		schemasCombo.setStore(schemasStore);

		mainPanel.add(schemasCombo);

		submitButton = Forms.button(I18N.CONSTANTS.save());

		mainPanel.add(submitButton);

		initPopup(mainPanel);

	}

	/**
	 * @return the schemasCombo
	 */
	@Override
	public ComboBox<ImportationSchemeDTO> getSchemasCombo() {
		return schemasCombo;
	}

	@Override
	public FormPanel getMainPanel() {
		return mainPanel;
	}

	@Override
	public Button getSubmitButton() {
		return submitButton;
	}

	@Override
	public ListStore<ImportationSchemeDTO> getSchemasStore() {
		return schemasStore;
	}

	@Override
	public void clearForm() {

		schemasCombo.clear();
		schemasCombo.clearSelections();
		schemasCombo.clearState();

	}
}
