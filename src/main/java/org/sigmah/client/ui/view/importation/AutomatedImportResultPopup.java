package org.sigmah.client.ui.view.importation;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import java.util.Arrays;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Popup displaying a short report about an automated import.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class AutomatedImportResultPopup extends PopupWidget {

	private Grid<BaseModelData> grid;
	private Button confirmButton;
	
	public AutomatedImportResultPopup() {
		super(true, Layouts.fitLayout());
		setWidth("900px");
		setHeight("500px");
	}
	
	/**
	 * Creates the inner components of the popup.
	 */
	public void initialize() {
		
		// Building the grid.
		grid = new Grid<BaseModelData>(new ListStore<BaseModelData>(), createColumnModel());
		
		// Creating the OK button.
		confirmButton = Forms.button(I18N.CONSTANTS.ok());
		
		// Preparing the popup.
		setContent(grid);
		addButton(confirmButton);
	}
	
	/**
	 * Creates the column model.
	 * 
	 * @param selectionModel
	 * @return 
	 */
	private ColumnModel createColumnModel() {
		
		// Project code column.
		final ColumnConfig projectCodeColumnConfig = new ColumnConfig(ProjectDTO.NAME, I18N.CONSTANTS.adminFlexible(), 100);
		
		// Project title column.
		final ColumnConfig projectTitleColumnConfig = new ColumnConfig(ProjectDTO.FULL_NAME, I18N.CONSTANTS.adminFlexible(), 100);
		
		// New value column.
		final ColumnConfig statusColumnConfig = new ColumnConfig("status", I18N.CONSTANTS.adminImportNewValue(), 70);
		
		// Creating the column model.
		return new ColumnModel(Arrays.asList(
				projectCodeColumnConfig,
				projectTitleColumnConfig,
				statusColumnConfig));
	}
	
}
