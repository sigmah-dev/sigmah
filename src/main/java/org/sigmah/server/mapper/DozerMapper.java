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
	 * {@inheritDoc}
	 */
	@Override
	public <O, T> Set<T> mapCollectionToSet(Collection<O> l, Class<T> c) {
		return mapCollectionToSet(l, c, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
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
	 * {@inheritDoc}
	 */
	@Override
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

}
