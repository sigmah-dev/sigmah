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
import org.sigmah.client.ui.view.admin.importation.AddImportationSchemeView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class AddImportationSchemePresenter extends AbstractPagePresenter<AddImportationSchemePresenter.View> {

	private ImportationSchemeDTO currentImportationSheme;

	/**
	 * The view interface managed by this presenter.
	 */

	@ImplementedBy(AddImportationSchemeView.class)
	public static interface View extends ViewInterface {

		TextField<String> getNameField();

		Radio getCsvRadio();

		Radio getOdsRadio();

		Radio getExcelRadio();

		Radio getUniqueRadio();

		Radio getSeveralRadio();

		Radio getLineRadio();

		RadioGroup getImportTypeGroup();

		RadioGroup getFileFormatGroup();

		FormPanel getFormPanel();

		ImportationSchemeFileFormat getCurrentFileFormat();

		ImportationSchemeImportType getCurrentImportType();

		Button getCreateButton();

		Radio getFileFormatRadioFilter(ImportationSchemeFileFormat type);

		Radio getImportTypeRadioFilter(ImportationSchemeImportType type);

		void clearForm();

	}

	@Inject
	protected AddImportationSchemePresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_IMPORTATION_SCHEME;
	}

	@Override
	public void onPageRequest(PageRequest request) {

		currentImportationSheme = request.getData(RequestParameter.DTO);

		view.clearForm();

		if (currentImportationSheme != null) {

			// CASE EDIT

			initUpdateImportationSchemeView(currentImportationSheme);

		} else {

			// CASE CREATE

			currentImportationSheme = new ImportationSchemeDTO();

		}

		setPageTitle(I18N.CONSTANTS.addItem());
	}

	@Override
	public void onBind() {

		// Save Button

		view.getCreateButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				if (!view.getFormPanel().isValid()) {
					N10N.warnNotif(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportationScheme()), null);
					return;
				}

				Map<String, Object> newSchemaProperties = new HashMap<String, Object>();

				newSchemaProperties.put(AdminUtil.ADMIN_SCHEMA, currentImportationSheme);
				newSchemaProperties.put(AdminUtil.PROP_SCH_NAME, view.getNameField().getValue());
				newSchemaProperties.put(AdminUtil.PROP_SCH_FILE_FORMAT, view.getCurrentFileFormat());
				newSchemaProperties.put(AdminUtil.PROP_SCH_IMPORT_TYPE, view.getCurrentImportType());

				CreateEntity cmd = new CreateEntity(ImportationSchemeDTO.ENTITY_NAME, newSchemaProperties);

				dispatch.execute(cmd, new CommandResultHandler<CreateResult>() {

					@Override
					protected void onCommandFailure(Throwable caught) {

						hideView();

					};

					@Override
					protected void onCommandSuccess(CreateResult result) {

						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminImportationSchemeUpdateConfirm());

						hideView();

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.IMPORTATION_SCHEME_UPDATE));

					};

				});

			}
		});

	}

	private void initUpdateImportationSchemeView(ImportationSchemeDTO currentImportationSheme2) {

		if (currentImportationSheme2.getId() > 0) {

			view.getNameField().setValue(currentImportationSheme2.getName()); // Prevent changing the import type and file
																																				// format to avoid
			// incoherence
			view.getImportTypeGroup().hide();
			view.getFileFormatGroup().hide();
		}

	}
}
