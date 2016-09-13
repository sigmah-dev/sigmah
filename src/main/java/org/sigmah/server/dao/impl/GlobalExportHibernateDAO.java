package org.sigmah.server.dao.impl;

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

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.export.GlobalContactExport;
import org.sigmah.server.domain.export.GlobalContactExportSettings;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportSettings;

/**
 * GlobalExportDAO implementation.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GlobalExportHibernateDAO extends AbstractDAO<GlobalExport, Integer> implements GlobalExportDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProjectModel> getProjectModelsByOrganization(final Organization organization) {
		final TypedQuery<ProjectModel> query = em().createQuery("SELECT pmv.model FROM ProjectModelVisibility pmv WHERE pmv.organization=:org", ProjectModel.class);
		query.setParameter("org", organization);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ContactModel> getContactModels() {
		final TypedQuery<ContactModel> query = em().createQuery("SELECT cm FROM ContactModel cm", ContactModel.class);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GlobalExport> getGlobalExports(final Date from, final Date to) {
		final TypedQuery<GlobalExport> query = em().createQuery("FROM GlobalExport e where e.date between :fromDate and :toDate", entityClass);
		query.setParameter("fromDate", from);
		query.setParameter("toDate", to);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GlobalExport> getOlderExports(final Date oldDate, final Organization organization) {
		final TypedQuery<GlobalExport> query = em().createQuery("FROM GlobalExport e WHERE e.organization = :org and e.date < :oldDate", entityClass);
		query.setParameter("oldDate", oldDate);
		query.setParameter("org", organization);
		return query.getResultList();
	}

	@Override
	public List<GlobalExportSettings> getGlobalExportSettings() {

		final TypedQuery<GlobalExportSettings> query = em().createQuery("FROM GlobalExportSettings ges", GlobalExportSettings.class);
		return query.getResultList();

	}

	@Override
	public List<GlobalContactExport> getGlobalContactExports(final Date from, final Date to) {
		final TypedQuery<GlobalContactExport> query = em().createQuery("FROM GlobalContactExport e where e.date between :fromDate and :toDate", GlobalContactExport.class);
		query.setParameter("fromDate", from);
		query.setParameter("toDate", to);
		return query.getResultList();
	}

	@Override
	public List<GlobalContactExport> getOlderContactExports(final Date oldDate, final Organization organization) {
		final TypedQuery<GlobalContactExport> query = em().createQuery("FROM GlobalContactExport e WHERE e.organization = :org and e.date < :oldDate", GlobalContactExport.class);
		query.setParameter("oldDate", oldDate);
		query.setParameter("org", organization);
		return query.getResultList();
	}

	@Override
	public List<GlobalContactExportSettings> getGlobalContactExportSettings() {

		final TypedQuery<GlobalContactExportSettings> query = em().createQuery("FROM GlobalContactExportSettings ges", GlobalContactExportSettings.class);
		return query.getResultList();

	}

}
