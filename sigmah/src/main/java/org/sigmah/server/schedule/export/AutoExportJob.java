package org.sigmah.server.schedule.export;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.logging.Log;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.sigmah.server.dao.GlobalExportDAO;
import org.sigmah.server.dao.hibernate.GlobalExportHibernateDAO;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.GlobalExportDataProvider;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.domain.export.GlobalExport;
import org.sigmah.shared.domain.export.GlobalExportSettings;

import com.google.inject.Inject;
import com.google.inject.Injector;

public class AutoExportJob implements Job {

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
			 *  from the injector which is out of scope 
			 */
			tx = em.getTransaction();
			tx.begin();
			
 
			final GlobalExportDAO exportDAO=new GlobalExportHibernateDAO(em);
			final GlobalExportDataProvider dataProvider=
				injector.getInstance(GlobalExportDataProvider.class);
			
			final List<GlobalExportSettings> settings = exportDAO.getGlobalExportSettings();
			for(final GlobalExportSettings setting:settings){
				
				/*
				 * Check for auto export schedule 
				 */
				
				//skip if no export schedule is specified
				if(setting.getAutoExportFrequency()==null 
						|| setting.getAutoExportFrequency()<1) continue;
				
				final Calendar systemCalendar = Calendar.getInstance();
				final Calendar scheduledCalendar = Calendar.getInstance();
				
 				Date lastExportDate = setting.getLastExportDate();
				if(lastExportDate==null){
					lastExportDate = systemCalendar.getTime();
					setting.setLastExportDate(lastExportDate);
					em.merge(setting);
				}else{
					scheduledCalendar.setTime(lastExportDate);
					// add scheduled days to the last exported date
					scheduledCalendar.add(Calendar.DAY_OF_MONTH,setting.getAutoExportFrequency());
				}
												
				final Date systemDate=getZeroTimeDate(systemCalendar.getTime());
				final Date scheduledDate=getZeroTimeDate(scheduledCalendar.getTime());
				 
				if(systemDate.compareTo(scheduledDate)>=0){ 
					/*
					 * Start auto export  
					 */
					
					// persist global export logger
					final GlobalExport globalExport=new GlobalExport();
					globalExport.setOrganization(setting.getOrganization());
					globalExport.setDate(systemCalendar.getTime());
					em.persist(globalExport);
				
					// generate export content
					final Map<String,List<String[]>> exportData =
						dataProvider.generateGlobalExportData(
							setting.getOrganization().getId(),
							em,
							setting.getLocale());
					
				// persist export content
					dataProvider.persistGlobalExportDataAsCsv(globalExport, em, exportData);
				}
			 
			}		 
			tx.commit();
			
			log.info("Scheduled EXPORT of global exports fired");
 		 
		} catch (Exception ex) {
			 if ( tx != null && tx.isActive() ) tx.rollback();
			log.error("Scheduled global export job failed : " + ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	public static Date getZeroTimeDate(Date fecha) {
	    Date res = fecha;
	    Calendar calendar = Calendar.getInstance();

	    calendar.setTime( fecha );
	    calendar.set(Calendar.HOUR_OF_DAY, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);

	    res = calendar.getTime();

	    return res;
	}

}
