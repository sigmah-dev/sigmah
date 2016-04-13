package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.sigmah.shared.command.result.UserUnitsResult;
import org.sigmah.shared.dto.UserUnitDTO;

public final class UserUnitsResultJS extends JavaScriptObject {

	protected UserUnitsResultJS() {
	}

	public static UserUnitsResultJS toJavaScript(UserUnitsResult userUnitsResult) {
		final UserUnitsResultJS userUnitsResultJS = Values.createJavaScriptObject(UserUnitsResultJS.class);

		userUnitsResultJS.setUserId(userUnitsResult.getUserId());
		userUnitsResultJS.setMainUserUnit(userUnitsResult.getMainUserUnit());
		userUnitsResultJS.setSecondaryUserUnits(userUnitsResult.getSecondaryUserUnits());

		return userUnitsResultJS;
	}

	public UserUnitsResult toDTO() {
		final UserUnitsResult userUnitsResult = new UserUnitsResult();

		userUnitsResult.setUserId(getUserId());
		userUnitsResult.setMainUserUnit(getMainUserUnitDTO());
		userUnitsResult.setSecondaryUserUnits(getSecondaryUserUnitsDTO());

		return userUnitsResult;
	}

	public native int getUserId() /*-{
		return this.id;
	}-*/;

	public native void setUserId(int userId) /*-{
		this.id = userId;
	}-*/;

	public native UserUnitJS getMainUserUnit() /*-{
		return this.mainUserUnit;
	}-*/;

	public UserUnitDTO getMainUserUnitDTO() {
		return getMainUserUnit().toDTO();
	}

	public native void setMainUserUnit(UserUnitJS mainUserUnit) /*-{
		this.mainUserUnit = mainUserUnit;
	}-*/;

	public void setMainUserUnit(UserUnitDTO mainUserUnit) {
		setMainUserUnit(UserUnitJS.toJavaScript(mainUserUnit));
	}

	public native JsArray<UserUnitJS> getSecondaryUserUnits() /*-{
		return this.secondaryUserUnits;
	}-*/;

	public List<UserUnitDTO> getSecondaryUserUnitsDTO() {
		JsArray<UserUnitJS> secondaryUserUnits = getSecondaryUserUnits();
		if (secondaryUserUnits == null) {
			return Collections.emptyList();
		}

		List<UserUnitDTO> userUnits = new ArrayList<UserUnitDTO>();
		for (int i = 0; i < secondaryUserUnits.length(); i++) {
			userUnits.add(secondaryUserUnits.get(i).toDTO());
		}
		return userUnits;
	}

	public native void setSecondaryUserUnits(JsArray<UserUnitJS> secondaryUserUnits) /*-{
		this.secondaryUserUnits = secondaryUserUnits;
	}-*/;

	public void setSecondaryUserUnits(List<UserUnitDTO> userUnits) {
		final JsArray<UserUnitJS> userUnitsJS = Values.createTypedJavaScriptArray(UserUnitJS.class);
		for (UserUnitDTO userUnit : userUnits) {
			userUnitsJS.push(UserUnitJS.toJavaScript(userUnit));
		}
		setSecondaryUserUnits(userUnitsJS);
	}
}
