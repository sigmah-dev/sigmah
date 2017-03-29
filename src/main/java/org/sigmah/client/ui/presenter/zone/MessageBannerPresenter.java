package org.sigmah.client.ui.presenter.zone;

import org.sigmah.client.ClientFactory;

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

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.MessageBannerView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.MessageType;

import com.google.gwt.user.client.ui.Panel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Message banner presenter displaying application main message.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MessageBannerPresenter extends AbstractZonePresenter<MessageBannerPresenter.View> {

	/**
	 * View interface.
	 */
	public static interface View extends ViewInterface {

		Panel getMessagePanel();

		void setMessage(String html, MessageType type);

	}


	public MessageBannerPresenter(View view, ClientFactory injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.MESSAGE_BANNER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(final ZoneRequest zoneRequest) {

		// Retrieves current message.
		final String message = zoneRequest.getData(RequestParameter.CONTENT);
		final MessageType type = zoneRequest.getData(RequestParameter.TYPE);

		view.setMessage(message, type);
		eventBus.fireEvent(new UpdateEvent(UpdateEvent.VIEWPORT_SIZE_UPDATE));

	}

}
