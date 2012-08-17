package org.sigmah.server.schedule.export;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.hibernate.GlobalExportHibernateDAO;
import org.sigmah.shared.domain.export.GlobalExport;
import org.sigmah.shared.domain.export.GlobalExportContent;
import org.sigmah.shared.domain.export.GlobalExportSettings;

import com.google.inject.Injector;

public class AutoDeleteJob implements Job {

	public void execute(JobExecutionContext executionContext)
			throws JobExecutionException {
		final JobDataMap dataMap = executionContext.getJobDetail().getJobDataMap();
		final EntityManager em = (EntityManager) dataMap.get("em");
		final Log log = (Log) dataMap.get("log");
		final Injector injector = (Injector)dataMap.get("injector");
		EntityTransaction tx = null;
		 
 		try {

			// Open transaction
			/*
			 *  NOTE: it is impossible to use @Transactional for this method
			 *  The reason is link{TransactionalInterceptor} gets EntityManager 
			 *  from the injector; this is server thread, so, EM is out of scope 
			 */
			tx = em.getTransaction();
			tx.begin();
			
 
			final GlobalExportDAO exportDAO=new GlobalExportHibernateDAO(em);
		 	
			final List<GlobalExportSettings> settings = exportDAO.getGlobalExportSettings();
			for(final GlobalExportSettings setting:settings){
				
				/*
				 * Check for auto delete schedule 
				 */			 					
				
				//skip if no delete schedule is specified
				if(setting.getAutoDeleteFrequency()==null 
						|| setting.getAutoDeleteFrequency()<1) continue;
				
 				final Calendar scheduledCalendar = Calendar.getInstance();
 				// subtract months from current date 
				scheduledCalendar.add(Calendar.MONTH,0-setting.getAutoDeleteFrequency().intValue());
				
				// get older exports
				List<GlobalExport> exports =
					exportDAO.getOlderExports(scheduledCalendar.getTime(), setting.getOrganization());
				//delete exports and their contents
				for(final GlobalExport export:exports){
					final List<GlobalExportContent> contents=export.getContents();
					for(GlobalExportContent content:contents){
						em.remove(content);
					}
					em.remove(export);
				}
				
			 
			}		 
			tx.commit();
			
			log.info("Scheduled DELETE of global exports fired");
 		 
		} catch (Exception ex) {
			 if ( tx != null && tx.isActive() ) tx.rollback();
			log.error("Scheduled global export job failed : " + ex.getMessage());
			ex.printStackTrace();
		}  
	}
	

}
