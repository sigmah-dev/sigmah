package org.sigmah.client.ui.presenter.orgunit;

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
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.page.event.PageRequestEvent;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter.SubPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.shared.command.GetOrgUnit;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;

/**
 * Abstract layer for OrgUnit presenters.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <V>
 *          The view type.
 */
public abstract class AbstractOrgUnitPresenter<V extends AbstractOrgUnitPresenter.View> extends AbstractPagePresenter<V> implements
																																																												SubPresenter<OrgUnitPresenter> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewInterface {

		// No methods yet.

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	protected AbstractOrgUnitPresenter(final V view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitPresenter getParentPresenter() {
		return injector.getOrgUnitPresenter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void executeOnPageRequest(final PageRequestEvent event, final Page page) {

		final PageRequest request = event.getRequest();

		final Integer orgUnitId = request.getParameterInteger(RequestParameter.ID);
		final OrgUnitDTO currentOrgUnit = getParentPresenter().getCurrentOrgUnit();

		if (currentOrgUnit != null && orgUnitId.equals(currentOrgUnit.getId())) {

			if (Log.isDebugEnabled()) {
				Log.debug("OrgUnit #" + orgUnitId + " has already been loaded. No need to load it again.");
			}

			onOrgUnitLoaded(currentOrgUnit, event, page);
			return;
		}

		dispatch.execute(new GetOrgUnit(orgUnitId, null), new CommandResultHandler<OrgUnitDTO>() {

			@Override
			public void onCommandSuccess(final OrgUnitDTO result) {

				if (Log.isDebugEnabled()) {
					Log.debug("OrgUnit #" + orgUnitId + " is not the current loaded OrgUnit. Loading it from server.");
				}

				onOrgUnitLoaded(result, event, page);
			}
		});
	}

	/**
	 * Method executed once OrgUnit has been loaded.
	 * 
	 * @param loadedOrgUnit
	 *          The loaded OrgUnit.
	 * @param event
	 *          The page request event.
	 * @param page
	 *          The accessed page.
	 */
	private void onOrgUnitLoaded(final OrgUnitDTO loadedOrgUnit, final PageRequestEvent event, final Page page) {

		// Stores project instance into local attribute AND parent presenter.
		getParentPresenter().setCurrentOrgUnit(loadedOrgUnit);

		final PageRequest request = event.getRequest();

		// Updates the tab title.
		eventBus.updateZoneRequest(Zone.MENU_BANNER.requestWith(RequestParameter.REQUEST, request).addData(RequestParameter.HEADER, loadedOrgUnit.getName()));

		// Executes child page 'onPageRequest()'.
		afterOnPageRequest(event, page);
	}

	/**
	 * Returns the current {@link OrgUnitDTO}.
	 * 
	 * @return The current {@link OrgUnitDTO}.
	 */
	protected final OrgUnitDTO getOrgUnit() {
		return getParentPresenter().getCurrentOrgUnit();
	}

}
