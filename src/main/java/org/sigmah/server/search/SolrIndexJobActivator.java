package org.sigmah.server.search;

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

import javax.persistence.EntityManager;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerUtils;
import org.sigmah.server.autoExport.QuartzScheduler;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Schedules jobs link{SolrIndexJob} 
 *  Executes every 10 minutes
 * @author Aditya Adhikary (aditya15007@iiitd.ac.in)
 * 
 */

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
