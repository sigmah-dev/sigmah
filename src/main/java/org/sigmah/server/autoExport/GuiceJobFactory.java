package org.sigmah.server.autoExport;

import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * Setting up quartz with guice
 * 
 * @author sherzod V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)v2.0
 */
public class GuiceJobFactory implements JobFactory {

	public final Injector injector;

	@Inject
	public GuiceJobFactory(final Injector injector) {
		this.injector = injector;
	}

	@Override
	@SuppressWarnings({
											"rawtypes",
											"unchecked"
	})
	public Job newJob(TriggerFiredBundle bundle) throws SchedulerException {

		JobDetail jobDetail = bundle.getJobDetail();
		Class jobClass = jobDetail.getJobClass();
		return (Job) injector.getInstance(jobClass);

	}
}
