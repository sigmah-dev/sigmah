package org.sigmah.server.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.OrgUnitModelDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * {@link OrgUnitModelDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class OrgUnitModelHibernateDAO extends AbstractDAO<OrgUnitModel, Integer> implements OrgUnitModelDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrgUnitModel> findOrgUnitModelsVisibleToOrganization(final Integer organizationId) {
		return findOrgUnitModelsVisibleToOrganization(organizationId, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrgUnitModel> findOrgUnitModelsVisibleToOrganization(final Integer organizationId, final List<ProjectModelStatus> status) {

		final StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append("  om ");
		builder.append("FROM ");
		builder.append("  OrgUnitModel om ");
		builder.append("  LEFT JOIN FETCH om.banner ob ");
		builder.append("  LEFT JOIN FETCH om.details od ");
		builder.append("WHERE ");
		builder.append("  om.organization.id = :organizationId ");
		if (CollectionUtils.isNotEmpty(status)) {
			builder.append(" AND om.status IN :status ");
		}
		builder.append("ORDER BY ");
		builder.append("  om.name");

		final TypedQuery<OrgUnitModel> query = em().createQuery(builder.toString(), OrgUnitModel.class);
		query.setParameter("organizationId", organizationId);
		if (CollectionUtils.isNotEmpty(status)) {
			query.setParameter("status", status);
		}

		return query.getResultList();
	}

}
