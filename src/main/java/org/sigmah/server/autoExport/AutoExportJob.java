package org.sigmah.server.autoExport;

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

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.domain.export.GlobalContactExport;
import org.sigmah.server.domain.export.GlobalContactExportSettings;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportSettings;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.server.servlet.exporter.data.GlobalExportDataContactProvider;
import org.sigmah.server.servlet.exporter.data.GlobalExportDataProjectProvider;
import org.sigmah.server.servlet.exporter.data.cells.GlobalExportDataCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * Scheduled (in link{GlobalExportJobActivator}) job to generate global exports by check export frequency of each
 * organization Runs in a separate thread ATTENTION: This job must not run at the same time with link{AutoDeleteJob}
 * 
 * @author sherzod V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */

public class AutoExportJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(AutoExportJob.class);

	@Override
	public void execute(JobExecutionContext executionContext) throws JobExecutionException {

		final JobDataMap dataMap = executionContext.getJobDetail().getJobDataMap();
		final EntityManager em = (EntityManager) dataMap.get("em");
		final Injector injector = (Injector) dataMap.get("injector");

		EntityTransaction tx = null;

		LOG.debug("******************* AUTO CREATE EXPORT ********************");

		try {

			// Open transaction

			/**
			 * NOTE: it is impossible to use @Transactional for this method The reason is link{TransactionalInterceptor} gets
			 * EntityManager from the injector which is out of scope
			 */

			tx = em.getTransaction();
			tx.begin();

			final GlobalExportDAO exportDAO = injector.getInstance(GlobalExportHibernateDAO.class);

			// PROJECTS

			final GlobalExportDataProjectProvider dataProvider = injector.getInstance(GlobalExportDataProjectProvider.class);

			final List<GlobalExportSettings> settings = exportDAO.getGlobalExportSettings();
			for (final GlobalExportSettings setting : settings) {

				/**
				 * Check for auto export schedule
				 */

				// skip if no export schedule is specified
				if (setting.getAutoExportFrequency() == null || setting.getAutoExportFrequency() < 1)
					continue;

				final Calendar systemCalendar = Calendar.getInstance();

				boolean doExport = false;

				if ((setting.getAutoExportFrequency() >= 31) && (setting.getAutoExportFrequency() <= 58)) {

					// Case of Monthly Auto Export
					if ((setting.getAutoExportFrequency() - 30) == systemCalendar.get(Calendar.DAY_OF_MONTH)) {
						doExport = true;
					}
				} else if ((setting.getAutoExportFrequency() >= 61) && (setting.getAutoExportFrequency() <= 67)) {

					// Case of Weekly Auto Export
					if ((setting.getAutoExportFrequency() - 60) == systemCalendar.get(Calendar.DAY_OF_WEEK)) {
						doExport = true;
					}

				} else {
					// Regular Auto-Export every N-days

					final Calendar scheduledCalendar = Calendar.getInstance();
					Date lastExportDate = setting.getLastExportDate();

					if (lastExportDate == null) {

						lastExportDate = systemCalendar.getTime();
						setting.setLastExportDate(lastExportDate);
						em.merge(setting);

					} else {
						scheduledCalendar.setTime(lastExportDate);
						// add scheduled days to the last exported date
						scheduledCalendar.add(Calendar.DAY_OF_MONTH, setting.getAutoExportFrequency());
					}

					final Date systemDate = getZeroTimeDate(systemCalendar.getTime());
					final Date scheduledDate = getZeroTimeDate(scheduledCalendar.getTime());

					if (systemDate.compareTo(scheduledDate) >= 0) {
						doExport = true;
					}

				}

				if (doExport) {

					/**
					 * Start auto export
					 */

					// persist global export logger
					final GlobalExport globalExport = new GlobalExport();
					globalExport.setOrganization(setting.getOrganization());
					globalExport.setDate(systemCalendar.getTime());
					em.persist(globalExport);

					em.flush();

					// generate export content

					final Map<String, List<GlobalExportDataCell[]>> exportData =
							dataProvider.generateGlobalExportData(setting.getOrganization().getId(), em, injector.getInstance(I18nServer.class), null, null);

					// persist export content
					dataProvider.persistGlobalExportDataAsCsv(globalExport, em, exportData);

				}

			}

			// CONTACTS

			final GlobalExportDataContactProvider dataContactProvider = injector.getInstance(GlobalExportDataContactProvider.class);

			final List<GlobalContactExportSettings> settingsContacts = exportDAO.getGlobalContactExportSettings();
			for (final GlobalContactExportSettings settingContacts : settingsContacts) {

				/**
				 * Check for auto export schedule
				 */

				// skip if no export schedule is specified
				if (settingContacts.getAutoExportFrequency() == null || settingContacts.getAutoExportFrequency() < 1)
					continue;

				final Calendar systemCalendar = Calendar.getInstance();

				boolean doExport = false;

				if ((settingContacts.getAutoExportFrequency() >= 31) && (settingContacts.getAutoExportFrequency() <= 58)) {

					// Case of Monthly Auto Export
					if ((settingContacts.getAutoExportFrequency() - 30) == systemCalendar.get(Calendar.DAY_OF_MONTH)) {
						doExport = true;
					}
				} else if ((settingContacts.getAutoExportFrequency() >= 61) && (settingContacts.getAutoExportFrequency() <= 67)) {

					// Case of Weekly Auto Export
					if ((settingContacts.getAutoExportFrequency() - 60) == systemCalendar.get(Calendar.DAY_OF_WEEK)) {
						doExport = true;
					}

				} else {
					// Regular Auto-Export every N-days

					final Calendar scheduledCalendar = Calendar.getInstance();
					Date lastExportDate = settingContacts.getLastExportDate();

					if (lastExportDate == null) {

						lastExportDate = systemCalendar.getTime();
						settingContacts.setLastExportDate(lastExportDate);
						em.merge(settingContacts);

					} else {
						scheduledCalendar.setTime(lastExportDate);
						// add scheduled days to the last exported date
						scheduledCalendar.add(Calendar.DAY_OF_MONTH, settingContacts.getAutoExportFrequency());
					}

					final Date systemDate = getZeroTimeDate(systemCalendar.getTime());
					final Date scheduledDate = getZeroTimeDate(scheduledCalendar.getTime());

					if (systemDate.compareTo(scheduledDate) >= 0) {
						doExport = true;
					}

				}

				if (doExport) {

					/**
					 * Start auto export
					 */

					// persist global export logger
					final GlobalContactExport globalContactExport = new GlobalContactExport();
					globalContactExport.setOrganization(settingContacts.getOrganization());
					globalContactExport.setDate(systemCalendar.getTime());
					em.persist(globalContactExport);

					em.flush();

					// generate export content

					final Map<String, List<GlobalExportDataCell[]>> exportData =
							dataProvider.generateGlobalExportData(settingContacts.getOrganization().getId(), em, injector.getInstance(I18nServer.class), null, null);

					// persist export content
					dataContactProvider.persistGlobalExportDataAsCsv(globalContactExport, em, exportData);

				}

			}












			tx.commit();

			LOG.info("Scheduled EXPORT of global exports fired");

		} catch (Exception ex) {

			if (tx != null && tx.isActive())
				tx.rollback();

			LOG.error("Scheduled global export job failed : " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public static Date getZeroTimeDate(Date fecha) {

		Date res = fecha;
		Calendar calendar = Calendar.getInstance();

		calendar.setTime(fecha);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		res = calendar.getTime();

		return res;
	}

}
