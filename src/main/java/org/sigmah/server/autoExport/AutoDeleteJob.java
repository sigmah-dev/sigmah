package org.sigmah.server.autoExport;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.impl.GlobalExportHibernateDAO;
import org.sigmah.server.domain.export.GlobalExport;
import org.sigmah.server.domain.export.GlobalExportContent;
import org.sigmah.server.domain.export.GlobalExportSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * Scheduled(in link{GlobalExportJobActivator}) job to delete old link{GlobalExport} entities Uses
 * link{GlobalExportSettings} to check delete frequency for each organization Runs in a separate thread ATTENTION: This
 * job must not run at the same time with link{AutoExportJob}
 * 
 * @author sherzod V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */
public class AutoDeleteJob implements Job {

	private static final Logger LOG = LoggerFactory.getLogger(AutoDeleteJob.class);

	@Override
	public void execute(JobExecutionContext executionContext) throws JobExecutionException {

		final JobDataMap dataMap = executionContext.getJobDetail().getJobDataMap();
		final EntityManager em = (EntityManager) dataMap.get("em");
		final Injector injector = (Injector) dataMap.get("injector");

		EntityTransaction tx = null;

		LOG.debug("*************** AUTO DELETE EXPORT ********************");

		try {

			// Open transaction

			/**
			 * NOTE: it is impossible to use @Transactional for this method The reason is link{TransactionalInterceptor} gets
			 * EntityManager from the injector; this is server thread, so, EM is out of scope
			 */

			tx = em.getTransaction();
			tx.begin();

			final GlobalExportDAO exportDAO = injector.getInstance(GlobalExportHibernateDAO.class);

			final List<GlobalExportSettings> settings = exportDAO.getGlobalExportSettings();
			for (final GlobalExportSettings setting : settings) {

				/**
				 * Check for auto delete schedule
				 */

				// skip if no delete schedule is specified

				if (setting.getAutoDeleteFrequency() == null || setting.getAutoDeleteFrequency() < 1)
					continue;

				final Calendar scheduledCalendar = Calendar.getInstance();

				// subtract months from current date
				scheduledCalendar.add(Calendar.MONTH, 0 - setting.getAutoDeleteFrequency().intValue());

				// get older exports
				List<GlobalExport> exports = exportDAO.getOlderExports(scheduledCalendar.getTime(), setting.getOrganization());

				// delete exports and their contents
				for (final GlobalExport export : exports) {
					final List<GlobalExportContent> contents = export.getContents();
					for (GlobalExportContent content : contents) {
						em.remove(content);
					}
					em.remove(export);
				}

			}

			tx.commit();

			LOG.info("Scheduled DELETE of global exports fired");

		} catch (Exception ex) {

			if (tx != null && tx.isActive())
				tx.rollback();

			LOG.error("Scheduled global export job failed : " + ex.getMessage());
			ex.printStackTrace();
		}
	}
}
