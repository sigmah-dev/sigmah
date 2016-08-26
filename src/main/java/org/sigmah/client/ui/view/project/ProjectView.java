package org.sigmah.client.ui.view.project;

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


import com.extjs.gxt.ui.client.GXT;
import java.util.LinkedHashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.presenter.project.ProjectPresenter;
import org.sigmah.client.ui.presenter.project.ProjectPresenter.ExportActionHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.Orientation;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.MessageBox.MessageBoxType;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.CoreVersionAction;
import org.sigmah.shared.dto.referential.CoreVersionActionType;

/**
 * {@link ProjectPresenter} view.
 *
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ProjectView extends AbstractView implements ProjectPresenter.View {

	/**
	 * Project title max <em>displayed</em> length.
	 */
	private static final int PROJECT_TITLE_MAX_LENGTH = 110;

	/**
	 * Amendments panel width.
	 */
	private static final float AMENDMENTS_PANEL_WIDTH = 250f;

	// CSS style names.
	public static final String STYLE_HEADER_BANNER = "banner";
	public static final String STYLE_HEADER_BANNER_LOGO = "banner-logo";
	public static final String STYLE_HEADER_BANNER_FLEX = "banner-flex";

	// Grids coordinates.
	private static final Pair<Integer, Integer> HEADER_BANNER_LOGO_CELL = new Pair<Integer, Integer>(0, 0);
	private static final Pair<Integer, Integer> HEADER_BANNER_WIDGET_CELL = new Pair<Integer, Integer>(0, 1);

	// UI widgets.
	private ContentPanel projectBannerPanel;
	private Grid projectBannerGrid;

	private ContentPanel coreVersionPanel;
	private FlexTable coreVersionTable;
	private Button lockProjectCoreButton;
	private Button unlockProjectCoreButton;
	private Button validateVersionButton;
	private Button backToWorkingVersionButton;
	private ComboBox<CoreVersionAction> coreVersionActionComboBox;

	private SubMenuWidget subMenu;
	private LayoutContainer subViewPlaceHolder;

	private Button exportButton;
	private Button deleteButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Banner container.
		// --

		final LayoutContainer headerContainer = Layouts.border();
		headerContainer.add(createProjectBannerPanel(), Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.RIGHT));
		headerContainer.add(createAmendmentsPanel(), Layouts.borderLayoutData(LayoutRegion.EAST, AMENDMENTS_PANEL_WIDTH));

		// --
		// Menu container.
		// --

		final Map<Page, String> linksMap = new LinkedHashMap<Page, String>();
		linksMap.put(Page.PROJECT_DASHBOARD, I18N.CONSTANTS.projectTabDashboard());
		linksMap.put(Page.PROJECT_DETAILS, I18N.CONSTANTS.projectDetails());
		linksMap.put(Page.PROJECT_LOGFRAME, I18N.CONSTANTS.projectTabLogFrame());
		linksMap.put(Page.PROJECT_INDICATORS_MANAGEMENT, I18N.CONSTANTS.projectTabIndicators());
		linksMap.put(Page.PROJECT_INDICATORS_MAP, I18N.CONSTANTS.projectTabMap());
		linksMap.put(Page.PROJECT_INDICATORS_ENTRIES, I18N.CONSTANTS.projectTabDataEntry());
		linksMap.put(Page.PROJECT_TEAM_MEMBERS, I18N.CONSTANTS.projectTabTeamMembers());
		linksMap.put(Page.PROJECT_CALENDAR, I18N.CONSTANTS.projectTabCalendar());
		linksMap.put(Page.PROJECT_REPORTS, I18N.CONSTANTS.projectTabReports());

		final Grid buttonsGrid = new Grid(1, 2);
		exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());
		deleteButton = Forms.button(I18N.CONSTANTS.deleteProjectAnchor(), IconImageBundle.ICONS.remove());
		buttonsGrid.setWidget(0, 0, exportButton);
		buttonsGrid.setWidget(0, 1, deleteButton);
		buttonsGrid.getElement().getStyle().setFloat(Float.RIGHT);

		subMenu = new SubMenuWidget(Orientation.HORIZONTAL, linksMap);
		subMenu.asWidget().getElement().getStyle().setFloat(Float.LEFT);

		final FlowPanel menuContainer = new FlowPanel();
		menuContainer.add(subMenu.asWidget());
		menuContainer.add(buttonsGrid);

		// --
		// Center container.
		// --

		subViewPlaceHolder = Layouts.fit();

		// --
		// Main layout.
		// --

		final LayoutContainer mainContainer = Layouts.vBox();
		mainContainer.add(headerContainer, Layouts.vBoxData(Margin.BOTTOM));
		mainContainer.add(menuContainer);

		add(mainContainer, Layouts.borderLayoutData(LayoutRegion.NORTH, Layouts.BANNER_PANEL_HEIGHT, Margin.BOTTOM));
		add(subViewPlaceHolder);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutContainer getPlaceHolder() {
		return subViewPlaceHolder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubMenuWidget getSubMenuWidget() {
		return subMenu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectTitle(final String projectName, final String projectFullName) {

		final StringBuilder builder = new StringBuilder();
		builder.append(I18N.CONSTANTS.projectMainTabTitle()).append(' ').append(projectName);

		if (ClientUtils.isNotBlank(projectFullName)) {
			builder.append(" (").append(ClientUtils.abbreviate(projectFullName, PROJECT_TITLE_MAX_LENGTH)).append(')');
		}

		// Panel header title.
		projectBannerPanel.setHeadingText(builder.toString());

		// Tool tip configuration.
		final ToolTipConfig projectBannerToolTipConfig = new ToolTipConfig(builder.toString());
		projectBannerToolTipConfig.setMaxWidth(500);
		projectBannerPanel.setToolTip(projectBannerToolTipConfig);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectLogo(final ProjectModelType projectType) {

		final AbstractImagePrototype projectIcon = FundingIconProvider.getProjectTypeIcon(projectType, IconSize.LARGE);
		projectBannerGrid.setWidget(HEADER_BANNER_LOGO_CELL.left, HEADER_BANNER_LOGO_CELL.right, projectIcon.createImage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectBanner(final Widget bannerWidget) {
		projectBannerGrid.setWidget(HEADER_BANNER_WIDGET_CELL.left, HEADER_BANNER_WIDGET_CELL.right, bannerWidget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HTMLTable buildBannerTable(final int rows, final int cols) {

		final Grid gridLayout = new Grid(rows, cols);
		gridLayout.addStyleName(ProjectView.STYLE_HEADER_BANNER_FLEX);
		gridLayout.setCellPadding(0);
		gridLayout.setCellSpacing(0);
		gridLayout.setWidth("100%");
		gridLayout.setHeight("100%");

		for (int i = 0; i < gridLayout.getColumnCount() - 1; i++) {
			gridLayout.getColumnFormatter().setWidth(i, "325px");
		}

		return gridLayout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getProjectCoreVersionPanel() {
		return coreVersionPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getLockProjectCoreButton() {
		return lockProjectCoreButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getUnlockProjectCoreButton() {
		return unlockProjectCoreButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getValidateVersionButton() {
		return validateVersionButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getBackToWorkingVersionButton() {
		return backToWorkingVersionButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<CoreVersionAction> getCoreVersionActionComboBox() {
		return coreVersionActionComboBox;
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
	public Button getDeleteButton() {
		return deleteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getProjectBannerPanel() {
		return projectBannerPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildExportDialog(final ExportActionHandler handler) {

		final Window w = new Window();
		w.setPlain(true);
		w.setModal(true);
		w.setBlinkModal(true);
		w.setLayout(new FitLayout());
		w.setSize(350, 180);
		w.setHeadingHtml(I18N.CONSTANTS.exportData());

		final FormPanel panel = Forms.panel();

		final CheckBox synthesisBox = Forms.checkbox(I18N.CONSTANTS.projectSynthesis(), Boolean.TRUE);
		synthesisBox.setEnabled(false);
		final CheckBox indicatorBox = Forms.checkbox(I18N.CONSTANTS.flexibleElementIndicatorsList());
		final CheckBox logFrameBox = Forms.checkbox(I18N.CONSTANTS.logFrame());
		final CheckBox contactsBox = Forms.checkbox(I18N.CONSTANTS.contacts());

		final CheckBoxGroup options =
				Forms.checkBoxGroup(I18N.CONSTANTS.exportOptions(), com.extjs.gxt.ui.client.Style.Orientation.VERTICAL, synthesisBox, logFrameBox, indicatorBox, contactsBox);

		panel.add(options);

		final Button export = Forms.button(I18N.CONSTANTS.export());
		panel.getButtonBar().add(export);
		export.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				if (handler != null) {
					handler.onExportProject(indicatorBox, logFrameBox, contactsBox);
				}
				w.hide();
			}
		});

		w.add(panel);
		w.show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void buildCreateCoreVersionDialog(final SelectionListener<ButtonEvent> callback) {
		final MessageBox box = new MessageBox();
		box.setTitleHtml(I18N.CONSTANTS.projectCoreValidateVersion());
		box.setMessage(I18N.CONSTANTS.projectCoreNewVersion());
		box.setType(MessageBoxType.PROMPT);

		// No automatic button.
		box.setButtons("");

		final Dialog dialog = box.getDialog();

		final SelectionListener<ButtonEvent> hideDialog = new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				dialog.hide(ce.getButton());
			}
		};

		// Create core version button.
		final Button okButton = new Button(I18N.CONSTANTS.projectCoreNewVersionButton());
		okButton.addSelectionListener(hideDialog);
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				ce.setSource(box.getTextBox().getValue());
				callback.handleEvent(ce);
			}
		});

		// Cancel button.
		final Button cancelButton = new Button(GXT.MESSAGES.messageBox_cancel());
		cancelButton.addSelectionListener(hideDialog);

		dialog.addButton(cancelButton);
		dialog.addButton(okButton);

		box.show();
	}

	/**
	 * Changes the current view to match the given state.
	 *
	 * @param state Current project core version state.
	 * @param coreVersionWasModified State of the current project core version (modified or not).
	 */
	@Override
	public void setProjectCoreVersionState(AmendmentState state, boolean coreVersionWasModified) {

		coreVersionTable.clear();
		coreVersionTable.removeAllRows();

		final boolean locked = state == AmendmentState.LOCKED;
		final boolean ended = state == AmendmentState.PROJECT_ENDED;

		if(state == AmendmentState.DRAFT || locked || ended) {
			validateVersionButton.setEnabled(locked && !ended);
			lockProjectCoreButton.setEnabled(!ended);
			unlockProjectCoreButton.setEnabled(true);

			coreVersionTable.insertRow(0);
			coreVersionTable.insertRow(0);
			coreVersionTable.addCell(0);
			coreVersionTable.addCell(0);
			coreVersionTable.addCell(1);

			coreVersionTable.getFlexCellFormatter().setWidth(0, 0, "50%");
			coreVersionTable.getFlexCellFormatter().setWidth(0, 1, "50%");
			coreVersionTable.getFlexCellFormatter().setColSpan(1, 0, 2);

			coreVersionTable.setWidget(0, 0, locked && !ended ? unlockProjectCoreButton : lockProjectCoreButton);
			coreVersionTable.setWidget(0, 1, validateVersionButton);
			coreVersionTable.setWidget(1, 0, coreVersionActionComboBox);

		} else {
			coreVersionTable.setWidget(0, 0, backToWorkingVersionButton);
			coreVersionTable.setWidget(1, 0, coreVersionActionComboBox);
		}

		coreVersionPanel.layout(true);

	}

	/**
	 * Displays the given core versions in the core version combo box.
	 *
	 * @param coreVersions List of project core versions to display.
	 * @param coreVersionWasModified State of the current project core version (modified or not).
	 * @param canRenameVersion <code>true</code> to add the "Rename core version" menu item.
	 */
	@Override
	public void setProjectCoreVersions(List<AmendmentDTO> coreVersions, boolean coreVersionWasModified, boolean canRenameVersion) {
		final ListStore<CoreVersionAction> store = coreVersionActionComboBox.getStore();
		store.removeAll();

		// Add compare and rename actions.
		coreVersionActionComboBox.getStore().add(CoreVersionEntry.create(I18N.CONSTANTS.amendmentCompare(), CoreVersionActionType.FUNCTION_COMPARE));
		if(canRenameVersion) {
			coreVersionActionComboBox.getStore().add(CoreVersionEntry.create(I18N.CONSTANTS.amendmentRename(), CoreVersionActionType.FUNCTION_RENAME));
		}

		// Add the separator if needed.
		if(!coreVersions.isEmpty()) {
			// Displays the name of the last core version if the project core has not been modified.
			final AmendmentDTO lastCoreVersion = coreVersions.get(coreVersions.size() - 1);
			coreVersionActionComboBox.setEmptyText(!coreVersionWasModified ? lastCoreVersion.getVersion() + ". " + lastCoreVersion.getName() : "");

			// Add a separator between the actions and the core versions.
			store.add(CoreVersionEntry.createSeparator());
			store.add(CoreVersionEntry.createComment(I18N.CONSTANTS.projectCoreDisplayVersion()));

			// Add the core versions to the store.
			for(final AmendmentDTO coreVersion : coreVersions) {
				coreVersion.set(CoreVersionEntry.TYPE, CoreVersionActionType.CORE_VERSION.name());
				store.add(coreVersion);
			}

		} else {
			coreVersionActionComboBox.setEmptyText(I18N.CONSTANTS.projectCoreNoValidated());
		}
	}



	/**
	 * Creates the project banner panel.
	 *
	 * @return The project banner panel.
	 */
	private Component createProjectBannerPanel() {

		// Main panel.
		projectBannerPanel = Panels.content(I18N.CONSTANTS.loading()); // Temporary title.

		// Main grid.
		projectBannerGrid = new Grid(1, 2);
		projectBannerGrid.addStyleName(STYLE_HEADER_BANNER);
		projectBannerGrid.setCellPadding(0);
		projectBannerGrid.setCellSpacing(0);
		projectBannerGrid.setWidth("100%");
		projectBannerGrid.setHeight("100%");

		// Logo cell style.
		projectBannerGrid.getCellFormatter().setStyleName(HEADER_BANNER_LOGO_CELL.left, HEADER_BANNER_LOGO_CELL.right, STYLE_HEADER_BANNER_LOGO);

		projectBannerPanel.add(projectBannerGrid);

		return projectBannerPanel;
	}

	/**
	 * Creates the amendments panel.
	 *
	 * @return The amendments panel component.
	 */
	private Component createAmendmentsPanel() {
		// Lock project core version button.
		lockProjectCoreButton = Forms.button(I18N.CONSTANTS.projectCorelockButton(), IconImageBundle.ICONS.lock());

		// Unlock project core version button.
		unlockProjectCoreButton = Forms.button(I18N.CONSTANTS.projectCoreUnlockButton(), IconImageBundle.ICONS.unlock());

		// Validate project core version button.
		validateVersionButton = Forms.button(I18N.CONSTANTS.projectCoreValidateVersion(), IconImageBundle.ICONS.validate());

		// Back to working version button.
		backToWorkingVersionButton = Forms.button(I18N.CONSTANTS.projectCoreBackToWorkingVersion(), IconImageBundle.ICONS.amendment());

		// Core version and actions combo box.
		coreVersionActionComboBox = new ComboBox<CoreVersionAction>();
		coreVersionActionComboBox.setDisplayField("name");
		coreVersionActionComboBox.setValueField("id");
		coreVersionActionComboBox.setStore(new ListStore<CoreVersionAction>());
		coreVersionActionComboBox.setTemplate(XTemplate.create(getTemplate()));
		coreVersionActionComboBox.setItemSelector(".x-combo-list-item");
		coreVersionActionComboBox.setTriggerAction(ComboBox.TriggerAction.ALL);

		// Define a width of 100% to all buttons.
		expandWidth(lockProjectCoreButton, unlockProjectCoreButton, validateVersionButton, backToWorkingVersionButton, coreVersionActionComboBox);

		// Layout.
		coreVersionTable = new FlexTable();
		coreVersionTable.setCellSpacing(6);

		// Content panel.
		coreVersionPanel = Panels.content(I18N.CONSTANTS.projectCoreBoxTitle());
		coreVersionPanel.setIcon(IconImageBundle.ICONS.DNABrownRed());
		coreVersionPanel.add(coreVersionTable);

		return coreVersionPanel;
	}

	private void expandWidth(Component... components) {
		for(final Component component : components) {
			component.setWidth("100%");
		}
	}

	/**
	 * Creates the XTemplate for the project core version combo box.
	 * @return The XTemplate used by the project core version combo box.
	 */
	private native String getTemplate() /*-{
		return [
			'<tpl for=".">',
			'<tpl if="entry == &quot;CORE_VERSION&quot;">',
			'<div class="x-combo-list-item" style="position: relative"><span style="font-weight: bold">{version}.</span> {name} <div style="color: #CCC; width: 60px; position: absolute; right: 0; top: 2px">{history_date:date("M/d/yy")}</div></div>',
			'</tpl>',
			'<tpl if="entry == &quot;FUNCTION_COMPARE&quot; || entry == &quot;FUNCTION_RENAME&quot;">',
			'<div class="x-combo-list-item">{name}</div>',
			'</tpl>',
			'<tpl if="entry == &quot;SEPARATOR&quot;">',
			'<div class="x-combo-list-item x-combo-unselectable" style="padding: 0; margin: 0"><hr style="border: 0; border-bottom: 1px solid #CCC"/></div>',
			'</tpl>',
			'<tpl if="entry == &quot;COMMENT&quot;">',
			'<div class="x-combo-list-item x-combo-unselectable" style="font-style: italic; color: #CCC; padding: 2px">{name}</div>',
			'</tpl>',
			'</tpl>'
		].join("");
	}-*/;

}
