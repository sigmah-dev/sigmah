package org.sigmah.client.ui.presenter.contact.dashboardlist;

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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.filters.GridFilters;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.contact.dashboardlist.ContactsListView;
import org.sigmah.client.ui.view.contact.dashboardlist.DashboardContact;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.contact.DedupeContactDialog;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ListComboBox;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.CheckContactDuplication;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.DedupeContact;
import org.sigmah.shared.command.GetContactDuplicatedProperties;
import org.sigmah.shared.command.GetContactHistory;
import org.sigmah.shared.command.GetContactModels;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.ContactDuplicatedProperty;
import org.sigmah.shared.command.result.ContactHistory;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.ContactModelDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.referential.ContactModelType;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

public class ContactsListWidget extends AbstractPresenter<ContactsListWidget.View> {

	@ImplementedBy(ContactsListView.class)
	public static interface View extends ViewInterface, HasGrid<DashboardContact> {

		void updateAccessibilityState(boolean authorized);

		ContentPanel getContactsPanel();

		GridFilters getGridFilters();

		void addContact(DashboardContact contact);

		void clearContacts();

		void updateToolbar(boolean addContact, boolean importContact, boolean exportContact);

		Button getAddButton();

		Button getImportButton();

		Button getExportButton();

		void syncSize();
	}

	public interface CreateContactHandler {
		void handleContactCreation(ContactModelDTO contactModelDTO, String email, String firstName, String familyName, String organizationName, OrgUnitDTO mainOrgUnit, List<OrgUnitDTO> secondaryOrgUnits);
	}

	// Current contacts grid parameters.
	private boolean currentlyLoading = false;
	private final Queue<GetContacts> commandQueue = new LinkedList<GetContacts>();
	private CreateContactHandler createContactHandler;

	@Inject
	private ImageProvider imageProvider;

	/**
	 * The GetContacts command which will be executed for the next refresh.
	 */
	private GetContacts command;

	/**
	 * Builds a new contact list panel with default values.
	 */
	@Inject
	public ContactsListWidget(final View view, final Injector injector) {
		super(view, injector);

		createContactHandler = new CreateContactHandler() {
			@Override
			public void handleContactCreation(final ContactModelDTO contactModelDTO, final String email, final String firstName, final String familyName, final String organizationName, final OrgUnitDTO mainOrgUnit, final List<OrgUnitDTO> secondaryOrgUnits) {
				CheckContactDuplication checkContactDuplication;
				if (contactModelDTO.getType() == ContactModelType.INDIVIDUAL) {
					checkContactDuplication = new CheckContactDuplication(null, email, familyName, firstName);
				} else {
					checkContactDuplication = new CheckContactDuplication(null, email, familyName, null);
				}
				dispatch.execute(checkContactDuplication, new AsyncCallback<ListResult<ContactDTO>>() {
					@Override
					public void onFailure(Throwable caught) {
						Log.error("Error while checking contact duplicates.");
					}

					@Override
					public void onSuccess(ListResult<ContactDTO> result) {
						final HashMap<String, Object> properties = buildPropertyMap(contactModelDTO, email, firstName, familyName, organizationName, mainOrgUnit, secondaryOrgUnits);
						if (result == null || result.getSize() == 0) {
							createEntity(properties);
							return;
						}

						final DedupeContactDialog dedupeContactDialog = new DedupeContactDialog(true);
						dedupeContactDialog.getPossibleDuplicatesGrid().getStore().add(result.getList());
						dedupeContactDialog.getFirstStepMainButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
							@Override
							public void componentSelected(ButtonEvent ce) {
								createEntity(properties);
								dedupeContactDialog.hide();
							}
						});
						dedupeContactDialog.setSecondStepHandler(new DedupeContactDialog.SecondStepHandler() {
							@Override
							public void initialize(final Integer contactId, final ListStore<ContactDuplicatedProperty> propertiesStore) {
								dispatch.execute(new GetContactDuplicatedProperties(contactId, null, properties), new CommandResultHandler<ListResult<ContactDuplicatedProperty>>() {
									@Override
									protected void onCommandSuccess(ListResult<ContactDuplicatedProperty> result) {
										propertiesStore.add(result.getList());
									}
								}, new LoadingMask(dedupeContactDialog));
							}

							@Override
							public void downloadImage(String id, final Image image) {
								imageProvider.provideDataUrl(id, new SuccessCallback<String>() {
									@Override
									public void onSuccess(String dataUrl) {
										image.setUrl(dataUrl);
									}
								});
							}

							@Override
							public void handleDedupeContact(final Integer targetedContactId, List<ContactDuplicatedProperty> selectedProperties) {
								dispatch.execute(new DedupeContact(selectedProperties, targetedContactId), new CommandResultHandler<ContactDTO>() {
									@Override
									protected void onCommandSuccess(ContactDTO targetedContactDTO) {
										dedupeContactDialog.hide();
										eventBus.navigateRequest(Page.CONTACT_DASHBOARD.requestWith(RequestParameter.ID, targetedContactId));
									}
								});
							}

							@Override
							public void handleCancel() {
								dedupeContactDialog.hide();
							}
						});
						dedupeContactDialog.show();
					}
				});

			}
		};
	}

	private void createEntity(HashMap<String, Object> properties) {
		dispatch.execute(new CreateEntity(ContactDTO.ENTITY_NAME, properties), new AsyncCallback<CreateResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while creating a new Contact from contact creation dialog.");
			}

			@Override
			public void onSuccess(CreateResult result) {
				final ContactDTO contact = (ContactDTO)result.getEntity();
				GetContactHistory historyCmd = new GetContactHistory(contact.getId(), true);
				dispatch.execute(historyCmd, new CommandResultHandler<ListResult<ContactHistory>>() {
					@Override
					protected void onCommandSuccess(ListResult<ContactHistory> result) {
						ContactHistory lastChange = result.isEmpty() ? null : result.getList().get(0);
						view.addContact(new DashboardContact(contact, lastChange));
					}
				});
			}
		});
	}

	private HashMap<String, Object> buildPropertyMap(ContactModelDTO contactModelDTO, String email, String firstName, String familyName, String organizationName, OrgUnitDTO mainOrgUnit, List<OrgUnitDTO> secondaryOrgUnits) {
		HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(ContactDTO.CONTACT_MODEL, contactModelDTO.getId());
		properties.put(ContactDTO.EMAIL, email);
		properties.put(ContactDTO.FIRSTNAME, contactModelDTO.getType() == ContactModelType.INDIVIDUAL ? firstName : null);
		properties.put(ContactDTO.NAME, contactModelDTO.getType() == ContactModelType.INDIVIDUAL ? familyName : organizationName);
		if (mainOrgUnit != null) {
			properties.put(ContactDTO.MAIN_ORG_UNIT, mainOrgUnit.getId());
		}
		if (secondaryOrgUnits != null) {
			HashSet<Integer> secondaryOrgUnitIds = new HashSet<Integer>();
			for (OrgUnitDTO secondaryOrgUnit : secondaryOrgUnits) {
				secondaryOrgUnitIds.add(secondaryOrgUnit.getId());
			}
			properties.put(ContactDTO.SECONDARY_ORG_UNITS, secondaryOrgUnitIds);
		}
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Update contact event.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.CONTACT_CREATE)) {
					// On contact creation event.
					final ContactDTO createdContact = event.getParam(1);

					GetContactHistory historyCmd = new GetContactHistory(createdContact.getId(), true);
					dispatch.execute(historyCmd, new CommandResultHandler<ListResult<ContactHistory>>() {
						@Override
						protected void onCommandSuccess(ListResult<ContactHistory> result) {
							ContactHistory lastChange = result.isEmpty() ? null : result.getList().get(0);
							view.addContact(new DashboardContact(createdContact, lastChange));
						}
					});
				}
			}
		}));

		// --
		// Add action handler.
		// --

		view.getAddButton().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				showContactCreator();
			}

		});

		// --
		// Import action handler.
		// --

		view.getImportButton().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				// TODO : eventBus.navigate(Page.CONTACT_EXPORTS);
			}

		});

		// --
		// Export action handler.
		// --

		view.getExportButton().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				eventBus.navigate(Page.CONTACT_EXPORTS);
			}

		});

		// --
		// Contact name click handler.
		// --

		view.setGridEventHandler(new HasGrid.GridEventHandler<DashboardContact>() {

			@Override
			public void onRowClickEvent(final DashboardContact rowElement) {
				Profiler.INSTANCE.startScenario(Scenario.OPEN_CONTACT);
				eventBus.navigateRequest(Page.CONTACT_DASHBOARD.requestWith(RequestParameter.ID, rowElement.getContact().getId()));
			}
		});
	}

	/**
	 * Asks for a refresh of the contacts list.
	 */
	public void refresh(final boolean mainOrgUnitOnly) {

		// Updates toolbar.
		final boolean addEnabled = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_VISIBLE_CONTACTS);
		final boolean importEnabled = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.IMPORT_CONTACTS);
		final boolean exportEnabled = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EXPORT_ALL_CONTACTS);
		view.updateToolbar(addEnabled, importEnabled, exportEnabled);

		// Updates accessibility.
		view.updateAccessibilityState(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_VISIBLE_CONTACTS));

		// Builds the next refresh command.
		command = new GetContacts();
		Set<Integer> orgUnitsIds = new HashSet<Integer>();
		if(mainOrgUnitOnly) {
			orgUnitsIds.add(auth().getMainOrgUnitId());
		} else {
			orgUnitsIds.addAll(auth().getOrgUnitIds());
		}
		command.setOrgUnitsIds(orgUnitsIds);

		refreshContactGrid(command);
	}

	// ---------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------

	/**
	 * Applies the store filters.
	 */
	private void applyContactFilters() {
		view.getStore().applyFilters(null);
	}

	/**
	 * Refreshes the contacts grid with the current parameters.
	 *
	 * @param cmd
	 *          The {@link GetContacts} command to execute.
	 */
	private void refreshContactGrid(final GetContacts cmd) {

		// Checks that the user can view contacts.
		if (!ProfileUtils.isGranted(auth(), GlobalPermissionEnum.VIEW_VISIBLE_CONTACTS)) {
			return;
		}

		if (cmd == null) {
			return;
		}

		if (currentlyLoading) {
			commandQueue.add(cmd);
			return;
		}

		currentlyLoading = true;

		view.clearContacts();

		// --
		// Builds a new chunks worker.
		// --

		final GetContactsWorker worker = new GetContactsWorker(dispatch, cmd, view.getContactsPanel());
		worker.addWorkerListener(new GetContactsWorker.WorkerListener() {

			private int chunk = 0;

			@Override
			public void serverError(final Throwable error) {

				if (Log.isErrorEnabled()) {
					Log.error("Error while getting contacts by chunks.", error);
				}

				applyContactFilters();
				N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.refreshContactListError());
			}

			@Override
			public void chunkRetrieved(final DashboardContact contact) {
				view.addContact(contact);
				Profiler.INSTANCE.markCheckpoint(Scenario.LOGIN, "Chunk #" + (chunk++) + " loaded.");
			}

			@Override
			public void ended() {
				applyContactFilters();
				view.getContactsPanel().layout();

				currentlyLoading = false;
				// Try to execute the next loader
				refreshContactGrid(commandQueue.poll());
			}
		});

		// --
		// Runs the worker.
		// --

		view.getStore().removeAll();
		view.getStore().clearFilters();

		Profiler.INSTANCE.markCheckpoint(Scenario.LOGIN, "Before contact loading.");

		worker.run();
	}

	private void showContactCreator() {
		final Window window = new Window();
		window.setPlain(true);
		window.setModal(true);
		window.setBlinkModal(true);
		window.setLayout(new FitLayout());
		window.setSize(700, 300);
		window.setHeadingHtml(I18N.CONSTANTS.createContactDialogTitle());

		final ComboBox<ContactModelDTO> contactModelComboBox = Forms.combobox(I18N.CONSTANTS.contactModelLabel(), true, ContactModelDTO.ID, ContactModelDTO.NAME);
		final TextField<String> emailField = Forms.text(I18N.CONSTANTS.contactEmailAddress(), false);

		final TextField<String> firstNameField = Forms.text(I18N.CONSTANTS.contactFirstName(), false);
		final TextField<String> familyNameField = Forms.text(I18N.CONSTANTS.contactFamilyName(), false);
		final TextField<String> organizationNameField = Forms.text(I18N.CONSTANTS.contactOrganizationName(), false);
		firstNameField.setVisible(false);
		familyNameField.setVisible(false);
		organizationNameField.setVisible(false);

		final ComboBox<OrgUnitDTO> mainOrgUnitComboBox = Forms.combobox(I18N.CONSTANTS.contactMainOrgUnit(), false, OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME);
		final ListComboBox<OrgUnitDTO> secondaryOrgUnitsComboBox = new ListComboBox<OrgUnitDTO>(OrgUnitDTO.ID, OrgUnitDTO.FULL_NAME);
		secondaryOrgUnitsComboBox.initComponent();
		final AdapterField secondaryOrgUnitsFieldAdapter = Forms.adapter(I18N.CONSTANTS.contactSecondaryOrgUnits(), secondaryOrgUnitsComboBox);
		secondaryOrgUnitsFieldAdapter.setVisible(false);

		dispatch.execute(new GetContactModels(null, Collections.<Integer>emptySet(), true), new AsyncCallback<ListResult<ContactModelDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while retrieving contact models for contact creation dialog.");
			}

			@Override
			public void onSuccess(ListResult<ContactModelDTO> result) {
				contactModelComboBox.getStore().add(result.getList());
			}
		});
		dispatch.execute(new GetOrgUnits(OrgUnitDTO.Mode.WITH_TREE), new AsyncCallback<ListResult<OrgUnitDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while retrieving org units for contact creation dialog.");
			}

			@Override
			public void onSuccess(ListResult<OrgUnitDTO> result) {

				for (OrgUnitDTO orgUnitDTO : result.getData()) {
					fillOrgUnitsComboboxes(orgUnitDTO, mainOrgUnitComboBox, secondaryOrgUnitsComboBox);
				}
			}
		});

		mainOrgUnitComboBox.addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<OrgUnitDTO> se) {
				if (se.getSelectedItem() == null) {
					secondaryOrgUnitsFieldAdapter.setVisible(false);
					return;
				}
				secondaryOrgUnitsFieldAdapter.setVisible(true);
			}
		});
		contactModelComboBox.addSelectionChangedListener(new SelectionChangedListener<ContactModelDTO>() {
			@Override
			public void selectionChanged(SelectionChangedEvent<ContactModelDTO> event) {
				ContactModelType currentType = null;
				if (event.getSelectedItem() != null) {
					currentType = event.getSelectedItem().getType();
				}
				firstNameField.setVisible(currentType == ContactModelType.INDIVIDUAL);
				familyNameField.setVisible(currentType == ContactModelType.INDIVIDUAL);
				organizationNameField.setVisible(currentType == ContactModelType.ORGANIZATION);
				firstNameField.setAllowBlank(currentType != ContactModelType.INDIVIDUAL);
				familyNameField.setAllowBlank(currentType != ContactModelType.INDIVIDUAL);
				organizationNameField.setAllowBlank(currentType != ContactModelType.ORGANIZATION);
			}
		});
		org.sigmah.client.ui.widget.button.Button button = Forms.button(I18N.CONSTANTS.createContact());

		final FormPanel formPanel = Forms.panel(200);
		formPanel.add(contactModelComboBox);
		formPanel.add(emailField);
		formPanel.add(firstNameField);
		formPanel.add(familyNameField);
		formPanel.add(organizationNameField);
		formPanel.add(mainOrgUnitComboBox);
		formPanel.add(secondaryOrgUnitsFieldAdapter);
		formPanel.getButtonBar().add(button);

		button.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent event) {
				if (!formPanel.isValid()) {
					return;
				}

				createContactHandler.handleContactCreation(contactModelComboBox.getValue(), emailField.getValue(),
						firstNameField.getValue(), familyNameField.getValue(), organizationNameField.getValue(),
						mainOrgUnitComboBox.getValue(), secondaryOrgUnitsComboBox.getListStore().getModels());
				window.hide();
			}
		});

		window.add(formPanel);
		window.show();
	}

	private void fillOrgUnitsComboboxes(OrgUnitDTO unit, final ComboBox<OrgUnitDTO> mainOrgUnitComboBox, final ListComboBox<OrgUnitDTO> secondaryOrgUnitsComboBox) {

		mainOrgUnitComboBox.getStore().add(unit);
		secondaryOrgUnitsComboBox.getAvailableValuesStore().add(unit);

		final Set<OrgUnitDTO> children = unit.getChildrenOrgUnits();
		if (children != null && !children.isEmpty()) {
			for (final OrgUnitDTO child : children) {
				fillOrgUnitsComboboxes(child, mainOrgUnitComboBox, secondaryOrgUnitsComboBox);
			}
		}

	}
}
