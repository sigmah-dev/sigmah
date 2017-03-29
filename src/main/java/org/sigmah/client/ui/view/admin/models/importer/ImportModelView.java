package org.sigmah.client.ui.view.admin.models.importer;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportModelPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)V2.0
 */

public class ImportModelView extends AbstractPopupView<PopupWidget> implements ImportModelPresenter.View {

	private Button importButton;
	private Button cancelButton;
	private FormPanel importPanel;
	private FileUploadField uploadField;
	private Label labelProjectModelType;
	private ListBox projectModelTypeList;

	public ImportModelView() {
		super(new PopupWidget(true), 450);
	}

	@Override
	public void initialize() {

		importPanel = Forms.panel();

		// importPanel.setLayout(Layouts.vBoxLayout());

		importPanel.setEncoding(Encoding.MULTIPART);
		importPanel.setMethod(Method.POST);

		importPanel.setBodyBorder(false);
		importPanel.setHeaderVisible(false);
		importPanel.setPadding(5);

		importPanel.setLabelWidth(120);
		importPanel.setFieldWidth(250);

		importPanel.setAutoHeight(true);
		importPanel.setAutoWidth(true);

		uploadField = new FileUploadField();
		uploadField.setAllowBlank(false);
		uploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		uploadField.setFieldLabel(I18N.CONSTANTS.adminFileImport());
		importPanel.add(uploadField, Layouts.vBoxData());

		// Add project model type choice

		labelProjectModelType = new Label(I18N.CONSTANTS.adminProjectModelType());

		labelProjectModelType.setStyleAttribute("font-size", "12px");
		labelProjectModelType.setStyleAttribute("margin-right", "30px");

		projectModelTypeList = new ListBox();

		projectModelTypeList.setName("project-model-type");
		projectModelTypeList.setVisibleItemCount(1);
		projectModelTypeList.addItem(ProjectModelType.getName(ProjectModelType.NGO), "NGO");
		projectModelTypeList.addItem(ProjectModelType.getName(ProjectModelType.FUNDING), "FUNDING");
		projectModelTypeList.addItem(ProjectModelType.getName(ProjectModelType.LOCAL_PARTNER), "LOCAL_PARTNER");

		HorizontalPanel hProjectPanel = new HorizontalPanel();

		hProjectPanel.add(labelProjectModelType);
		hProjectPanel.add(projectModelTypeList);

		importPanel.add(hProjectPanel, Layouts.vBoxData());

		labelProjectModelType.hide();
		projectModelTypeList.setVisible(false);

		importButton = new Button(I18N.CONSTANTS.ok());
		cancelButton = new Button(I18N.CONSTANTS.cancel());

		HorizontalPanel hPanel = new HorizontalPanel();

		hPanel.add(importButton);
		hPanel.add(cancelButton);

		importPanel.add(hPanel, Layouts.vBoxData());

		initPopup(importPanel);

	}

	@Override
	public Button getImportButton() {
		return importButton;
	}

	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	@Override
	public FormPanel getForm() {
		return this.importPanel;
	}

	@Override
	public FileUploadField getUploadField() {
		return uploadField;
	}

	@Override
	public void setProjectPerspective() {

		labelProjectModelType.show();
		projectModelTypeList.setVisible(true);

		importPanel.setLabelWidth(120);
		importPanel.setFieldWidth(270);

		importPanel.recalculate();

	}

	@Override
	public void removeProjectPerspective() {

		labelProjectModelType.hide();
		projectModelTypeList.setVisible(false);

		importPanel.setLabelWidth(120);
		importPanel.setFieldWidth(250);

		importPanel.recalculate();

	}

}
