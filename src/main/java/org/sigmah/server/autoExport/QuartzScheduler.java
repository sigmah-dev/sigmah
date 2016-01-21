package org.sigmah.server.autoExport;

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

import java.text.ParseException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;

import com.google.inject.Inject;

/**
 * Setting up quartz with guice
 * 
 * @author sherzod V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */
public class QuartzScheduler {

	private final static Log log = LogFactory.getLog(QuartzScheduler.class);
	private final Scheduler scheduler;

	@Inject
	public QuartzScheduler(final SchedulerFactory factory, final GuiceJobFactory jobFactory) throws SchedulerException, ParseException {

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

		} catch (SchedulerException e) {

			log.fatal("QuartzScheduler falied to shutdown : " + e);

		}

	}

	public void start() {

		try {

			scheduler.start();

		} catch (SchedulerException ex) {

			log.fatal("QuartzScheduler falied to start : " + ex);

		}

	}
}
