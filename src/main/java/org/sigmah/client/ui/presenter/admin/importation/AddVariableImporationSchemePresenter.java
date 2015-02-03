package org.sigmah.client.ui.presenter.admin.importation;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.importation.AddVariableImporationSchemeView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Present the form to add or edit an importation scheme variable.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class AddVariableImporationSchemePresenter extends AbstractPagePresenter<AddVariableImporationSchemePresenter.View> {

	private ImportationSchemeDTO currentImportationSheme;

	private VariableDTO variableDTO;

	/**
	 * The view interface managed by this presenter.
	 */

	@ImplementedBy(AddVariableImporationSchemeView.class)
	public static interface View extends ViewInterface {

		TextField<String> getNameField();

		TextField<String> getReferenceField();

		Button getSaveButton();

		FormPanel getMainPanel();

		void clearForm();

	}

	@Inject
	public AddVariableImporationSchemePresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_VARIABLE_IMPORTATION_SCHEME;
	}

	@Override
	public void onPageRequest(PageRequest request) {

		currentImportationSheme = request.getData(RequestParameter.IMPORTATION_SCHEME);
		variableDTO = request.getData(RequestParameter.VARIABLE_IMPORTATION_SCHEME);
		view.clearForm();

		if (currentImportationSheme != null) {

			initView(variableDTO, currentImportationSheme);

		} else {

			hideView();

			N10N.message(I18N.MESSAGES.importSchemenNoSelected(), MessageType.INFO);

		}

		if (variableDTO == null) {
			variableDTO = new VariableDTO();
		}

	}

	@Override
	public void onBind() {

		// Save Variable Importation Scheme

		view.getSaveButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				// Validate Form

				if (!view.getMainPanel().isValid()) {
					N10N.infoNotif(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportationScheme()), null);
					return;
				}

				// Create Command

				Map<String, Object> newSchemaProperties = new HashMap<String, Object>();
				newSchemaProperties.put(AdminUtil.PROP_VAR_NAME, view.getNameField().getValue());
				newSchemaProperties.put(AdminUtil.PROP_VAR_REFERENCE, view.getReferenceField().getValue());
				newSchemaProperties.put(AdminUtil.PROP_VAR_VARIABLE, variableDTO);
				newSchemaProperties.put(AdminUtil.ADMIN_SCHEMA, currentImportationSheme);
				CreateEntity cmd = new CreateEntity(ImportationSchemeDTO.ENTITY_NAME, newSchemaProperties);

				// Run Command

				dispatch.execute(cmd, new CommandResultHandler<CreateResult>() {

					@Override
					protected void onCommandSuccess(CreateResult result) {
						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminImportVariable() + " " + I18N.CONSTANTS.adminVariableAddConfirm());
						hideView();
						eventBus.fireEvent(new UpdateEvent(UpdateEvent.VARIABLE_SCHEME_UPDATE, result.getEntity()));
					};
				});

			}
		});

	}

	/**
	 * Init View
	 * 
	 * @param variable
	 * @param importationSheme
	 */
	public void initView(VariableDTO variable, ImportationSchemeDTO importationSheme) {

		switch (importationSheme.getImportType()) {

			case ROW:
				view.getReferenceField().setFieldLabel(I18N.CONSTANTS.adminImportReferenceColumn());
				break;
			case SEVERAL:
				view.getReferenceField().setFieldLabel(I18N.CONSTANTS.adminImportReferenceCell());
				break;
			case UNIQUE:
				view.getReferenceField().setFieldLabel(I18N.CONSTANTS.adminImportReferenceSheetCell());
				break;
			default:
				break;

		}

		if (variable != null) {
			setPageTitle(I18N.CONSTANTS.edit());
			
			view.getNameField().setValue(variable.getName());
			view.getReferenceField().setValue(variable.getReference());
			
		} else {
			setPageTitle(I18N.CONSTANTS.addItem());
		}

	}

}
