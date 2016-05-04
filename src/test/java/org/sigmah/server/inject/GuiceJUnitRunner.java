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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.PersistService;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.runner.notification.RunNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * <p>
 * Guice JUnit runner class.
 * </p>
 * <p>
 * Use this class as following :
 * </p>
 * 
 * <pre>
 * @RunWith(GuiceJUnitRunner.class)
 * @GuiceModules({ ComponentsTestModule.class, ServicesTestModule.class })
 * public class ServiceTest {
 * 
 *     @Inject
 *     private IService service;
 *  
 *     @Test
 *     public void testApp() {
 *         Assert.assertEquals("Hello World!", service.doSomething());
 *     }
 * }
 * </pre>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class GuiceJUnitRunner extends BlockJUnit4ClassRunner {
	
	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(GuiceJUnitRunner.class);

	/**
	 * Annotation used to specify modules to load for JUnit tests.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@Inherited
	public static @interface GuiceModules {

		/**
		 * Module(s) classe(s) to initialize into injector.
		 * 
		 * @return An array of the classes to use to initialize the injector.
		 */
		Class<?>[] value();

	}

	/**
	 * The injector.
	 */
	private final Injector injector;
	
	/**
	 * State of the database.
	 * <p>
	 * <code>true</code> if the database is required by the test class but
	 * no local database was found.
	 * <code>false</code> otherwise.
	 * <p>
	 * If <code>true</code> the whole test class will be skipped.
	 */
	private final boolean databaseRequiredButUnavailable;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object createTest() throws Exception {
		final Object obj = super.createTest();
		injector.injectMembers(obj);
		return obj;
	}

	/**
	 * Guice JUnit runner initialization.
	 * 
	 * @param klass
	 *          The class defining injector modules to initialize.
	 * @throws InitializationError
	 *           If an error occurs during injector creation.
	 * @throws ParserConfigurationException
	 *           If a required feature of the XML parser is not available.
	 * @throws SAXException
	 *           If the database configuration file could not be parsed.
	 * @throws IOException 
	 *           If an error occurs while reading the database configuration file.
	 * @throws ClassNotFoundException
	 *           If the SQL driver was not found.
	 */
	public GuiceJUnitRunner(final Class<?> klass) throws InitializationError, ParserConfigurationException, SAXException, IOException, ClassNotFoundException {

		super(klass);

		final List<Class<?>> classes = getModulesFor(klass);
		this.injector = createInjectorFor(classes);

		boolean databaseIsRequiredAndUnavailable = false;
		
		if (classes.contains(PersistenceModule.class)) {
			if (isDatabaseAvailable()) {
				startPersistUnit();
			} else {
				databaseIsRequiredAndUnavailable = true;
			}
		}
		
		this.databaseRequiredButUnavailable = databaseIsRequiredAndUnavailable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(RunNotifier notifier) {
		
		if (databaseRequiredButUnavailable) {
			notifier.fireTestIgnored(getDescription());
		} else {
			super.run(notifier);
		}
	}
	
	/**
	 * Creates the {@link Injector} instance for the given modules {@code classes}.
	 * 
	 * @param classes
	 *          The modules classes.
	 * @return the created {@link Injector} instance.
	 * @throws InitializationError
	 *           If an error occurs during module(s) instantiation.
	 */
	private static Injector createInjectorFor(final List<Class<?>> classes) throws InitializationError {

		final Module[] modules = new Module[classes.size()];
		int index = 0;

		for (final Class<?> klass : classes) {
			try {

				modules[index++] = (Module) (klass).newInstance();

			} catch (final InstantiationException | IllegalAccessException e) {
				throw new InitializationError(e);
			}
		}

		return Guice.createInjector(modules);
	}

	/**
	 * Returns the modules classes defined in given {@code klass} {@link GuiceModules} annotation.
	 * 
	 * @param klass
	 *          The class defining a {@code GuiceModules} annotation.
	 * @return The modules classes defined in given {@code klass} {@link GuiceModules} annotation.
	 * @throws InitializationError
	 *           If the {@code klass} does not define a {@link GuiceModules} annotation.
	 */
	private static List<Class<?>> getModulesFor(final Class<?> klass) throws InitializationError {

		final GuiceModules annotation = klass.getAnnotation(GuiceModules.class);

		if (annotation == null) {
			throw new InitializationError("Missing @GuiceModules annotation for unit test '" + klass.getName() + "'");
		}

		return Arrays.asList(annotation.value());
	}

	/**
	 * Starts the {@link PersistService}.
	 * <b>Should be done only once.</b>
	 */
	private void startPersistUnit() {
		injector.getInstance(PersistService.class).start();
	}
	
	/**
	 * Parse the <code>META-INF/persistence.xml</code> file and try to connect to 
	 * the database.
	 * 
	 * @return <code>true</code> if the connection was successful, 
	 * <code>false</code> otherwise.
	 */
	private boolean isDatabaseAvailable() throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
		
		final PersistenceXmlHandler configuration = parsePersistenceXml();
		Class.forName(configuration.getDriverClass());
		
		boolean available;
		try {
			final Connection testConnection = DriverManager.getConnection(
					configuration.getConnectionUrl(), configuration.getUsername(), configuration.getPassword());
			testConnection.close();
			available = true;
		} catch (SQLException ex) {
			LOGGER.trace("Unable to open a connection to the test database.", ex);
			available = false;
		}
		
		return available;
	}
	
	/**
	 * Parse the <code>META-INF/persistence.xml</code> file and return its
	 * configuration.
	 * 
	 * @return an instance of <code>PersistenceXmlHandler</code> containing the
	 * test database connection properties.
	 * 
	 * @throws ParserConfigurationException
	 *           If a required feature is not available.
	 * @throws SAXException
	 *           If the XML file could not be parsed.
	 * @throws IOException 
	 *           If an error occurs while reading the file.
	 */
	private PersistenceXmlHandler parsePersistenceXml() throws ParserConfigurationException, SAXException, IOException {
		
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		
		// Disable validation to avoid fetching the schema for each test.
		factory.setValidating(false);
		factory.setFeature("http://xml.org/sax/features/validation", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
		factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
		
		final SAXParser parser = factory.newSAXParser();
		final PersistenceXmlHandler persistenceXmlHandler = new PersistenceXmlHandler();
		parser.parse(getClass().getResourceAsStream("/META-INF/persistence.xml"), persistenceXmlHandler);
		
		return persistenceXmlHandler;
	}
	
}
