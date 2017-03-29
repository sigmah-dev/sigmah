package org.sigmah.client.ui.view.admin;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.ReportModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.ReportModelsAdminPresenter.ReportModelPresenterHandler;
import org.sigmah.client.ui.presenter.admin.ReportModelsAdminPresenter.ReportModelSectionPresenterHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.ToggleAnchor;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.inject.Singleton;

/**
 * {@link ReportModelsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public class ReportModelsAdminView extends AbstractView implements ReportModelsAdminPresenter.View {

	private Grid<ReportModelDTO> reportModelsGrid;
	private ListStore<ReportModelDTO> modelsStore;
	private ListStore<ProjectReportModelSectionDTO> reportSectionsStore;
	private ListStore<ProjectReportModelSectionDTO> reportSectionsComboStore;
	private EditorGrid<ProjectReportModelSectionDTO> sectionsGrid;
	private Button saveReportSectionButton;
	private Button addReportSectionButton;
	private ComboBox<ProjectReportModelSectionDTO> parentSectionsCombo;
	private ContentPanel reportModelPanel;
	private ContentPanel reportModelSectionsPanel;
	private Button addReportButton;
	private TextField<String> reportName;
	private List<ProjectReportModelSectionDTO> sectionsToBeSaved = new ArrayList<ProjectReportModelSectionDTO>();

	private Button buttonImport;
	private ReportModelPresenterHandler reportModelPresenterHandler;
	private Button deleteReportModelButton;
	private ReportModelSectionPresenterHandler reportModelSectionPresenterHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		reportModelPanel = Panels.content(null, Layouts.fitLayout());

		reportModelsGrid = buildModelsListGrid();
		reportModelPanel.add(reportModelsGrid);
		reportModelPanel.setTopComponent(reportModelToolBar());

		reportModelSectionsPanel = Panels.content(null, false, Layouts.fitLayout(), Scroll.AUTOY);
		reportModelSectionsPanel.add(buildReportSectionsGrid());
		reportModelSectionsPanel.setTopComponent(reportSectionToolBar());

		add(reportModelPanel, Layouts.borderLayoutData(LayoutRegion.WEST, 370f, Margin.HALF_RIGHT));
		add(reportModelSectionsPanel, Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.HALF_LEFT));

	}

	/**
	 * Build Report Model Sections Grid.
	 * 
	 * @return The {@link EditorGrid} component.
	 */
	private EditorGrid<ProjectReportModelSectionDTO> buildReportSectionsGrid() {

		reportSectionsStore = new ListStore<ProjectReportModelSectionDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("id");
		column.setWidth(50);
		column.setHeaderHtml(I18N.CONSTANTS.adminFlexibleFieldId());
		configs.add(column);

		column = new ColumnConfig();
		column.setId("index");
		column.setWidth(50);
		column.setHeaderHtml(I18N.CONSTANTS.adminReportSectionIndex());
		NumberField index = new NumberField();
		index.setAllowBlank(false);
		column.setEditor(new CellEditor(index) {

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((Number) value).intValue();
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("name");
		column.setWidth(280);
		column.setHeaderHtml(I18N.CONSTANTS.adminReportSectionName());
		TextField<String> name = new TextField<String>();
		name.setAllowBlank(false);
		column.setEditor(new CellEditor(name));
		configs.add(column);

		column = new ColumnConfig();
		column.setId("numberOfTextarea");
		column.setWidth(75);
		column.setHeaderHtml(I18N.CONSTANTS.adminReportSectionNbText());
		NumberField nbTextAreas = new NumberField();
		nbTextAreas.setAllowBlank(false);
		column.setEditor(new CellEditor(nbTextAreas) {

			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((Number) value).intValue();
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("parentSectionModelName");
		column.setWidth(280);
		column.setHeaderHtml(I18N.CONSTANTS.adminReportSectionParentSection());
		parentSectionsCombo = new ComboBox<ProjectReportModelSectionDTO>();
		parentSectionsCombo.setTriggerAction(TriggerAction.ALL);
		parentSectionsCombo.setEditable(false);
		reportSectionsComboStore = new ListStore<ProjectReportModelSectionDTO>();
		parentSectionsCombo.setStore(reportSectionsComboStore);
		parentSectionsCombo.setDisplayField("compositeName");

		column.setEditor(new CellEditor(parentSectionsCombo) {

			@Override
			// Get the ProjectReportModelSection equivalent to the value
			// displayed when there's one
			public Object preProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				ProjectReportModelSectionDTO section = null;
				for (ProjectReportModelSectionDTO sectionI : reportSectionsStore.getModels()) {
					if (sectionI.getName().equals(value.toString())) {
						section = sectionI;
					}
				}
				return section;
			}

			@Override
			// Get the field to display if a ProjectReportModelSection has been
			// chosen
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				Log.debug("value " + ((ProjectReportModelSectionDTO) value).getName());
				return ((ProjectReportModelSectionDTO) value).getName();
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setWidth(75);
		column.setAlignment(Style.HorizontalAlignment.LEFT);
		column.setRenderer(new GridCellRenderer<ProjectReportModelSectionDTO>() {

			@Override
			public Object render(final ProjectReportModelSectionDTO model, final String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ProjectReportModelSectionDTO> store, Grid<ProjectReportModelSectionDTO> grid) {

				Button deleteSectionButton = new Button(I18N.CONSTANTS.delete());
				deleteSectionButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						reportModelSectionPresenterHandler.onClickHandler(model);
					}
				});

				return deleteSectionButton;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		sectionsGrid = new EditorGrid<ProjectReportModelSectionDTO>(reportSectionsStore, cm);
		sectionsGrid.enable();
		sectionsGrid.getView().setForceFit(true);
		return sectionsGrid;
	}

	/**
	 * Build Report Model Grid
	 * 
	 * @return Grid<ReportModelDTO>
	 */
	private Grid<ReportModelDTO> buildModelsListGrid() {

		modelsStore = new ListStore<ReportModelDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("name", I18N.CONSTANTS.adminReportName(), 280);
		column.setRenderer(new GridCellRenderer<ReportModelDTO>() {

			@Override
			public Object render(final ReportModelDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ReportModelDTO> store,
					Grid<ReportModelDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getName());
				anchor.setAnchorMode(true);
				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						reportModelPresenterHandler.onSelectHandler(model);
					}
				});
				return anchor;
			}

		});
		configs.add(column);

		column = new ColumnConfig();
		column.setWidth(70);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ReportModelDTO>() {

			@Override
			public Object render(final ReportModelDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ReportModelDTO> store,
					Grid<ReportModelDTO> grid) {

				Button buttonExport = new Button(I18N.CONSTANTS.export());
				buttonExport.addListener(Events.OnClick, new Listener<BaseEvent>() {

					@Override
					public void handleEvent(BaseEvent be) {
						reportModelPresenterHandler.onClickHandler(model);
					};
				});
				return buttonExport;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ReportModelDTO> grid = new Grid<ReportModelDTO>(modelsStore, cm);
		grid.getView().setForceFit(true);
		return grid;
	}

	/**
	 * Builld Report Model ToolBar
	 * 
	 * @return ToolBar
	 */
	private ToolBar reportModelToolBar() {

		ToolBar toolbar = new ToolBar();

		reportName = new TextField<String>();
		reportName.setFieldLabel(I18N.CONSTANTS.adminReportName());
		toolbar.add(reportName);

		addReportButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		deleteReportModelButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		buttonImport = new Button(I18N.CONSTANTS.importItem());

		toolbar.add(addReportButton);
		toolbar.add(deleteReportModelButton);
		toolbar.add(buttonImport);

		return toolbar;
	}

	/**
	 * build Report Model Sections ToolBar
	 * 
	 * @return ToolBar
	 */
	private ToolBar reportSectionToolBar() {

		ToolBar toolbar = new ToolBar();

		addReportSectionButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addReportSectionButton.disable();
		toolbar.add(addReportSectionButton);

		saveReportSectionButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		saveReportSectionButton.disable();
		toolbar.add(saveReportSectionButton);

		return toolbar;
	}

	@Override
	public LoadingMask getReportModelsSectionsLoadingMonitor() {
		return new LoadingMask(reportModelSectionsPanel, I18N.CONSTANTS.loading());
	}

	@Override
	public LoadingMask getReportModelsLoadingMonitor() {
		return new LoadingMask(reportModelPanel, I18N.CONSTANTS.loading());
	}

	@Override
	public Button getAddReportButton() {

		return this.addReportButton;

	}

	@Override
	public ListStore<ReportModelDTO> getModelsStore() {

		return this.modelsStore;
	}

	@Override
	public TextField<String> getReportName() {
		return this.reportName;
	}

	@Override
	public Button getSaveReportSectionButton() {
		return this.saveReportSectionButton;
	}

	@Override
	public EditorGrid<ProjectReportModelSectionDTO> getSectionsGrid() {

		return this.sectionsGrid;
	}

	@Override
	public ListStore<ProjectReportModelSectionDTO> getReportSectionsStore() {
		return this.reportSectionsStore;
	}

	@Override
	public ListStore<ProjectReportModelSectionDTO> getReportSectionsComboStore() {
		return this.reportSectionsComboStore;
	}

	@Override
	public Button getAddReportSectionButton() {
		return this.addReportSectionButton;
	}

	@Override
	public List<ProjectReportModelSectionDTO> getSectionsToBeSaved() {
		return this.sectionsToBeSaved;
	}

	@Override
	public Grid<ReportModelDTO> getReportModelsGrid() {
		return this.reportModelsGrid;
	}

	@Override
	public ComboBox<ProjectReportModelSectionDTO> getParentSectionsCombo() {
		return this.parentSectionsCombo;
	}

	@Override
	public void setReportModelPresenterHandler(ReportModelPresenterHandler handler) {
		reportModelPresenterHandler = handler;
	}

	@Override
	public Button getButtonImport() {
		return buttonImport;
	}

	@Override
	public Button getDeleteReportModelButton() {
		return deleteReportModelButton;
	}

	@Override
	public ContentPanel getReportModelPanel() {
		return reportModelPanel;
	}

	@Override
	public ContentPanel getReportModelSectionsPanel() {
		return reportModelSectionsPanel;
	}

	@Override
	public void setReportModelSectionPresenterHandler(ReportModelSectionPresenterHandler handler) {
		this.reportModelSectionPresenterHandler = handler;
	}

}
