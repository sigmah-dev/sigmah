package org.sigmah.server.inject;

import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.sigmah.server.autoExport.GuiceJobFactory;
import org.sigmah.server.autoExport.QuartzScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Setting up quartz with guice
 * 
 * @author sherzod V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)V2.0
 */
public class SchedulerModule extends AbstractModule {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(SchedulerModule.class);

	@Override
	protected void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing quartz module.");
		}

		bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Singleton.class);
		bind(GuiceJobFactory.class).in(Singleton.class);
		bind(QuartzScheduler.class).in(Singleton.class);

	}

}
