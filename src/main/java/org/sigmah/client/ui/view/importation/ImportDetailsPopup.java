package org.sigmah.client.ui.view.importation;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.importation.ActionRenderer;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ImportDetails;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ImportStatusCode;

/**
 * Displays the list of projects and org units that will be modified by an 
 * import.
 * 
 * @author Guerline Jean-Baptiste (gjbaptiste@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class ImportDetailsPopup extends PopupWidget implements HasGrid<ImportDetails> {

	private static final String MODEL_NAME = "modelName";
	private static final String STATUS_MESSAGE = "status";
	private static final String IDENTIFICATION_KEY = "idKey";
	private static final String IMPORT_STATUS_CODE = "code";
	private static final String PROJECT_NAME = "name";
	private static final String ACTIONS = "actions";
	
	private Grid<ImportDetails> grid;
	private Button importButton;
	
	private ActionRenderer actionRenderer;
	
	
	public ImportDetailsPopup() {
		super(true, Layouts.fitLayout());
		setWidth("700px");
		setHeight("400px");
	}
	
	public void initialize() {
		setTitle(I18N.CONSTANTS.importProjectOrgUnitsPanelHeader());
		
		// Building the grid.
		final CheckBoxSelectionModel<ImportDetails> selectionModel = new CheckBoxSelectionModel<ImportDetails>();
		final ColumnModel columnModel = createColumnModel(selectionModel);
		grid = createGrid(columnModel, selectionModel);
		
		// Creating the import button.
		importButton = Forms.button(I18N.CONSTANTS.importItem());
		
		// Preparing the popup.
		setContent(grid);
		addButton(importButton);
	}

	@Override
	public Grid<ImportDetails> getGrid() {
		return grid;
	}

	public Button getImportButton() {
		return importButton;
	}
	
	@Override
	public ListStore<ImportDetails> getStore() {
		return grid.getStore();
	}
	

	@Override
	public void setGridEventHandler(GridEventHandler<ImportDetails> handler) {
	}

	public void setActionRenderer(ActionRenderer actionRenderer) {
		this.actionRenderer = actionRenderer;
	}
	
	public List<ImportDetails> getSelection() {
		return grid.getSelectionModel().getSelection();
	}
	
	public void addModelToSelection(ImportDetails importDetails) {
		grid.getSelectionModel().select(importDetails, true);
	}
	
	// --
	// Utility methods.
	// --
	private Grid<ImportDetails> createGrid(final ColumnModel columnModel, final GridSelectionModel<ImportDetails> selectionModel) {
		final Grid<ImportDetails> grid = new Grid<ImportDetails>(new ListStore<ImportDetails>(), columnModel);
		
		grid.setSelectionModel(selectionModel);
		grid.getView().setForceFit(true);
		
		return grid;
	}
	
	private ColumnModel createColumnModel(CheckBoxSelectionModel<ImportDetails> selectionModel) {
		// Checkbox column (to select changes to apply).
		final ColumnConfig checkboxColumnConfig = selectionModel.getColumn();
		
		// Model name column.
		final ColumnConfig modelNameColumnConfig = new ColumnConfig(MODEL_NAME, I18N.CONSTANTS.projectModel(), 100);
		modelNameColumnConfig.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {

				return model.getModelName();
			}
		});
		
		// Identification key column.
		final ColumnConfig idKeyColumnConfig = new ColumnConfig(IDENTIFICATION_KEY, I18N.CONSTANTS.adminImportKeyIdentification(), 120);
		idKeyColumnConfig.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {

				return model.getKeyIdentification();
			}
		});
		
		// Status message column.
		final ColumnConfig statusMessageColumnConfig = new ColumnConfig(STATUS_MESSAGE, I18N.CONSTANTS.importHeadingStatus(), 120);
		statusMessageColumnConfig.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {
				if (model.getEntityStatus() != null) {
					return ImportStatusCode.getStringValue(model.getEntityStatus());
				} else {
					return I18N.CONSTANTS.UNAVAILABLE();
				}
			}
		});
		
		// Project or OrgUnit code column.
		final ColumnConfig codeColumnConfig = new ColumnConfig(IMPORT_STATUS_CODE, I18N.CONSTANTS.code(), 100);
		codeColumnConfig.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {
				final Set<EntityDTO<?>> entitySet = model.getEntitiesToImport().keySet();
				
				if (!entitySet.isEmpty()) {
					final Iterator<EntityDTO<?>> iterator = entitySet.iterator();
					final EntityDTO<?> entity = iterator.next();
					
					if (entity instanceof ProjectDTO) {
						final ProjectDTO project = (ProjectDTO) entity;
						return project.getName();
						
					} else if(entity instanceof OrgUnitDTO) {
						final OrgUnitDTO orgUnit = (OrgUnitDTO) entity;
						return orgUnit.getName();
					}
				}
				return null;
			}
		});
		
		// Project or OrgUnit full name column.
		final ColumnConfig entityNameColumnConfig = new ColumnConfig(PROJECT_NAME, I18N.CONSTANTS.name(), 120);
		entityNameColumnConfig.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(final ImportDetails model, String property, ColumnData config, int rowIndex,
			                int colIndex, ListStore<ImportDetails> store, Grid<ImportDetails> grid) {
				final Set<EntityDTO<?>> entitySet = model.getEntitiesToImport().keySet();
				
				if (!entitySet.isEmpty()) {
					final Iterator<EntityDTO<?>> iterator = entitySet.iterator();
					final EntityDTO<?> entity = iterator.next();
					
					if (entity instanceof ProjectDTO) {
						final ProjectDTO project = (ProjectDTO) entity;
						return project.getFullName();
						
					} else if(entity instanceof OrgUnitDTO) {
						final OrgUnitDTO orgUnit = (OrgUnitDTO) entity;
						return orgUnit.getFullName();
					}
				}
				return null;
			}
		});
		
		// Available actions column.
		final ColumnConfig actionColumnConfig = new ColumnConfig(ACTIONS, 200);
		actionColumnConfig.setRenderer(new GridCellRenderer<ImportDetails>() {

			@Override
			public Object render(ImportDetails model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				return actionRenderer.renderActionsForModel(model);
			}
		});
		
		// Creating the column model.
		final ArrayList<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
		columnConfigs.add(checkboxColumnConfig);
		columnConfigs.add(modelNameColumnConfig);
		columnConfigs.add(idKeyColumnConfig);
		columnConfigs.add(statusMessageColumnConfig);
		columnConfigs.add(codeColumnConfig);
		columnConfigs.add(entityNameColumnConfig);
		columnConfigs.add(actionColumnConfig);
		
		return new ColumnModel(columnConfigs);
	}

}
