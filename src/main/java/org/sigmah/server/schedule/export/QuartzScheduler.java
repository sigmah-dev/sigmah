/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.schedule.export;

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import com.google.inject.Inject;


/*
 * Setting up quartz with guice 
 * @author sherzod
 */
public class QuartzScheduler {

	private final static Log log = LogFactory.getLog(QuartzScheduler.class);
	private final Scheduler scheduler;

	@Inject
	public QuartzScheduler(final SchedulerFactory factory, final GuiceJobFactory jobFactory) 
	throws SchedulerException, ParseException {
	   scheduler = factory.getScheduler();
	   scheduler.setJobFactory(jobFactory);	  	 
	   scheduler.start();
	}

	public final Scheduler getScheduler() {
	   return scheduler;
	}

	public void shutdown() {
	   try {
	       scheduler.shutdown();
	   } catch (SchedulerException e){ 
	   			log.fatal( "QuartzScheduler falied to shutdown : " + e);
	   }
	}

	public void start() {
	   try {
	       scheduler.start();
	   } catch (SchedulerException ex) {
		   log.fatal( "QuartzScheduler falied to start : " + ex);
	   }
	}
}
