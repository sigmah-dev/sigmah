package org.sigmah.server.service.base;

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


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.Attribute;
import org.sigmah.server.domain.AttributeGroup;
import org.sigmah.server.domain.ContactModel;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.domain.Site;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.service.*;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeModelDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

/**
 * Entity services utility class.<br>
 * Registers entity services with their corresponding entity id.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class EntityServices {

	/**
	 * Entities map with their corresponding service class.
	 */
	private static final Map<String, Class<? extends EntityService<?, ? extends Serializable, ?>>> services;
	static {
		services = new HashMap<>();
	}

	static {
		addService(Activity.class, ActivityService.class);
		addService(AmendmentDTO.ENTITY_NAME, AmendmentService.class);
		addService(AttributeGroup.class, AttributeGroupService.class);
		addService(Attribute.class, AttributeService.class);
		addService(CategoryElement.class, CategoryElementService.class);
		addService(CategoryType.class, CategoryTypeService.class);
		addService(ContactDTO.ENTITY_NAME, ContactService.class);
		addService(ContactModelDTO.ENTITY_NAME, ContactModelService.class);
		addService(ImportationSchemeModelDTO.ENTITY_NAME, ImportationSchemeModelService.class);
		addService(Indicator.class, IndicatorService.class);
		addService(LayoutGroupDTO.ENTITY_NAME, LayoutGroupService.class);
		addService(MonitoredPointDTO.ENTITY_NAME, MonitoredPointService.class);
		addService(OrgUnitModelDTO.ENTITY_NAME, OrgUnitModelService.class);
		addService(PersonalEventDTO.ENTITY_NAME, PersonalEventService.class);
		addService(PrivacyGroupDTO.ENTITY_NAME, PrivacyGroupService.class);
		addService(ProfileDTO.ENTITY_NAME, ProfileService.class);
		addService(Project.class, ProjectService.class);
		addService(ProjectFunding.class, ProjectFundingService.class);
		addService(ProjectModelDTO.ENTITY_NAME, ProjectModelService.class);
		addService(ProjectReportDTO.ENTITY_NAME, ProjectReportService.class);
		addService(ReportModelDTO.ENTITY_NAME, ProjectReportModelService.class);
		addService(ProjectReportDTO.ENTITY_NAME_DRAFT, ProjectReportDraftService.class);
		addService(ReminderDTO.ENTITY_NAME, ReminderService.class);
		addService(Site.class, SitePolicy.class);
		addService(UserDTO.ENTITY_NAME, UserService.class);
		addService(UserDatabase.class, UserDatabaseService.class);
		addService(ImportationSchemeDTO.ENTITY_NAME, ImportationSchemeService.class);

	}

	/**
	 * Gets the given {@code entityClass} corresponding {@link EntityService} class.
	 * 
	 * @param serviceId
	 *          The service id (most-likely the entity class name).
	 * @return The given {@code entityClass} corresponding {@link EntityService} class, or {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public static Class<? extends EntityService<?, Serializable, ?>> getServiceClass(final String serviceId) {

		return (Class<? extends EntityService<?, Serializable, ?>>) services.get(serviceId);

	}

	/**
	 * Registers the given {@code entityClass} with its corresponding {@code serviceClass}.<br>
	 * Does nothing if one of the arguments is {@code null}.
	 * 
	 * @param entityClass
	 *          The entity class.
	 * @param serviceClass
	 *          The {@link EntityService} class.
	 */
	private static <E extends EntityId<K>, K extends Serializable, D extends EntityDTO<K>> void addService(final Class<E> entityClass,
			final Class<? extends EntityService<E, K, D>> serviceClass) {

		addService(entityClass != null ? entityClass.getSimpleName() : null, serviceClass);

	}

	/**
	 * Registers the given {@code serviceId} with its corresponding {@code serviceClass}.<br>
	 * Does nothing if one of the arguments is {@code null}.
	 * 
	 * @param serviceId
	 *          The service id (most-likely the entity class name).
	 * @param serviceClass
	 *          The {@link EntityService} class.
	 */
	private static <E extends EntityId<K>, K extends Serializable, D extends EntityDTO<K>> void addService(final String serviceId,
			final Class<? extends EntityService<E, K, D>> serviceClass) {

		if (serviceId == null || serviceClass == null) {
			return;
		}

		services.put(serviceId, serviceClass);

	}

	private EntityServices() {
		// Utility class.
	}

}
