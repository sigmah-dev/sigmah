package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

/**
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetCategories extends AbstractCommand<ListResult<CategoryTypeDTO>> {

	public GetCategories() {
		// Serialization.
	}

}
