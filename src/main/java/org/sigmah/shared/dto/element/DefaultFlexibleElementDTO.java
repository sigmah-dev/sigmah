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


import java.util.Date;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.command.GetSitesCount;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.DatePickerEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DTO mapping class for entity element.DefaultFlexibleElement.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DefaultFlexibleElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3746586633233053639L;
	
	public static final String ENTITY_NAME = "element.DefaultFlexibleElement";
	
	private static final String EMPTY_VALUE = "-";

	private transient ListStore<CountryDTO> countriesStore;
	private transient ListStore<UserDTO> usersStore;
	private transient ListStore<OrgUnitDTO> orgUnitsStore;
	protected transient DefaultFlexibleElementContainer container;

	/**
	 * Creates a new default flexible element DTO.
	 */
	public DefaultFlexibleElementDTO() {
		// Empty constructor.
	}
	
	/**
	 * Creates a new default flexible DTO with the given type.
	 * 
	 * @param type 
	 *			Type of the default flexible element DTO to create.
	 */
	public DefaultFlexibleElementDTO(final DefaultFlexibleElementType type) {
		setType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	// Type.
	public DefaultFlexibleElementType getType() {
		return get("type");
	}

	public void setType(DefaultFlexibleElementType type) {
		set("type", type);
	}
	
	@Override
	public String getFormattedLabel() {
		return getLabel() != null ? getLabel() : DefaultFlexibleElementType.getName(getType());
	}

	public ListStore<CountryDTO> getCountriesStore() {
		return countriesStore;
	}

	public ListStore<UserDTO> getManagersStore() {
		return usersStore;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		
		if (currentContainerDTO instanceof DefaultFlexibleElementContainer) {
			container = (DefaultFlexibleElementContainer) currentContainerDTO;
		}
		
		if (valueResult != null && valueResult.isValueDefined())
			return getComponentWithValue(valueResult, enabled);
		else
			return getComponent(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponentInBanner(ValueResult valueResult) {

		if (currentContainerDTO instanceof DefaultFlexibleElementContainer) {
			container = (DefaultFlexibleElementContainer) currentContainerDTO;
		} else {
			throw new IllegalArgumentException(
				"The flexible elements container isn't an instance of DefaultFlexibleElementContainer. The default flexible element connot be instanciated.");
		}
		// Budget case handled by the budget element itself
		return super.getComponentInBanner(valueResult);

	}

	protected Component getComponent(boolean enabled) {

		if (currentContainerDTO == null) {
			throw new IllegalArgumentException(
				"The flexible elements container isn't an instance of DefaultFlexibleElementContainer. The default flexible element connot be instanciated.");
		}

		final Component component;

		switch (getType()) {
			// Project code.
			case CODE:
				component = buildCodeField(container.getName(), enabled);
				break;
				
			// Project title.
			case TITLE:
				component = buildTitleField(container.getFullName(), enabled);
				break;

			case START_DATE:
				component = buildStartDateField(container.getStartDate(), enabled);
				break;
				
			case END_DATE:
				component = buildEndDateField(container.getEndDate(), enabled);
				break;
				
			case COUNTRY:
				component = buildCountryField(container.getCountry(), enabled);
				break;
				
			case OWNER:
				component = buildOwnerField(container.getOwnerFirstName(), container.getOwnerName());
				break;
				
			case MANAGER:
				component = buildManagerField(container.getManager(), enabled);
				break;
				
			case ORG_UNIT:
				component = buildOrgUnitField(container.getOrgUnitId(), enabled);
				break;
				
			default:
				component = createLabelField("ERROR: The default element type '" + getType() + "' is not supported. Your model may need to be updated.");
				break;
		}

		return component;
	}

	protected Component getComponentWithValue(ValueResult valueResult, boolean enabled) {

		final Component component;

		switch (getType()) {
			// Project code.
			case CODE:
				component = buildCodeField(valueResult.getValueObject(), enabled);
				break;
				
			// Project title.
			case TITLE:
				component = buildTitleField(valueResult.getValueObject(), enabled);
				break;

			case START_DATE:
				component = buildStartDateField(valueResult.getValueObject(), enabled);
				break;
				
			case END_DATE:
				component = buildEndDateField(valueResult.getValueObject(), enabled);
				break;

			case COUNTRY:
				component = buildCountryField(valueResult.getValueObject(), enabled);
				break;
				
			case OWNER:
				component = buildOwnerField(valueResult.getValueObject());
				break;
				
			case MANAGER:
				component = buildManagerField(valueResult.getValueObject(), enabled);
				break;
				
			case ORG_UNIT:
				component = buildOrgUnitField(valueResult.getValueObject(), enabled);
				break;
				
			default:
				component = createLabelField("ERROR: The default element type '" + getType() + "' is not supported. Your model may need to be updated.");
				break;
		}

		return component;
	}

	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		// These elements don't have any value.
		return true;
	}

	/**
	 * Method in charge of firing value events.
	 * 
	 * @param value
	 *          The raw value which is serialized to the server and saved to the data layer.
	 * @param isValueOn
	 *          If the value is correct.
	 */
	protected void fireEvents(String value, boolean isValueOn) {

		Log.debug("raw Value is : " + value + "  isValueOn is :" + isValueOn);

		handlerManager.fireEvent(new ValueEvent(this, value));

		// Required element ?
		if (getValidates()) {
			handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
		}
	}
	
	/**
	 * Creates the code field.
	 * 
	 * @param value Code of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The code field.
	 */
	private Field<?> buildCodeField(String value, boolean enabled) {
		return buildTextField(I18N.CONSTANTS.projectName(), value, 50, enabled);
	}
	
	/**
	 * Creates the title field.
	 * 
	 * @param value Title of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The title field.
	 */
	private Field<?> buildTitleField(String value, boolean enabled) {
		return buildTextField(I18N.CONSTANTS.projectFullName(), value, 500, enabled);
	}
	
	/**
	 * Creates a text field.
	 * This method is shared between the code and the title fields.
	 * 
	 * @param label Label of the field.
	 * @param value Current value.
	 * @param size Maximum number of characters allowed.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return A new text field.
	 */
	private Field<?> buildTextField(String label, String value, int size, boolean enabled) {
		final Field<?> field;

		// Builds the field and sets its value.
		if (enabled) {
			final TextField<String> textField = createStringField(size, false);
			textField.setValue(value);
			field = textField;

		} else {
			field = createLabelField(value);
		}

		// Sets the field label.
		setLabel(label);
		field.setFieldLabel(getLabel());
		
		return field;
	}
	
	/**
	 * Creates the start date field.
	 * 
	 * @param date Start date of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The start date field.
	 */
	private Field<?> buildStartDateField(Date date, boolean enabled) {
		return buildDateField(I18N.CONSTANTS.projectStartDate(), date, enabled);
	}
	
	/**
	 * Creates the start date field.
	 * 
	 * @param date Start date of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The start date field.
	 */
	private Field<?> buildStartDateField(String date, boolean enabled) {
		return buildDateField(I18N.CONSTANTS.projectStartDate(), new Date(Long.parseLong(date)), enabled);
	}
	
	/**
	 * Creates the end date field.
	 * 
	 * @param date End date of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The end date field.
	 */
	private Field<?> buildEndDateField(Date date, boolean enabled) {
		return buildDateField(I18N.CONSTANTS.projectEndDate(), date, enabled);
	}
	
	/**
	 * Creates the end date field.
	 * 
	 * @param date End date of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The end date field.
	 */
	private Field<?> buildEndDateField(String date, boolean enabled) {
		return buildDateField(I18N.CONSTANTS.projectEndDate(), new Date(Long.parseLong(date)), enabled);
	}
	
	/**
	 * Creates a date field.
	 * This method is shared between the start date and the end date fields.
	 * 
	 * @param label Label of the field.
	 * @param value Current value.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return A new date field.
	 */
	private Field<?> buildDateField(String label, Date value, boolean enabled) {
		final Field<?> field;

		// Builds the field and sets its value.
		if (enabled) {
			final DateField dateField = createDateField(true);
			dateField.setValue(value);
			field = dateField;

		} else {
			final LabelField labelField = createLabelField();
			if (value != null) {
				labelField.setValue(DateUtils.DATE_SHORT.format(value));
			} else {
				labelField.setValue(EMPTY_VALUE);
			}
			field = labelField;
		}

		// Sets the field label.
		setLabel(label);
		field.setFieldLabel(getLabel());

		return field;
	}
	
	/**
	 * Creates the country field.
	 * 
	 * @param country Country of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The country field.
	 */
	private Field<?> buildCountryField(CountryDTO country, boolean enabled) {
		// COUNTRY of project should not be changeable except OrgUnit's
		enabled &= !(currentContainerDTO instanceof ProjectDTO);

		final Field<?> field;
		
		if (enabled) {
			final ComboBox<CountryDTO> comboBox = new ComboBox<CountryDTO>();
			comboBox.setEmptyText(I18N.CONSTANTS.flexibleElementDefaultSelectCountry());

			ensureCountryStore();

			comboBox.setStore(countriesStore);
			comboBox.setDisplayField(CountryDTO.NAME);
			comboBox.setValueField(CountryDTO.ID);
			comboBox.setTriggerAction(TriggerAction.ALL);
			comboBox.setEditable(true);
			comboBox.setAllowBlank(true);

			// Listens to the selection changes.
			comboBox.addSelectionChangedListener(new SelectionChangedListener<CountryDTO>() {

				@Override
				public void selectionChanged(SelectionChangedEvent<CountryDTO> se) {

					String value = null;
					final boolean isValueOn;

					// Gets the selected choice.
					final CountryDTO choice = se.getSelectedItem();

					// Checks if the choice isn't the default empty choice.
					isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;

					if (choice != null) {
						value = String.valueOf(choice.getId());
					}

					if (value != null) {
						// Fires value change event.
						handlerManager.fireEvent(new ValueEvent(DefaultFlexibleElementDTO.this, value));
					}

					// Required element ?
					if (getValidates()) {
						handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
					}
				}
			});

			if (country != null) {
				comboBox.setValue(country);
			}

			field = comboBox;
			
		} else /* not enabled */{

			final LabelField labelField = createLabelField();

			if (country == null) {
				labelField.setValue(EMPTY_VALUE);
			} else {
				labelField.setValue(country.getName());
			}

			field = labelField;
		}

		// Sets the field label.
		setLabel(I18N.CONSTANTS.projectCountry());
		field.setFieldLabel(getLabel());
		
		return field;
	}
	
	/**
	 * Creates the country field.
	 * 
	 * @param country Country of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The country field.
	 */
	private Field<?> buildCountryField(String country, boolean enabled) {
		final Field<?> field = buildCountryField((CountryDTO)null, enabled);
		
		final int countryId = Integer.parseInt(country);

		dispatch.execute(new GetCountry(countryId), new CommandResultHandler<org.sigmah.shared.dto.country.CountryDTO>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
			}

			@Override
			public void onCommandSuccess(final CountryDTO result) {
				// BUGFIX #694: Disable events on first set.
				field.enableEvents(false);
				
				if(field instanceof ComboBox) {
					((ComboBox<CountryDTO>)field).setValue(result);

				} else if(field instanceof LabelField) {
					((LabelField)field).setValue(result.getName());
				}
				
				field.enableEvents(true);
			}

		});
		
		return field;
	}
	
	/**
	 * Creates the owner field.
	 * This field is always read-only.
	 * 
	 * @param firstName First name of the owner.
	 * @param lastName Last name of the owner.
	 * @return The owner field.
	 */
	private Field<?> buildOwnerField(String firstName, String lastName) {
		return buildOwnerField(firstName != null ? firstName + ' ' + lastName : lastName);
	}
	
	/**
	 * Creates the owner field.
	 * This field is always read-only.
	 * 
	 * @param fullName Full name of the owner.
	 * @return The owner field.
	 */
	private Field<?> buildOwnerField(String fullName) {
		final LabelField labelField = createLabelField();

		// Sets the field label.
		setLabel(I18N.CONSTANTS.projectOwner());
		labelField.setFieldLabel(getLabel());

		// Sets the value to the field.
		labelField.setValue(fullName);

		return labelField;
	}
	
	/**
	 * Creates the manager field.
	 * 
	 * @param manager Manager of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The manager field.
	 */
	private Field<?> buildManagerField(final UserDTO manager, boolean enabled) {
		final Field<?> field;
		
		if (enabled) {
			final ComboBox<UserDTO> comboBox = new ComboBox<UserDTO>();
			comboBox.setEmptyText(I18N.CONSTANTS.flexibleElementDefaultSelectManager());

			// Sets the value to the field.
			// BUGFIX #756 : Iterating through users to give the right instance to the combobox.
			final ConfirmCallback listener;
			if(manager != null && manager.getId() != null) {
				listener = new ConfirmCallback() {

					@Override
					public void onAction() {
						for (final UserDTO model : usersStore.getModels()) {
							if (manager.getId().equals(model.getId())) {
								comboBox.setValue(model);
								return;
							}
						}
					}
				};
			} else {
				listener = null;
			}
			
			// Load the user store if needed
			ensureUserStore(listener);

			comboBox.setStore(usersStore);
			comboBox.setDisplayField(UserDTO.COMPLETE_NAME);
			comboBox.setValueField(UserDTO.ID);
			comboBox.setTriggerAction(TriggerAction.ALL);
			comboBox.setEditable(true);
			comboBox.setAllowBlank(true);

			// Listens to the selection changes.
			comboBox.addSelectionChangedListener(new SelectionChangedListener<UserDTO>() {

				@Override
				public void selectionChanged(SelectionChangedEvent<UserDTO> se) {

					String value = null;
					final boolean isValueOn;

					// Gets the selected choice.
					final UserDTO choice = se.getSelectedItem();

					// Checks if the choice isn't the default empty choice.
					isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;

					if (choice != null) {
						value = String.valueOf(choice.getId());
					}

					if (value != null) {
						// Fires value change event.
						handlerManager.fireEvent(new ValueEvent(DefaultFlexibleElementDTO.this, value));
					}

					// Required element ?
					if (getValidates()) {
						handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
					}
				}
			});

			field = comboBox;

		} else {
			final LabelField labelField = createLabelField();

			if (manager == null) {
				labelField.setValue(EMPTY_VALUE);
			} else {
				labelField.setValue(manager.getFirstName() != null ? manager.getFirstName() + ' ' + manager.getName() : manager.getName());
			}

			field = labelField;
		}

		// Sets the field label.
		setLabel(I18N.CONSTANTS.projectManager());
		field.setFieldLabel(getLabel());
		
		return field;
	}
	
	/**
	 * Creates the manager field.
	 * 
	 * @param managerId ID of the manager.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The manager field.
	 */
	private Field<?> buildManagerField(String managerId, boolean enabled) {
		final Field<?> field = buildManagerField((UserDTO) null, enabled);

		final int userId = Integer.parseInt(managerId);
		
		dispatch.execute(new GetUsersByOrganization(auth().getOrganizationId(), userId, null), new CommandResultHandler<ListResult<UserDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				// Field is already set to null. Nothing to do.
			}

			@Override
			public void onCommandSuccess(final ListResult<UserDTO> result) {
				// BUGFIX #694: Disable events on first set.
				field.enableEvents(false);
				
				if(!result.isEmpty()) {
					final UserDTO manager = result.getList().get(0);
					
					if(field instanceof ComboBox) {
						((ComboBox<UserDTO>)field).setValue(manager);
						
					} else if(field instanceof LabelField) {
						((LabelField)field).setValue(manager.getFirstName() != null ? manager.getFirstName() + ' ' + manager.getName() : manager.getName());
					}
				}
				
				field.enableEvents(true);
			}

		});

		return field;
	}
	
	/**
	 * Creates the organization unit field.
	 * 
	 * @param orgUnitId ID of the organization unit.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The organization unit field.
	 */
	private Field<?> buildOrgUnitField(String orgUnitId, boolean enabled) {
		return buildOrgUnitField(Integer.parseInt(orgUnitId), enabled);
	}
	
	/**
	 * Creates the organization unit field.
	 * 
	 * @param orgUnitId ID of the organization unit.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The organization unit field.
	 */
	private Field<?> buildOrgUnitField(Integer orgUnitId, boolean enabled) {
		final Field<?> field;

		// Org unit field is always read-only for org unit.
		enabled &= !(container instanceof OrgUnitDTO);

		if (enabled) {

			final ComboBox<OrgUnitDTO> comboBox = new ComboBox<OrgUnitDTO>();

			ensureOrgUnitStore();

			comboBox.setStore(orgUnitsStore);
			comboBox.setDisplayField(OrgUnitDTO.COMPLETE_NAME);
			comboBox.setValueField(OrgUnitDTO.ID);
			comboBox.setTriggerAction(TriggerAction.ALL);
			comboBox.setEditable(true);
			comboBox.setAllowBlank(true);

			// BUGFIX #694 : SelectionChangedEvent listener is added AFTER 
			// setting the initial value to avoid sending a 
			// SelectionChangedEvent during view initialization.
			
			// Loading the current value from the cache.
			cache.getOrganizationCache().get(orgUnitId, new AsyncCallback<OrgUnitDTO>() {

				@Override
				public void onFailure(final Throwable caught) {
					// Not found.
					
					// Listens to the selection changes.
					addSelectionChangedListener(comboBox);
				}

				@Override
				public void onSuccess(final OrgUnitDTO result) {
					comboBox.setValue(result);
					
					// Listens to the selection changes.
					addSelectionChangedListener(comboBox);
				}

			});

			field = comboBox;
			
		} else {
			// Builds the field and sets its value.
			final LabelField labelField = createLabelField();

			cache.getOrganizationCache().get(orgUnitId, new AsyncCallback<OrgUnitDTO>() {

				@Override
				public void onSuccess(final OrgUnitDTO result) {
					// BUGFIX: Issue #718
					if(result != null) {
						labelField.setValue(result.getName() + " - " + result.getFullName());
					} else {
						labelField.setValue(EMPTY_VALUE);
					}
				}

				@Override
				public void onFailure(final Throwable caught) {
					labelField.setValue(EMPTY_VALUE);
				}
			});

			// Sets the field label.
			setLabel(I18N.CONSTANTS.orgunit());
			labelField.setFieldLabel(getLabel());

			field = labelField;
		}

		// Sets the field label.
		setLabel(I18N.CONSTANTS.orgunit());
		field.setFieldLabel(getLabel());

		return field;
	}

	/**
	 * Adds the selection changed listener to the given orgunit combobox.
	 * 
	 * @param comboBox Combo box to configure.
	 */
	private void addSelectionChangedListener(final ComboBox<OrgUnitDTO> comboBox) {
		comboBox.addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {
			
			@Override
			public void selectionChanged(final SelectionChangedEvent<OrgUnitDTO> se) {
				// Action called to save the new value.
				final Runnable fireChangeEventRunnable = new Runnable() {
					
					@Override
					public void run() {
						String value = null;
						final boolean isValueOn;
						
						// Gets the selected choice.
						final OrgUnitDTO choice = se.getSelectedItem();
						
						// Checks if the choice isn't the default empty choice.
						isValueOn = choice != null && choice.getId() != null && choice.getId() != -1;
						
						if (choice != null) {
							value = String.valueOf(choice.getId());
						}
						
						if (value != null) {
							// Fires value change event.
							handlerManager.fireEvent(new ValueEvent(DefaultFlexibleElementDTO.this, value));
						}
						
						// Required element ?
						if (getValidates()) {
							handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
						}
					}
				};
				
				if (container instanceof ProjectDTO) {
					Log.debug("OrgUnit in project details.");
					
					final ProjectDTO currentProject = (ProjectDTO) container;
					
					final Filter filter = new Filter();
					filter.addRestriction(DimensionType.Database, currentProject.getId());
					
					GetSitesCount getSitesCountCmd = new GetSitesCount(filter);
					
					dispatch.execute(getSitesCountCmd, new CommandResultHandler<SiteResult>() {
						
						@Override
						public void onCommandFailure(final Throwable caught) {
							Log.error("[getSitesCountCmd] Error while getting the count of sites.", caught);
						}
						
						@Override
						public void onCommandSuccess(final SiteResult result) {
							
							// Gets the selected choice.
							final OrgUnitDTO choice = se.getSelectedItem();
							
							// Current poject's country
							final CountryDTO projectCountry = currentProject.getCountry();
							
							// New OrgUnit's country
							final CountryDTO orgUnitCountry = choice != null ? choice.getOfficeLocationCountry() : null;
							
							if (result != null
								&& result.getSiteCount() > 0
								&& projectCountry != null
								&& orgUnitCountry != null
								&& projectCountry != orgUnitCountry) {
								
								// If the new OrgUnit's country different from the current country of project inform users
								// that it will continue use the country of project not new OrgUnit's.
								
								Log.debug("[getSitesCountCmd]-Site count is: " + result.getSiteCount());
								
								N10N.confirmation(I18N.CONSTANTS.changeOrgUnit(), I18N.CONSTANTS.changeOrgUnitDetails(), new ConfirmCallback() {
									
									// YES callback.
									@Override
									public void onAction() {
										fireChangeEventRunnable.run();
									}
								}, new ConfirmCallback() {
									
									// NO callback.
									@Override
									public void onAction() {
										comboBox.setValue(orgUnitsStore.findModel(OrgUnitDTO.ID, currentProject.getOrgUnitId()));
									}
								});
								
							} else {
								fireChangeEventRunnable.run();
							}
						}
					});
					
				} else {
					// Non project container
					Log.debug("OrgUnit in non-project.");
					fireChangeEventRunnable.run();
				}
			}
		});
	}

	/**
	 * Create a text field to represent a default flexible element.
	 * 
	 * @param length
	 *          The max length of the field.
	 * @param allowBlank
	 *          If the field allow blank value.
	 * @return The text field.
	 */
	private TextField<String> createStringField(final int length, final boolean allowBlank) {

		final TextField<String> textField = new TextField<String>();
		textField.setAllowBlank(allowBlank);

		// Sets the max length.
		textField.setMaxLength(length);

		// Adds the listeners.
		textField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				String rawValue = textField.getValue();

				if (rawValue == null) {
					rawValue = "";
				}

				// The value is valid if it contains at least one non-blank
				// character.
				final boolean isValueOn = !rawValue.trim().equals("") && !(rawValue.length() > length);

				if (!(!allowBlank && !isValueOn)) {
					fireEvents(rawValue, isValueOn);
				}
			}
		});

		return textField;
	}

	/**
	 * Create a date field to represent a default flexible element.
	 * 
	 * @param allowBlank
	 *          If the field allow blank value.
	 * @return The date field.
	 */
	private DateField createDateField(final boolean allowBlank) {

		final DateTimeFormat dateFormat = DateUtils.DATE_SHORT;

		// Creates a date field which manages date picker selections and
		// manual selections.
		final DateField dateField = new DateField();
		dateField.getPropertyEditor().setFormat(dateFormat);
		dateField.setEditable(allowBlank);
		dateField.setAllowBlank(allowBlank);
		preferredWidth = FlexibleElementDTO.NUMBER_FIELD_WIDTH;

		// Adds the listeners.

		dateField.getDatePicker().addListener(Events.Select, new Listener<DatePickerEvent>() {

			@Override
			public void handleEvent(DatePickerEvent be) {

				// The date is saved as a timestamp.
				final String rawValue = String.valueOf(be.getDate().getTime());
				// The date picker always returns a valid date.
				final boolean isValueOn = true;

				fireEvents(rawValue, isValueOn);
			}
		});

		dateField.addListener(Events.OnKeyUp, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				final Date date = dateField.getValue();

				// The date is invalid, fires only a required event to
				// invalidate some previously valid date.
				if (date == null) {

					// Required element ?
					if (getValidates()) {
						handlerManager.fireEvent(new RequiredValueEvent(false));
					}

					if (allowBlank) {
						fireEvents("", false);
					}

					return;
				}

				// The date is saved as a timestamp.
				final String rawValue = String.valueOf(date.getTime());
				// The date is valid here.
				final boolean isValueOn = true;

				if (!(!allowBlank && !isValueOn)) {
					fireEvents(rawValue, isValueOn);
				}
			}
		});

		return dateField;
	}

	/**
	 * Create a number field to represent a default flexible element.
	 * 
	 * @param allowBlank
	 *          If the field allow blank value.
	 * @return The number field.
	 */
	protected NumberField createNumberField(final boolean allowBlank) {

		final NumberField numberField = new NumberField();
		numberField.setAllowDecimals(true);
		numberField.setAllowNegative(false);
		numberField.setAllowBlank(allowBlank);
		preferredWidth = FlexibleElementDTO.NUMBER_FIELD_WIDTH;

		// Decimal format
		final NumberFormat format = NumberFormat.getDecimalFormat();
		numberField.setFormat(format);

		// Sets the min value.
		final Number minValue = 0.0;
		numberField.setMinValue(minValue);

		return numberField;
	}

	/**
	 * Create a label field to represent a default flexible element.
	 * 
	 * @return The label field.
	 */
	protected LabelField createLabelField() {

		final LabelField labelField = new LabelField();
		labelField.setLabelSeparator(":");

		return labelField;
	}
	
	/**
	 * Create a label field and sets its value.
	 * 
	 * @param value Value to set.
	 * @return The label field.
	 */
	protected LabelField createLabelField(String value) {
		final LabelField labelField = createLabelField();
		labelField.setValue(value);
		return labelField;
	}
	
	private String formatCountry(String value) {
		if (cache != null) {
			try {
				final CountryDTO c = cache.getCountryCache().get(Integer.valueOf(value));
				if (c != null) {
					return c.getName();
				} else {
					return '#' + value;
				}
			} catch(NumberFormatException e) {
				return "";
			}
		} else {
			return '#' + value;
		}
	}
	
	private String formatDate(String value) {
		try {
			final long time = Long.parseLong(value);
			final Date date = new Date(time);

			// Using a shared instance to allow parsing from client and server side.
			final com.google.gwt.i18n.shared.DateTimeFormat formatter = DateUtils.SHARED_DATE_SHORT;
			return formatter.format(date);
			
		} catch(NumberFormatException e) {
			return "";
		}
	}
	
	private String formatManager(String value) {
		if (cache != null) {
			try {
				final UserDTO u = cache.getUserCache().get(Integer.valueOf(value));
				if (u != null) {
					return u.getFirstName() != null ? u.getFirstName() + ' ' + u.getName() : u.getName();
				} else {
					return '#' + value;
				}
			} catch(NumberFormatException e) {
				return "";
			}
		} else {
			return '#' + value;
		}
	}
	
	private String formatOrgUnit(String value) {
		if (cache != null) {
			try {
				final OrgUnitDTO o = cache.getOrganizationCache().get(Integer.valueOf(value));
				if (o != null) {
					return o.getName() + " - " + o.getFullName();
				} else {
					return '#' + value;
				}
			} catch(NumberFormatException e) {
				return "";
			}
		} else {
			return '#' + value;
		}
	}
	
	private String formatText(String value) {
		return value.replace("\n", "<br>");
	}
	
	/**
	 * Creates and populates the shared country store if needed.
	 */
	private void ensureCountryStore() {
		if (countriesStore == null) {
			countriesStore = new ListStore<CountryDTO>();
		}
		
		// if country store is empty
		if (countriesStore.getCount() == 0) {

			if (cache != null) {
				cache.getCountryCache().get(new AsyncCallback<List<CountryDTO>>() {

					@Override
					public void onFailure(Throwable e) {
						Log.error("[getComponent] Error while getting countries list.", e);
					}

					@Override
					public void onSuccess(List<CountryDTO> result) {
						// Fills the store.
						countriesStore.add(result);
					}
				});
				
			} else /* cache is null */ {
				dispatch.execute(new GetCountries(CountryDTO.Mode.BASE), new CommandResultHandler<ListResult<CountryDTO>>() {

					@Override
					protected void onCommandSuccess(final ListResult<CountryDTO> result) {
						// Fills the store.
						countriesStore.add(result.getData());
					}

					@Override
					protected void onCommandFailure(final Throwable caught) {
						Log.error("[getComponent] Error while getting countries list.", caught);
					};

				});
			}
		}
	}
	
	/**
	 * Creates and populates the shared user store if needed.
	 */
	private void ensureUserStore(final ConfirmCallback onLoad) {
		if (usersStore == null) {
			usersStore = new ListStore<UserDTO>();
		}
		
		if (usersStore.getCount() == 0) {
			if (cache != null) {
				cache.getUserCache().get(new AsyncCallback<List<UserDTO>>() {

					@Override
					public void onFailure(final Throwable e) {
						Log.error("[getComponent] Error while getting users list.", e);
					}

					@Override
					public void onSuccess(final List<UserDTO> result) {
						// Fills the store.
						usersStore.add(result);
						
						if(onLoad != null) {
							onLoad.onAction();
						}
					}
				});
				
			} else /* cache is null */ {
				dispatch.execute(new GetUsersByOrganization(auth().getOrganizationId(), null), new CommandResultHandler<ListResult<UserDTO>>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						Log.error("[getComponent] Error while getting users list.", caught);
					}

					@Override
					public void onCommandSuccess(final ListResult<UserDTO> result) {
						// Fills the store.
						usersStore.add(result.getList());
						
						if(onLoad != null) {
							onLoad.onAction();
						}
					}
				});
			}
		} else if(onLoad != null) {
			onLoad.onAction();
		}
	}
	
	/**
	 * Creates and populates the shared org unit store if needed.
	 */
	private void ensureOrgUnitStore() {
		if (orgUnitsStore == null) {
			orgUnitsStore = new ListStore<OrgUnitDTO>();
		}
		
		if(orgUnitsStore.getCount() == 0) {
			cache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTO>() {
				
				@Override
				public void onFailure(Throwable e) {
					Log.error("[getComponent] Error while getting users info.", e);
				}

				@Override
				public void onSuccess(OrgUnitDTO result) {
					// Fills the store.
					recursiveFillOrgUnitsList(result);
				}
			});
		}
	}
	
	/**
	* Fills recursively the org unit store from the given root org unit.
	* 
	* @param root
	*          The root org unit.
	*/
   private void recursiveFillOrgUnitsList(OrgUnitDTO root) {

	   if (root.isCanContainProjects()) {
		   orgUnitsStore.add(root);
	   }

	   for (final OrgUnitDTO child : root.getChildrenOrgUnits()) {
		   recursiveFillOrgUnitsList(child);
	   }
   }

	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {

		ensureHistorable();

		final String value = token.getTokens().get(0).getValue();

		if (getType() != null) {
			switch (getType()) {

				case COUNTRY:
					return new HistoryTokenText(formatCountry(value));

				case START_DATE:
				case END_DATE:
					return new HistoryTokenText(formatDate(value));

				case MANAGER:
					return new HistoryTokenText(formatManager(value));

				case ORG_UNIT:
					return new HistoryTokenText(formatOrgUnit(value));

				default:
					return super.renderHistoryToken(token);
			}
		} else {
			return super.renderHistoryToken(token);
		}
	}

	@Override
	public String toHTML(String value) {
		if(value == null || value.length() == 0) {
			return "";
		}
		
		if (getType() != null) {
			switch (getType()) {
				case COUNTRY:
					return formatCountry(value);

				case START_DATE:
				case END_DATE:
					return formatDate(value);

				case MANAGER:
					return formatManager(value);

				case ORG_UNIT:
					return formatOrgUnit(value);

				default:
					return formatText(value);
			}
		} else {
			return formatText(value);
		}
	}
	
}
