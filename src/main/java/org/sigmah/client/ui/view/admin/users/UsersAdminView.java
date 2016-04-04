package org.sigmah.client.ui.view.admin.users;

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
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.users.UsersAdminPresenter;
import org.sigmah.client.ui.presenter.admin.users.UsersAdminPresenter.GridEditHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.ui.widget.toolbar.ActionToolBar;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.toolbar.LabelToolItem;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.inject.Singleton;

/**
 * Admin users view implementation.
 *
 * @author Maxime Lombard (mlombard@ideia.fr) v1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class UsersAdminView extends AbstractView implements UsersAdminPresenter.View {

	// CSS style names.
	private static final String STYLE_PROJECT_GRID_LEAF = "project-grid-leaf";

	/**
	 * Grids edit button width (in pixels).
	 */
	private static final int GRID_EDIT_BUTTON_WIDTH = 50;

	// --
	// Containers.
	// --

	private LayoutContainer mainContainer;
	private LayoutContainer southContainer;

	// --
	// Users Panel.
	// --

	private Grid<UserDTO> usersGrid;
	private ContentPanel usersListPanel;
	private Button usersAddButton;
	private Button usersDesactiveActiveButton;
	private Button usersRefreshButton;
	private Field<String> usersFilterField;

	// --
	// Profiles Panel.
	// --

	private Grid<ProfileDTO> profilesGrid;
	private ContentPanel profilesListPanel;
	private Button profilesAddButton;
	private Button profilesDeleteButton;
	private Button profilesRefreshButton;

	// --
	// Privacy Groups Panel.
	// --

	private ContentPanel privacyGroupsPanel;
	private Grid<PrivacyGroupDTO> privacyGroupsGrid;
	private Button privacyGroupsAddButton;
	private Button privacyGroupsDeleteButton;

	private GridEditHandler gridEditHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Main container.
		// --

		mainContainer = Layouts.vBox();
		southContainer = Layouts.border();

		// --
		// Users panel.
		// --

		usersListPanel = Panels.content(I18N.CONSTANTS.adminUsersPanel());
		usersListPanel.add(usersGrid = buildUsersGrid());
		usersListPanel.setTopComponent(initToolBar(USERS)); // After grid initialization.

		// --
		// Profiles Panel.
		// --

		profilesListPanel = Panels.content(I18N.CONSTANTS.adminProfilesPanel());
		profilesListPanel.add(profilesGrid = buildProfilesGrid());
		profilesListPanel.setTopComponent(initToolBar(PROFILES)); // After grid initialization.

		// --
		// Privacy groups.
		// --

		privacyGroupsPanel = Panels.content(I18N.CONSTANTS.adminPrivacyGroups());
		privacyGroupsPanel.add(privacyGroupsGrid = buildPrivacyGroupsGrid());
		privacyGroupsPanel.setTopComponent(initToolBar(PRIVACY_GROUPS)); // After grid initialization.

		// --
		// General layout.
		// --

		southContainer.add(profilesListPanel, Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.HALF_RIGHT));
		southContainer.add(privacyGroupsPanel, Layouts.borderLayoutData(LayoutRegion.EAST, 300f, Margin.HALF_LEFT));

		mainContainer.add(usersListPanel, Layouts.vBoxData(Margin.HALF_BOTTOM));
		mainContainer.add(southContainer, Layouts.vBoxData(Margin.HALF_TOP));

		add(mainContainer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UserDTO> getUsersSelection() {
		return usersGrid.getSelectionModel().getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ProfileDTO> getProfilesSelection() {
		return profilesGrid.getSelectionModel().getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PrivacyGroupDTO> getPrivacyGroupsSelection() {
		return privacyGroupsGrid.getSelectionModel().getSelectedItems();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<UserDTO> getUsersStore() {
		return usersGrid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getUsersLoadable() {
		return new Loadable[] { new LoadingMask(usersListPanel)
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clearFilters() {
		getUsersStore().clearFilters();
		usersFilterField.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<ProfileDTO> getProfilesStore() {
		return profilesGrid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getProfilesLoadable() {
		return new Loadable[] { new LoadingMask(profilesListPanel)
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<PrivacyGroupDTO> getPrivacyGroupsStore() {
		return privacyGroupsGrid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getPrivacyGroupsLoadable() {
		return new Loadable[] { new LoadingMask(privacyGroupsPanel)
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getPrivacyGroupsAddButton() {
		return privacyGroupsAddButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getPrivacyGroupsDeleteButton() {
		return privacyGroupsDeleteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getProfilesAddButton() {
		return profilesAddButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getProfilesDeleteButton() {
		return profilesDeleteButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getProfilesRefreshButton() {
		return profilesRefreshButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getUsersAddButton() {
		return usersAddButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getUsersActiveButton() {
		return usersDesactiveActiveButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getUsersRefreshButton() {
		return usersRefreshButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGridEditHandler(GridEditHandler handler) {
		this.gridEditHandler = handler;
	}

	// ----------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ----------------------------------------------------------------------------------------------------

	/**
	 * Create the {@link PrivacyGroupDTO} grid.
	 *
	 * @return The grid component.
	 */
	private Grid<PrivacyGroupDTO> buildPrivacyGroupsGrid() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Code column.
		// --

		configs.add(new ColumnConfig(PrivacyGroupDTO.CODE, I18N.CONSTANTS.adminPrivacyGroupsCode(), 60));

		// --
		// Title column.
		// --

		configs.add(new ColumnConfig(PrivacyGroupDTO.TITLE, I18N.CONSTANTS.adminPrivacyGroupsName(), 100));

		// --
		// Edit button column.
		// --

		final ColumnConfig column = new ColumnConfig(null, GRID_EDIT_BUTTON_WIDTH);
		column.setAlignment(Style.HorizontalAlignment.RIGHT);
		column.setRenderer(new GridCellRenderer<PrivacyGroupDTO>() {

			@Override
			public Object render(final PrivacyGroupDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<PrivacyGroupDTO> store, final Grid<PrivacyGroupDTO> grid) {

				return Forms.button(I18N.CONSTANTS.edit(), new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(final ButtonEvent be) {
						gridEditHandler.onEditAction(model);
					};
				});
			}
		});

		configs.add(column);

		return createGrid(configs, PrivacyGroupDTO.TITLE);
	}

	/**
	 * Create the {@link ProfileDTO} grid.
	 *
	 * @return The grid component.
	 */
	private Grid<ProfileDTO> buildProfilesGrid() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Name column.
		// --

		configs.add(new ColumnConfig(ProfileDTO.NAME, I18N.CONSTANTS.adminProfilesName(), 100));

		// --
		// Global permissions column.
		// --

		ColumnConfig column = new ColumnConfig(ProfileDTO.GLOBAL_PERMISSIONS, I18N.CONSTANTS.adminProfilesGlobalPermissions(), 200);
		column.setRenderer(new GridCellRenderer<ProfileDTO>() {

			@Override
			public Object render(final ProfileDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProfileDTO> store, final Grid<ProfileDTO> grid) {

				final StringBuilder builder = new StringBuilder();

				// get selected permissions
				for (final GlobalPermissionEnum globalPermission : model.getGlobalPermissions()) {
					if (builder.length() > 0) {
						builder.append(TEXT_VALUES_SEPARATOR);
					}
					builder.append(GlobalPermissionEnum.getName(globalPermission));
				}

				return createTextWidget(builder.toString());
			}

		});
		configs.add(column);

		// --
		// Privacy groups column.
		// --

		column = new ColumnConfig(ProfileDTO.PRIVACY_GROUPS, I18N.CONSTANTS.adminProfilesPrivacyGroups(), 100);
		column.setRenderer(new GridCellRenderer<ProfileDTO>() {

			@Override
			public Object render(final ProfileDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProfileDTO> store, final Grid<ProfileDTO> grid) {

				final StringBuilder builder = new StringBuilder();

				for (final Entry<PrivacyGroupDTO, PrivacyGroupPermissionEnum> privacyGroup : model.getPrivacyGroups().entrySet()) {
					if (builder.length() > 0) {
						builder.append(TEXT_VALUES_SEPARATOR);
					}

					builder.append('(').append(privacyGroup.getKey().getTitle()).append(", ");
					builder.append(PrivacyGroupPermissionEnum.getName(privacyGroup.getValue())).append(')');
				}

				return createTextWidget(builder.toString());
			}

		});
		configs.add(column);

		// --
		// Edit button column.
		// --

		column = new ColumnConfig(null, GRID_EDIT_BUTTON_WIDTH);
		column.setAlignment(Style.HorizontalAlignment.RIGHT);
		column.setRenderer(new GridCellRenderer<ProfileDTO>() {

			@Override
			public Object render(final ProfileDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ProfileDTO> store, final Grid<ProfileDTO> grid) {

				return new Button(I18N.CONSTANTS.edit(), new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(final ButtonEvent be) {
						gridEditHandler.onEditAction(model);
					}
				});
			}

		});
		configs.add(column);

		return createGrid(configs, ProfileDTO.GLOBAL_PERMISSIONS);
	}

	/**
	 * Create the {@link UserDTO} grid.
	 *
	 * @return The grid component.
	 */
	private Grid<UserDTO> buildUsersGrid() {

		final List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		// --
		// Name column.
		// --

		configs.add(new ColumnConfig(UserDTO.NAME, I18N.CONSTANTS.adminUsersName(), 100));

		// --
		// First name column.
		// --

		configs.add(new ColumnConfig(UserDTO.FIRST_NAME, I18N.CONSTANTS.adminUsersFirstName(), 100));

		// --
		// Active flag column.
		// --

		ColumnConfig column = new ColumnConfig(UserDTO.ACTIVE, I18N.CONSTANTS.adminUsersActive(), 50);
		column.setRenderer(new GridCellRenderer<UserDTO>() {

			@Override
			public Object render(final UserDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<UserDTO> store, final Grid<UserDTO> grid) {

				return createTextWidget(model.getActive() ? I18N.CONSTANTS.adminUsersIsActive() : I18N.CONSTANTS.adminUsersNotActive());
			}
		});
		configs.add(column);

		// --
		// Email column.
		// --

		configs.add(new ColumnConfig(UserDTO.EMAIL, I18N.CONSTANTS.adminUsersEmail(), 150));

		// --
		// Locale (language) column.
		// --

		configs.add(new ColumnConfig(UserDTO.LOCALE, I18N.CONSTANTS.adminUsersLocale(), 50));

		// --
		// OrgUnit column.
		// --

		column = new ColumnConfig(UserDTO.MAIN_ORG_UNIT, I18N.CONSTANTS.adminUsersOrgUnit(), 110);
		column.setRenderer(new GridCellRenderer<UserDTO>() {

			@Override
			public Object render(final UserDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<UserDTO> store, final Grid<UserDTO> grid) {

				final OrgUnitDTO orgUnit = model.getMainOrgUnit();
				return createTextWidget(orgUnit != null ? (ClientUtils.isNotBlank(orgUnit.getFullName()) ? orgUnit.getFullName() : "") : "");
			}
		});
		configs.add(column);

		// --
		// Password change date column.
		// --

		column = new ColumnConfig(UserDTO.PWD_CHANGE_DATE, I18N.CONSTANTS.adminUsersDatePasswordChange(), 120);
		final DateTimeFormat format = DateUtils.DATE_SHORT;
		column.setHeaderHtml(I18N.CONSTANTS.adminUsersDatePasswordChange());
		column.setDateTimeFormat(format);
		column.setRenderer(new GridCellRenderer<UserDTO>() {

			@Override
			public Object render(final UserDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<UserDTO> store, final Grid<UserDTO> grid) {

				final Date changePasswordDate = model.getDateChangePasswordKeyIssued();
				return createTextWidget(changePasswordDate != null ? format.format(changePasswordDate) : "");
			}
		});
		configs.add(column);

		// --
		// Profiles column.
		// --

		column = new ColumnConfig(UserDTO.PROFILES, I18N.CONSTANTS.adminUsersProfiles(), 300);
		column.setRenderer(new GridCellRenderer<UserDTO>() {

			@Override
			public Object render(final UserDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<UserDTO> store, final Grid<UserDTO> grid) {

				final StringBuilder builder = new StringBuilder();

				if (model.getProfiles() != null) {
					// User has profile(s).
					for (final ProfileDTO profile : model.getProfiles()) {
						if (builder.length() > 0) {
							builder.append(TEXT_VALUES_SEPARATOR);
						}
						builder.append(profile.getName());
					}

				} else {
					// No profiles.
					builder.append(I18N.CONSTANTS.adminUsersNoProfiles());
				}

				return createTextWidget(builder.toString());
			}

		});
		configs.add(column);

		// --
		// Edit button column.
		// --

		column = new ColumnConfig(null, GRID_EDIT_BUTTON_WIDTH);
		column.setAlignment(Style.HorizontalAlignment.RIGHT);
		column.setRenderer(new GridCellRenderer<UserDTO>() {

			@Override
			public Object render(final UserDTO model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<UserDTO> store, final Grid<UserDTO> grid) {

				return Forms.button(I18N.CONSTANTS.edit(), new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(final ButtonEvent be) {
						gridEditHandler.onEditAction(model);
					}
				});
			}

		});
		configs.add(column);

		return createGrid(configs, UserDTO.PROFILES);
	}

	/**
	 * Initializes the grids toolbars.
	 *
	 * @param panel
	 *          The grid type.
	 * @return The toolbar component.
	 */
	private ActionToolBar initToolBar(final int type) {

		final ActionToolBar toolBar = new ActionToolBar();

		switch (type) {

			case USERS:
				usersAddButton = toolBar.addButton(I18N.CONSTANTS.addUser(), IconImageBundle.ICONS.addUser());
				usersDesactiveActiveButton = toolBar.addButton(I18N.CONSTANTS.adminUserDisable(), IconImageBundle.ICONS.deleteUser());

				toolBar.add(new LabelToolItem(I18N.CONSTANTS.adminUsersSearchByName()));

				// --
				// Adds a 'name/email' filter implementation.
				// --

				usersGrid.getStore().clearFilters();

				usersGrid.getStore().addFilter(new StoreFilter<UserDTO>() {

					@Override
					public boolean select(final Store<UserDTO> store, final UserDTO parent, final UserDTO item, final String property) {

						if (ClientUtils.isBlank(property)) {
							return true;
						}

						return item.getName().toUpperCase().contains(property.toUpperCase())
							|| item.getFirstName().toUpperCase().contains(property.toUpperCase())
							|| item.getEmail().toUpperCase().contains(property.toUpperCase());
					}
				});

				// --
				// Adds a search field to filter store data.
				// --

				usersFilterField = new TextField<String>();
				usersFilterField.addKeyListener(new KeyListener() {

					@Override
					public void componentKeyUp(final ComponentEvent event) {

						final String fieldValue = usersFilterField.getValue();

						if (ClientUtils.isNotBlank(fieldValue)) {
							// Applies user filter.
							usersGrid.getStore().applyFilters(fieldValue);

						} else {
							// Clears filter.
							usersGrid.getStore().clearFilters();
						}
					}
				});

				toolBar.add(usersFilterField);
				usersRefreshButton = toolBar.addButton(I18N.CONSTANTS.refresh(), IconImageBundle.ICONS.refresh());
				break;

			case PROFILES:
				profilesAddButton = toolBar.addButton(I18N.CONSTANTS.adminProfileAdd(), IconImageBundle.ICONS.add());
				profilesRefreshButton = toolBar.addButton(I18N.CONSTANTS.refresh(), IconImageBundle.ICONS.refresh());
				profilesDeleteButton = toolBar.addButton(I18N.CONSTANTS.adminProfileDelete(), IconImageBundle.ICONS.delete());
				break;

			case PRIVACY_GROUPS:
				privacyGroupsAddButton = toolBar.addButton(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
				privacyGroupsDeleteButton = toolBar.addButton(I18N.CONSTANTS.adminPrivacyGroupDelete(), IconImageBundle.ICONS.delete());
				break;

			default:
				break;
		}

		return toolBar;
	}

	/**
	 * Creates a new {@link Grid} instance for the given {@code columnConfigs}.
	 *
	 * @param columnConfigs
	 *          The columns configurations list.
	 * @param autoExpandColumn
	 *          The auto expand column.
	 * @return The grid component.
	 */
	private static <T extends ModelData> Grid<T> createGrid(final List<ColumnConfig> columnConfigs, final String autoExpandColumn) {

		final Grid<T> grid = new Grid<T>(new ListStore<T>(), new ColumnModel(columnConfigs));

		grid.setStyleAttribute("borderTop", "none");
		grid.setBorders(false);
		grid.setStripeRows(true);
		grid.setSelectionModel(new GridSelectionModel<T>());
		grid.setAutoExpandColumn(autoExpandColumn);
		grid.getView().setForceFit(true);

		return grid;
	}

	/**
	 * Builds a new {@link Text} widget instance with the given {@code content}.
	 *
	 * @param content
	 *          The text content.
	 * @return The widget instance.
	 */
	private static Text createTextWidget(final String content) {

		final Text label = new Text(content);
		label.addStyleName(STYLE_PROJECT_GRID_LEAF);

		return label;
	}

}
