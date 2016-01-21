package org.sigmah.shared.dto.orgunit;

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
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.AbstractTreeModelEntityDTO;
import org.sigmah.shared.dto.base.mapping.CustomMappingField;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.base.mapping.MappingField;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * OrgUnit DTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class OrgUnitDTO extends AbstractTreeModelEntityDTO<Integer> implements DefaultFlexibleElementContainer {

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "OrgUnit";

	// Map keys.
	public static final String NAME = "name";
	public static final String FULL_NAME = "fullName";
	public static final String CALENDAR_ID = "calendarId";
	public static final String DELETED = "deleted";
	public static final String COMPLETE_NAME = "completeName";

	public static final String PLANNED_BUDGET = "plannedBudget";
	public static final String SPEND_BUDGET = "spendBudget";
	public static final String RECEIVED_BUDGET = "receivedBudget";
	public static final String CAN_CONTAIN_PROJECTS = "canContainProjects";

	public static final String OFFICE_LOCATION_COUNTRY = "officeLocationCountry";
	public static final String MODEL = "oum";
	public static final String PARENT_ORG_UNIT = "parentOrgUnit";
	public static final String CHILDREN_ORG_UNITS = "childrenOrgUnits";

	/**
	 * Mapping configurations.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	public static enum Mode implements IsMappingMode {

		/**
		 * Basic mapping without model, parent and children.
		 */
		BASE(new MappingField("orgUnitModel", MODEL), new MappingField(PARENT_ORG_UNIT), new MappingField(CHILDREN_ORG_UNITS)),

		/**
		 * Maps the unit tree (children and parent), but <b>not</b> the model.
		 */
		WITH_TREE(new MappingField("orgUnitModel", MODEL)),

		;

		private final CustomMappingField[] customFields;
		private final MappingField[] excludedFields;

		private Mode(final MappingField... excludedFields) {
			this(null, excludedFields);
		}

		private Mode(final CustomMappingField... customFields) {
			this(customFields, (MappingField[]) null);
		}

		private Mode(final CustomMappingField[] customFields, final MappingField... excludedFields) {
			this.customFields = customFields;
			this.excludedFields = excludedFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getMapId() {
			return name();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public CustomMappingField[] getCustomFields() {
			return customFields;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public MappingField[] getExcludedFields() {
			return excludedFields;
		}
	}

	/**
	 * Localizes an flexible element in the orgUnit model.
	 * 
	 * @author kma
	 */
	public static final class LocalizedElement extends OrgUnitModelDTO.LocalizedElement {

		public LocalizedElement(OrgUnitModelDTO.LocalizedElement localized) {
			super(localized.getElement());
		}
	}

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8747081157741379941L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	// Name
	@Override
	public String getName() {
		return get(NAME);
	}

	public void setName(String name) {
		set(NAME, name);
		generateCompleteName();
	}

	// Full name
	@Override
	public String getFullName() {
		return get(FULL_NAME);
	}

	public void setFullName(String fullName) {
		set(FULL_NAME, fullName);
		generateCompleteName();
	}

	// Planned budget
	@Override
	public Double getPlannedBudget() {
		final Double b = (Double) get(PLANNED_BUDGET);
		return b != null ? b : 0.0;
	}

	public void setPlannedBudget(Double plannedBudget) {
		set(PLANNED_BUDGET, plannedBudget);
	}

	// Spent budget
	@Override
	public Double getSpendBudget() {
		final Double b = (Double) get(SPEND_BUDGET);
		return b != null ? b : 0.0;
	}

	public void setSpendBudget(Double spendBudget) {
		set(SPEND_BUDGET, spendBudget);
	}

	// Received budget
	@Override
	public Double getReceivedBudget() {
		final Double b = (Double) get(RECEIVED_BUDGET);
		return b != null ? b : 0.0;
	}

	public void setReceivedBudget(Double receivedBudget) {
		set(RECEIVED_BUDGET, receivedBudget);
	}

	// Calendar id
	public Integer getCalendarId() {
		return (Integer) get(CALENDAR_ID);
	}

	public void setCalendarId(Integer calendarId) {
		set(CALENDAR_ID, calendarId);
	}

	// Country.
	public CountryDTO getOfficeLocationCountry() {
		return get(OFFICE_LOCATION_COUNTRY);
	}

	public void setOfficeLocationCountry(CountryDTO officeLocationCountry) {
		set(OFFICE_LOCATION_COUNTRY, officeLocationCountry);
	}

	// Deleted.
	public void setDeleted(Date deleted) {
		set(DELETED, deleted);
	}

	public Date getDeleted() {
		return get(DELETED);
	}

	// Parent
	public OrgUnitDTO getParentOrgUnit() {
		return get(PARENT_ORG_UNIT);
	}

	public void setParentOrgUnit(OrgUnitDTO parent) {
		set(PARENT_ORG_UNIT, parent);
	}

	// Children
	public Set<OrgUnitDTO> getChildrenOrgUnits() {
		return get(CHILDREN_ORG_UNITS);
	}

	public void setChildrenOrgUnits(Set<OrgUnitDTO> children) {

		if (children != null) {
			for (final Iterator<OrgUnitDTO> it = children.iterator(); it.hasNext();) {
				final OrgUnitDTO child = it.next();
				if (child != null && child.getDeleted() != null) {
					it.remove();
				}
			}
		}

		set(CHILDREN_ORG_UNITS, children);
		setChildren(children != null ? new ArrayList<ModelData>(children) : null);
	}

	// Can contain projects ?
	public boolean isCanContainProjects() {
		return ClientUtils.isTrue(get(CAN_CONTAIN_PROJECTS));
	}

	public void setCanContainProjects(boolean canContainProjects) {
		set(CAN_CONTAIN_PROJECTS, canContainProjects);
	}

	// Complete name
	private void generateCompleteName() {
		setCompleteName(getName() + " - " + getFullName());
	}

	public String getCompleteName() {
		return get(COMPLETE_NAME);
	}

	public void setCompleteName(String completeName) {
		set(COMPLETE_NAME, completeName);
	}

	// Model
	public OrgUnitModelDTO getOrgUnitModel() {
		return get(MODEL);
	}

	public void setOrgUnitModel(OrgUnitModelDTO oum) {
		set(MODEL, oum);
	}

	@Override
	public Date getStartDate() {
		return null;
	}

	@Override
	public Date getEndDate() {
		return null;
	}

	@Override
	public CountryDTO getCountry() {
		return getOfficeLocationCountry();
	}

	@Override
	public String getOwnerFirstName() {
		return null;
	}

	@Override
	public String getOwnerName() {
		return null;
	}

	@Override
	public UserDTO getManager() {
		return null;
	}

	@Override
	public Integer getOrgUnitId() {
		final OrgUnitDTO parent = getParentOrgUnit();
		return parent != null ? parent.getId() : getId();
	}

	/**
	 * Gets all the flexible elements instances of the given class in this organizational unit (details page). The banner
	 * is ignored cause the elements in it are read-only.
	 * 
	 * @param clazz
	 *          The class of the searched flexible elements.
	 * @return The elements localized for the given class, or <code>null</code> if there is no element of this class.
	 */
	public List<LocalizedElement> getLocalizedElements(Class<? extends FlexibleElementDTO> clazz) {

		final ArrayList<LocalizedElement> elements = new ArrayList<LocalizedElement>();

		final List<OrgUnitModelDTO.LocalizedElement> localizedElements = getOrgUnitModel().getLocalizedElements(clazz);

		if (localizedElements != null) {
			for (final OrgUnitModelDTO.LocalizedElement localized : localizedElements) {
				elements.add(new LocalizedElement(localized));
			}
		}

		return elements;
	}

}
