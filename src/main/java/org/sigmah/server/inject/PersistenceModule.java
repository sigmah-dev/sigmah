package org.sigmah.server.inject;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sigmah.server.dao.ActivityDAO;
import org.sigmah.server.dao.AdminDAO;
import org.sigmah.server.dao.AmendmentDAO;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.GlobalExportSettingsDAO;
import org.sigmah.server.dao.IndicatorDAO;
import org.sigmah.server.dao.LocationDAO;
import org.sigmah.server.dao.LocationTypeDAO;
import org.sigmah.server.dao.MonitoredPointDAO;
import org.sigmah.server.dao.MonitoredPointListDAO;
import org.sigmah.server.dao.OrgUnitBannerDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.OrgUnitDetailsDAO;
import org.sigmah.server.dao.OrgUnitModelDAO;
import org.sigmah.server.dao.OrganizationDAO;
import org.sigmah.server.dao.PartnerDAO;
import org.sigmah.server.dao.PersonalEventDAO;
import org.sigmah.server.dao.PhaseModelDAO;
import org.sigmah.server.dao.PrivacyGroupDAO;
import org.sigmah.server.dao.ProfileDAO;
import org.sigmah.server.dao.ProjectBannerDAO;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.ProjectDetailsDAO;
import org.sigmah.server.dao.ProjectModelDAO;
import org.sigmah.server.dao.ProjectReportDAO;
import org.sigmah.server.dao.ReminderDAO;
import org.sigmah.server.dao.ReminderListDAO;
import org.sigmah.server.dao.ReportDefinitionDAO;
import org.sigmah.server.dao.ReportingPeriodDAO;
import org.sigmah.server.dao.SiteDAO;
import org.sigmah.server.dao.SiteTableDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dao.UserPermissionDAO;
import org.sigmah.server.dao.UserUnitDAO;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.dao.impl.ActivityHibernateDAO;
import org.sigmah.server.dao.impl.AdminHibernateDAO;
import org.sigmah.server.dao.impl.AmendmentHibernateDAO;
import org.sigmah.server.dao.impl.AuthenticationHibernateDAO;
import org.sigmah.server.dao.impl.CountryHibernateDAO;
import org.sigmah.server.dao.impl.FileHibernateDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.dao.impl.GlobalExportSettingsHibernateDAO;
import org.sigmah.server.dao.impl.IndicatorHibernateDAO;
import org.sigmah.server.dao.impl.LocationHibernateDAO;
import org.sigmah.server.dao.impl.LocationTypeHibernateDAO;
import org.sigmah.server.dao.impl.MonitoredPointHibernateDAO;
import org.sigmah.server.dao.impl.MonitoredPointListHibernateDAO;
import org.sigmah.server.dao.impl.OrgUnitBannerHibernateDAO;
import org.sigmah.server.dao.impl.OrgUnitDetailsHibernateDAO;
import org.sigmah.server.dao.impl.OrgUnitHibernateDAO;
import org.sigmah.server.dao.impl.OrgUnitModelHibernateDAO;
import org.sigmah.server.dao.impl.OrganizationHibernateDAO;
import org.sigmah.server.dao.impl.PartnerHibernateDAO;
import org.sigmah.server.dao.impl.PersonalEventHibernateDAO;
import org.sigmah.server.dao.impl.PhaseModelHibernateDAO;
import org.sigmah.server.dao.impl.PrivacyGroupHibernateDAO;
import org.sigmah.server.dao.impl.ProfileHibernateDAO;
import org.sigmah.server.dao.impl.ProjectBannerHibernateDAO;
import org.sigmah.server.dao.impl.ProjectDetailsHibernateDAO;
import org.sigmah.server.dao.impl.ProjectHibernateDAO;
import org.sigmah.server.dao.impl.ProjectModelHibernateDAO;
import org.sigmah.server.dao.impl.ProjectReportHibernateDAO;
import org.sigmah.server.dao.impl.ReminderHibernateDAO;
import org.sigmah.server.dao.impl.ReminderListHibernateDAO;
import org.sigmah.server.dao.impl.ReportDefinitionHibernateDAO;
import org.sigmah.server.dao.impl.ReportingPeriodHibernateDAO;
import org.sigmah.server.dao.impl.SiteHibernateDAO;
import org.sigmah.server.dao.impl.SiteTableHibernateDAO;
import org.sigmah.server.dao.impl.UserDatabaseHibernateDAO;
import org.sigmah.server.dao.impl.UserHibernateDAO;
import org.sigmah.server.dao.impl.UserPermissionHibernateDAO;
import org.sigmah.server.dao.impl.UserUnitDAOImpl;
import org.sigmah.server.dao.impl.ValueHibernateDAO;
import org.sigmah.server.dao.util.SQLDialect;
import org.sigmah.server.dao.util.SQLDialectProvider;
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
		bind(CountryDAO.class).to(CountryHibernateDAO.class).in(Singleton.class);
		bind(FileDAO.class).to(FileHibernateDAO.class).in(Singleton.class);
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
