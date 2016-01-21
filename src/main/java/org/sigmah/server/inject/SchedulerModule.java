package org.sigmah.server.inject;

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
