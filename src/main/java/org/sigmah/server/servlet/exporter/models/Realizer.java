package org.sigmah.server.servlet.exporter.models;

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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.sigmah.server.computation.ServerComputations;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.element.ComputationElement;
import org.sigmah.shared.computation.Computations;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Creates plain objects from Hibernate proxies.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) V1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) V2.0
 */
public class Realizer {

	private final static Log LOG = LogFactory.getLog(Realizer.class);
	
	private final static String ORGANIZATION_FIELD = "organization";
	private final static String FORMULA_FIELD = "rule";

	private Realizer() {
	}

	/**
	 * Creates a new instance of <code>object</code> with <code>ArrayList</code>s instead of <code>PersistentBag</code>s
	 * and <code>HashSet</code> instead of <code>PersistentSet</code>s. <br>
	 * <b>Note:</b> do not use this without testing its compatibility with your objects.
	 * 
	 * @param <T>
	 *          Type of the object to copy.
	 * @param object
	 *          An hibernate proxy.
	 * @param ignores 
	 *          Array of classes to ignore.
	 * @return A copy of the given object without any proxy instance.
	 */
	public static <T> T realize(T object, Class<?>... ignores) {
		return realize(object, Collections.singleton(ORGANIZATION_FIELD), ignores);
	}
	
	public static <T> T realize(T object, Set<String> ignoredFields, Class<?>... ignores) {
		return realize(object, new HashMap<>(), ignoredFields, new HashSet<>(Arrays.asList(ignores)), object);
	}

	/**
	 * Creates a new object and recursively fills its fields.
	 * 
	 * @param <T>
	 *          Type of the object to copy.
	 * @param object
	 *          Object to copy.
	 * @param alreadyRealizedObjects
	 *          Set of already copied objects.
	 * @param ignores
	 *          Set of classes to ignore.
	 * @param parent
	 *          Parent object.
	 * @return A copy of the given object without any proxy instance.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T realize(T object, Map<Object, Object> alreadyRealizedObjects, Set<String> ignoredFields, Set<Class<?>> ignores, Object parent) {
		T result = null;

		if (object != null) {
			
			// If the given object has already been instantiated, no need to instantiate it again
			if (alreadyRealizedObjects.containsKey(object)) {
				return (T) alreadyRealizedObjects.get(object);
			}

			// Extracting the class of the current object
			final Class<T> clazz = object instanceof HibernateProxy ?
				((HibernateProxy)object).getHibernateLazyInitializer().getPersistentClass() :
				(Class<T>) object.getClass();
			
			if (ignores.contains(clazz) || clazz.getName().startsWith("java.") || clazz.isEnum()) {
				LOG.trace("\t\tUsing the given value for " + clazz);
				return object;
			}
			
			LOG.trace("Realizing " + clazz + "...");

			try {
				final Constructor<T> emptyConstructor = clazz.getConstructor();

				// Creating a new instance of the current object
				// REM: this will crash if the object doesn't have an empty constructor
				final T instance = emptyConstructor.newInstance();
				alreadyRealizedObjects.put(object, instance);

				final List<Field> fields = getFieldsOfClass(clazz);

				for (final Field field : fields) {
					final Object destinationValue = getFieldValue(field, clazz, object, alreadyRealizedObjects, ignoredFields, ignores, parent);
					
					if (destinationValue != null) {
						setFieldValue(field, clazz, instance, destinationValue);
					}
				}

				result = instance;

			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | HibernateException e) {
				LOG.debug("An error occured while realizing " + object, e);
			}

		}

		return result;
	}

	/**
	 * Sets the given <code>field</code> with the given <code>value</code> in
	 * the object <code>instance</code>.
	 * 
	 * @param <T>
	 *          Type of the object to set.
	 * @param field
	 *          Field to set.
	 * @param clazz
	 *          Class of the object to set.
	 * @param instance
	 *          Object to set.
	 * @param value
	 *          Value to set.
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws SecurityException 
	 */
	private static <T> void setFieldValue(final Field field, final Class<T> clazz, final T instance, final Object value) throws IllegalArgumentException, InvocationTargetException, IllegalAccessException, SecurityException {
		
		// Setting the field of the new object
		final Method setterMethod = getSetterMethod(field, clazz);
		
		if(setterMethod != null) {
			setterMethod.invoke(instance, value);
		} else {
			field.setAccessible(true); // Force the accessibility of the current field
			field.set(instance, value);
		}
	}

	/**
	 * Retrieve the value of the given <code>field</code> for the given 
	 * <code>source</code> object.
	 * 
	 * @param <T>
	 *          Type of the source object.
	 * @param field
	 *          Field to extract.
	 * @param clazz
	 *          Class of the source object.
	 * @param source
	 *          Object to read from.
	 * @param alreadyRealizedObjects
	 *          Set of already handled objects (to avoid infinite recursion).
	 * @param ignores
	 *          Set of classes to ignore.
	 * @param parent
	 *          Parent object.
	 * @return The value of the given field for the given object.
	 * @throws HibernateException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException 
	 */
	private static <T> Object getFieldValue(final Field field, final Class<T> clazz, final T source, final Map<Object, Object> alreadyRealizedObjects, final Set<String> ignoredFields, final Set<Class<?>> ignores, final Object parent) 
			throws HibernateException, SecurityException, IllegalAccessException, InvocationTargetException, IllegalArgumentException {
		
		// Avoid trying to modify static fields.
		// Organization should not be exported.
		if (Modifier.isStatic(field.getModifiers()) || (!ignores.contains(clazz) && ignoredFields.contains(field.getName()))) {
			return null;
		}
		
		LOG.trace("\tfield " + field.getName());
		final Method getterMethod = getGetterMethod(field, clazz);
		
		final Object sourceValue = getterMethod.invoke(source);
		final Object destinationValue;
		
		final Class<?> sourceValueClass = getterMethod.getReturnType();
		
		if (sourceValue instanceof PersistentCollection || sourceValue instanceof HibernateProxy) {
			Hibernate.initialize(sourceValue);
		}
		
		if (sourceValue == null || sourceValueClass == null) {
			destinationValue = null;

		} else if (sourceValue instanceof PersistentBag || sourceValue instanceof PersistentList) {
			// Turning persistent bags into array lists
			final ArrayList<Object> list = new ArrayList<Object>();

			for (Object value : (PersistentBag) sourceValue) {
				list.add(realize(value, alreadyRealizedObjects, ignoredFields, ignores, parent));
			}

			destinationValue = list;

		} else if (sourceValue instanceof PersistentSet) {
			// Turning persistent sets into hash sets
			final HashSet<Object> set = new HashSet<>();

			for (Object value : (PersistentSet) sourceValue) {
				set.add(realize(value, alreadyRealizedObjects, ignoredFields, ignores, parent));
			}

			destinationValue = set;
			
		} else if (source instanceof ComputationElement && field.getName().equals(FORMULA_FIELD)) {
			final Collection<FlexibleElementDTO> elements;
			
			if (parent instanceof ProjectModel) {
				elements = ServerComputations.getAllElementsFromModel((ProjectModel) parent);
			} else if (parent instanceof OrgUnitModel) {
				elements = ServerComputations.getAllElementsFromModel((OrgUnitModel) parent);
			} else {
				elements = Collections.<FlexibleElementDTO>emptyList();
			}
			
			destinationValue = Computations.formatRuleForEdition(((ComputationElement) source).getRule(), elements);

		} else {
			destinationValue = realize(sourceValue, alreadyRealizedObjects, ignoredFields, ignores, parent);
		}
		
		return destinationValue;
	}

	/**
	 * Find all the declared fields of the given class and its super classes
	 * for Sigmah objects.
	 * 
	 * @param clazz
	 *          Class to read.
	 * @return A list of every declared fields (also contains static fields).
	 * @throws SecurityException If one of the fields is protected.
	 */
	private static List<Field> getFieldsOfClass(final Class<?> clazz) throws SecurityException {
		
		// Accessing fields from the given object.
		final ArrayList<Field> fields = new ArrayList<>();
		
		fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
		
		// Accessing fields from the super classes.
		Class<?> superClass = clazz.getSuperclass();
		while (superClass.getPackage().getName().startsWith("org.sigmah")) {
			
			fields.addAll(Arrays.asList(superClass.getDeclaredFields()));
			
			superClass = superClass.getSuperclass();
		}
		
		return fields;
	}
	
	/**
	 * Find the getter method for the given <code>field</code> in the given
	 * <code>class</code>.
	 * <p>
	 * The name of the getter method is assumed to starts by <code>get</code> or
	 * by <code>is</code>.
	 * 
	 * @param field
	 *          Field to search.
	 * @param clazz
	 *          Class to use.
	 * @return The getter field or <code>null</code> if not found.
	 */
	private static Method getGetterMethod(Field field, Class<?> clazz) {
		final String getAccessor = "get" + field.getName().toLowerCase();
		
		for(final Method method : clazz.getMethods()) {
			if(getAccessor.equals(method.getName().toLowerCase()) && method.getParameterTypes().length == 0) {
				return method;
			}
		}
		
		final String isAccessor = "is" + field.getName().toLowerCase();
		
		for(final Method method : clazz.getMethods()) {
			if(isAccessor.equals(method.getName().toLowerCase()) && method.getParameterTypes().length == 0) {
				return method;
			}
		}
		
		return null;
	}
	
	/**
	 * Find the setter method for the given <code>field</code> in the given
	 * <code>class</code>.
	 * <p>
	 * The name of the setter method is assumed to starts by <code>set</code>.
	 * 
	 * @param field
	 *          Field to search.
	 * @param clazz
	 *          Class to use.
	 * @return The setter field or <code>null</code> if not found.
	 */
	private static Method getSetterMethod(Field field, Class<?> clazz) {
		final String getAccessor = "set" + field.getName().toLowerCase();
		
		for(final Method method : clazz.getMethods()) {
			if(getAccessor.equals(method.getName().toLowerCase()) && method.getParameterTypes().length == 1) {
				return method;
			}
		}
		
		return null;
	}
}
