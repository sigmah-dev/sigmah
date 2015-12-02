package org.sigmah.client.ui.presenter.importation;

import com.google.gwt.user.client.ui.Widget;
import org.sigmah.shared.dto.ImportDetails;

/**
 * Interface to allow the presenter to create action buttons in the
 * import details grid.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface ActionRenderer {
	
	/**
	 * Creates actions buttons for the given model.
	 * 
	 * @param model Details of an import.
	 * @return A widget containing the available actions.
	 */
	Widget renderActionsForModel(ImportDetails model);
	
}
