package org.sigmah.server.service.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.Attribute;
import org.sigmah.server.domain.AttributeGroup;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.domain.Site;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.category.CategoryElement;
import org.sigmah.server.domain.category.CategoryType;
import org.sigmah.server.domain.importation.ImportationScheme;
import org.sigmah.server.domain.importation.ImportationSchemeModel;
import org.sigmah.server.service.ActivityService;
import org.sigmah.server.service.AmendmentService;
import org.sigmah.server.service.AttributeGroupService;
import org.sigmah.server.service.AttributeService;
import org.sigmah.server.service.CategoryElementService;
import org.sigmah.server.service.CategoryTypeService;
import org.sigmah.server.service.ImportationSchemeModelService;
import org.sigmah.server.service.ImportationSchemeService;
import org.sigmah.server.service.IndicatorService;
import org.sigmah.server.service.LayoutGroupService;
import org.sigmah.server.service.MonitoredPointService;
import org.sigmah.server.service.OrgUnitModelService;
import org.sigmah.server.service.PersonalEventService;
import org.sigmah.server.service.PrivacyGroupService;
import org.sigmah.server.service.ProfileService;
import org.sigmah.server.service.ProjectFundingService;
import org.sigmah.server.service.ProjectModelService;
import org.sigmah.server.service.ProjectReportDraftService;
import org.sigmah.server.service.ProjectReportModelService;
import org.sigmah.server.service.ProjectReportService;
import org.sigmah.server.service.ProjectService;
import org.sigmah.server.service.ReminderService;
import org.sigmah.server.service.SitePolicy;
import org.sigmah.server.service.UserDatabaseService;
import org.sigmah.server.service.UserService;
import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
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
		addService(ImportationSchemeModel.class, ImportationSchemeModelService.class);
		addService(ImportationScheme.class, ImportationSchemeService.class);
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
