package org.sigmah.server.autoExport;

import java.util.Calendar;

import javax.persistence.EntityManager;

import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Schedules jobs link{AutoDeleteJob}, link{AutoExportJob} To see details of schedule read comments for corresponding
 * jobs Activator itself instantiated by link{SigmahAuthDictionaryServlet} to obtain access to EntityManager object
 * which is request scoped and inaccessable by other server side threads like jobs ATTENTION: This job must not run at
 * the same time with link{AutoExportJob}
 * 
 * @author sherzod V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */

@Singleton
public class GlobalExportJobActivator {

	private static final Logger LOG = LoggerFactory.getLogger(GlobalExportJobActivator.class);

	private final Provider<EntityManager> entityManagerProvider;

	@Inject
	public GlobalExportJobActivator(final QuartzScheduler quartz, Provider<EntityManager> entityManagerProvider, final Injector injector) {

		LOG.debug("****************  STARTING JOBS *****************");

		this.entityManagerProvider = entityManagerProvider;

		try {

			/**
			 * Schedule auto exports
			 */

			final JobDetail exportJobDetail = new JobDetail("autoExportJob", null, AutoExportJob.class);
			exportJobDetail.getJobDataMap().put("em", this.entityManagerProvider.get());
			exportJobDetail.getJobDataMap().put("injector", injector);

			// Trigger exportTrigger = TriggerUtils.makeSecondlyTrigger(120); // test fire every 5sec

			Trigger exportTrigger = TriggerUtils.makeDailyTrigger(0, 0); // Fire every day at midnight
			exportTrigger.setName("autoExportTrigger");

			// start next day at midnight

			final Calendar exportCalendar = Calendar.getInstance();
			exportCalendar.add(Calendar.DAY_OF_MONTH, 1);
			exportTrigger.setStartTime(AutoExportJob.getZeroTimeDate(exportCalendar.getTime()));
			quartz.getScheduler().scheduleJob(exportJobDetail, exportTrigger);

			LOG.info("AutoExportJob job scheduled for EVERY DAY at MIDNIGHT ");

			/**
			 * Schedule auto delete
			 */

			final JobDetail deleteJobDetail = new JobDetail("autoDeleteJob", null, AutoDeleteJob.class);
			deleteJobDetail.getJobDataMap().put("em", this.entityManagerProvider.get());
			deleteJobDetail.getJobDataMap().put("injector", injector);

			// Trigger deleteTrigger = TriggerUtils.makeSecondlyTrigger(60, 0); // test

			// fire every 25th of the month at 01:00
			Trigger deleteTrigger = TriggerUtils.makeMonthlyTrigger(25, 1, 0);
			deleteTrigger.setName("autoDeleteTrigger");

			// start next day at 01:00
			final Calendar deleteCalendar = Calendar.getInstance();
			deleteCalendar.add(Calendar.DAY_OF_MONTH, 1);
			deleteCalendar.setTime(AutoExportJob.getZeroTimeDate(deleteCalendar.getTime()));
			deleteCalendar.add(Calendar.HOUR_OF_DAY, 1);

			deleteTrigger.setStartTime(deleteCalendar.getTime());
			quartz.getScheduler().scheduleJob(deleteJobDetail, deleteTrigger);
			LOG.info("AutoDeleteJob job scheduled for EVERY 25th day of the MONTH at 01:00 ");

		} catch (Exception e) {

			LOG.error("Scheduling failed");
			e.printStackTrace();

		}
	}
}
