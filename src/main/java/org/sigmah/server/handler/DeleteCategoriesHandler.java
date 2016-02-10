package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
