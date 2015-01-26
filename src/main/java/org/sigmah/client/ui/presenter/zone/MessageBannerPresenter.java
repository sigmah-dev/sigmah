package org.sigmah.client.ui.presenter.zone;

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
@Singleton
public class MessageBannerPresenter extends AbstractZonePresenter<MessageBannerPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(MessageBannerView.class)
	public static interface View extends ViewInterface {

		Panel getMessagePanel();

		void setMessage(String html, MessageType type);

	}

	@Inject
	public MessageBannerPresenter(View view, Injector injector) {
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
