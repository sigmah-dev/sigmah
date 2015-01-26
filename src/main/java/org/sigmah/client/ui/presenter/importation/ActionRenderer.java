package org.sigmah.client.ui.presenter.importation;

import org.sigmah.shared.dto.ImportDetails;

/**
 * Interface to allow the presenter to create action buttons in the
 * import details grid.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface ActionRenderer {
	Object renderActionsForModel(ImportDetails model);
}
