package org.sigmah.server.search;

import java.io.IOException;
import java.util.Calendar;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import com.google.inject.Injector;

public class SolrIndexJob implements Job{

	@Override
	public void execute(JobExecutionContext executionContext) throws JobExecutionException {
		
		final JobDataMap dataMap = executionContext.getJobDetail().getJobDataMap();
		final EntityManager em = (EntityManager) dataMap.get("em");
		final Injector injector = (Injector) dataMap.get("injector");
		System.out.println("Starting Solr Full Data Import!");
		if( !SolrSearcher.getInstance().FullDataImport()){
			System.out.println("Could not finish Full Data Import!");
			return;
		}
		System.out.println("Finished Solr Full Data Import!");
		EntityTransaction tx = null;
		
		try {
			tx = em.getTransaction();
			tx.begin();
			FilesSolrManager filesSolrManager = injector.getInstance(FilesSolrManager.class);
			System.out.println("Starting Files Solr Indexing!");
			filesSolrManager.FilesImport(SolrSearcher.getInstance());
			System.out.println("Finished Files Solr Indexing!");
			tx.commit();
			System.out.println("Scheduled indexing of solr server over");
			
		}catch(RuntimeException | IOException e){
			if (tx != null && tx.isActive())
				tx.rollback();
			System.out.println("Could not complete Files Solr Indexing!");
			System.out.println("Ran into unexpected Runtime Exception while indexing files!");
			e.printStackTrace();
		}
		
	}

}
