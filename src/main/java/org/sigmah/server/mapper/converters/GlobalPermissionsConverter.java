package org.sigmah.server.mapper.converters;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dozer.DozerConverter;
import org.sigmah.server.domain.profile.GlobalPermission;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * <p>
 * Custom dozer converter mapping a {@code List} of {@link GlobalPermission} to a {@code Set} of
 * {@link GlobalPermissionEnum}.
 * </p>
 * <p>
 * See "{@code dozer-schema-mapping.xml}" configuration file.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GlobalPermissionsConverter extends DozerConverter<List<GlobalPermission>, Set<GlobalPermissionEnum>> {

	/**
	 * Custom converter initialization.
	 */
	@SuppressWarnings("unchecked")
	public GlobalPermissionsConverter() {
		super((Class<List<GlobalPermission>>) (Class<?>) List.class, (Class<Set<GlobalPermissionEnum>>) (Class<?>) Set.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<GlobalPermissionEnum> convertTo(final List<GlobalPermission> source, Set<GlobalPermissionEnum> destination) {

		if (source == null) {
			return null;
		}

		destination = new HashSet<>();

		for (final GlobalPermission permission : source) {
			if (permission == null) {
				continue;
			}
			destination.add(permission.getPermission());
		}

		return destination;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<GlobalPermission> convertFrom(final Set<GlobalPermissionEnum> source, List<GlobalPermission> destination) {
		// One-way mapping.
		return null;
	}

}
