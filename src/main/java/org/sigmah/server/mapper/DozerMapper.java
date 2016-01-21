package org.sigmah.server.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dto.base.mapping.HasMappingMode;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;

import com.google.inject.Inject;

/**
 * Mapper service implementation using {@code org.dozer.Mapper}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DozerMapper implements Mapper {

	/**
	 * Dozer mapper.
	 */
	private final org.dozer.Mapper mapper;

	@Inject
	protected DozerMapper(final org.dozer.Mapper mapper) {
		this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T map(Object o, Class<T> c) {
		if (o == null) {
			return null;
		}
		return mapper.map(o, c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T map(Object o, T t) {
		if (o == null) {
			return null;
		}
		mapper.map(o, t);
		return t;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <O, T> List<T> mapCollection(Collection<O> l, Class<T> c) {
		return mapCollection(l, c, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <O, T> List<T> mapCollection(Collection<O> l, Class<T> c, IsMappingMode mode) {

		final ArrayList<T> dto = new ArrayList<T>();

		if (l != null) {
			for (final O object : l) {
				dto.add(map(object, c, mode));
			}
		}

		return dto;
	}

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
	public <O, T> Set<T> mapCollectionToSet(Collection<O> l, Class<T> c) {
		return mapCollectionToSet(l, c, null);
	}

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
	public <O, T> Set<T> mapCollectionToSet(Collection<O> l, Class<T> c, Set<T> setImpl) {

		if (setImpl == null) {
			setImpl = new HashSet<T>();
		}

		if (l != null) {
			for (final O object : l) {
				setImpl.add(map(object, c));
			}
		}

		return setImpl;
	}

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
	public <T> T populate(Object src, T populatedDest) {
		if (populatedDest == null) {
			return null;
		}

		if (src != null) {
			mapper.map(src, populatedDest);
		}

		return populatedDest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T map(Object source, Class<T> destinationClass, IsMappingMode mapId) {

		if (source == null) {
			return null;
		}

		final T t;
		if (mapId != null) {

			t = mapper.map(source, destinationClass, mapId.getMapId());
			if (t instanceof HasMappingMode) {
				((HasMappingMode) t).setCurrentMappingMode(mapId);
			}

		} else {
			t = map(source, destinationClass);
		}

		return t;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T map(Object source, T destination, IsMappingMode mapId) {
		
		if (source == null) {
			return null;
		}

		if (mapId != null) {

			mapper.map(source, destination, mapId.getMapId());
			if (destination instanceof HasMappingMode) {
				((HasMappingMode) destination).setCurrentMappingMode(mapId);
			}

		} else {
			map(source, destination);
		}

		return destination;
		
	}

}
