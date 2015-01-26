package org.sigmah.server.service;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Create layout group policy.
 * 
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LayoutGroupService extends AbstractEntityService<LayoutGroup, Integer, LayoutGroupDTO> {

	/**
	 * Injected application mapper.
	 */
	@Inject
	private Mapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutGroup create(final PropertyMap properties, final UserExecutionContext context) {

		LayoutGroup groupToPersist = null;
		final LayoutGroupDTO layoutGroupDTOToPersist = (LayoutGroupDTO) properties.get(AdminUtil.PROP_NEW_GROUP_LAYOUT);

		groupToPersist = mapper.map(layoutGroupDTOToPersist, LayoutGroup.class);

		if (groupToPersist.getParentLayout().getRowsCount() <= groupToPersist.getRow()) {
			Layout layout = groupToPersist.getParentLayout();
			layout.setRowsCount(groupToPersist.getRow() + 1);
			em().merge(layout);
		}

		if (layoutGroupDTOToPersist.getId() != null) {
			// update
			groupToPersist = em().merge(groupToPersist);

		} else {
			// Save group
			if (groupToPersist != null) {
				groupToPersist.setId(null);
				em().persist(groupToPersist);
			}
		}

		return groupToPersist;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LayoutGroup update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {
		throw new UnsupportedOperationException("No policy update operation implemented for '" + entityClass.getSimpleName() + "' entity.");
	}

}
