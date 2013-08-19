package org.sigmah.shared.dto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.DefaultFlexibleElementContainer;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class OrgUnitDTO extends BaseModelData implements EntityDTO, DefaultFlexibleElementContainer {

	private static final long serialVersionUID = -8812034670573721384L;

	/**
	 * Localizes an flexible element in the orgUnit model.
	 * 
	 * @author kma
	 * 
	 */
	public static final class LocalizedElement extends OrgUnitModelDTO.LocalizedElement {
		private LocalizedElement(OrgUnitModelDTO.LocalizedElement localized) {
			super(localized.getElement());
		}
	}

	@Override
	public String getEntityName() {
		return "OrgUnit";
	}

	// Id
	@Override
	public int getId() {
		return (Integer) get("id");
	}

	public void setId(int id) {
		set("id", id);
	}

	// Name
	@Override
	public String getName() {
		return get("name");
	}

	public void setName(String name) {
		set("name", name);
	}

	// Full name
	@Override
	public String getFullName() {
		return get("fullName");
	}

	public void setFullName(String fullName) {
		set("fullName", fullName);
	}

	// Model
	public OrgUnitModelDTO getOrgUnitModel() {
		return get("oum");
	}

	public void setOrgUnitModel(OrgUnitModelDTO oum) {
		set("oum", oum);
	}

	// Planned budget
	@Override
	public Double getPlannedBudget() {
		final Double b = (Double) get("plannedBudget");
		return b != null ? b : 0.0;
	}

	public void setPlannedBudget(Double plannedBudget) {
		set("plannedBudget", plannedBudget);
	}

	// Spent budget
	@Override
	public Double getSpendBudget() {
		final Double b = (Double) get("spendBudget");
		return b != null ? b : 0.0;
	}

	public void setSpendBudget(Double spendBudget) {
		set("spendBudget", spendBudget);
	}

	// Received budget
	@Override
	public Double getReceivedBudget() {
		final Double b = (Double) get("receivedBudget");
		return b != null ? b : 0.0;
	}

	public void setReceivedBudget(Double receivedBudget) {
		set("receivedBudget", receivedBudget);
	}

	// Organization
	public OrganizationDTO getOrganization() {
		return get("organization");
	}

	public void setOrganization(OrganizationDTO organization) {
		set("organization", organization);
	}

	// Parent
	public OrgUnitDTO getParent() {
		return get("parent");
	}

	public void setParent(OrgUnitDTO parent) {
		set("parent", parent);
	}

	// Children
	public Set<OrgUnitDTO> getChildren() {
		return get("children");
	}

	public void setChildren(Set<OrgUnitDTO> children) {

		if (children != null) {
			for (final Iterator<OrgUnitDTO> it = children.iterator(); it.hasNext();) {
				final OrgUnitDTO child = it.next();
				if (child != null && child.getDeleted() != null) {
					it.remove();
				}
			}
		}

		set("children", children);
	}

	// Calendar id
	public Integer getCalendarId() {
		return (Integer) get("calendarId");
	}

	public void setCalendarId(Integer calendarId) {
		set("calendarId", calendarId);
	}

	// Deleted.
	public void setDeleted(Date deleted) {
		set("deleted", deleted);
	}

	public Date getDeleted() {
		return get("deleted");
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

	public CountryDTO getOfficeLocationCountry() {
		return get("country");
	}

	public void setOfficeLocationCountry(CountryDTO officeLocationCountry) {
		set("country", officeLocationCountry);
	}

	@Override
	public UserDTO getManager() {
		return null;
	}

	@Override
	public int getOrgUnitId() {
		final OrgUnitDTO parent = getParent();
		return parent != null ? parent.getId() : getId();
	}

	/**
	 * Transforms this entity into a {@link OrgUnitDTOLight} entity.
	 * 
	 * @return The {@link OrgUnitDTOLight} entity.
	 */
	public OrgUnitDTOLight light() {
		return light(null);
	}

	private OrgUnitDTOLight light(OrgUnitDTOLight parent) {

		final OrgUnitDTOLight light = new OrgUnitDTOLight();
		light.setId(getId());
		light.setName(getName());
		light.setFullName(getFullName());
		light.setTitle(getOrgUnitModel().getTitle());
		light.setCanContainProjects(getOrgUnitModel().getCanContainProjects());
		light.generateCompleteName();
		light.setParentDTO(parent);
		light.setOfficeLocationCountry(getOfficeLocationCountry());
		final HashSet<OrgUnitDTOLight> children = new HashSet<OrgUnitDTOLight>();
		for (final OrgUnitDTO c : getChildren()) {
			children.add(c.light(light));
		}
		light.setChildrenDTO(children);
		light.setDeleted(getDeleted());

		// Set the orgunit model
		light.setOrgUnitModel(getOrgUnitModel());

		return light;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj == null) {
			return false;
		}

		if (!(obj instanceof OrgUnitDTO)) {
			return false;
		}

		final OrgUnitDTO other = (OrgUnitDTO) obj;
		return getId() == other.getId();
	}

	/**
	 * Gets all the flexible elements instances of the given class in this
	 * organizational unit (details page). The banner is ignored cause the
	 * elements in it are read-only.
	 * 
	 * @param clazz
	 *            The class of the searched flexible elements.
	 * @return The elements localized for the given class, or <code>null</code>
	 *         if there is no element of this class.
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

	public BudgetElementDTO getBudgetElement() {
		if (getOrgUnitModel().getAllElements() != null) {
			for (FlexibleElementDTO fleElmt : getOrgUnitModel().getAllElements()) {
				if (fleElmt instanceof BudgetElementDTO) {
					return (BudgetElementDTO) fleElmt;
				}
			}
		}
		return null;
	}
}
