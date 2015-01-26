package org.sigmah.server.mapper;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dto.base.mapping.IsMappingMode;

/**
 * Mapper service interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Mapper {

	/**
	 * Maps an object into another one.
	 * 
	 * @param <T>
	 *          The dest type.
	 * @param o
	 *          The source object.
	 * @param c
	 *          The dest type.
	 * @return The dest mapping of the given object.
	 */
	<T> T map(Object o, Class<T> c);

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
	 * Maps a collection of objects into a set of another ones.
	 * 
	 * @param <O>
	 *          The objects type.
	 * @param <T>
	 *          The dest type.
	 * @param l
	 *          The source collection.
	 * @param c
	 *          The dest type.
	 * @return The dest mapping set of the given collection (never <code>null</code>).
	 */
	<O, T> Set<T> mapCollectionToSet(Collection<O> l, Class<T> c);

	/**
	 * Maps a collection of objects into a given {@code Set} implementation.
	 * 
	 * @param <O>
	 *          The objects type.
	 * @param <T>
	 *          The dest type.
	 * @param l
	 *          The source collection.
	 * @param c
	 *          The dest type.
	 * @param setImpl
	 *          The {@code Set} implementation.
	 * @return The dest mapping set of the given collection (never <code>null</code>).
	 */
	<O, T> Set<T> mapCollectionToSet(Collection<O> l, Class<T> c, Set<T> setImpl);

	/**
	 * Populates the given {@code dest} object with the values of the given {@code src} object.
	 * 
	 * @param <T>
	 *          The populated dest type.
	 * @param src
	 *          The source object used to populate {@code dest} fields.
	 * @param dest
	 *          The populated dest object.
	 * @return The populated dest object or {@code null} if {@code dest} is {@code null}.
	 */
	<T> T populate(Object src, T dest);

	/**
	 * Constructs new instance of destinationClass and performs mapping between from source
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
	<T> T map(Object source, Class<T> destinationClass, IsMappingMode mapId);

}
