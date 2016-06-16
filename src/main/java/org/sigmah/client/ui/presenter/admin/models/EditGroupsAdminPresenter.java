package org.sigmah.client.ui.presenter.admin.models;

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


import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.admin.models.EditGroupsAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.GroupsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin create/edit phase model which manages {@link EditGroupsAdminView}.
 */
@Singleton
public class EditGroupsAdminPresenter extends AbstractPagePresenter<EditGroupsAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(EditGroupsAdminView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<String> getNameField();

		ComboBox<BaseModelData> getContainerField();

		Field<Number> getPositionField();

		Button getSaveButton();
	}

	/**
	 * The parent project model.<br>
	 * Should never be {@code null}.
	 */
	private ProjectModelDTO parentProjectModel;

	/**
	 * The edited phase model.<br>
	 * Set to {@code null} in case of creation.
	 */
	private GroupsDTO groupsUpdate;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected EditGroupsAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_EDIT_GROUPS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { view.getForm()
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Container field change handler.
		// --

		view.getContainerField().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {

				final BaseModelData hasLayout = view.getContainerField().getValue();
				final LayoutDTO selectedContainer = EditLayoutGroupAdminPresenter.getLayout(hasLayout);

				if (hasLayout instanceof ProjectBannerDTO || hasLayout instanceof OrgUnitBannerDTO) {
					view.getBannerField().setValue(true); // Updates the bannerPosition field.
				}

				view.getLayoutGroupField().getStore().removeAll();
				view.getLayoutGroupField().disable();

				if (selectedContainer != null) {
					view.getLayoutGroupField().getStore().add(selectedContainer.getGroups());
					view.getLayoutGroupField().getStore().commitChanges();
					view.getLayoutGroupField().setValue(view.getLayoutGroupField().getStore().getAt(0));
					view.getLayoutGroupField().enable();
				}
			}
		});

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onSaveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		parentProjectModel = request.getData(RequestParameter.MODEL);
		groupsUpdate = request.getData(RequestParameter.DTO);

		if (parentProjectModel == null) {
			hideView();
			throw new IllegalArgumentException("Invalid parent project model.");
		}

		// The existing Groups list 
		final List<GroupsDTO> groups = request.getData(RequestParameter.CONTENT);

		view.getForm().clearAll();
		
		setPageTitle(groupsUpdate == null ? I18N.CONSTANTS.adminGroupsAdd() : I18N.CONSTANTS.adminGroupsEdit());
		view.getSaveButton().setText(groupsUpdate == null ? I18N.CONSTANTS.adminOrgUnitCreateButton() : I18N.CONSTANTS.edit());

		

		void loadContainers(final IsModel parentProjectModel) {

		view.getContainerField().getStore().removeAll();

		if (ClientUtils.isNotEmpty(parentProjectModel.getHasLayoutElements())) {
			for (final AbstractModelDataEntityDTO<?> hasLayout : currentModel.getHasLayoutElements()) {
				view.getContainerField().getStore().add(hasLayout);
			}
		}

		view.getContainerField().getStore().commitChanges();
	}
		// --
		// Form loading
		// --

		if (groupsUpdate != null) {
			view.getNameField().setValue(groupsUpdate.getName());
			view.getContainerField().setValue(groupsUpdate.getContainer());
			view.getPositionField().setValue(groupsUpdate.getPosition());
		} else {
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Callback executed on save button action.
	 */
	private void onSaveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		final String name = view.getNameField().getValue();
		final String container = view.getContainerField().getValue();
		final Number position = view.getPositionField().getValue();
		
		final GroupsDTO groupToSave = new GroupsDTO();
		groupToSave.setId(groupsUpdate != null ? groupsUpdate.getId() : null);
		groupToSave.setName(name);
		
		final Map<String, Object> newGroupsProperties = new HashMap<String, Object>();
		newGroupsProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, parentProjectModel);
		newGroupsProperties.put(AdminUtil.PROP_GRP, groupToSave);
		newGroupsProperties.put(AdminUtil.PROP_GRP_CONTAINER, container);
		newGroupsProperties.put(AdminUtil.PROP_GRP_POSITION, position);

		// Use 'CreateEntity' to obtain a result.
		dispatch.execute(new CreateEntity(ProjectModelDTO.ENTITY_NAME, newGroupsProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminGroupsCreationBox(), I18N.MESSAGES.adminStandardCreationFailureF(I18N.MESSAGES.adminStandardPhase() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null) {
					N10N.warn(I18N.CONSTANTS.adminGroupsCreationBox(), I18N.MESSAGES.adminStandardCreationNullF(I18N.MESSAGES.adminStandardGroups() + " '" + name + "'"));
					return;
				}

				final ProjectModelDTO projectModelUpdated = (ProjectModelDTO) result.getEntity();

				if (groupsUpdate != null) {
					// Update case.
					N10N.infoNotif(I18N.CONSTANTS.adminGroupsCreationBox(),
						I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.MESSAGES.adminStandardGroups() + " '" + name + "'"));

				} else {
					// Creation case.
					N10N.infoNotif(I18N.CONSTANTS.adminGroupsCreationBox(),
						I18N.MESSAGES.adminStandardCreationSuccessF(I18N.MESSAGES.adminStandardPhase() + " '" + name + "'"));
				}

				// Sends an update event to notify registered elements.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.GROUPS_UPDATE, projectModelUpdated));

				hideView();
			}

		}, view.getSaveButton());
	}

}
