package org.sigmah.server.mapper;

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
