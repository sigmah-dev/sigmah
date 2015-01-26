package org.sigmah.server.inject.util;

import java.lang.reflect.ParameterizedType;

/**
 * Utility class providing static methods for injectors.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Injectors {

	private Injectors() {
		// Only provides static methods.
	}

	/**
	 * Classes enhanced by <b>guice</b> injector possess this tag into wrapped class name.
	 */
	private static final String GUICE_ENHANCED_CLASS_TAG = "EnhancerByGuice";

	/**
	 * <p>
	 * Returns the given object {@code instance} corresponding <em>original</em> class.
	 * </p>
	 * <p>
	 * Classes using {@link com.google.inject.persist.Transactional} annotation are automatically enhanced by <b>guice</b>
	 * injector. This method retrieves the <em>original</em> class (if necessary).
	 * </p>
	 * 
	 * @param instance
	 *          An object instance which <em>original</em> class is returned.
	 * @return the given object {@code instance} corresponding <em>original</em> class.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getClass(final T instance) {

		if (instance == null) {
			return null;
		}

		Class<?> result = instance.getClass();
		while (result.getSimpleName().contains(GUICE_ENHANCED_CLASS_TAG)) {
			// Implementations with @Transactional annotation are wrapped by Guice.
			result = result.getSuperclass();
		}

		return (Class<T>) result;
	}

	/**
	 * Recursive method finding the given {@code clazz} first super generic class with parameterized type.
	 * 
	 * @param clazz
	 *          The source class.
	 * @return the given {@code clazz} first super generic class parameterized type.
	 */
	public static ParameterizedType findGenericSuperClass(final Class<?> clazz) {
		if (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			return findGenericSuperClass(clazz.getSuperclass());
		}
		return (ParameterizedType) clazz.getGenericSuperclass();
	}

}
