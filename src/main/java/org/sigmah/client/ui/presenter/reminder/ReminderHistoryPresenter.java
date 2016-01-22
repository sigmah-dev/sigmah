package org.sigmah.client.ui.presenter.reminder;

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

import java.util.List;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.reminder.ReminderHistoryView;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Reminder/Monitored Point presenter which manages the {@link ReminderHistoryView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ReminderHistoryPresenter extends AbstractPagePresenter<ReminderHistoryPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ReminderHistoryView.class)
	public static interface View extends ViewInterface {

		void setData(List<? extends AbstractModelDataEntityDTO<?>> dataList);

	}

	/**
	 * The edited reminder/monitored point entity DTO.<br>
	 * Set to {@code null} in case of creation.
	 */
	private EntityDTO<Integer> entityDTO;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ReminderHistoryPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.REMINDER_HISTORY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// --
		// Reads entity from request (required).
		// --

		entityDTO = request.getData(RequestParameter.DTO);

		if (entityDTO == null) {
			if (Log.isErrorEnabled()) {
				Log.error("Reminder entity is required to show its history.");
			}
			hideView();
			return;
		}

		// --
		// Loads entity history.
		// --

		if (ReminderDTO.ENTITY_NAME.equals(entityDTO.getEntityName())) {
			// Reminder case.
			final ReminderDTO reminder = (ReminderDTO) entityDTO;
			setPageTitle(reminder.getLabel());
			view.setData(reminder.getHistory());

		} else if (MonitoredPointDTO.ENTITY_NAME.equals(entityDTO.getEntityName())) {
			// Monitored point case.
			final MonitoredPointDTO monitoredPoint = (MonitoredPointDTO) entityDTO;
			setPageTitle(monitoredPoint.getLabel());
			view.setData(monitoredPoint.getHistory());

		} else {
			if (Log.isErrorEnabled()) {
				Log.error("Reminder entity type is invalid.");
			}
			hideView();
		}
	}

}
