package org.sigmah.client.ui.view.project.dashboard;

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


import java.util.Arrays;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.dashboard.PhasesPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementType;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.grid.CheckColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.Widget;

/**
 * Phases view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PhasesView extends AbstractView implements PhasesPresenter.View {

	// CSS style names.
	public static final String PROJECT_CURRENT_PHASE_PANEL = "project-current-phase-panel";
	public static final String PROJECT_PHASE_ACTIVE = "project-phase-active";
	public static final String PROJECT_PHASE_CLOSED = "project-phase-closed";
	private static final String X_BORDER_PANEL = "x-border-panel";
	private static final String WHITE_TAB_BODY = "white-tab-body";

	private ContentPanel requiredElementContentPanel;
	private Grid<FlexibleElementDTO> gridRequiredElements;

	private TabPanel tabPanelPhases;
	private ToolBar toolBar;
	private LayoutContainer panelProjectModel;
	private LayoutContainer panelSelectedPhase;

	private Button buttonSavePhase;
	private Button buttonActivatePhase;
	private Button buttonPhaseGuide;

	/**
	 * The counter before the main panel is unmasked.
	 */
	private int maskCount;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		gridRequiredElements = new Grid<FlexibleElementDTO>(new ListStore<FlexibleElementDTO>(), getRequiredElementsColumModel());
		gridRequiredElements.setAutoExpandColumn(FlexibleElementDTO.LABEL);
		gridRequiredElements.getView().setForceFit(true);

		// Phases tab panel
		tabPanelPhases = new TabPanel();
		tabPanelPhases.setPlain(true);
		tabPanelPhases.setTabScroll(true);
		tabPanelPhases.setAnimScroll(true);
		tabPanelPhases.addStyleName(X_BORDER_PANEL);
		tabPanelPhases.addStyleName(WHITE_TAB_BODY);

		// Toolbar
		toolBar = new ToolBar();
		toolBar.setAlignment(HorizontalAlignment.LEFT);
		toolBar.setBorders(false);

		buttonSavePhase = Forms.button(I18N.CONSTANTS.projectSavePhaseButton(), IconImageBundle.ICONS.save());
		buttonActivatePhase = Forms.button(I18N.CONSTANTS.projectClosePhaseButton(), IconImageBundle.ICONS.activate());
		buttonPhaseGuide = Forms.button(I18N.CONSTANTS.projectPhaseGuideHeader(), IconImageBundle.ICONS.info());

		buttonActivatePhase.setEnabled(false);
		buttonSavePhase.setEnabled(false);
		buttonPhaseGuide.setEnabled(false);

		toolBar.add(buttonActivatePhase);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(buttonSavePhase);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(buttonPhaseGuide);

		// Tab item main panel
		panelProjectModel = Layouts.border(false, PROJECT_CURRENT_PHASE_PANEL);
		panelProjectModel.setBorders(false);

		panelSelectedPhase = Layouts.fit(false);

		requiredElementContentPanel = new ContentPanel(new FitLayout());

		requiredElementContentPanel.add(gridRequiredElements);
		panelProjectModel.add(requiredElementContentPanel,
			Layouts.borderLayoutData(LayoutRegion.WEST, Layouts.LEFT_COLUMN_WIDTH, Margin.HALF_RIGHT, Margin.HALF_BOTTOM, Margin.HALF_LEFT));

		final ContentPanel cp2 = Panels.content(I18N.CONSTANTS.phaseDetails(), Layouts.fitLayout());
		cp2.setScrollMode(Scroll.AUTOY);
		cp2.setTopComponent(toolBar);
		cp2.add(panelSelectedPhase, Layouts.fitData(Margin.HALF_TOP, Margin.HALF_RIGHT, Margin.HALF_BOTTOM, Margin.HALF_LEFT));

		panelProjectModel.add(cp2, Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.HALF_RIGHT, Margin.HALF_BOTTOM));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return tabPanelPhases;
	}

	/**
	 * Generates the {@link ColumnModel} for the required elements grid.
	 * 
	 * @return the {@link ColumnModel} for the required elements grid.
	 */
	private static ColumnModel getRequiredElementsColumModel() {

		// Element's label.
		final ColumnConfig labelColumn = new ColumnConfig(FlexibleElementDTO.LABEL, I18N.CONSTANTS.projectRequiredElementsGridLabel(), 150);

		// Element's completion.
		final CheckColumnConfig filledInColumn = new CheckColumnConfig("filledIn", I18N.CONSTANTS.projectRequiredElementsGridChecked(), 50);
		filledInColumn.setMenuDisabled(false);
		filledInColumn.setSortable(false);
		filledInColumn.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(FlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FlexibleElementDTO> store,
					Grid<FlexibleElementDTO> grid) {
				if (model.isFilledIn()) {
					return IconImageBundle.ICONS.elementCompleted().createImage();
				} else {
					return IconImageBundle.ICONS.elementUncompleted().createImage();
				}
			}
		});

		// Element's type.
		final ColumnConfig typeColumn = new ColumnConfig("typeOfElement", I18N.CONSTANTS.projectRequiredElementsElementType(), 75);
		typeColumn.setRenderer(new GridCellRenderer<FlexibleElementDTO>() {

			@Override
			public Object render(FlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FlexibleElementDTO> store,
					Grid<FlexibleElementDTO> grid) {
				return FlexibleElementType.getFlexibleElementTypeName(model);
			}
		});

		return new ColumnModel(Arrays.asList(filledInColumn, labelColumn, typeColumn));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void mask(int count) {

		maskCount = count;

		tabPanelPhases.mask(I18N.CONSTANTS.loading());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean unmask() {

		maskCount--;

		if (maskCount == 0) {
			tabPanelPhases.unmask();
			return true;
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getButtonActivatePhase() {
		return buttonActivatePhase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getButtonPhaseGuide() {
		return buttonPhaseGuide;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getButtonSavePhase() {
		return buttonSavePhase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Grid<FlexibleElementDTO> getGridRequiredElements() {
		return gridRequiredElements;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutContainer getPanelProjectModel() {
		return panelProjectModel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutContainer getPanelSelectedPhase() {
		return panelSelectedPhase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TabPanel getTabPanelPhases() {
		return tabPanelPhases;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flushToolbar() {
		toolBar.removeAll();
		toolBar.removeAllListeners();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fillToolbar(final boolean changePhaseAuthorized) {

		flushToolbar();

		if (changePhaseAuthorized) {
			toolBar.add(buttonActivatePhase);
			toolBar.add(new SeparatorToolItem());
		}

		toolBar.add(buttonSavePhase);
		toolBar.add(new SeparatorToolItem());
		toolBar.add(buttonPhaseGuide);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ContentPanel getRequiredElementContentPanel() {
		return requiredElementContentPanel;
	}

	@Override
	public void setLoading(boolean loading) {
		if(loading) {
			mask(1);
		} else {
			unmask();
		}
	}

	@Override
	public boolean isLoading() {
		return maskCount > 0;
	}

	@Override
	public void layout() {
		layoutContainer.layout(true);
	}
	
}
