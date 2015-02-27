package org.sigmah.server.service;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.util.AdminUtil;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnitBanner;
import org.sigmah.server.domain.OrgUnitDetails;
import org.sigmah.server.domain.OrgUnitModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.BudgetElement;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.element.DefaultFlexibleElement;
import org.sigmah.server.domain.layout.Layout;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.mapper.Mapper;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.ModelUtil;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;

/**
 * Handler for updating Org unit model command.
 * 
 * @author nrebiai
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class OrgUnitModelService extends AbstractEntityService<OrgUnitModel, Integer, OrgUnitModelDTO> {

	@Inject
	private Mapper mapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitModel create(final PropertyMap properties, final UserExecutionContext context) {

		final OrgUnitModelDTO orgUnitModel = (OrgUnitModelDTO) properties.get(AdminUtil.ADMIN_ORG_UNIT_MODEL);

		// Only draft models can be changed
		if (orgUnitModel == null) {
			return null;
		}

		if (orgUnitModel.getId() != null) {
			// properties can only contain actual changes between old version and new one as verification has already been
			// done.
			return update(orgUnitModel.getId(), properties, context);
		}

		// Create new draft OrgUnitModel
		OrgUnitModel oM = createOrgUnitModel(null, properties, context.getUser());

		OrgUnitDetails oMDetails = new OrgUnitDetails();

		Layout oMDetailsLayout = new Layout();
		oMDetailsLayout.setColumnsCount(1);
		oMDetailsLayout.setRowsCount(1);
		oMDetails.setLayout(oMDetailsLayout);
		oMDetails.setOrgUnitModel(oM);

		LayoutGroup detailsGroup = new LayoutGroup();
		detailsGroup.setTitle("Default informations group");
		detailsGroup.setColumn(0);
		detailsGroup.setRow(0);
		detailsGroup.setParentLayout(oMDetailsLayout);

		// Default flexible elements all in default details group
		int order = 0;
		for (DefaultFlexibleElementType e : DefaultFlexibleElementType.values()) {
			if (!DefaultFlexibleElementType.OWNER.equals(e)
				&& !DefaultFlexibleElementType.START_DATE.equals(e)
				&& !DefaultFlexibleElementType.END_DATE.equals(e)
				&& !(DefaultFlexibleElementType.BUDGET.equals(e))) {
				DefaultFlexibleElement defaultElement = new DefaultFlexibleElement();
				defaultElement.setType(e);
				defaultElement.setValidates(false);
				defaultElement.setAmendable(false);
				em().persist(defaultElement);
				LayoutConstraint defaultLayoutConstraint = new LayoutConstraint();
				defaultLayoutConstraint.setParentLayoutGroup(detailsGroup);
				defaultLayoutConstraint.setElement(defaultElement);
				defaultLayoutConstraint.setSortOrder(order++);
				detailsGroup.addConstraint(defaultLayoutConstraint);
			}
		}
		if (oM.getHasBudget()) {

			DefaultFlexibleElement defaultElement = new BudgetElement();

			List<BudgetSubField> budgetSubFields = new ArrayList<BudgetSubField>();
			// Adds the 3 default budget sub fields
			int y = 1;
			for (BudgetSubFieldType type : BudgetSubFieldType.values()) {
				BudgetSubField b = new BudgetSubField();
				b.setBudgetElement(((BudgetElement) defaultElement));
				b.setType(type);
				b.setFieldOrder(y);
				if (BudgetSubFieldType.PLANNED.equals(type)) {
					((BudgetElement) defaultElement).setRatioDivisor(b);
				} else if (BudgetSubFieldType.SPENT.equals(type)) {
					((BudgetElement) defaultElement).setRatioDividend(b);
				}
				budgetSubFields.add(b);
				y++;
			}
			((BudgetElement) defaultElement).setBudgetSubFields(budgetSubFields);

			defaultElement.setType(DefaultFlexibleElementType.BUDGET);
			defaultElement.setValidates(false);
			defaultElement.setAmendable(false);
			em().persist(defaultElement);
			LayoutConstraint defaultLayoutConstraint = new LayoutConstraint();
			defaultLayoutConstraint.setParentLayoutGroup(detailsGroup);
			defaultLayoutConstraint.setElement(defaultElement);
			defaultLayoutConstraint.setSortOrder(order++);
			detailsGroup.addConstraint(defaultLayoutConstraint);

		}

		List<LayoutGroup> detailsGroups = new ArrayList<LayoutGroup>();
		detailsGroups.add(detailsGroup);
		oMDetailsLayout.setGroups(detailsGroups);

		// Banner and groups for banner
		OrgUnitBanner oMBanner = new OrgUnitBanner();
		Layout oMBannerLayout = new Layout();
		oMBannerLayout.setColumnsCount(3);
		oMBannerLayout.setRowsCount(2);
		oMBanner.setLayout(oMBannerLayout);
		oMBanner.setOrgUnitModel(oM);

		List<LayoutGroup> bannerGroups = new ArrayList<LayoutGroup>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 2; j++) {
				LayoutGroup bannerGroup = new LayoutGroup();
				bannerGroup.setColumn(i);
				bannerGroup.setRow(j);
				bannerGroup.setParentLayout(oMBannerLayout);
				bannerGroups.add(bannerGroup);
			}
		}

		oMBannerLayout.setGroups(bannerGroups);

		oM.setDetails(oMDetails);
		oM.setBanner(oMBanner);

		em().persist(oM);
		em().flush();
		return oM;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitModel update(Integer entityId, PropertyMap changes, final UserExecutionContext context) {

		OrgUnitModel model = em().find(OrgUnitModel.class, entityId);

		if (model == null) {
			throw new IllegalArgumentException("No OrgUnitModel found for id #" + entityId);
		}
		
		if (changes.get(AdminUtil.PROP_OM_NAME) != null) {
			// Update model.
			model = createOrgUnitModel(model, changes, context.getUser());
			return em().merge(model);
		}

		if (changes.get(AdminUtil.PROP_FX_FLEXIBLE_ELEMENT) != null) {
			ModelUtil.persistFlexibleElement(em(), mapper, changes, model);
			return em().find(OrgUnitModel.class, model.getId());
		}
		
		em().flush();
		return null;
	}

	private static OrgUnitModel createOrgUnitModel(OrgUnitModel oM, PropertyMap properties, User user) {

		if (oM == null) {
			oM = new OrgUnitModel();
			oM.setStatus(ProjectModelStatus.DRAFT);
		}

		String oMName = null;
		if (properties.get(AdminUtil.PROP_OM_NAME) != null) {
			oMName = (String) properties.get(AdminUtil.PROP_OM_NAME);
		}
		String oMTitle = null;
		if (properties.get(AdminUtil.PROP_OM_TITLE) != null) {
			oMTitle = (String) properties.get(AdminUtil.PROP_OM_TITLE);
		}
		Boolean hasBudget = null;
		if (properties.get(AdminUtil.PROP_OM_HAS_BUDGET) != null) {
			hasBudget = (Boolean) properties.get(AdminUtil.PROP_OM_HAS_BUDGET);
		}
		Boolean containsProjects = null;
		if (properties.get(AdminUtil.PROP_OM_CONTAINS_PROJECTS) != null) {
			containsProjects = (Boolean) properties.get(AdminUtil.PROP_OM_CONTAINS_PROJECTS);
		}
		
		if (properties.containsKey(AdminUtil.PROP_OM_MAINTENANCE_DATE)) {
			final Object maintenanceDate = properties.get(AdminUtil.PROP_OM_MAINTENANCE_DATE);
			if(maintenanceDate instanceof Date) {
				oM.setDateMaintenance((Date)maintenanceDate);
			} else {
				oM.setDateMaintenance(null);
			}
		}

		oM.setName(oMName);
		oM.setTitle(oMTitle);
		oM.setHasBudget(hasBudget);
		oM.setOrganization(user.getOrganization());
		oM.setCanContainProjects(containsProjects);
		// Status
		if (properties.get(AdminUtil.PROP_OM_STATUS) != null) {
			oM.setStatus((ProjectModelStatus) properties.get(AdminUtil.PROP_OM_STATUS));
		}

		return oM;
	}

}
