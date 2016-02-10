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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dozer.DozerConverter;
import org.dozer.Mapper;
import org.dozer.MapperAware;
import org.sigmah.server.domain.profile.PrivacyGroupPermission;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 * <p>
 * Custom dozer converter mapping a {@code List} of {@link PrivacyGroupPermission} to a {@code Map} of [
 * {@link PrivacyGroupDTO} ; {@link PrivacyGroupPermissionEnum} ] entries.
 * </p>
 * <p>
 * See "{@code dozer-schema-mapping.xml}" configuration file.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class PrivacyGroupsConverter extends DozerConverter<List<PrivacyGroupPermission>, Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum>> implements
																																																																					MapperAware {

	/**
	 * Injected dozer mapper.
	 */
	private Mapper mapper;

	/**
	 * Custom converter initialization.
	 */
	@SuppressWarnings("unchecked")
	public PrivacyGroupsConverter() {
		super((Class<List<PrivacyGroupPermission>>) (Class<?>) List.class, (Class<Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum>>) (Class<?>) Map.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> convertTo(final List<PrivacyGroupPermission> source,
			Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> destination) {

		if (source == null) {
			return null;
		}

		destination = new HashMap<>();

		for (final PrivacyGroupPermission p : source) {

			final PrivacyGroupDTO groupDTO = new PrivacyGroupDTO();
			mapper.map(p.getPrivacyGroup(), groupDTO);

			destination.put(groupDTO, p.getPermission());
		}

		return destination;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PrivacyGroupPermission> convertFrom(final Map<PrivacyGroupDTO, PrivacyGroupPermissionEnum> source, List<PrivacyGroupPermission> destination) {
		// One-way mapping.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMapper(final Mapper mapper) {
		this.mapper = mapper;
	}

}
