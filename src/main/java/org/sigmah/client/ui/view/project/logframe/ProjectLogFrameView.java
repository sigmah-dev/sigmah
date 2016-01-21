package org.sigmah.client.ui.view.project.logframe;

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
import org.sigmah.client.ui.presenter.project.logframe.ProjectLogFramePresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions;
import org.sigmah.client.ui.widget.layout.Layouts.LayoutOptions.Scroll;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Padding;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout.VBoxLayoutAlign;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link ProjectLogFramePresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectLogFrameView extends AbstractView implements ProjectLogFramePresenter.View {

	// CSS style names.
	private static final String STYLE_GRID_MAIN_PANEL = "logframe-grid-main-panel";
	private static final String STYLE_FLEXIBILITY_TEXT_FIELD = "flexibility-text-field";

	/**
	 * Width of the action title and main objective label column (in pixels).
	 */
	private static final int LABEL_WIDTH = 120;

	// Main panel.
	private ContentPanel mainPanel;

	// Toolbar buttons.
	private Button saveButton;
	private Button copyButton;
	private Button pasteButton;
	private Button exportButton;

	// Title lable
	private LabelField titleField;
	private TextField<String> mainObjectiveField;

	// Grid.
	@Inject
	private ProjectLogFrameGrid logFrameGrid;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// Configuration.
		final VBoxLayout layout = Layouts.vBoxLayout(VBoxLayoutAlign.STRETCH, new LayoutOptions(new Padding(5), false, Scroll.VERTICAL));
		mainPanel = Panels.content(I18N.CONSTANTS.logFrame() + "&nbsp;" + IconImageBundle.ICONS.DNABrownGreen().getHTML(), layout, STYLE_GRID_MAIN_PANEL);
		mainPanel.setBorders(true);

		// Toolbar.
		mainPanel.setTopComponent(buildToolbar());

		mainPanel.add(buildHeaderPanel(), Layouts.vBoxData(0d, Margin.HALF_BOTTOM, Margin.RIGHT, Margin.LEFT));
		mainPanel.add(logFrameGrid.asWidget(), Layouts.vBoxData(0d, Margin.RIGHT, Margin.LEFT));

		add(mainPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getMainPanel() {
		return mainPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectLogFrameGrid getLogFrameGrid() {
		return logFrameGrid;
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
	public Button getCopyButton() {
		return copyButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getPasteButton() {
		return pasteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getExportButton() {
		return exportButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Object> getLogFrameTitleField() {
		return titleField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getLogFrameMainObjectiveField() {
		return mainObjectiveField;
	}

	/**
	 * Builds the log frame header panel.
	 * 
	 * @return The main objective panel.
	 */
	private ContentPanel buildHeaderPanel() {

		// --
		// Title field.
		// --

		titleField = Forms.label(I18N.CONSTANTS.logFrameActionTitle());
		titleField.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				mainObjectiveField.focus();
			}

		});

		// --
		// Main objective field.
		// --

		mainObjectiveField = Forms.text(I18N.CONSTANTS.logFrameMainObjective(), false);
		mainObjectiveField.addStyleName(STYLE_FLEXIBILITY_TEXT_FIELD);

		// --
		// Header form panel.
		// --

		final FormPanel headerFormPanel = Forms.panel(LABEL_WIDTH);
		headerFormPanel.setPadding(0, false);

		headerFormPanel.add(titleField);
		headerFormPanel.add(mainObjectiveField);

		return headerFormPanel;
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

		// Copy button.
		copyButton = Forms.button(I18N.CONSTANTS.copy());
		copyButton.setEnabled(true);

		// Paste button.
		pasteButton = Forms.button(I18N.CONSTANTS.paste());
		pasteButton.setEnabled(false);

		// ExportForm button
		exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());

		// Actions toolbar.
		final ToolBar toolBar = new ToolBar();
		toolBar.setBorders(false);

		toolBar.add(saveButton);
		// Use FillToolItem to align the left 3 buttons on the right
		toolBar.add(new FillToolItem());
		toolBar.add(copyButton);
		toolBar.add(pasteButton);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(exportButton);

		return toolBar;
	}

}
