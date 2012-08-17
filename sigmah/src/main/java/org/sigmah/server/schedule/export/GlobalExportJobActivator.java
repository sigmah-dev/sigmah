package org.sigmah.server.schedule.export;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bouncycastle.asn1.cmp.CAKeyUpdAnnContent;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
 

@Singleton
public class GlobalExportJobActivator {
	
	private final static Log log=LogFactory.getLog(AutoExportJob.class);
 	
	@Inject
	public GlobalExportJobActivator(final QuartzScheduler quartz,
			final EntityManager entityManager,
			final Injector injector,final HttpServletRequest req)
	 {
		try{
			/*
			 * Schedule auto exports
			 */
			final JobDetail exportJobDetail = new JobDetail("autoExportJob", null,
					AutoExportJob.class);
			exportJobDetail.getJobDataMap().put("em",entityManager);
			exportJobDetail.getJobDataMap().put("log",LogFactory.getLog(AutoExportJob.class));
			exportJobDetail.getJobDataMap().put("injector",injector);
					 
		        
	 		//Trigger exportTrigger = TriggerUtils.makeSecondlyTrigger(5); //test fire every 5sec 
			
			Trigger exportTrigger = TriggerUtils.makeDailyTrigger(0, 0);  // Fire every day at midnight
	 		exportTrigger.setName("autoExportTrigger");
	 		//start next day at midnight
	 		final Calendar exportCalendar=Calendar.getInstance();
	 		exportCalendar.add(Calendar.DAY_OF_MONTH,1);	 		
			exportTrigger.setStartTime(AutoExportJob.getZeroTimeDate(exportCalendar.getTime())); 	 		
	
			quartz.getScheduler().scheduleJob(exportJobDetail, exportTrigger);
			log.info("AutoExportJob job scheduled for EVERY DAY at MIDNIGHT ");
		
			
			/*
			 * Schedule auto delete
			 */
			final JobDetail deleteJobDetail = new JobDetail("autoDeleteJob", null,
					AutoDeleteJob.class);
			deleteJobDetail.getJobDataMap().put("em",entityManager);
			deleteJobDetail.getJobDataMap().put("log",LogFactory.getLog(AutoDeleteJob.class));
			deleteJobDetail.getJobDataMap().put("injector",injector);
			
			//Trigger deleteTrigger = TriggerUtils.makeSecondlyTrigger(30,0);  // test
			
			// fire every 25th of the month at 01:00
	 		Trigger deleteTrigger = TriggerUtils.makeMonthlyTrigger(25,1,0); 						
			deleteTrigger.setName("autoDeleteTrigger");
			
			//start next day at 01:00
			final Calendar deleteCalendar=Calendar.getInstance();
			deleteCalendar.add(Calendar.DAY_OF_MONTH,1);
			deleteCalendar.setTime(AutoExportJob.getZeroTimeDate(deleteCalendar.getTime()));
			deleteCalendar.add(Calendar.HOUR_OF_DAY, 1);
			deleteTrigger.setStartTime(deleteCalendar.getTime()); 	 								
			
			quartz.getScheduler().scheduleJob(deleteJobDetail, deleteTrigger);
			log.info("AutoDeleteJob job scheduled for EVERY 25th day of the MONTH at 01:00 ");
		
		}catch (Exception e) {
			log.error("Scheduling failed");
			e.printStackTrace();
		}
	}
}
