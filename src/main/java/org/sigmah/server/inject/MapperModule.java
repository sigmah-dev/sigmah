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

import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.Entity;
import org.sigmah.server.domain.profile.Profile;
import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.reminder.Reminder;
import org.sigmah.server.mapper.DozerMapper;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.mapper.MapperProvider;
import org.sigmah.server.mapper.MappingModeDefinition;
import org.sigmah.server.mapper.MappingModeDefinitions;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.DTO;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

/**
 * Module to install the DTO mapper.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MapperModule extends AbstractModule {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(MapperModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure() {

		if (LOG.isInfoEnabled()) {
			LOG.info("Installing mapper module.");
		}

		// Binds the mapper.
		bind(org.dozer.Mapper.class).toProvider(MapperProvider.class).in(Singleton.class);
		bind(Mapper.class).to(DozerMapper.class).in(Singleton.class);

		// Mapping mdoes definitions.
		final MappingModeDefinitions modes = new MappingModeDefinitions();
		bind(MappingModeDefinitions.class).toInstance(modes);

		// Mapping modes (Entity, DTO, IsMappingMode...).
		bindMappingMode(modes, Organization.class, OrganizationDTO.class, OrganizationDTO.Mode.values());
		bindMappingMode(modes, OrgUnit.class, OrgUnitDTO.class, OrgUnitDTO.Mode.values());
		bindMappingMode(modes, OrgUnitModel.class, OrgUnitModelDTO.class, OrgUnitModelDTO.Mode.values());
		bindMappingMode(modes, Profile.class, ProfileDTO.class, ProfileDTO.Mode.values());
		bindMappingMode(modes, Project.class, ProjectDTO.class, ProjectDTO.Mode.values());
		bindMappingMode(modes, ProjectModel.class, ProjectModelDTO.class, ProjectModelDTO.Mode.values());
		bindMappingMode(modes, User.class, UserDTO.class, UserDTO.Mode.values());
		bindMappingMode(modes, Reminder.class, ReminderDTO.class, ReminderDTO.Mode.values());
		bindMappingMode(modes, MonitoredPoint.class, MonitoredPointDTO.class, MonitoredPointDTO.Mode.values());

	}

	private <E extends Entity, D extends DTO> void bindMappingMode(final MappingModeDefinitions defs, final Class<E> entityClass, final Class<D> dtoClass,
			IsMappingMode... modes) {
		defs.add(new MappingModeDefinition<>(entityClass, dtoClass, modes));
	}

}
