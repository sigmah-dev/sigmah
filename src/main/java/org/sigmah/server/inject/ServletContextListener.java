package org.sigmah.server.inject;

import java.util.concurrent.Callable;
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

/**
 * Builds the Guice injector.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ServletContextListener extends GuiceServletContextListener {

	/**
	 * Log.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ServletContextListener.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Injector getInjector() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Creating Guice injector.");
		}

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

		final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

		executorService.schedule(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				// Context has been initialized.

				injector.getInstance(GlobalExportJobActivator.class);

				return null;

			}
		}, 60, TimeUnit.SECONDS);

	}
}
