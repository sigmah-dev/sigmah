package org.sigmah.client.ui.view.reports;

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
import org.sigmah.client.ui.presenter.reports.ReportCreatePresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * Report create frame view used to create a project or org. unit report.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportCreateView extends AbstractPopupView<PopupWidget> implements ReportCreatePresenter.View {

	private FormPanel form;
	private TextField<String> reportTitleField;
	private LabelField elementLabelField;
	private Button saveButton;
	private Button cancelButton;

	/**
	 * Builds the view.
	 */
	public ReportCreateView() {
		super(new PopupWidget(true), 400);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		form = Forms.panel();

		reportTitleField = Forms.text(I18N.CONSTANTS.reportName(), true);
		elementLabelField = Forms.label(I18N.CONSTANTS.flexibleElementReport());

		saveButton = Forms.button(I18N.CONSTANTS.formWindowSubmitAction(), IconImageBundle.ICONS.save());
		cancelButton = Forms.button(I18N.CONSTANTS.cancel());

		form.add(reportTitleField);
		form.add(elementLabelField);

		form.addButton(cancelButton);
		form.addButton(saveButton);

		initPopup(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return form;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TextField<String> getReportTitleField() {
		return reportTitleField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LabelField getElementLabelField() {
		return elementLabelField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

}
