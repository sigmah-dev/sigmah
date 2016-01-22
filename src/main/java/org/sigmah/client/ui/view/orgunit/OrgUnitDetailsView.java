package org.sigmah.client.ui.view.orgunit;

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
import org.sigmah.client.ui.presenter.orgunit.OrgUnitDetailsPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions.Scroll;
import org.sigmah.client.ui.widget.panel.Panels;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

/**
 * OrgUnitDetailsView implementation.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class OrgUnitDetailsView extends AbstractView implements OrgUnitDetailsPresenter.View {

	private ContentPanel contentOrgUnitDetailsPanel;
	private Button saveButton;
	private Button exportButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(createOrgUnitDetailsPanel());
	}

	/**
	 * Builds the details panel.
	 * 
	 * @return The component.
	 */
	private Component createOrgUnitDetailsPanel() {

		final Layout layout = Layouts.vBoxLayout(VBoxLayoutAlign.STRETCH, new LayoutOptions(new Padding(5), false, Scroll.VERTICAL));

		contentOrgUnitDetailsPanel = Panels.content(I18N.CONSTANTS.details(), layout);
		contentOrgUnitDetailsPanel.setTopComponent(buildToolbar());

		return contentOrgUnitDetailsPanel;
	}

	/**
	 * Builds the actions toolbar.
	 * 
	 * @return The actions toolbar.
	 */
	private ToolBar buildToolbar() {

		// Save button.
		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		saveButton.setEnabled(false);

		// Actions toolbar.
		final ToolBar toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setBorders(false);

		toolBar.add(saveButton);

		// ExportForm button
		exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());
		toolBar.add(new FillToolItem());
		toolBar.add(exportButton);

		return toolBar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getContentOrgUnitDetailsPanel() {
		return contentOrgUnitDetailsPanel;
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
	public Button getExcelExportButton() {
		return exportButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMainPanelWidget(final Widget widget) {
		contentOrgUnitDetailsPanel.add(widget);
		contentOrgUnitDetailsPanel.layout();
	}

}
