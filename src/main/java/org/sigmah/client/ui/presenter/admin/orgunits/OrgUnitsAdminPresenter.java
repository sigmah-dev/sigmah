package org.sigmah.client.ui.presenter.admin.orgunits;

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
import org.sigmah.client.ui.view.admin.orgunits.OrgUnitsAdminView;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.HasTreeGrid.TreeGridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.shared.command.RemoveOrgUnit;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.FunctionalException;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin OrgUnits Presenter which manages {@link OrgUnitsAdminView}.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@SuppressWarnings("deprecation")
@Singleton
public class OrgUnitsAdminPresenter extends AbstractAdminPresenter<OrgUnitsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(OrgUnitsAdminView.class)
	public static interface View extends AbstractAdminPresenter.View, HasTreeGrid<OrgUnitDTO> {

		Button getAddButton();

		Button getMoveButton();

		Button getRemoveButton();

		Component getMainPanel();

	}

	/**
	 * Tree grid refresh delay (in milliseconds).<br>
	 * This delay is a 'trick' to make the tree grid refresh work.
	 */
	private static final int REFRESH_DELAY = 2000;

	/**
	 * The client-side local cache.
	 */
	private final UserLocalCache localCache;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected OrgUnitsAdminPresenter(View view, Injector injector) {
		super(view, injector);
		this.localCache = injector.getClientCache();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_ORG_UNITS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Grid name cell handler.
		// --

		view.setTreeGridEventHandler(new TreeGridEventHandler<OrgUnitDTO>() {

			@Override
			public void onRowClickEvent(final OrgUnitDTO rowElement) {

				if (rowElement == null) {
					return;
				}

				eventBus.navigateRequest(Page.ORGUNIT_DASHBOARD.requestWith(RequestParameter.ID, rowElement.getId()));
			}
		});

		// --
		// On OrgUnit creation/move event.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.ORG_UNIT_UPDATE)) {
					refreshCache();
				}
			}
		}));

		// --
		// Add button handler.
		// --

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {

				final OrgUnitDTO parent = view.getTreeGrid().getSelectionModel().getSelectedItem();

				if (parent == null) {
					return;
				}

				eventBus.navigateRequest(Page.ADMIN_ADD_ORG_UNIT.requestWith(RequestParameter.ID, parent.getId()));
			}
		});

		// --
		// Move button handler.
		// --

		view.getMoveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {

				final OrgUnitDTO moved = view.getTreeGrid().getSelectionModel().getSelectedItem();

				if (moved == null) {
					return;
				}

				eventBus.navigateRequest(Page.ADMIN_MOVE_ORG_UNIT.request().addData(RequestParameter.DTO, moved));
			}
		});

		// --
		// Delete button handler.
		// --

		view.getRemoveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent be) {

				final OrgUnitDTO toDelete = view.getTreeGrid().getSelectionModel().getSelectedItem();

				if (toDelete == null) {
					return;
				}

				onDeleteAction(toDelete);
			}
		});

		// --
		// Tree grid selection change handler.
		// --

		view.getTreeGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<OrgUnitDTO>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<OrgUnitDTO> se) {
				onSelectionChange(se.getSelectedItem());
			}
		});

		// --
		// Tree grid attach listener handler.
		// --

		view.getTreeGrid().addListener(Events.Attach, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				view.getTreeGrid().expandAll();
			}
		});

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		view.getAddButton().setEnabled(false);
		view.getMoveButton().setEnabled(false);
		view.getRemoveButton().setEnabled(false);
		refreshTree();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Callback executed on tree grid selection change.
	 * 
	 * @param selection
	 *          The new selected item.
	 */
	private void onSelectionChange(final OrgUnitDTO selection) {

		final boolean addEnabled = selection != null;
		final boolean moveEnabled = selection != null;
		final boolean removeEnabled = selection != null && selection.getParent() != null && selection.getChildCount() == 0;

		view.getAddButton().setEnabled(addEnabled);
		view.getMoveButton().setEnabled(moveEnabled);
		view.getRemoveButton().setEnabled(removeEnabled);

		view.getRemoveButton().setTitle(" ");
		view.getRemoveButton().removeToolTip();
		if (!removeEnabled && selection != null) {
			if (selection.getParent() == null) {
				view.getRemoveButton().setTitle(I18N.CONSTANTS.adminOrgUnitRemoveIsRoot());
			} else if (selection.getChildCount() != 0) {
				view.getRemoveButton().setTitle(I18N.CONSTANTS.adminOrgUnitRemoveHasChildren());
			}
		}

	}

	/**
	 * Refreshes the local organization cache.<br>
	 * Once it has been refreshed, refreshes the orgunits tree grid.
	 */
	private void refreshCache() {
		localCache.refreshOrganization(new AsyncCallback<OrganizationDTO>() {

			@Override
			public void onFailure(final Throwable caught) {
				refreshTree();
			}

			@Override
			public void onSuccess(final OrganizationDTO result) {
				refreshTree();
			}
		});
	}

	/**
	 * Refreshes the OrgUnits tree grid.
	 */
	private void refreshTree() {

		// Gets user's organization.
		localCache.getOrganizationCache().get(new AsyncCallback<OrgUnitDTO>() {

			@Override
			public void onFailure(final Throwable e) {
				if (Log.isErrorEnabled()) {
					Log.error("An error occured while refreshing the org units tree grid.", e);
				}
				// Digest.
			}

			@Override
			public void onSuccess(final OrgUnitDTO result) {

				if (result == null) {
					return;
				}

				view.getStore().removeAll();
				view.getStore().add(result, true);
				view.getTreeGrid().expandAll();

				new Timer() {

					@Override
					public void run() {
						view.getTreeGrid().expandAll();
					}
				}.schedule(REFRESH_DELAY);
			}
		});
	}

	/**
	 * Callback executed on delete event.
	 * 
	 * @param removed
	 *          The selected item to remove.
	 */
	private void onDeleteAction(final OrgUnitDTO removed) {

		N10N.confirmation(I18N.CONSTANTS.adminOrgUnitRemove(), I18N.MESSAGES.adminOrgUnitRemoveConfirm(removed.getName()), new ConfirmCallback() {

			@Override
			public void onAction() {

				if (removed.getChildCount() != 0) {
					N10N.warn(I18N.CONSTANTS.adminOrgUnitRemoveUnavailable(), I18N.CONSTANTS.adminOrgUnitRemoveHasChildren());
					return;
				}

				if (removed.getParent() == null) {
					N10N.warn(I18N.CONSTANTS.adminOrgUnitRemoveUnavailable(), I18N.CONSTANTS.adminOrgUnitRemoveIsRoot());
					return;
				}

				dispatch.execute(new RemoveOrgUnit(removed.getId()), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.warn(I18N.CONSTANTS.adminOrgUnitRemoveUnavailable(), I18N.CONSTANTS.adminOrgUnitRemoveHasChildrenOrProjects());
						refreshCache();
					}

					@Override
					protected void onFunctionalException(final FunctionalException exception) {
						super.onFunctionalException(exception);
						refreshCache();
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {
						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminOrgUnitRemoveSucceed());
						refreshCache();
					}

				});
			}
		});
	}

}
