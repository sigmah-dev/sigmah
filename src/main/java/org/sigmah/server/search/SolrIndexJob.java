package org.sigmah.server.search;

import java.util.Calendar;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

public class SolrIndexJob implements Job{

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		SolrSearcher.getInstance();
		// TODO Auto-generated method stub
		SolrSearcher.FullDataImport();
	}

}
