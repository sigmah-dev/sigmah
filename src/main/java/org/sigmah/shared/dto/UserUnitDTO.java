package org.sigmah.shared.dto;

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class UserUnitDTO extends AbstractModelDataEntityDTO<Integer> {
	private static final long serialVersionUID = -2014804121906463785L;

	public static final String ENTITY_NAME = "UserUnit";

	public static final String ID = "id";
	public static final String ORG_UNIT = "orgUnit";
	public static final String PROFILES = "profiles";
	public static final String MAIN_USER_UNIT = "mainUserUnit";

	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public Integer getId() {
		return get(ID);
	}

	public void setId(Integer id) {
		set(ID, id);
	}

	public OrgUnitDTO getOrgUnit() {
		return get(ORG_UNIT);
	}

	public void setOrgUnit(OrgUnitDTO orgUnitDTO) {
		set(ORG_UNIT, orgUnitDTO);
	}

	public List<ProfileDTO> getProfiles() {
		return get(PROFILES);
	}

	public void setProfiles(List<ProfileDTO> profiles) {
		set(PROFILES, profiles);
	}

	public boolean getMainUserUnit() {
		return get(MAIN_USER_UNIT);
	}

	public void setMainUserUnit(boolean mainUserUnit) {
		set(MAIN_USER_UNIT, mainUserUnit);
	}

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append(ORG_UNIT, getOrgUnit());

		StringBuilder stringBuilder = new StringBuilder();
		for (ProfileDTO profileDTO : getProfiles()) {
			stringBuilder.append(profileDTO);
		}

		builder.append(PROFILES, stringBuilder.toString());
		builder.append(MAIN_USER_UNIT, getMainUserUnit());
	}
}
