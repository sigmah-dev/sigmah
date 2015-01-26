/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.schedule.export;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

/*
 * Setting up quartz with guice 
 * @author sherzod
 */
public class GuiceJobFactory implements JobFactory {

	public final Injector injector;

	@Inject
	public GuiceJobFactory(final Injector injector) {
	   this.injector = injector;
	}

	public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {
		 JobDetail jobDetail = bundle.getJobDetail();
		 Class jobClass = jobDetail.getJobClass();
		 return (Job) injector.getInstance(jobClass);
	}
	}
