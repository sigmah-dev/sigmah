package org.sigmah.shared.dto.element;

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


import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.security.AuthenticationProvider;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.ui.widget.HistoryWindow;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.RequiredValueHandler;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.element.event.ValueHandler;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.history.HistoryTokenManager;
import org.sigmah.shared.dto.layout.LayoutConstraintDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.ElementTypeEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.event.shared.HandlerManager;
import java.util.Collection;
import java.util.Date;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.shared.dto.computation.ComputationTriggerDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

/**
 * Abstract flexible element DTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class FlexibleElementDTO extends AbstractModelDataEntityDTO<Integer> implements HistoryTokenManager {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
	
	public static final int NUMBER_FIELD_WIDTH = 200;

	// DTO 'base' attributes keys.
	public static final String LABEL = "label";
	public static final String CODE = "code";
	public static final String VALIDATES = "validates";
	public static final String FILLED_IN = "filledIn";
	public static final String AMENDABLE = "amendable";
	public static final String EXPORTABLE = "exportable";
	public static final String GLOBALLY_EXPORTABLE = "globallyExportable";
	public static final String HISTORABLE = "historable";
	public static final String PRIVACY_GROUP = "privacyGroup";
	public static final String GROUP = "group";
	public static final String CONTAINER = "container";
	public static final String CONSTRAINT = "constraint";
	public static final String BANNER = "banner";
	public static final String DISABLED_DATE = "disabledDate";
	public static final String CREATION_DATE = "creationDate";
	public static final String COMPUTATION_TRIGGERS = "computationTriggers";

	// Provided elements.
	protected transient HandlerManager handlerManager;
	protected transient DispatchAsync dispatch;
	protected transient EventBus eventBus;
	protected transient AuthenticationProvider authenticationProvider;
	protected transient FlexibleElementContainer currentContainerDTO;
	protected transient boolean phaseIsEnded;
	protected transient TransfertManager transfertManager;
	protected transient int preferredWidth;
	private transient Menu historyMenu;
	protected transient UserLocalCache cache;

	/**
	 * Sets the dispatch service to be used in the {@link #getElementComponent(ValueResult)} method.
	 * 
	 * @param dispatch
	 *          The presenter's dispatch service.
	 */
	public void setService(DispatchAsync dispatch) {
		this.dispatch = dispatch;
	}

	/**
	 * Sets the event bus to be used by {@link #getElementComponent(ValueResult)}.
	 * 
	 * @param eventBus
	 *          The presenter's event bus.
	 */
	public void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Sets the authentication provider to be used in the {@link #getElementComponent(ValueResult)} method.
	 * 
	 * @param authenticationProvider
	 *          The authentication provider.
	 */
	public void setAuthenticationProvider(AuthenticationProvider authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	/**
	 * Sets the current container (not model, but instance) using this flexible element to be used in the
	 * {@link #getElementComponent(ValueResult)} method.
	 * 
	 * @param currentContainerDTO
	 *          The current container using this flexible element.
	 */
	public void setCurrentContainerDTO(FlexibleElementContainer currentContainerDTO) {
		this.currentContainerDTO = currentContainerDTO;
	}

	/**
	 * Sets the current transfert manager to allow download and upload operations from flexible elements.
	 * 
	 * @param transfertManager
	 *          The current transfert manager.
	 */
	public void setTransfertManager(TransfertManager transfertManager) {
		this.transfertManager = transfertManager;
	}

	/**
	 * Sets the cache to be used in the {@link #getElementComponent(ValueResult)} method.
	 * 
	 * @param cache
	 *          The cache.
	 */
	public void setCache(UserLocalCache cache) {
		this.cache = cache;
	}

	/**
	 * Method called just before the {@link FlexibleElementDTO#getElementComponent(ValueResult)} method to ensure the
	 * instantiation of the attributes used by the client-side.
	 * This method can be override by subclasses.
	 */
	public void init() {

		// Checks preconditions.
		assert dispatch != null;
		assert authenticationProvider != null;
		assert currentContainerDTO != null;
		handlerManager = new HandlerManager(this);
	}

	public Authentication auth() {
		return authenticationProvider.get();
	}

	/**
	 * Gets the widget of a flexible element with its value.
	 * 
	 * @param valueResult
	 *          value of the flexible element, or {@code null} to display the element without its value.
	 * @return the widget corresponding to the flexible element (can be <code>null</code> if the user cannot see this
	 *         element).
	 */
	public Component getElementComponent(ValueResult valueResult) {
		return getComponentWithHistory(valueResult, false, false);
	}

	/**
	 * Gets the widget of a flexible element with its value to be displayed in the banner.
	 * 
	 * @param valueResult
	 *          value of the flexible element, or {@code null} to display the element without its value.
	 * @return The widget corresponding to the flexible element (can be <code>null</code> if the user cannot see this
	 *         element).
	 */
	public Component getElementComponentInBanner(ValueResult valueResult) {
		return getComponentWithHistory(valueResult, false, true);
	}

	/**
	 * Gets the widget of a flexible element with its value.
	 * 
	 * @param valueResult
	 *          value of the flexible element, or {@code null} to display the element without its value.
	 * @param phaseIsEnded
	 *          If the component is enabled.
	 * @return The widget corresponding to the flexible element (can be <code>null</code> if the user cannot see this
	 *         element).
	 */
	public Component getElementComponent(ValueResult valueResult, boolean phaseIsEnded) {
		return getComponentWithHistory(valueResult, phaseIsEnded, false);
	}
	
	/**
	 * Gets the widget of a flexible element with its value. This method manages the history of the element.
	 * 
	 * @param valueResult
	 *          value of the flexible element, or {@code null} to display the element without its value.
	 * @param phaseIsEnded
	 *          If the component is enabled.
	 * @param inBanner
	 *          If the component will be displayed in the banner.
	 * @return The widget component.
	 */
	private Component getComponentWithHistory(ValueResult valueResult, boolean phaseIsEnded, boolean inBanner) {

		if(ProfileUtils.getPermission(auth(), getPrivacyGroup()) == PrivacyGroupPermissionEnum.NONE) {
			return null;
		}
		
		this.phaseIsEnded = phaseIsEnded;
		
		// Checking if the user has the right to edit this element.
		final boolean enabled = userCanPerformChangeType(ValueEventChangeType.EDIT);
		
		// Building the component.
		Component component = inBanner ? getComponentInBanner(valueResult) : getComponent(valueResult, enabled);

		// Adding the DNA icon if this field is part of the core version.
		if(getAmendable() && component instanceof Field) {
			final Field<?> field = (Field<?>)component;
			field.setFieldLabel(field.getFieldLabel() + "&nbsp;" + IconImageBundle.ICONS.DNABrownGreen().getHTML());
		}
		
		// Adds the history menu if needed.
		if (isHistorable() && !(this instanceof BudgetElementDTO)) {
			if(inBanner || !(component instanceof Field)) {
				createHistoryMenu(component);
				
			} else {
				final HistoryWrapper wrapper = new HistoryWrapper((Field<?>)component);
				wrapper.getHistoryButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						loadAndShowHistory(wrapper.getHistoryButton());
					}
				});
				component = wrapper;
			}
		}

		return component;
	}
	
	/**
	 * Creates the history menu. Displayed when the user right click a flexible 
	 * element.
	 * 
	 * @param component Where to attach the menu.
	 */
	private void createHistoryMenu(Component component) {
		// Builds the menu.
		if (historyMenu == null) {

			final MenuItem historyItem = new MenuItem(I18N.CONSTANTS.historyShow(), IconImageBundle.ICONS.history(), new SelectionListener<MenuEvent>() {

				@Override
				public void componentSelected(MenuEvent ce) {
					loadAndShowHistory();
				}
			});

			historyMenu = new Menu();
			historyMenu.add(historyItem);
		}

		// Attaches it to the element.
		component.setContextMenu(historyMenu);
	}
	
	/**
	 * Send a GetHistory command to retrieve the history of the current element
	 * and displays it in a popup.
	 * 
	 * @param loadables Element to mask during the load of the history.
	 */
	protected void loadAndShowHistory(Loadable... loadables) {
		dispatch.execute(new GetHistory(getId(), currentContainerDTO.getId()), new CommandResultHandler<ListResult<HistoryTokenListDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("[execute] The history cannot be fetched.", e);
				}
				N10N.warn(I18N.CONSTANTS.historyError(), I18N.CONSTANTS.historyErrorDetails());
			}

			@Override
			public void onCommandSuccess(final ListResult<HistoryTokenListDTO> result) {
				HistoryWindow.show(result.getList(), FlexibleElementDTO.this);
			}
		}, loadables);
	}

	/**
	 * Gets the widget of a flexible element with its value to be displayed in the banner. The default implementation uses
	 * the {@link #getComponent(ValueResult, boolean)} method.
	 * 
	 * @param valueResult
	 *          value of the flexible element, or {@code null} to display the element without its value.
	 * @return the widget corresponding to the flexible element.
	 */
	protected Component getComponentInBanner(ValueResult valueResult) {
		return getComponent(valueResult, false);
	}

	/**
	 * Gets the widget of a flexible element with its value.
	 * 
	 * @param valueResult
	 *          value of the flexible element, or {@code null} to display the element without its value.
	 * @param enabled
	 *          If the component is enabled.
	 * @return the widget corresponding to the flexible element.
	 */
	protected abstract Component getComponent(ValueResult valueResult, boolean enabled);
	
	/**
	 * Returns <code>true</code> if the current user is allowed to perform
	 * the given change type.
	 * 
	 * @param changeType Type of change to verify.
	 * @return <code>true</code> if the current user can perform the given 
	 * <code>changeType</code>, <code>false</code> otherwise.
	 */
	protected boolean userCanPerformChangeType(ValueEventChangeType changeType) {
		final PrivacyGroupPermissionEnum permission = ProfileUtils.getPermission(auth(), getPrivacyGroup());
		
		if(permission == PrivacyGroupPermissionEnum.READ) {
			return false;
			
		} else if(permission == PrivacyGroupPermissionEnum.WRITE) {
			if(currentContainerDTO instanceof ProjectDTO) {
				return userCanPerformChangeTypeOnProject(changeType, (ProjectDTO)currentContainerDTO);

			} else if(currentContainerDTO instanceof OrgUnitDTO) {
				return userCanPerformChangeTypeOnOrgUnit(changeType, (OrgUnitDTO)currentContainerDTO);
			}
		}
		
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the current user is allowed to perform
	 * the given change type on the given project.
	 *
	 * The default implementation checks for the 
	 * {@link GlobalPermissionEnum#EDIT_PROJECT} right and to the current
	 * amendment state.
	 * 
	 * @param changeType Type of change to verify.
	 * @param project Project containing this element.
	 * @return <code>true</code> if the current user can perform the given 
	 * <code>changeType</code>, <code>false</code> otherwise.
	 */
	protected boolean userCanPerformChangeTypeOnProject(ValueEventChangeType changeType, ProjectDTO project) {
		// No modifications are possible if the model is under maintenance or
		// if the user is consulting a previous core version.
		if(project.getProjectModel().isUnderMaintenance() || project.getCurrentAmendment() != null) {
			return false;
		}
		
		// Special case for users with the MODIFY_LOCKED_CONTENT permission.
		if(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MODIFY_LOCKED_CONTENT) &&
			(phaseIsEnded || project.isClosed() || project.getAmendmentState() == AmendmentState.LOCKED)) {
			// #818: MODIFY_LOCKED_CONTENT can now delete values. 
			return true;
		}
		
		return
			// Current phase is opened.
			!phaseIsEnded &&
			// Current project is opened.
			!project.isClosed() &&
			// The user is granted edit rights on the project.
			ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_PROJECT) && (
				// This element is not part of the core version
				!getAmendable() || 
				// OR the core version is in an editable state
				project.getAmendmentState() == AmendmentState.DRAFT || 
				// OR the user has the right to unlock the project
				ProfileUtils.isGranted(auth(), GlobalPermissionEnum.LOCK_PROJECT)
			);
	}
	
	/**
	 * Returns <code>true</code> if the current user is allowed to perform
	 * the given change type on the given organization unit.
	 *
	 * For organization units, the default implementation only checks for the
	 * {@link GlobalPermissionEnum#EDIT_ORG_UNIT} right.
	 * 
	 * @param changeType Type of change to verify.
	 * @param orgUnit OrgUnit containing this element.
	 * @return <code>true</code> if the current user can perform the given 
	 * <code>changeType</code>, <code>false</code> otherwise.
	 */
	protected boolean userCanPerformChangeTypeOnOrgUnit(ValueEventChangeType changeType, OrgUnitDTO orgUnit) {
		return 
			// The project model is not under maintenance.
			!orgUnit.getOrgUnitModel().isUnderMaintenance() &&
			// The is granted edit rights on organizational units.
			ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ORG_UNIT);
	}

	/**
	 * Adds a {@link ValueHandler} to the flexible element.
	 * 
	 * @param handler
	 *          a {@link ValueHandler} object
	 */
	public void addValueHandler(ValueHandler handler) {
        if (handlerManager == null) {
            init();
        }
		handlerManager.addHandler(ValueEvent.getType(), handler);
	}

	/**
	 * Adds a {@link RequiredValueHandler} to the flexible element.
	 * 
	 * @param handler
	 *          a {@link RequiredValueHandler} object
	 */
	public void addRequiredValueHandler(RequiredValueHandler handler) {
		handlerManager.addHandler(RequiredValueEvent.getType(), handler);
	}

	/**
	 * Gets the most adapted width to display this component.
	 * 
	 * @return The preferred width of this element.
	 */
	public int getPreferredWidth() {
		return preferredWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(LABEL, getLabel());
		builder.append(VALIDATES, getValidates());
		builder.append(FILLED_IN, isFilledIn());
		builder.append(AMENDABLE, getAmendable());
		builder.append(EXPORTABLE, getExportable());
		builder.append(GLOBALLY_EXPORTABLE, getGloballyExportable());
		builder.append(HISTORABLE, isHistorable());
	}

	/**
	 * Returns the flexible element's formatted label.<br>
	 * May be overridden by sub-implementations.
	 * 
	 * @return The flexible element's formatted label.
	 */
	public String getFormattedLabel() {
		// May be overridden.
		return getLabel();
	}

	// Flexible element label
	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		set(LABEL, label);
	}

	// Flexible element code
	public String getCode() {
		return get(CODE);
	}

	public void setCode(String code) {
		set(CODE, code);
	}

	// Flexible element validates
	public boolean getValidates() {
		final Boolean validates = get(VALIDATES);
		return validates != null && validates;
	}

	public void setValidates(boolean validates) {
		set(VALIDATES, validates);
	}

	public boolean isFilledIn() {
		final Boolean filledIn = get(FILLED_IN);
		return filledIn != null && filledIn;
	}

	public void setFilledIn(boolean filledIn) {
		set(FILLED_IN, filledIn);
	}

	public boolean getAmendable() {
		final Boolean amendable = get(AMENDABLE);
		return amendable != null && amendable;
	}

	public void setAmendable(boolean amendable) {
		set(AMENDABLE, amendable);
	}

	public boolean getExportable() {
		final Boolean exportable = get(EXPORTABLE);
		return exportable != null && exportable;
	}

	public void setExportable(boolean exportable) {
		set(EXPORTABLE, exportable);
	}

	public boolean getGloballyExportable() {
		final Boolean globallyExportable = get(GLOBALLY_EXPORTABLE);
		return globallyExportable != null && globallyExportable;
	}

	public void setGloballyExportable(boolean globallyExportable) {
		set(GLOBALLY_EXPORTABLE, globallyExportable);
	}

	public boolean isHistorable() {
		final Boolean historable = get(HISTORABLE);
		return historable != null && historable;
	}

	public void setHistorable(boolean historable) {
		set(HISTORABLE, historable);
	}

	public PrivacyGroupDTO getPrivacyGroup() {
		return get(PRIVACY_GROUP);
	}

	public void setPrivacyGroup(PrivacyGroupDTO privacyGroup) {
		set(PRIVACY_GROUP, privacyGroup);
	}
	
	public Collection<ComputationTriggerDTO> getComputationTriggers() {
		return get(COMPUTATION_TRIGGERS);
	}
	
	public void setComputationTriggers(Collection<ComputationTriggerDTO> computationTriggers) {
		set(COMPUTATION_TRIGGERS, computationTriggers);
	}

	protected void ensureHistorable() {
		if (!isHistorable()) {
			throw new IllegalStateException("The current flexible element '" + getClass().getName() + "' #" + getId() + " doesn't manage an history.");
		}
	}

	/**
	 * Assigns a value to a flexible element.
	 * 
	 * @param result
	 *          The value.
	 */
	public void assignValue(ValueResult result) {
		setFilledIn(isCorrectRequiredValue(result));
	}

	/**
	 * Returns if a value can be considered as a correct required value for this specific flexible element.
	 * 
	 * @param result
	 *          The value.
	 * @return If the value can be considered as a correct required value.
	 */
	public abstract boolean isCorrectRequiredValue(ValueResult result);

	@Override
	public String getElementLabel() {
		return getLabel();
	}

	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {
		ensureHistorable();
		return new HistoryTokenText(token);
	}
	
	public String toHTML(String value) {
		if(value != null) {
			return value;
		} else {
			return "";
		}
	}

	public ElementTypeEnum getElementType() {
		ElementTypeEnum type = null;
		
		// INFO: Budget elements are handled like DEFAULT elements.
		
		if (this instanceof TextAreaElementDTO) {
			type = ElementTypeEnum.TEXT_AREA;
		} else if (this instanceof CheckboxElementDTO) {
			type = ElementTypeEnum.CHECKBOX;
		} else if (this instanceof DefaultFlexibleElementDTO) {
			type = ElementTypeEnum.DEFAULT;
		} else if (this instanceof FilesListElementDTO) {
			type = ElementTypeEnum.FILES_LIST;
		} else if (this instanceof IndicatorsListElementDTO) {
			type = ElementTypeEnum.INDICATORS;
		} else if (this instanceof MessageElementDTO) {
			type = ElementTypeEnum.MESSAGE;
		} else if (this instanceof QuestionElementDTO) {
			type = ElementTypeEnum.QUESTION;
		} else if (this instanceof ReportElementDTO) {
			type = ElementTypeEnum.REPORT;
		} else if (this instanceof ReportListElementDTO) {
			type = ElementTypeEnum.REPORT_LIST;
		} else if (this instanceof TripletsListElementDTO) {
			type = ElementTypeEnum.TRIPLETS;
		} else if (this instanceof CoreVersionElementDTO) {
			type = ElementTypeEnum.CORE_VERSION;
		} else if (this instanceof ComputationElementDTO) {
			type = ElementTypeEnum.COMPUTATION;
		}
		return type;
	}

	public LayoutGroupDTO getGroup() {
		return get(GROUP);
	}

	public void setGroup(LayoutGroupDTO group) {
		set(GROUP, group);
	}

	public BaseModelData getContainerModel() {
		return get(CONTAINER);
	}

	public void setContainerModel(BaseModelData model) {
		set(CONTAINER, model);
	}

	public LayoutConstraintDTO getConstraint() {
		return get(CONSTRAINT);
	}

	public void setConstraint(LayoutConstraintDTO constraint) {
		set(CONSTRAINT, constraint);
	}

	public LayoutConstraintDTO getBannerConstraint() {
		return get(BANNER);
	}

	public void setBannerConstraint(LayoutConstraintDTO constraint) {
		set(BANNER, constraint);
	}
	
	public Date getDisabledDate() {
		return get(DISABLED_DATE);
	}

	public void setDisabledDate(Date disabledDate) {
		set(DISABLED_DATE, disabledDate);
	}
	
	public boolean isDisabled() {
		return get(DISABLED_DATE) != null;
	}
	
	public Date getCreationDate() {
		return get(CREATION_DATE);
	}
	
	public void setCreationDate(Date creationDate) {
		set(CREATION_DATE, creationDate);
	}
}
