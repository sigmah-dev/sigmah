package org.sigmah.client.ui.presenter.admin.users;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.AbstractAdminPresenter;
import org.sigmah.client.ui.view.admin.users.UsersAdminView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.DeactivateUsers;
import org.sigmah.shared.command.DeletePrivacyGroups;
import org.sigmah.shared.command.DeleteProfiles;
import org.sigmah.shared.command.GetContacts;
import org.sigmah.shared.command.GetPrivacyGroups;
import org.sigmah.shared.command.GetProfilesWithDetails;
import org.sigmah.shared.command.GetUsersWithProfiles;
import org.sigmah.shared.command.result.DeleteResult;
import org.sigmah.shared.command.result.DeleteResult.DeleteErrorCause;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ContactDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.ContactModelType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin Users Presenter which manages {@link UsersAdminView}.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr) (v1.3)
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class UsersAdminPresenter extends AbstractAdminPresenter<UsersAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(UsersAdminView.class)
	public static interface View extends AbstractAdminPresenter.View {

		/**
		 * Text values separator.
		 */
		static final String TEXT_VALUES_SEPARATOR = ", ";

		// Panels types.
		static final int USERS = 1;
		static final int PROFILES = 2;
		static final int PRIVACY_GROUPS = 3;

		// --
		// Users Panel.
		// --

		List<UserDTO> getUsersSelection();

		ListStore<UserDTO> getUsersStore();

		Button getUsersAddButton();

		Button getUsersAddByEmailButton();

		Button getUsersActiveButton();

		Button getUsersRefreshButton();

		Loadable[] getUsersLoadable();

		void buildAddUserByEmailWindow(List<ContactDTO> availableContacts, AddUserByEmailHandler handler);

		/**
		 * Clear the store filters as well as the search field.
		 */
		void clearFilters();

		// --
		// Privacy Groups Panel.
		// --

		Button getPrivacyGroupsAddButton();

		Button getPrivacyGroupsDeleteButton();

		ListStore<PrivacyGroupDTO> getPrivacyGroupsStore();

		List<PrivacyGroupDTO> getPrivacyGroupsSelection();

		Loadable[] getPrivacyGroupsLoadable();

		// --
		// Profiles Panel.
		// --

		Button getProfilesAddButton();

		Button getProfilesDeleteButton();

		Button getProfilesRefreshButton();

		List<ProfileDTO> getProfilesSelection();

		ListStore<ProfileDTO> getProfilesStore();

		Loadable[] getProfilesLoadable();

		// --
		// Other utility methods.
		// --

		void setGridEditHandler(GridEditHandler handler);

	}

	public interface AddUserByEmailHandler {
		void handleSubmit(ContactDTO contactDTO);
	}

	/**
	 * Grid edit button handler.
	 */
	public static interface GridEditHandler {

		/**
		 * Callback executed on grid edit button event.
		 * 
		 * @param entityDTO
		 *          The source button corresponding row entity (user, profile or privacy group).
		 */
		void onEditAction(final EntityDTO<?> entityDTO);

	}

	/**
	 * Loading error message display flag.
	 */
	private static boolean alert;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected UsersAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_USERS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// ALL GRIDS : edit button.
		// --

		view.setGridEditHandler(new GridEditHandler() {

			@Override
			public void onEditAction(final EntityDTO<?> entityDTO) {

				if (entityDTO instanceof UserDTO) {
					eventBus.navigateRequest(Page.ADMIN_USER_EDIT.request().addData(RequestParameter.DTO, entityDTO));

				} else if (entityDTO instanceof ProfileDTO) {
					eventBus.navigateRequest(Page.ADMIN_PROFILE_EDIT.request().addData(RequestParameter.DTO, entityDTO));

				} else if (entityDTO instanceof PrivacyGroupDTO) {
					eventBus.navigateRequest(Page.ADMIN_PRIVACY_GROUP_EDIT.request().addData(RequestParameter.DTO, entityDTO));
				}
			}
		});

		// --
		// USERS : add button handler.
		// --

		view.getUsersAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				eventBus.navigate(Page.ADMIN_USER_EDIT);
			}
		});

		view.getUsersAddByEmailButton().addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				dispatch.execute(new GetContacts(ContactModelType.INDIVIDUAL, true, true), new CommandResultHandler<ListResult<ContactDTO>>() {
					@Override
					protected void onCommandSuccess(ListResult<ContactDTO> result) {
						if (result.isEmpty()) {
							N10N.warn(I18N.CONSTANTS.adminContactNoContactFound());
							return;
						}
						view.buildAddUserByEmailWindow(result.getList(), new AddUserByEmailHandler() {
							@Override
							public void handleSubmit(ContactDTO contactDTO) {
								eventBus.navigateRequest(Page.ADMIN_USER_EDIT.requestWith(RequestParameter.CONTACT_ID, contactDTO.getId()));
							}
						});
					}
				});
			}
		});

		// --
		// USERS : active button handler.
		// --

		view.getUsersActiveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				confirmDeleteSelected(new ConfirmCallback() {

					@Override
					public void onAction() {
						onUserActivationUpdate(view.getUsersSelection());
					}
				}, View.USERS);

			}
		});

		// --
		// USERS : refresh button handler.
		// --

		view.getUsersRefreshButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				refreshUsersPanel();
			}
		});

		// --
		// PROFILES : add button handler.
		// --

		view.getProfilesAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				eventBus.navigate(Page.ADMIN_PROFILE_EDIT);
			}

		});

		// --
		// PROFILES : refresh button handler.
		// --

		view.getProfilesRefreshButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				refreshProfilesPanel();
			}
		});

		// --
		// PROFILES : delete button handler.
		// --

		view.getProfilesDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				confirmDeleteSelected(new ConfirmCallback() {

					@Override
					public void onAction() {
						onProfileDelete(view.getProfilesSelection());
					}
				}, View.PROFILES);
			}
		});

		// --
		// PRIVACY GROUPS : add button handler.
		// --

		view.getPrivacyGroupsAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				eventBus.navigate(Page.ADMIN_PRIVACY_GROUP_EDIT);
			}

		});

		// --
		// PRIVACY GROUPS : delete button handler.
		// --

		view.getPrivacyGroupsDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {
				confirmDeleteSelected(new ConfirmCallback() {

					@Override
					public void onAction() {
						onPrivacyGroupDelete(view.getPrivacyGroupsSelection());
					}
				}, View.PRIVACY_GROUPS);
			}
		});

		// --
		// ALL GRIDS : Update events handler.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.PRIVACY_GROUP_UPDATE)) {
					refreshPrivacyGroupsPanel();

				} else if (event.concern(UpdateEvent.PROFILE_UPDATE)) {
					refreshProfilesPanel();

				} else if (event.concern(UpdateEvent.USER_UPDATE)) {
					refreshUsersPanel();
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(PageRequest request) {

		alert = false;

		// Getting Users
		refreshUsersPanel();

		// Getting profiles
		refreshProfilesPanel();

		// Getting privacy groups
		refreshPrivacyGroupsPanel();
	}

	// ----------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ----------------------------------------------------------------------------------------------------

	/**
	 * Loads the users and populates the corresponding grid store.
	 */
	public void refreshUsersPanel() {

		view.getUsersStore().removeAll();
		view.clearFilters();

		dispatch.execute(new GetUsersWithProfiles(), new CommandResultHandler<ListResult<UserDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				showLoadingErrorMessage();
			}

			@Override
			public void onCommandSuccess(final ListResult<UserDTO> result) {
				view.getUsersStore().add(result.getList());
				view.getUsersStore().commitChanges();
			}
		}, view.getUsersLoadable());
	}

	/**
	 * Loads the profiles and populates the corresponding grid store.
	 */
	public void refreshProfilesPanel() {

		view.getProfilesStore().removeAll();

		dispatch.execute(new GetProfilesWithDetails(null), new CommandResultHandler<ListResult<ProfileDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				showLoadingErrorMessage();
			}

			@Override
			public void onCommandSuccess(final ListResult<ProfileDTO> result) {
				view.getProfilesStore().add(result.getList());
				view.getProfilesStore().commitChanges();
			}
		}, view.getProfilesLoadable());
	}

	/**
	 * Loads the privacy groups and populates the corresponding grid store.
	 */
	public void refreshPrivacyGroupsPanel() {

		view.getPrivacyGroupsStore().removeAll();

		dispatch.execute(new GetPrivacyGroups(), new CommandResultHandler<ListResult<PrivacyGroupDTO>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				showLoadingErrorMessage();
			}

			@Override
			public void onCommandSuccess(final ListResult<PrivacyGroupDTO> result) {
				view.getPrivacyGroupsStore().add(result.getList());
				view.getPrivacyGroupsStore().commitChanges();
			}
		}, view.getPrivacyGroupsLoadable());
	}

	/**
	 * Shows failed loading error message.<br>
	 * Uses the {@link #alert} flag to show this message only once, even if multiple loading actions fail.
	 */
	private static void showLoadingErrorMessage() {
		if (alert) {
			return;
		}
		alert = true;
		N10N.error(I18N.CONSTANTS.adminUsers(), I18N.CONSTANTS.adminProblemLoading());
	}

	/**
	 * Deletes the selected Privacy Group(s).
	 * 
	 * @param selection
	 *          The selected privacy group(s).
	 */
	private void onPrivacyGroupDelete(final List<PrivacyGroupDTO> selection) {

		dispatch.execute(new DeletePrivacyGroups(selection), new CommandResultHandler<DeleteResult<PrivacyGroupDTO>>() {

			/**
			 * On command error.
			 */
			@Override
			public void onCommandFailure(final Throwable caught) {

				final Map<String, List<String>> errorMessage = new HashMap<String, List<String>>();
				final List<String> items = new ArrayList<String>();

				for (final PrivacyGroupDTO privacyGroup : selection) {
					items.add(privacyGroup.getTitle());
				}

				errorMessage.put(I18N.CONSTANTS.admin_deleteItems_error_header(), items);
				errorMessage.put(I18N.CONSTANTS.admin_deleteItems_error_footer(), null);
				N10N.error(errorMessage);
			}

			/**
			 * On command success (but some privacy group(s) may have not been deleted).
			 */
			@Override
			public void onCommandSuccess(final DeleteResult<PrivacyGroupDTO> result) {

				if (result.hasDeletedEntities()) {

					// Some (maybe all) entities have been deleted.

					final List<String> privacyGroupNames = new ArrayList<String>();
					for (final PrivacyGroupDTO privacyGroup : result.getDeletedEntities()) {
						privacyGroupNames.add(privacyGroup.getTitle());
						view.getPrivacyGroupsStore().remove(privacyGroup);
					}

					N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminPrivacyGroupsDeleteSuccess(), privacyGroupNames);

					view.getPrivacyGroupsStore().commitChanges();
				}

				if (result.hasErrors()) {

					// Some error(s) have been detected.

					final Map<PrivacyGroupDTO, List<DeleteErrorCause>> errors = result.getErrors();
					final Map<String, List<String>> errorMessage = new HashMap<String, List<String>>();

					for (final Entry<PrivacyGroupDTO, List<DeleteErrorCause>> error : errors.entrySet()) {

						final PrivacyGroupDTO privacyGroup = error.getKey();
						final List<String> errorMessageItems = new ArrayList<String>();
						final StringBuilder builder = new StringBuilder();

						for (final DeleteErrorCause arguments : error.getValue()) {

							builder.setLength(0);

							if (arguments.isFlexibleElementError()) {
								builder.append(I18N.MESSAGES.admin_privacyGroups_delete_ko_field(arguments.isDefaultFlexibleElement() ? I18N.CONSTANTS.flexibleElementDefault() : arguments
									.getCauseLabel()));
								builder.append(" (").append(I18N.MESSAGES.admin_privacyGroups_delete_ko_model(arguments.getModelName())).append(')');

							} else {
								builder.append(I18N.MESSAGES.admin_privacyGroups_delete_ko_profile(arguments.getCauseLabel()));
							}

							errorMessageItems.add(builder.toString());
						}

						errorMessage.put(I18N.MESSAGES.admin_privacyGroups_delete_ko(privacyGroup.getTitle()), errorMessageItems);
					}

					N10N.warn(errorMessage);
				}
			}
		}, view.getPrivacyGroupsLoadable());
	}

	/**
	 * Controls if the given selected profiles can be deleted and deletes them.
	 * 
	 * @param selection
	 *          The selected profile(s).
	 */
	public void onProfileDelete(final List<ProfileDTO> selection) {

		dispatch.execute(new DeleteProfiles(selection), new CommandResultHandler<DeleteResult<ProfileDTO>>() {

			/**
			 * On command failure.
			 */
			@Override
			public void onCommandFailure(final Throwable caught) {

				final Map<String, List<String>> errorMessage = new HashMap<String, List<String>>();
				final List<String> items = new ArrayList<String>();

				for (final ProfileDTO profile : selection) {
					items.add(profile.getName());
				}

				errorMessage.put(I18N.CONSTANTS.admin_deleteItems_error_header(), items);
				errorMessage.put(I18N.CONSTANTS.admin_deleteItems_error_footer(), null);
				N10N.error(errorMessage);
			}

			/**
			 * On command success (but some profile(s) may have not been deleted).
			 */
			@Override
			public void onCommandSuccess(final DeleteResult<ProfileDTO> result) {

				if (result.hasDeletedEntities()) {

					// Some (maybe all) entities have been deleted.

					final List<String> profileNames = new ArrayList<String>();
					for (final ProfileDTO profile : result.getDeletedEntities()) {
						profileNames.add(profile.getName());
						view.getProfilesStore().remove(profile);
					}

					N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminProfilesDeleteSuccess(), profileNames);

					view.getProfilesStore().commitChanges();
				}

				if (result.hasErrors()) {

					// Some error(s) have been detected.

					final Map<ProfileDTO, List<DeleteErrorCause>> errors = result.getErrors();
					final Map<String, List<String>> errorMessage = new HashMap<String, List<String>>();

					for (final Entry<ProfileDTO, List<DeleteErrorCause>> error : errors.entrySet()) {

						final ProfileDTO profile = error.getKey();
						final List<String> errorMessageItems = new ArrayList<String>();

						for (final DeleteErrorCause arguments : error.getValue()) {
							errorMessageItems.add(arguments.getCauseLabel());
						}

						errorMessage.put(I18N.MESSAGES.admin_profiles_delete_ko(profile.getName()), errorMessageItems);
					}

					N10N.warn(errorMessage);
				}
			}
		}, view.getProfilesLoadable());
	}

	/**
	 * Activates or deactivates the given selected users.
	 * 
	 * @param selection
	 *          The selected user(s).
	 */
	private void onUserActivationUpdate(final List<UserDTO> selection) {

		dispatch.execute(new DeactivateUsers(selection), new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {

				final Map<String, List<String>> errorMessage = new HashMap<String, List<String>>();
				final List<String> items = new ArrayList<String>();

				for (final UserDTO user : selection) {
					items.add(user.getName());
				}

				errorMessage.put(I18N.CONSTANTS.admin_deleteItems_error_header(), items);
				errorMessage.put(I18N.CONSTANTS.admin_deleteItems_error_footer(), null);
				N10N.error(errorMessage);
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {
				for (final UserDTO user : selection) {
					user.setActive(!user.getActive());
					view.getUsersStore().update(user);
				}
			}
		});
	}

	/**
	 * Displays the appropriate confirm message and executes the given {@code confirmCallback}.
	 * 
	 * @param confirmCallback
	 *          The confirmation callback action.
	 * @param type
	 *          The context type (users, profiles or privacy groups).
	 */
	private void confirmDeleteSelected(final ConfirmCallback confirmCallback, final int type) {

		switch (type) {

			case View.USERS:
				// Not a 'delete' action.
				confirmCallback.onAction();
				return;

			case View.PROFILES:
				if (ClientUtils.isEmpty(view.getProfilesSelection())) {
					N10N.warn(I18N.CONSTANTS.delete(), I18N.CONSTANTS.admin_profiles_delete_noSelection());
					return;
				}

				final List<String> profilesNames = new ArrayList<String>();
				for (final ProfileDTO profile : view.getProfilesSelection()) {
					profilesNames.add(profile.getName());
				}

				N10N.confirmation(I18N.CONSTANTS.delete(), I18N.CONSTANTS.admin_profiles_delete_confirmation(), profilesNames, confirmCallback);
				break;

			case View.PRIVACY_GROUPS:
				if (ClientUtils.isEmpty(view.getPrivacyGroupsSelection())) {
					N10N.warn(I18N.CONSTANTS.delete(), I18N.CONSTANTS.admin_privayGroups_delete_noSelection());
					return;
				}

				final List<String> privacyGroupsNames = new ArrayList<String>();
				for (final PrivacyGroupDTO privacyGroup : view.getPrivacyGroupsSelection()) {
					privacyGroupsNames.add(privacyGroup.getTitle());
				}

				N10N.confirmation(I18N.CONSTANTS.delete(), I18N.CONSTANTS.admin_privacyGroups_delete_confirmation(), privacyGroupsNames, confirmCallback);
				break;

			default:
				break;
		}
	}

}
