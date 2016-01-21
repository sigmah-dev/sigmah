package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for {@link GetCategories} command.
 * 
 * @author nrebiai (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetCategoriesHandler extends AbstractCommandHandler<GetCategories, ListResult<CategoryTypeDTO>> {

	/**
	 * Logger.
	 */
	@SuppressWarnings("unused")
	private static final Logger LOG = LoggerFactory.getLogger(GetCategoriesHandler.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<CategoryTypeDTO> execute(final GetCategories cmd, final UserExecutionContext context) throws CommandException {

		List<CategoryTypeDTO> categories = new ArrayList<CategoryTypeDTO>();

		final TypedQuery<CategoryType> query = em().createQuery("SELECT c FROM CategoryType c WHERE c.organization.id = :orgid ORDER BY c.id", CategoryType.class);
		query.setParameter("orgid", context.getUser().getOrganization().getId());

		final List<CategoryType> resultCategories = query.getResultList();

		if (resultCategories != null) {
			for (final CategoryType oneCategory : resultCategories) {
				categories.add(mapper().map(oneCategory, new CategoryTypeDTO()));
			}
		}

		return new ListResult<CategoryTypeDTO>(categories);
	}

}
