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
