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

import org.sigmah.server.dao.ProjectReportDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.report.ProjectReport;
import org.sigmah.server.domain.report.ProjectReportModel;
import org.sigmah.server.domain.report.ProjectReportVersion;
import org.sigmah.server.domain.report.RichTextElement;
import org.sigmah.server.domain.value.Value;

/**
 * ProjectReportDAO implementation.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProjectReportHibernateDAO extends AbstractDAO<ProjectReport, Integer> implements ProjectReportDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge(RichTextElement element) {
		em().merge(element);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportModel findModelById(final Integer id) {
		return em().find(ProjectReportModel.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public RichTextElement findRichTextElementById(final Integer id) {
		return em().find(RichTextElement.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void persist(final ProjectReportVersion version) {
		em().persist(version);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReport findReportById(final Integer id) {
		return em().find(ProjectReport.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportVersion findReportVersionById(final Integer id) {
		return em().find(ProjectReportVersion.class, id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge(final ProjectReport report) {
		em().merge(report);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge(final ProjectReportVersion version) {
		em().merge(version);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void merge(final Value value) {
		em().merge(value);
	}

}
