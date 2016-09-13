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
public class DefaultFlexibleElementDTO extends AbstractDefaultFlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3746586633233053639L;
	
	public static final String ENTITY_NAME = "element.DefaultFlexibleElement";
	
	private static final String EMPTY_VALUE = "-";

	private transient ListStore<UserDTO> usersStore;
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
				component = buildOrgUnitField(I18N.CONSTANTS.orgunit(), container.getOrgUnitId(), enabled);
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
				component = buildOrgUnitField(I18N.CONSTANTS.orgunit(), valueResult.getValueObject(), enabled);
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
	 * Creates the code field.
	 * 
	 * @param value Code of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The code field.
	 */
	private Field<?> buildCodeField(String value, boolean enabled) {
		return buildTextField(I18N.CONSTANTS.projectName(), value, 50, enabled, false);
	}
	
	/**
	 * Creates the title field.
	 * 
	 * @param value Title of the container.
	 * @param enabled <code>true</code> if the field must be editable, <code>false</code> otherwise.
	 * @return The title field.
	 */
	private Field<?> buildTitleField(String value, boolean enabled) {
		return buildTextField(I18N.CONSTANTS.projectFullName(), value, 500, enabled, false);
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
	 * Creates the owner field.
	 * This field is always read-only.
	 * 
	 * @param firstName First name of the owner.
	 * @param lastName Last name of the owner.
	 * @return The owner field.@
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
	
				@Override
	protected void addOrgUnitSelectionChangedListener(final ComboBox<OrgUnitDTO> comboBox) {
		comboBox.addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {
			
			@Override
			public void selectionChanged(final SelectionChangedEvent<OrgUnitDTO> se) {
				
				final boolean countryChanged = isProjectCountryChanged(se);
				
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
							handlerManager.fireEvent(new ValueEvent(DefaultFlexibleElementDTO.this, value, countryChanged));
						}
						
						// Required element ?
						if (getValidates()) {
							handlerManager.fireEvent(new RequiredValueEvent(isValueOn));
						}
					}
				};
				
				if (countryChanged) {
					Log.debug("Country changed.");
					
					final Filter filter = new Filter();
					filter.addRestriction(DimensionType.Database, container.getId());
					
					dispatch.execute(new GetSitesCount(filter), new CommandResultHandler<SiteResult>() {
						
						@Override
						public void onCommandSuccess(final SiteResult result) {
							
							if (result != null && result.getSiteCount() > 0) {
								
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
										comboBox.setValue(orgUnitsStore.findModel(OrgUnitDTO.ID, container.getOrgUnitId()));
									}
								});
								
							} else {
								fireChangeEventRunnable.run();
							}
						}
					});
					
				} else {
					// Non project container
					Log.debug("Country did not changed.");
					fireChangeEventRunnable.run();
				}
			}
		});
	}
	
	/**
	 * Checks if the country of the enclosing project was changed.
	 * 
	 * @param event
	 *			Change event fired by an OrgUnit default flexible element.
	 * @return <code>true</code> if the container is a project and if the new
	 * org unit has a different country, <code>false</code> otherwise.
	 */
	private boolean isProjectCountryChanged(final SelectionChangedEvent<OrgUnitDTO> event) {
		
		if (container instanceof ProjectDTO) {
			// Gets the selected choice.
			final OrgUnitDTO choice = event.getSelectedItem();

			// Current poject's country
			final CountryDTO projectCountry = container.getCountry();

			// New OrgUnit's country
			final CountryDTO orgUnitCountry = choice != null ? choice.getOfficeLocationCountry() : null;
			
			if (projectCountry == null) {
				return orgUnitCountry != null;
			}
			else if (orgUnitCountry == null) {
				// Rejecting changes to null values.
				return false;
			}
			else {
				return !projectCountry.equals(orgUnitCountry);
			}
		}
		else {
			// No country change if the container is not a project.
			return false;
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
	
				@Override
	protected Field<?> buildCountryField(CountryDTO country, boolean enabled) {
		// COUNTRY of project should not be changeable except OrgUnit's
		enabled &= !(currentContainerDTO instanceof ProjectDTO);

		return super.buildCountryField(country, enabled);
				}

	@Override
	protected Field<?> buildOrgUnitField(String label, Integer orgUnitId, boolean enabled) {
		// Org unit field is always read-only for org unit.
		enabled &= !(container instanceof OrgUnitDTO);

		return super.buildOrgUnitField(label, orgUnitId, enabled);
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
