package org.sigmah.shared.command;

import org.sigmah.offline.presenter.TreeGridFileModel;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;

/**
 * Find all files from the favorites projects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetFilesFromFavoriteProjects extends AbstractCommand<ListResult<TreeGridFileModel>> {
	
	public GetFilesFromFavoriteProjects() {
	}
	
}
