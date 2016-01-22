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
import org.sigmah.client.ui.view.admin.orgunits.MoveOrgUnitAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.shared.command.MoveOrgUnit;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin move OrgUnit Presenter which manages {@link MoveOrgUnitAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class MoveOrgUnitAdminPresenter extends AbstractPagePresenter<MoveOrgUnitAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(MoveOrgUnitAdminView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		ComboBox<OrgUnitDTO> getParentField();

		Button getMoveButton();

	}

	/**
	 * The OrgUnit to move, should never be {@code null}.
	 */
	private OrgUnitDTO orgUnitToMove;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected MoveOrgUnitAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_MOVE_ORG_UNIT;
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
		view.getMoveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onMoveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		orgUnitToMove = request.getData(RequestParameter.DTO);

		if (orgUnitToMove == null) {
			hideView();
			throw new IllegalArgumentException("Invalid required OrgUnit to move.");
		}

		view.getForm().clearAll();

		setPageTitle(I18N.CONSTANTS.adminOrgUnitMove() + ' ' + orgUnitToMove.getName() + " - " + orgUnitToMove.getFullName());

		loadParents();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the countries from the local cache and populates the corresponding form field.
	 */
	@SuppressWarnings("deprecation")
	private void loadParents() {

		view.getParentField().getStore().removeAll();

		// Retrieves the units.
		injector.getClientCache().getOrganizationCache().get(new AsyncCallback<OrgUnitDTO>() {

			@Override
			public void onFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("Error while retrieving the org units.", caught);
				}
				N10N.warn(I18N.CONSTANTS.adminOrgUnitMoveUnavailable(), I18N.CONSTANTS.adminOrgUnitMoveMissingUnit());
			}

			@Override
			public void onSuccess(final OrgUnitDTO result) {

				recursiveFillOrgUnitsList(result);

				if (view.getParentField().getStore().getCount() == 0) {
					if (Log.isErrorEnabled()) {
						Log.error("No available org unit.");
					}
					N10N.warn(I18N.CONSTANTS.adminOrgUnitMoveUnavailable(), I18N.CONSTANTS.adminOrgUnitMoveMissingUnit());
				}
			}

		});
	}

	/**
	 * Fills recursively the parents form field from the given {@code root} org unit.
	 * 
	 * @param root
	 *          The root org unit.
	 */
	private void recursiveFillOrgUnitsList(final OrgUnitDTO root) {

		if (root == null) {
			return;
		}

		if (!root.equals(orgUnitToMove)) {
			// Org unit to move should not be added.
			view.getParentField().getStore().add(root);
		}

		for (final OrgUnitDTO child : root.getChildrenOrgUnits()) {
			recursiveFillOrgUnitsList(child);
		}
	}

	/**
	 * Callback executed on move button action.
	 */
	private void onMoveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		final Integer newParentId = view.getParentField().getValue().getId();

		dispatch.execute(new MoveOrgUnit(orgUnitToMove.getId(), newParentId), new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.warn(I18N.CONSTANTS.adminOrgUnitMoveFailed(), I18N.CONSTANTS.adminOrgUnitMoveFailedDetails());
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {

				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminOrgUnitMoveSucceed());

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.ORG_UNIT_UPDATE));

				hideView();
			}

		}, view.getMoveButton());
	}

}
