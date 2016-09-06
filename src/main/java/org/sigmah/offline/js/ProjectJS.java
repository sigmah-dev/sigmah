package org.sigmah.offline.js;

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

import com.allen_sauer.gwt.log.client.Log;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.reminder.MonitoredPointListDTO;
import org.sigmah.shared.dto.reminder.ReminderListDTO;
import org.sigmah.shared.dto.value.ValueDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.core.client.JsDate;
import java.util.HashSet;
import org.sigmah.shared.util.Collections;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectJS extends JavaScriptObject {

	protected ProjectJS() {
	}

	public static ProjectJS toJavaScript(ProjectDTO projectDTO) {
		final ProjectJS projectJS = Values.createJavaScriptObject(ProjectJS.class);

		projectJS.setId(projectDTO.getId());
		projectJS.setName(projectDTO.getName());
		projectJS.setFullName(projectDTO.getFullName());
		projectJS.setStartDate(Values.toJsDate(projectDTO.getStartDate()));
		projectJS.setEndDate(Values.toJsDate(projectDTO.getEndDate()));
		projectJS.setCloseDate(Values.toJsDate(projectDTO.getCloseDate()));
		projectJS.setActivityAdvancement(projectDTO.getActivityAdvancement());
		projectJS.setCalendarId(projectDTO.getCalendarId());
		projectJS.setAmendmentState(projectDTO.getAmendmentState());
		projectJS.setAmendmentVersion(projectDTO.getAmendmentVersion());
		projectJS.setAmendmentRevision(projectDTO.getAmendmentRevision());
		
		projectJS.setAmendments(projectDTO.getAmendments());
		projectJS.setOwner(projectDTO.getOwner());
		projectJS.setFavoriteUsers(projectDTO.getFavoriteUsers());
		projectJS.setProjectModel(projectDTO.getProjectModel());
		projectJS.setPhases(projectDTO.getPhases());
		projectJS.setValues(projectDTO.getValues());
		projectJS.setCurrentPhase(projectDTO.getCurrentPhase());
		projectJS.setLogFrame(projectDTO.getLogFrame());
		projectJS.setFunding(projectDTO.getFunding());
		projectJS.setFunded(projectDTO.getFunded());
		projectJS.setProjectFundings(projectDTO.getFunded(), projectDTO.getFunding());
		projectJS.setCountry(projectDTO.getCountry());
		projectJS.setManager(projectDTO.getManager());
		projectJS.setPointsList(projectDTO.getPointsList());
		projectJS.setRemindersList(projectDTO.getRemindersList());

		projectJS.setPlannedBudget(projectDTO.getPlannedBudget());
		projectJS.setSpendBudget(projectDTO.getSpendBudget());
		projectJS.setReceivedBudget(projectDTO.getReceivedBudget());
		projectJS.setCurrentAmendment(projectDTO.getCurrentAmendment());
		projectJS.setOrgUnit(projectDTO.getOrgUnitId());
		
		projectJS.setRatioDividendValue(projectDTO.getRatioDividendValue());
		projectJS.setRatioDividendLabel(projectDTO.getRatioDividendLabel());
		projectJS.setRatioDividendType(projectDTO.getRatioDividendType());
		projectJS.setRatioDivisorValue(projectDTO.getRatioDivisorValue());
		projectJS.setRatioDivisorLabel(projectDTO.getRatioDivisorLabel());
		projectJS.setRatioDivisorType(projectDTO.getRatioDivisorType());
		projectJS.setCategoryElements(projectDTO.getCategoryElements());
        
		return projectJS;
	}

	public ProjectDTO toDTO() {
		final ProjectDTO projectDTO = new ProjectDTO();

		projectDTO.setId(getId());
		projectDTO.setName(getName());
		projectDTO.setFullName(getFullName());
		projectDTO.setStartDate(Values.toDate(getStartDate()));
		projectDTO.setEndDate(Values.toDate(getEndDate()));
		projectDTO.setCloseDate(Values.toDate(getCloseDate()));
		projectDTO.setActivityAdvancement(getActivityAdvancement());
		projectDTO.setCalendarId(getCalendarId());
		projectDTO.setAmendmentState(getAmendmentState());
		projectDTO.setAmendmentVersion(getAmendmentVersion());
		projectDTO.setAmendmentRevision(getAmendmentRevision());
		projectDTO.setAmendments(getAmendmentDTOs());
		projectDTO.setOwner(getOwnerDTO());
		projectDTO.setFavoriteUsers(getFavoriteUserDTOs());
		projectDTO.setOrgUnitId(getOrgUnit());
		projectDTO.setPointsList(getPointsListDTO());
		projectDTO.setRemindersList(getRemindersListDTO());
		
		projectDTO.setPlannedBudget(getPlannedBudget());
		projectDTO.setSpendBudget(getSpendBudget());
		projectDTO.setReceivedBudget(getReceivedBudget());
		
		projectDTO.setRatioDividendValue(getRatioDividendValue());
		projectDTO.setRatioDividendLabel(getRatioDividendLabel());
		projectDTO.setRatioDividendType(getRatioDividendType());
		projectDTO.setRatioDivisorValue(getRatioDivisorValue());
		projectDTO.setRatioDivisorLabel(getRatioDivisorLabel());
		projectDTO.setRatioDivisorType(getRatioDivisorType());
		projectDTO.setCategoryElements(getCategoryElements());

		return projectDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native String getFullName() /*-{
		return this.fullName;
	}-*/;

	public native void setFullName(String fullName) /*-{
		this.fullName = fullName;
	}-*/;

	public native JsDate getStartDate() /*-{
		return this.startDate;
	}-*/;

	public native void setStartDate(JsDate startDate) /*-{
		this.startDate = startDate;
	}-*/;

	public native JsDate getEndDate() /*-{
		return this.endDate;
	}-*/;

	public native void setEndDate(JsDate endDate) /*-{
		this.endDate = endDate;
	}-*/;

	public native int getProjectModel() /*-{
		return this.projectModel;
	}-*/;

	public void setProjectModel(ProjectModelDTO projectModel) {
		if (projectModel != null) {
			setProjectModel(projectModel.getId());
		}
	}

	public native void setProjectModel(int projectModel) /*-{
		this.projectModel = projectModel;
	}-*/;

	public native UserJS getOwner() /*-{
		return this.owner;
	}-*/;

	public native void setOwner(UserJS owner) /*-{
		this.owner = owner;
	}-*/;

	public void setOwner(UserDTO owner) {
		if (owner != null) {
			setOwner(UserJS.toJavaScript(owner));
		}
	}

	public UserDTO getOwnerDTO() {
		if (getOwner() != null) {
			return getOwner().toDTO();
		}
		return null;
	}

	public native JsArrayInteger getPhases() /*-{
		return this.phases;
	}-*/;

	public void setPhases(List<PhaseDTO> phases) {
		if (phases != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();

			for (final PhaseDTO phase : phases) {
				array.push(phase.getId());
			}

			setPhases(array);
		}
	}

	public native void setPhases(JsArrayInteger phases) /*-{
		this.phases = phases;
	}-*/;

	public native JsArrayInteger getValues() /*-{
		return this.values;
	}-*/;

	public void setValues(List<ValueDTO> values) {
		if (values != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();

			for (final ValueDTO value : values) {
				array.push(value.getId());
			}

			setValues(array);
		}
	}

	public native void setValues(JsArrayInteger values) /*-{
		this.values = values;
	}-*/;

	public native int getCurrentPhase() /*-{
		return this.currentPhase;
	}-*/;

	public void setCurrentPhase(PhaseDTO currentPhase) {
		if (currentPhase != null) {
			setCurrentPhase(currentPhase.getId());
		}
	}

	public native void setCurrentPhase(int currentPhase) /*-{
		this.currentPhase = currentPhase;
	}-*/;

	public Integer getCalendarId() {
		return Values.getInteger(this, "calendarId");
	}

	public void setCalendarId(Integer calendarId) {
        Values.setInteger(this, "calendarId", calendarId);
	}

	public native int getLogFrame() /*-{
		return this.logFrame;
	}-*/;

	public void setLogFrame(LogFrameDTO logFrame) {
		if (logFrame != null) {
			setLogFrame(logFrame.getId());
		}
	}

	public native void setLogFrame(int logFrame) /*-{
		this.logFrame = logFrame;
	}-*/;

	public Integer getActivityAdvancement() {
		return Values.getInteger(this, "activityAdvancement");
	}

	public void setActivityAdvancement(Integer activityAdvancement) {
        Values.setInteger(this, "activityAdvancement", activityAdvancement);
	}

	public native double getPlannedBudget() /*-{
		return this.plannedBudget;
	}-*/;

	public native void setPlannedBudget(double plannedBudget) /*-{
		this.plannedBudget = plannedBudget;
	}-*/;

	public native double getSpendBudget() /*-{
		return this.spendBudget;
	}-*/;

	public native void setSpendBudget(double spendBudget) /*-{
		this.spendBudget = spendBudget;
	}-*/;

	public native double getReceivedBudget() /*-{
		return this.receivedBudget;
	}-*/;

	public native void setReceivedBudget(double receivedBudget) /*-{
		this.receivedBudget = receivedBudget;
	}-*/;

	public native JsArray<ProjectFundingJS> getFunding() /*-{
		return this.funding;
	}-*/;

	public void setFunding(List<ProjectFundingDTO> funding) {
		if (funding != null) {
			final JsArray<ProjectFundingJS> array = (JsArray<ProjectFundingJS>) JavaScriptObject.createArray();

			for (final ProjectFundingDTO projectFundingDTO : funding) {
				array.push(ProjectFundingJS.toJavaScript(projectFundingDTO));
			}

			setFunding(array);
		}
	}

	public native void setFunding(JsArray<ProjectFundingJS> funding) /*-{
		this.funding = funding;
	}-*/;

	public native JsArray<ProjectFundingJS> getFunded() /*-{
		return this.funded;
	}-*/;

	public void setFunded(List<ProjectFundingDTO> funded) {
		if (funded != null) {
			final JsArray<ProjectFundingJS> array = (JsArray<ProjectFundingJS>) JavaScriptObject.createArray();

			for (final ProjectFundingDTO projectFundingDTO : funded) {
				array.push(ProjectFundingJS.toJavaScript(projectFundingDTO));
			}

			setFunded(array);
		}
	}

	public native void setFunded(JsArray<ProjectFundingJS> funded) /*-{
		this.funded = funded;
	}-*/;
	
	public JsArrayInteger getProjectFundings() {
		return Values.getJavaScriptObject(this, "projectFundings");
	}
	
	public void setProjectFundings(final List<ProjectFundingDTO> funded, final List<ProjectFundingDTO> funding) {
		Values.setArrayOfIdentifiers(this, "projectFundings", Collections.merge(funded, funding));
	}

	public native int getCountry() /*-{
		return this.country;
	}-*/;

	public void setCountry(CountryDTO countryDTO) {
		if (countryDTO != null) {
			setCountry(countryDTO.getId());
		} else {
			setCountry();
		}
	}

	private native void setCountry() /*-{
		this.country = undefined;
	}-*/;

	public native void setCountry(int country) /*-{
		this.country = country;
	}-*/;

	public native int getManager() /*-{
		return this.manager;
	}-*/;

	public void setManager(UserDTO userDTO) {
		if (userDTO != null) {
			setManager(userDTO.getId());
		}
	}

	public native void setManager(int manager) /*-{
		this.manager = manager;
	}-*/;

	public Integer getPointsListId() {
		return Values.getInteger(this, "pointsListId");
	}
	
	public void setPointsListId(Integer pointsListId) {
		Values.setInteger(this, "pointsListId", pointsListId);
	}
	
	public MonitoredPointListDTO getPointsListDTO() {
		if (getPointsListId()!= null) {
			final MonitoredPointListDTO monitoredPointListDTO = new MonitoredPointListDTO();
			monitoredPointListDTO.setId(getPointsListId());
			return monitoredPointListDTO;
			
		} else {
			return null;
		}
	}

	public void setPointsList(MonitoredPointListDTO pointsList) {
		if (pointsList != null) {
			setPointsListId(pointsList.getId());
		}
	}

	public Integer getRemindersListId() {
		return Values.getInteger(this, "remindersListId");
	}
	
	public void setRemindersListId(Integer remindersListId) {
		Values.setInteger(this, "remindersListId", remindersListId);
	}
	
	public ReminderListDTO getRemindersListDTO() {
		if (getRemindersListId() != null) {
			final ReminderListDTO reminderListDTO = new ReminderListDTO();
			reminderListDTO.setId(getRemindersListId());
			return reminderListDTO;
			
		} else {
			return null;
		}
	}

	public void setRemindersList(ReminderListDTO remindersList) {
		if (remindersList != null) {
			setRemindersListId(remindersList.getId());
		}
	}

	public native JsDate getCloseDate() /*-{
		return this.closeDate;
	}-*/;

	public native void setCloseDate(JsDate closeDate) /*-{
		this.closeDate = closeDate;
	}-*/;

	public AmendmentState getAmendmentState() {
		return Values.getEnum(this, ProjectDTO.AMENDMENT_STATE, AmendmentState.class);
	}

	public void setAmendmentState(AmendmentState amendmentState) {
		Values.setEnum(this, ProjectDTO.AMENDMENT_STATE, amendmentState);
	}

	public native int getAmendmentVersion() /*-{
		return this.amendmentVersion;
	}-*/;

	public void setAmendmentVersion(Integer amendmentVersion) {
		if (amendmentVersion != null) {
			setAmendmentVersion(amendmentVersion.intValue());
		}
	}

	public native void setAmendmentVersion(int amendmentVersion) /*-{
		this.amendmentVersion = amendmentVersion;
	}-*/;

	public native int getAmendmentRevision() /*-{
		return this.amendmentRevision;
	}-*/;

	public void setAmendmentRevision(Integer amendmentRevision) {
		if (amendmentRevision != null) {
			setAmendmentRevision(amendmentRevision.intValue());
		}
	}

	public native void setAmendmentRevision(int amendmentRevision) /*-{
		this.amendmentRevision = amendmentRevision;
	}-*/;

	public native JsArray<AmendmentJS> getAmendments() /*-{
		return this.amendments;
	}-*/;
	
	public native void setAmendments(JsArray<AmendmentJS> amendments) /*-{
		this.amendments = amendments;
	}-*/;
	
	public List<AmendmentDTO> getAmendmentDTOs() {
		if (getAmendments() != null) {
			final ArrayList<AmendmentDTO> list = new ArrayList<AmendmentDTO>();

			final JsArray<AmendmentJS> amendments = getAmendments();
			for (int index = 0; index < amendments.length(); index++) {
				list.add(amendments.get(index).toDTO());
			}

			return list;
		}
		return null;
	}
	
	public void setAmendments(List<AmendmentDTO> amendments) {
		if (amendments != null) {
			final JsArray<AmendmentJS> array = (JsArray<AmendmentJS>) JavaScriptObject.createArray();

			for (final AmendmentDTO amendmentDTO : amendments) {
				array.push(AmendmentJS.toJavaScript(amendmentDTO));
			}

			setAmendments(array);
		}
	}

	public native int getCurrentAmendment() /*-{
		return this.currentAmendment;
	}-*/;

	public void setCurrentAmendment(AmendmentDTO currentAmendment) {
		if (currentAmendment != null) {
			setCurrentAmendment(currentAmendment.getId());
		}
	}

	public native void setCurrentAmendment(int currentAmendment) /*-{
		this.currentAmendment = currentAmendment;
	}-*/;

	public Integer getOrgUnit() {
		return Values.getInteger(this, "orgUnit");
	}
	
	public void setOrgUnit(Integer orgUnit) {
		Values.setInteger(this, "orgUnit", orgUnit);
    }

	public native JsArray<UserJS> getFavoriteUsers() /*-{
		return this.favoriteUsers;
	}-*/;

	public native void setFavoriteUsers(JsArray<UserJS> favoriteUsers) /*-{
		this.favoriteUsers = favoriteUsers;
	}-*/;
	
	public Set<UserDTO> getFavoriteUserDTOs() {
		if(getFavoriteUsers() != null) {
			final HashSet<UserDTO> set = new HashSet<UserDTO>();
			
			final JsArray<UserJS> users = getFavoriteUsers();
			for(int index = 0; index < users.length(); index++) {
				set.add(users.get(index).toDTO());
			}
			
			return set;
		}
		return null;
	}
	
	public void setFavoriteUsers(Set<UserDTO> favoriteUsers) {
		if (favoriteUsers != null) {
			final JsArray<UserJS> array = Values.createTypedJavaScriptArray(UserJS.class);

			for (final UserDTO userDTO : favoriteUsers) {
				array.push(UserJS.toJavaScript(userDTO));
			}

			setFavoriteUsers(array);
		}
	}
	
	// ---------------------------------------------------------------------------------------------
	//
	// MANUAL MAPPING METHODS EQUIVALENTS.
	//
	// ---------------------------------------------------------------------------------------------
	
	public Double getRatioDividendValue() {
		return Values.getDouble(this, ProjectDTO.RATIO_DIVIDEND_VALUE);
	}

	public void setRatioDividendValue(final Double ratioDividendValue) {
		Values.setDouble(this, ProjectDTO.RATIO_DIVIDEND_VALUE, ratioDividendValue);
	}

	public String getRatioDividendLabel() {
		return Values.getString(this, ProjectDTO.RATIO_DIVIDEND_LABEL);
	}

	public void setRatioDividendLabel(final String ratioDividendLabel) {
		Values.setString(this, ProjectDTO.RATIO_DIVIDEND_LABEL, ratioDividendLabel);
	}

	public BudgetSubFieldType getRatioDividendType() {
		return Values.getEnum(this, ProjectDTO.RATIO_DIVIDEND_TYPE, BudgetSubFieldType.class);
	}

	public void setRatioDividendType(final BudgetSubFieldType ratioDividendType) {
		Values.setEnum(this, ProjectDTO.RATIO_DIVIDEND_TYPE, ratioDividendType);
	}

	public Double getRatioDivisorValue() {
		return Values.getDouble(this, ProjectDTO.RATIO_DIVISOR_VALUE);
	}

	public void setRatioDivisorValue(final Double ratioDivisorValue) {
		Values.setDouble(this, ProjectDTO.RATIO_DIVISOR_VALUE, ratioDivisorValue);
	}

	public String getRatioDivisorLabel() {
		return Values.getString(this, ProjectDTO.RATIO_DIVISOR_LABEL);
	}

	public void setRatioDivisorLabel(final String ratioDivisorLabel) {
		Values.setString(this, ProjectDTO.RATIO_DIVISOR_LABEL, ratioDivisorLabel);
	}

	public BudgetSubFieldType getRatioDivisorType() {
		return Values.getEnum(this, ProjectDTO.RATIO_DIVISOR_TYPE, BudgetSubFieldType.class);
	}

	public void setRatioDivisorType(final BudgetSubFieldType ratioDivisorType) {
		Values.setEnum(this, ProjectDTO.RATIO_DIVISOR_TYPE, ratioDivisorType);
	}

	public Set<CategoryElementDTO> getCategoryElements() {
		if (!Values.isDefined(this, ProjectDTO.CATEGORY_ELEMENTS)) {
			return null;
		}
		final JsArray<CategoryElementJS> array = Values.getJavaScriptObject(this, ProjectDTO.CATEGORY_ELEMENTS);
		final HashSet<CategoryElementDTO> set = new HashSet<CategoryElementDTO>();
		for (int index = 0; index < array.length(); index++) {
			set.add(array.get(index).toDTO());
		}
		return set;
	}

	public void setCategoryElements(final Set<CategoryElementDTO> categoryElements) {
		if (categoryElements == null) {
			return;
		}
		final JsArray<CategoryElementJS> array = Values.createTypedJavaScriptArray(CategoryElementJS.class);
		for (final CategoryElementDTO categoryElement : categoryElements) {
			array.push(CategoryElementJS.toJavaScript(categoryElement));
		}
		Values.setJavaScriptObject(this, ProjectDTO.CATEGORY_ELEMENTS, array);
	}
	
}
