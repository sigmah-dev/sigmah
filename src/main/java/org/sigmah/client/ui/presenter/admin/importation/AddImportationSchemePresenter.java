package org.sigmah.client.ui.presenter.admin.importation;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
import com.extjs.gxt.ui.client.event.FieldEvent;
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
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class AddImportationSchemePresenter extends AbstractPagePresenter<AddImportationSchemePresenter.View> {

	public static final String VALUE = "type";
	
	private ImportationSchemeDTO currentImportationSheme;
	private ImportationSchemeFileFormat currentFileFormat;
	private ImportationSchemeImportType currentImportType;

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
	public void onBind() {
		
		// Adds actions on filter by model type.
		view.getFileFormatGroup().addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				final Radio value = view.getFileFormatGroup().getValue();
				if(value != null) {
					currentFileFormat = value.getData(VALUE);
					
					// For csv files, it will automatically consider each line
					// as a project
					if (currentFileFormat == ImportationSchemeFileFormat.CSV) {
						currentImportType = ImportationSchemeImportType.ROW;
						view.getLineRadio().setValue(true);
						view.getImportTypeGroup().disable();
						
					} else {
						view.getImportTypeGroup().enable();
					}
					
				} else {
					currentFileFormat = null;
				}
			}
			
		});

		view.getImportTypeGroup().addListener(Events.Change, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				final Radio value = view.getImportTypeGroup().getValue();
				if(value != null) {
					currentImportType = value.getData(VALUE);
					
				} else {
					currentImportType = null;
				}
			}
			
		});
		
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
				newSchemaProperties.put(AdminUtil.PROP_SCH_FILE_FORMAT, currentFileFormat);
				newSchemaProperties.put(AdminUtil.PROP_SCH_IMPORT_TYPE, currentImportType);

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
	
	@Override
	public void onPageRequest(PageRequest request) {

		currentImportationSheme = request.getData(RequestParameter.DTO);
		view.clearForm();

		if (currentImportationSheme != null) {
			// CASE EDIT
			setPageTitle(I18N.CONSTANTS.editItem());
			initUpdateImportationSchemeView();

		} else {
			// CASE CREATE
			setPageTitle(I18N.CONSTANTS.addItem());
			currentImportationSheme = new ImportationSchemeDTO();
		}

	}

	private void initUpdateImportationSchemeView() {

		if (currentImportationSheme.getId() > 0) {
			// Prevent changing the import type and file format to avoid incoherence.
			view.getNameField().setValue(currentImportationSheme.getName()); 
			view.getFileFormatGroup().setValue(view.getFileFormatRadioFilter(currentImportationSheme.getFileFormat()));
			view.getImportTypeGroup().setValue(view.getImportTypeRadioFilter(currentImportationSheme.getImportType()));
		}

	}
}
