package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 * Find all files from the favorites projects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetFilesFromFavoriteProjects extends AbstractCommand<ListResult<FileVersionDTO>> {
	
	public GetFilesFromFavoriteProjects() {
	}
	
}
