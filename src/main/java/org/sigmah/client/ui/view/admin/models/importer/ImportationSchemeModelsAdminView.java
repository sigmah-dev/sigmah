package org.sigmah.client.ui.view.admin.models.importer;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.importer.ImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.ToggleAnchor;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.importation.VariableBudgetElementDTO;
import org.sigmah.shared.dto.importation.VariableBudgetSubFieldDTO;
import org.sigmah.shared.dto.importation.VariableFlexibleElementDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.HorizontalPanel;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

public class ImportationSchemeModelsAdminView extends AbstractView implements ImportationSchemeModelsAdminPresenter.View {

	private ListStore<ImportationSchemeModelDTO> importationSchemeModelsStore;
	private ListStore<VariableFlexibleElementDTO> variableFlexibleElementStore;

	private Grid<ImportationSchemeModelDTO> importationSchemeModelsGrid;
	private Grid<VariableFlexibleElementDTO> variableFlexibleElementsGrid;

	private Button addVariableFlexibleElementButton;
	private Button deleteVariableFlexibleElementButton;
	private Button addImportationSchemeModelButton;
	private Button deleteImportationSchemeModelButton;

	@Override
	public void initialize() {

		final ContentPanel mainPanel = Panels.content("Importation Scheme");

		// mainPanel.setHeaderVisible(false);

		mainPanel.setLayout(new FitLayout());
		mainPanel.setBorders(false);
		mainPanel.setBodyBorder(false);

		final HorizontalPanel panel = new HorizontalPanel();

		final VBoxLayoutData topVBoxLayoutData2 = new VBoxLayoutData();
		topVBoxLayoutData2.setMargins(new Margins(0, 0, 0, 2));
		topVBoxLayoutData2.setFlex(1.0);

		final ContentPanel importationSchemeModelsPanel = new ContentPanel(new FitLayout());
		importationSchemeModelsPanel.setHeaderVisible(false);
		importationSchemeModelsPanel.setScrollMode(Scroll.AUTOY);
		importationSchemeModelsPanel.setBorders(false);

		importationSchemeModelsGrid = buildImportationSchemeModelsGrid();

		importationSchemeModelsPanel.add(importationSchemeModelsGrid, topVBoxLayoutData2);
		importationSchemeModelsPanel.setTopComponent(importationSchemeModelToolBar());
		importationSchemeModelsPanel.layout();

		panel.add(importationSchemeModelsPanel);

		final ContentPanel variableFlexibleElementPanel = new ContentPanel(new FitLayout());
		variableFlexibleElementPanel.setScrollMode(Scroll.AUTOY);
		variableFlexibleElementPanel.setHeaderVisible(false);
		variableFlexibleElementPanel.setBorders(false);
		variableFlexibleElementsGrid = buildVariableFlexibleElementsGrid();

		final VBoxLayoutData topVBoxLayoutData = new VBoxLayoutData();
		topVBoxLayoutData.setMargins(new Margins(0, 0, 0, 2));
		topVBoxLayoutData.setFlex(1.0);

		variableFlexibleElementPanel.add(variableFlexibleElementsGrid, topVBoxLayoutData);
		variableFlexibleElementPanel.setTopComponent(variableFlexibleElementToolBar());
		variableFlexibleElementPanel.layout();

		// panel.setWidth("600px");

		panel.add(variableFlexibleElementPanel);

		mainPanel.add(panel);

		add(mainPanel);

	}

	private Component importationSchemeModelToolBar() {

		final ToolBar toolbar = new ToolBar();

		addImportationSchemeModelButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		toolbar.add(addImportationSchemeModelButton);

		deleteImportationSchemeModelButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		toolbar.add(deleteImportationSchemeModelButton);

		return toolbar;

	}

	private Component variableFlexibleElementToolBar() {

		final ToolBar toolbar = new ToolBar();

		addVariableFlexibleElementButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());

		addVariableFlexibleElementButton.disable();
		toolbar.add(addVariableFlexibleElementButton);

		deleteVariableFlexibleElementButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());

		toolbar.add(deleteVariableFlexibleElementButton);

		return toolbar;
	}

	private Grid<VariableFlexibleElementDTO> buildVariableFlexibleElementsGrid() {

		variableFlexibleElementStore = new ListStore<VariableFlexibleElementDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("icon", "", 50);
		column.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {

			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<VariableFlexibleElementDTO> store, Grid<VariableFlexibleElementDTO> grid) {
				if (model.getIsKey()) {
					return IconImageBundle.ICONS.login().createImage();
				}
				return null;

			}
		});
		configs.add(column);

		column = new ColumnConfig("field", I18N.CONSTANTS.adminFlexible(), 200);
		column.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {

			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<VariableFlexibleElementDTO> store, Grid<VariableFlexibleElementDTO> grid) {
				return model.getFlexibleElementDTO().getFormattedLabel();
			}
		});
		configs.add(column);

		column = new ColumnConfig("variable", I18N.CONSTANTS.adminImportationSchemeModelVariableHeading(), 200);
		column.setRenderer(new GridCellRenderer<VariableFlexibleElementDTO>() {

			@Override
			public Object render(VariableFlexibleElementDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<VariableFlexibleElementDTO> store, Grid<VariableFlexibleElementDTO> grid) {
				if (model instanceof VariableBudgetElementDTO) {
					String variableNames = "";
					VariableBudgetElementDTO variableBudgetElement = (VariableBudgetElementDTO) model;
					for (VariableBudgetSubFieldDTO varSubField : variableBudgetElement.getVariableBudgetSubFieldsDTO()) {
						variableNames += varSubField.getVariableDTO().getName() + "; ";
					}
					return variableNames;
				} else {
					return model.getVariableDTO().getName();
				}
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<VariableFlexibleElementDTO> variableFlexibleElementsDTOGrid = new Grid<VariableFlexibleElementDTO>(variableFlexibleElementStore, cm);
		variableFlexibleElementsDTOGrid.setAutoHeight(true);
		variableFlexibleElementsDTOGrid.setAutoWidth(true);
		variableFlexibleElementsDTOGrid.addStyleName("importation-scheme-models-grid");
		variableFlexibleElementsDTOGrid.getView().setForceFit(true);

		return variableFlexibleElementsDTOGrid;
	}

	private Grid<ImportationSchemeModelDTO> buildImportationSchemeModelsGrid() {

		importationSchemeModelsStore = new ListStore<ImportationSchemeModelDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();

		column = new ColumnConfig("importationScheme", I18N.CONSTANTS.adminImportationScheme(), 250);
		column.setRenderer(new GridCellRenderer<ImportationSchemeModelDTO>() {

			@Override
			public Object render(final ImportationSchemeModelDTO model, String property, ColumnData config, int rowIndex, int colIndex,
					ListStore<ImportationSchemeModelDTO> store, Grid<ImportationSchemeModelDTO> grid) {

				final ToggleAnchor anchor = new ToggleAnchor(model.getImportationSchemeDTO().getName());
				anchor.setAnchorMode(true);
				/*
				 * anchor.addClickHandler(new ClickHandler() {
				 * @Override public void onClick(ClickEvent event) { currentImportationSchemeModelDTO = model;
				 * variableFlexibleElementsGrid.show(); variableFlexibleElementStore.removeAll();
				 * variableFlexibleElementStore.add(model.getVariableFlexibleElementsDTO());
				 * variableFlexibleElementStore.commitChanges(); addVariableFlexibleElementButton.enable(); if
				 * (currentImportationSchemeModelDTO.getIdKey() == null) { showNewVariableFlexibleElementForm(true); } } });
				 */
				return anchor;
			}

		});

		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ImportationSchemeModelDTO> importationSchemeModelsDTOGrid = new Grid<ImportationSchemeModelDTO>(importationSchemeModelsStore, cm);
		importationSchemeModelsDTOGrid.getView().setForceFit(true);
		importationSchemeModelsDTOGrid.setAutoHeight(true);
		importationSchemeModelsDTOGrid.addStyleName("importation-scheme-models-grid");
		return importationSchemeModelsDTOGrid;
	}

	@Override
	public ListStore<ImportationSchemeModelDTO> getImportationSchemeModelsStore() {
		return importationSchemeModelsStore;
	}

	@Override
	public ListStore<VariableFlexibleElementDTO> getVariableFlexibleElementStore() {
		return variableFlexibleElementStore;
	}

	@Override
	public Grid<ImportationSchemeModelDTO> getImportationSchemeModelsGrid() {
		return importationSchemeModelsGrid;
	}

	@Override
	public Grid<VariableFlexibleElementDTO> getVariableFlexibleElementsGrid() {
		return variableFlexibleElementsGrid;
	}

	@Override
	public Button getAddVariableFlexibleElementButton() {
		return addVariableFlexibleElementButton;
	}

	@Override
	public Button getDeleteVariableFlexibleElementButton() {
		return deleteVariableFlexibleElementButton;
	}

	@Override
	public Button getAddImportationSchemeModelButton() {
		return addImportationSchemeModelButton;
	}

	@Override
	public Button getDeleteImportationSchemeModelButton() {
		return deleteImportationSchemeModelButton;
	}

}
