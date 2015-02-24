package org.sigmah.client.ui.view.project.indicator;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import org.sigmah.client.ui.presenter.project.indicator.ProjectIndicatorManagementPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.indicator.IndicatorNumberFormats;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorElement;
import org.sigmah.shared.dto.IndicatorGroup;

/**
 * {@link ProjectIndicatorManagementPresenter} view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectIndicatorManagementView extends AbstractView implements ProjectIndicatorManagementPresenter.View {

	/** Main panel. */
	private ContentPanel mainPanel;
	
	// ToolBar buttons.
	private SplitButton saveButton;
	private MenuItem saveItem;
	private MenuItem discardChangesItem;
	private Button newIndicatorGroupButton;
	private Button newIndicatorButton;
	private Button deleteButton;
	private Button refreshButton;
	private Button exportButton;
	
	private EditorTreeGrid<IndicatorElement> treeGrid;
	
	/** New indicator group view */
	private ViewPopupInterface indicatorGroupPopup;
	private FormPanel indicatorGroupForm;
	private Field<String> indicatorGroupNameField;
	private Button indicatorGroupSaveButton;
	
	private boolean editable;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		mainPanel = Panels.content(I18N.CONSTANTS.projectTabIndicators(), Layouts.fitLayout());

		mainPanel.setTopComponent(buildToolbar());
		mainPanel.add(buildTreeGrid());
		
		indicatorGroupPopup = buildIndicatorGroupPopup();

		add(mainPanel);
	}

	@Override
	public TreeGrid<IndicatorElement> getTreeGrid() {
		return treeGrid;
	}

	@Override
	public TreeStore<IndicatorElement> getStore() {
		return treeGrid.getTreeStore();
	}

	@Override
	public SplitButton getSaveButton() {
		return saveButton;
	}

	@Override
	public MenuItem getSaveItem() {
		return saveItem;
	}

	@Override
	public MenuItem getDiscardChangesItem() {
		return discardChangesItem;
	}

	@Override
	public Button getNewIndicatorGroupButton() {
		return newIndicatorGroupButton;
	}

	@Override
	public Button getNewIndicatorButton() {
		return newIndicatorButton;
	}

	@Override
	public Button getDeleteButton() {
		return deleteButton;
	}

	@Override
	public Button getRefreshButton() {
		return refreshButton;
	}

	@Override
	public Button getExportButton() {
		return exportButton;
	}

	@Override
	public ViewPopupInterface getIndicatorGroupPopup() {
		return indicatorGroupPopup;
	}

	@Override
	public FormPanel getIndicatorGroupForm() {
		return indicatorGroupForm;
	}

	@Override
	public Field<String> getIndicatorGroupNameField() {
		return indicatorGroupNameField;
	}
	
	@Override
	public Button getIndicatorGroupSaveButton() {
		return indicatorGroupSaveButton;
	}
	
	@Override
	public void setTreeGridEventHandler(TreeGridEventHandler<IndicatorElement> handler) {
	}

	@Override
	public void refreshTreeGrid() {
		treeGrid.getView().refresh(false);
	}

	@Override
	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	/**
	 * Builds the actions toolbar.
	 * 
	 * @return The actions toolbar.
	 */
	private ToolBar buildToolbar() {
		// Save or cancel changes button.
		saveButton = Forms.saveSplitButton();
		saveItem = (MenuItem) saveButton.getMenu().getItem(0);
		discardChangesItem = (MenuItem) saveButton.getMenu().getItem(1);
		
		// New indicator group button.
		newIndicatorGroupButton = Forms.button(I18N.CONSTANTS.newIndicatorGroup());
		
		// New indicator button.
		newIndicatorButton = Forms.button(I18N.CONSTANTS.newIndicator());
		
		// Delete button.
		deleteButton = Forms.button(I18N.CONSTANTS.delete());
		
		// Refresh button.
		refreshButton = Forms.button(I18N.CONSTANTS.refreshPreview(), IconImageBundle.ICONS.refresh());
		
		// Export form button.
		exportButton = Forms.button(I18N.CONSTANTS.export(), IconImageBundle.ICONS.excel());

		// Create the toolbar instance and add the buttons.
		final ToolBar toolBar = new ToolBar();
		toolBar.add(saveButton);
		toolBar.add(newIndicatorGroupButton);
		toolBar.add(newIndicatorButton);
		toolBar.add(deleteButton);
		toolBar.add(refreshButton);
		toolBar.add(new FillToolItem());
		toolBar.add(exportButton);
		
		return toolBar;
	}
	
	/**
	 * Builds the tree grid component.
	 * 
	 * @return The tree grid component.
	 */
	private TreeGrid<IndicatorElement> buildTreeGrid() {
		// Columns
		IndicatorResources.INSTANCE.css().ensureInjected();
		final List<ColumnConfig> columns = createColumns();
		
		// Store
		final TreeStore<IndicatorElement> store = createTreeStore();
		
		// Grid
		treeGrid = createTreeGrid(store, columns);
		
		treeGrid.setBorders(true);
		treeGrid.getStyle().setNodeCloseIcon(null);
		treeGrid.getStyle().setNodeOpenIcon(null);
		treeGrid.getStyle().setLeafIcon(null);
		treeGrid.setAutoExpandColumn(IndicatorDTO.NAME);
		treeGrid.setTrackMouseOver(false);
		treeGrid.setClicksToEdit(EditorGrid.ClicksToEdit.TWO);
		
		// TODO: Add a SelectionModel
		
		return treeGrid;
	}
	
	/**
	 * Builds the "new indicator group" popup and its fields.
	 * 
	 * @return The popup.
	 */
	private ViewPopupInterface buildIndicatorGroupPopup() {
		indicatorGroupForm = Forms.panel(130);
		
		indicatorGroupNameField = Forms.text(I18N.CONSTANTS.name(), true, 128);
		indicatorGroupForm.add(indicatorGroupNameField);
		
		indicatorGroupSaveButton = Forms.button(I18N.CONSTANTS.adminOrgUnitCreateButton(), IconImageBundle.ICONS.save());
		indicatorGroupForm.addButton(indicatorGroupSaveButton);
		
		return new AbstractPopupView(new PopupWidget(true)) {
			
			@Override
			public void initialize() {
				setPopupTitle(I18N.CONSTANTS.newIndicatorGroup());
				initPopup(indicatorGroupForm);
			}
		};
	}

	/**
	 * Creates the tree grid element using the given store and columns.
	 * 
	 * @param store Tree store to use.
	 * @param columns List of columns.
	 * @return The tree grid element.
	 */
	private EditorTreeGrid<IndicatorElement> createTreeGrid(final TreeStore<IndicatorElement> store, final List<ColumnConfig> columns) {
		return new EditorTreeGrid<IndicatorElement>(store, new ColumnModel(columns)) {
			
			@Override
			protected boolean hasChildren(IndicatorElement indicatorElement) {
				return indicatorElement instanceof IndicatorGroup;
			}
		};
	}
	
	/**
	 * Creates the column configs of the tree grid.
	 * 
	 * @return The list of column config.
	 */
	protected List<ColumnConfig> createColumns() {
		final ArrayList<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		TextField<String> nameEditor = new TextField<String>();
		nameEditor.setAllowBlank(false);

		final ColumnConfig nameColumn = new ColumnConfig(IndicatorDTO.NAME, I18N.CONSTANTS.name(), 150);
		nameColumn.setRenderer(new TreeGridCellRenderer() {

			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				if(model instanceof IndicatorDTO) {
					final IndicatorDTO indicator = (IndicatorDTO) model;
					final IndicatorResources.Style css = IndicatorResources.INSTANCE.css();
					
					return new StringBuilder(toHTMLIcon(css.mapIcon()))
						.append("<span class='")
						.append(editable ? css.indicatorLabel() : css.indicatorLabelInactive())
						.append("'>")
						.append(indicator.get(property))
						.append("</span>")
						.toString();
					
				} else {
					return super.render(model, property, config, rowIndex, colIndex, store, grid);
				}
			}
		});
		nameColumn.setEditor(new CellEditor(nameEditor));
		columns.add(nameColumn);

		final ColumnConfig codeColumn = new ColumnConfig(IndicatorDTO.CODE, I18N.CONSTANTS.code(), 75);
		codeColumn.setEditor(new CellEditor(new TextField<String>()));
		columns.add(codeColumn);

		final ColumnConfig objectiveColumn = new ColumnConfig(IndicatorDTO.OBJECTIVE, I18N.CONSTANTS.targetValue(), 75);
		objectiveColumn.setRenderer(new GridCellRenderer() {

			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				if(model instanceof IndicatorDTO) {
					return formatIndicatorValue((IndicatorDTO) model, property);
					
				} else {
					return "";
				}
			}
		});
		objectiveColumn.setEditor(new CellEditor(new NumberField()));
		objectiveColumn.setAlignment(Style.HorizontalAlignment.RIGHT);
		columns.add(objectiveColumn);

		final ColumnConfig valueColumn = new ColumnConfig(IndicatorDTO.CURRENT_VALUE, I18N.CONSTANTS.value(), 75);
		valueColumn.setRenderer(new GridCellRenderer() {

			@Override
			public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				if(model instanceof IndicatorDTO) {
					final IndicatorDTO indicator = (IndicatorDTO) model;
					if (indicator.getLabelCounts() != null) {
						return indicator.formatMode();
						
					} else {
						return formatIndicatorValue(indicator, property);
					}
					
				} else {
					return "";
				}
			}
		});
		valueColumn.setAlignment(Style.HorizontalAlignment.RIGHT);
		columns.add(valueColumn);

		return columns;
	}
	
	private String toHTMLIcon(String spriteStyle) {
		// we can't use the normal div produced by GWT because the icons need to be inline
		// to display properly in the existing GXT html structure
		return "<img width='16' height='16' src='" + GWT.getModuleBaseURL() + "clear.cache.gif' class='" + spriteStyle + "'>";
	}
	
	/**
	 * Format the value <code>property</code> of the given indicator.
	 * 
	 * @param indicator Indicator to use.
	 * @param property The property to retrieve.
	 * 
	 * @return The value formatted according to the aggregation mode of the given indicator.
	 */
	private String formatIndicatorValue(final IndicatorDTO indicator, String property) {
		final Double value = indicator.get(property);
		
		if(value == null) {
			return "";
		}

		final NumberFormat numberFormat = IndicatorNumberFormats.getNumberFormat(indicator);

		return numberFormat.format(value);
	}
	
	/**
	 * Creates the store of the tree grid.
	 * 
	 * @return The store of the tree grid.
	 */
	private TreeStore<IndicatorElement> createTreeStore() {
		final TreeStore<IndicatorElement> store = new TreeStore<IndicatorElement>();
		
		store.setKeyProvider(new ModelKeyProvider<IndicatorElement>() {

			@Override
			public String getKey(IndicatorElement model) {
				final StringBuilder stringBuilder = new StringBuilder();
				
				if (model instanceof IndicatorGroup) {
					stringBuilder.append("group");
				} else {
					stringBuilder.append('i');
				}
				
				stringBuilder.append(model.get(IndicatorDTO.ID));
				return stringBuilder.toString();
			}
		});
		
		return store;
	}
}
