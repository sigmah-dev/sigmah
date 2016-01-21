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
		 */
		Class<?>[] value();

	}

	/**
	 * The injector.
	 */
	private final Injector injector;

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
	 */
	public GuiceJUnitRunner(final Class<?> klass) throws InitializationError {

		super(klass);

		final List<Class<?>> classes = getModulesFor(klass);
		this.injector = createInjectorFor(classes);

		if (classes.contains(PersistenceModule.class)) {
			startPersistUnit();
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
	 * Starts the {@link PersistService}.<br/>
	 * <b>Should be done only once.</b>
	 */
	private void startPersistUnit() {
		injector.getInstance(PersistService.class).start();
	}
}
