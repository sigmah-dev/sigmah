package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.UserUnitDTO;

public class UserUnitsResult implements Result {
	private Integer userId;
	private UserUnitDTO mainUserUnit;
	private List<UserUnitDTO> secondaryUserUnits;

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public UserUnitDTO getMainUserUnit() {
		return mainUserUnit;
	}

	public void setMainUserUnit(UserUnitDTO mainUserUnit) {
		this.mainUserUnit = mainUserUnit;
	}

	public List<UserUnitDTO> getSecondaryUserUnits() {
		return secondaryUserUnits;
	}

	public void setSecondaryUserUnits(List<UserUnitDTO> secondaryUserUnits) {
		this.secondaryUserUnits = secondaryUserUnits;
	}
}
