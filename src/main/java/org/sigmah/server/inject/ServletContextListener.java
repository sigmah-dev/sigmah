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


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletContextEvent;

import org.sigmah.server.autoExport.GlobalExportJobActivator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.sigmah.server.autoExport.QuartzScheduler;
import org.sigmah.server.search.SolrIndexJobActivator;

/**
 * Builds the Guice injector.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ServletContextListener extends GuiceServletContextListener {

	/**
	 * Log.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ServletContextListener.class);
	
	/**
	 * Service used to schedule the initialization of jobs.
	 */
	private ScheduledExecutorService executorService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Injector getInjector() {
		LOGGER.info("Creating Guice injector.");

		return Guice.createInjector(
		// Configuration module.
			new ConfigurationModule(),
			// Servlet module.
			new ServletModule(),
			// Persistence module.
			new PersistenceModule(),
			// Security module.
			new SecurityModule(),
			// CommandHandler module.
			new CommandHandlerModule(),
			// Mapper module.
			new MapperModule(),
			// Mail module.
			new MailModule(),
			// I18nServer module.
			new I18nServerModule(),
			// scheduler Module
			new SchedulerModule());
	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		super.contextInitialized(servletContextEvent);

		final Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(Injector.class.getName());
		executorService = Executors.newScheduledThreadPool(1);
		executorService.schedule(new Runnable() {

			@Override
			public void run() {
				if(!Thread.currentThread().isInterrupted()) {
					// Context has been initialized.
					injector.getInstance(GlobalExportJobActivator.class);
					injector.getInstance(SolrIndexJobActivator.class);
				}
			}
			
		}, 60, TimeUnit.SECONDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) {
		LOGGER.info("Stopping executor service...");
		if(executorService != null) {
			executorService.shutdownNow();
		}
		
		LOGGER.info("Stopping Quartz scheduler...");
		final Injector injector = (Injector) servletContextEvent.getServletContext().getAttribute(Injector.class.getName());
		
		if(injector != null) {
			final QuartzScheduler scheduler = injector.getInstance(QuartzScheduler.class);
			scheduler.shutdown();
		}
		
		super.contextDestroyed(servletContextEvent);
	}
	
}
