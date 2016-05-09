package org.sigmah.server.inject;

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

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sigmah.server.dao.*;
import org.sigmah.server.dao.impl.*;
import org.sigmah.server.dao.util.SQLDialect;
import org.sigmah.server.dao.util.SQLDialectProvider;
import org.sigmah.server.domain.FrameworkFulfillment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;
import org.sigmah.server.dao.PivotDAO;
import org.sigmah.server.dao.impl.PivotHibernateDAO;

/**
 * Abstract module providing methods to install the DAO-layer interfaces.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PersistenceModule extends AbstractModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(PersistenceModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing persistence module.");
		}

		// Binds providers.
		bind(SQLDialect.class).toProvider(SQLDialectProvider.class).in(Singleton.class);

		// Installs the JPA module.
		install(new JpaPersistModule("sigmah-dev"));

		// JSR-303 : bean validation.
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		bind(Validator.class).toInstance(validator);

		// DAOs (Thank you for maintaining alphabetical order).
		bind(ActivityDAO.class).to(ActivityHibernateDAO.class).in(Singleton.class);
		bind(AdminDAO.class).to(AdminHibernateDAO.class).in(Singleton.class);
		bind(AmendmentDAO.class).to(AmendmentHibernateDAO.class).in(Singleton.class);
		bind(AuthenticationDAO.class).to(AuthenticationHibernateDAO.class).in(Singleton.class);
		bind(ContactModelDAO.class).to(ContactModelHibernateDAO.class).in(Singleton.class);
		bind(CountryDAO.class).to(CountryHibernateDAO.class).in(Singleton.class);
		bind(FileDAO.class).to(FileHibernateDAO.class).in(Singleton.class);
		bind(FrameworkDAO.class).to(FrameworkHibernateDAO.class).in(Singleton.class);
		bind(FrameworkFulfillmentDAO.class).to(FrameworkFulfillmentHibernateDAO.class).in(Singleton.class);
		bind(GlobalExportDAO.class).to(GlobalExportHibernateDAO.class).in(Singleton.class);
		bind(GlobalExportSettingsDAO.class).to(GlobalExportSettingsHibernateDAO.class).in(Singleton.class);
		bind(IndicatorDAO.class).to(IndicatorHibernateDAO.class).in(Singleton.class);
		bind(LocationDAO.class).to(LocationHibernateDAO.class).in(Singleton.class);
		bind(LocationTypeDAO.class).to(LocationTypeHibernateDAO.class).in(Singleton.class);
		bind(MonitoredPointDAO.class).to(MonitoredPointHibernateDAO.class).in(Singleton.class);
		bind(MonitoredPointListDAO.class).to(MonitoredPointListHibernateDAO.class).in(Singleton.class);
		bind(OrganizationDAO.class).to(OrganizationHibernateDAO.class).in(Singleton.class);
		bind(OrgUnitDAO.class).to(OrgUnitHibernateDAO.class).in(Singleton.class);
		bind(OrgUnitBannerDAO.class).to(OrgUnitBannerHibernateDAO.class).in(Singleton.class);
		bind(OrgUnitDetailsDAO.class).to(OrgUnitDetailsHibernateDAO.class).in(Singleton.class);
		bind(OrgUnitModelDAO.class).to(OrgUnitModelHibernateDAO.class).in(Singleton.class);
		bind(PartnerDAO.class).to(PartnerHibernateDAO.class).in(Singleton.class);
		bind(PersonalEventDAO.class).to(PersonalEventHibernateDAO.class).in(Singleton.class);
		bind(PhaseModelDAO.class).to(PhaseModelHibernateDAO.class).in(Singleton.class);
		bind(PivotDAO.class).to(PivotHibernateDAO.class).in(Singleton.class);
		bind(PrivacyGroupDAO.class).to(PrivacyGroupHibernateDAO.class).in(Singleton.class);
		bind(ProfileDAO.class).to(ProfileHibernateDAO.class).in(Singleton.class);
		bind(ProjectDAO.class).to(ProjectHibernateDAO.class).in(Singleton.class);
		bind(ProjectBannerDAO.class).to(ProjectBannerHibernateDAO.class).in(Singleton.class);
		bind(ProjectDetailsDAO.class).to(ProjectDetailsHibernateDAO.class).in(Singleton.class);
		bind(ProjectModelDAO.class).to(ProjectModelHibernateDAO.class).in(Singleton.class);
		bind(ProjectReportDAO.class).to(ProjectReportHibernateDAO.class).in(Singleton.class);
		bind(ReminderDAO.class).to(ReminderHibernateDAO.class).in(Singleton.class);
		bind(ReminderListDAO.class).to(ReminderListHibernateDAO.class).in(Singleton.class);
		bind(ReportDefinitionDAO.class).to(ReportDefinitionHibernateDAO.class).in(Singleton.class);
		bind(ReportingPeriodDAO.class).to(ReportingPeriodHibernateDAO.class).in(Singleton.class);
		bind(SiteDAO.class).to(SiteHibernateDAO.class).in(Singleton.class);
		bind(SiteTableDAO.class).to(SiteTableHibernateDAO.class).in(Singleton.class);
		bind(UserDAO.class).to(UserHibernateDAO.class).in(Singleton.class);
		bind(UserDatabaseDAO.class).to(UserDatabaseHibernateDAO.class).in(Singleton.class);
		bind(UserPermissionDAO.class).to(UserPermissionHibernateDAO.class).in(Singleton.class);
		bind(UserUnitDAO.class).to(UserUnitDAOImpl.class).in(Singleton.class);
		bind(ValueDAO.class).to(ValueHibernateDAO.class).in(Singleton.class);

		// TODO [DAO] A intégrer (si nécessaire).
		// BaseMapDAO.java + BaseMapFsDAO.java
		// PivotDAO.java + PivotHibernateDAO.java
	}

}
