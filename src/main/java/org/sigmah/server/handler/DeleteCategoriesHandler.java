package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.DeleteCategories;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import java.util.List;

/**
 * Handler for {@link DeleteCategories} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DeleteCategoriesHandler extends AbstractCommandHandler<DeleteCategories, VoidResult> {

	@Inject
	public DeleteCategoriesHandler() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final DeleteCategories cmd, final UserExecutionContext context) throws CommandException {

		performDelete(cmd.getCategoryTypes(), cmd.getCategoryElements());

		return null;
	}

	/**
	 * Deletes the given category types and elements in a transaction.
	 * 
	 * @param categoryTypes List of types to delete (can be <code>null</code>).
	 * @param categoryElements List of elements to delete (can be <code>null</code>).
	 */
	@Transactional
	protected void performDelete(List<CategoryTypeDTO> categoryTypes, List<CategoryElementDTO> categoryElements) {
		if (categoryTypes != null) {
			for (CategoryTypeDTO categoryDTO : categoryTypes) {
				CategoryType category = em().find(CategoryType.class, categoryDTO.getId());
				if (category != null)
					em().remove(category);
			}
		}

		if (categoryElements != null) {
			for (CategoryElementDTO categoryDTO : categoryElements) {
				CategoryElement categoryElement = em().find(CategoryElement.class, categoryDTO.getId());
				if (categoryElement != null)
					em().remove(categoryElement);
			}
		}

		em().flush();
	}

}
