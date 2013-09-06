package org.sigmah.client.page.admin.model.common.importation;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ImportationSchemeListResult;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportationSchemeModelForm extends FormPanel {
	private ComboBox<ImportationSchemeDTO> schemasCombo;
	private EntityDTO model;
	private Button submitButton;
	private Dispatcher dispatcher;

	public ImportationSchemeModelForm(final Dispatcher dispatcher, final MaskingAsyncMonitor maskingAsyncMonitor,
	                final AsyncCallback<CreateResult> callback, final EntityDTO model) {
		this.dispatcher = dispatcher;
		this.model = model;

		setWidth(400);
		setLabelWidth(120);

		schemasCombo = new ComboBox<ImportationSchemeDTO>(); 
		schemasCombo.setFieldLabel(I18N.CONSTANTS.adminImportationScheme());
		
		add(schemasCombo);
		schemasCombo.setDisplayField("name");
		
		final ListStore<ImportationSchemeDTO> schemasStore = new ListStore<ImportationSchemeDTO>();
		schemasCombo.setStore(schemasStore);
		
		submitButton = new Button(I18N.CONSTANTS.save());
		submitButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				createImporationSchemeModel(maskingAsyncMonitor, callback);

			}
		});
		add(submitButton);
		

	}

	private void createImporationSchemeModel(MaskingAsyncMonitor maskingAsyncMonitor,
	                AsyncCallback<CreateResult> callback) {
		if (!this.isValid()) {
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
			                I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportationScheme()), null);
			return;
		}
		Map<String, Object> newImportationSchemeModelProperties = new HashMap<String, Object>();
		newImportationSchemeModelProperties.put(AdminUtil.ADMIN_SCHEMA, schemasCombo.getValue());
		if (model instanceof ProjectModelDTO) {
			newImportationSchemeModelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, model);
		} else {
			newImportationSchemeModelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, model);
		}
		newImportationSchemeModelProperties.put(AdminUtil.ADMIN_IMPORTATION_SCHEME_MODEL,
		                new ImportationSchemeModelDTO());
		CreateEntity cmd = new CreateEntity("ImportationSchemeModel", newImportationSchemeModelProperties);
		dispatcher.execute(cmd, maskingAsyncMonitor, callback);
	}
	
	/**
	 * @return the schemasCombo
	 */
	public ComboBox<ImportationSchemeDTO> getSchemasCombo() {
		return schemasCombo;
	}

}
