package org.sigmah.server.mapper;

import java.util.Collection;
import java.util.List;

import org.sigmah.shared.dto.base.mapping.IsMappingMode;

/**
 * Mapper service interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Mapper {

	/**
	 * Maps an object into another one.
	 * <p>
	 * This method is slow. Prefere using 
	 * {@link #map(java.lang.Object, java.lang.Object)} if possible.
	 * </p>
	 * 
	 * @param <T>
	 *          The dest type.
	 * @param o
	 *          The source object.
	 * @param c
	 *          The dest type.
	 * @return The dest mapping of the given object.
	 */
	@Deprecated
	<T> T map(Object o, Class<T> c);
	
	/**
	 * Maps an object into another one.
	 * 
	 * @param <T>
	 *          The dest type.
	 * @param o
	 *          The source object.
	 * @param t
	 *          The dest object.
	 * @return The dest mapping of the given object.
	 */
	<T> T map(Object o, T t);

	/**
	 * Maps a collection of objects into a list of another ones.
	 * 
	 * @param <O>
	 *          The objects type.
	 * @param <T>
	 *          The dest type.
	 * @param l
	 *          The source collection.
	 * @param c
	 *          The dest type.
	 * @return The dest mapping list of the given collection (never <code>null</code>).
	 */
	<O, T> List<T> mapCollection(Collection<O> l, Class<T> c);

	/**
	 * Maps a collection of objects into a list of another ones.
	 * 
	 * @param <O>
	 *          The objects type.
	 * @param <T>
	 *          The dest type.
	 * @param l
	 *          The source collection.
	 * @param c
	 *          The dest type.
	 * @param mapId
	 *          The mapping to perform.
	 * @return The dest mapping list of the given collection (never <code>null</code>).
	 */
	<O, T> List<T> mapCollection(Collection<O> l, Class<T> c, IsMappingMode mapId);

    /**
	 * Constructs new instance of destinationClass and performs mapping between from source
	 * 
     * <p>
	 * This method is slow. Prefere using 
	 * {@link #map(java.lang.Object, java.lang.Object, IsMappingMode)} if possible.
	 * </p>
     * 
	 * @param <T>
	 *          The dest type.
	 * @param source
	 *          The source object.
	 * @param destinationClass
	 *          The dest type.
	 * @param mapId
	 *          The mapping to perform
	 * @return The dest mapping of the given object.
	 */
    @Deprecated
	<T> T map(Object source, Class<T> destinationClass, IsMappingMode mapId);
    
	/**
	 * Performs mapping between source and the given object.
	 * 
	 * @param <T>
	 *          The dest type.
	 * @param source
	 *          The source object.
	 * @param destination
	 *          The destination object.
	 * @param mapId
	 *          The mapping to perform
	 * @return The dest mapping of the given object.
	 */
	<T> T map(Object source, T destination, IsMappingMode mapId);

}
