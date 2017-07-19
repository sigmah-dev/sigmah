package org.sigmah.server.search;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.quartz.impl.StdSchedulerFactory;
import org.sigmah.server.autoExport.QuartzScheduler;


public class SolrIndexJobActivator {
	
	private static Scheduler scheduler;
	private static SolrIndexJobActivator instance;
	
	public static SolrIndexJobActivator getSolrIndexJobActivator(){  //Singleton
		if( instance == null ){
			instance = new SolrIndexJobActivator();
		}
		return instance;
	}
	
	private SolrIndexJobActivator() {

		try {

			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
			
			System.out.println("Started Scheduler!");

			final JobDetail solrIndexJobDetail = new JobDetail("solrIndexJob", null, SolrIndexJob.class);
			Trigger solrIndexTrigger = TriggerUtils.makeMinutelyTrigger(10);
			solrIndexTrigger.setName("solrIndexTrigger");
			try {
				scheduler.scheduleJob(solrIndexJobDetail, solrIndexTrigger);
			} catch (SchedulerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// scheduler.shutdown();
		} catch (SchedulerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
