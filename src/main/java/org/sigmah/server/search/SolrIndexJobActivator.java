package org.sigmah.server.search;

import javax.persistence.EntityManager;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.sigmah.server.autoExport.QuartzScheduler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class SolrIndexJobActivator {
	
	private final Provider<EntityManager> entityManagerProvider;
	
	@Inject
	public SolrIndexJobActivator(final QuartzScheduler quartz, Provider<EntityManager> entityManagerProvider, final Injector injector) {

		this.entityManagerProvider = entityManagerProvider;
		
		JobDetail solrIndexJobDetail = new JobDetail("solrIndexJob", null, SolrIndexJob.class);
		solrIndexJobDetail.getJobDataMap().put("em", this.entityManagerProvider.get());
		solrIndexJobDetail.getJobDataMap().put("injector", injector);
		Trigger solrIndexTrigger = TriggerUtils.makeMinutelyTrigger(10);
		solrIndexTrigger.setName("solrIndexTrigger");
		try {
			quartz.getScheduler().scheduleJob(solrIndexJobDetail, solrIndexTrigger);
		} catch (SchedulerException e) {
			System.out.println("Failed to execute Solr Index Job due to Scheduler Exception!");
			e.printStackTrace();
		}
	}
}
