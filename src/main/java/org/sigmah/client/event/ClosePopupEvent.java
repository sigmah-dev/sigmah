package org.sigmah.client.event;

import org.sigmah.client.event.handler.ClosePopupHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when a popup is closed.
 * 
 * @author Raphaël GRENIER (rgrenier@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ClosePopupEvent extends GwtEvent<ClosePopupHandler> {

	private static Type<ClosePopupHandler> TYPE;

	public static Type<ClosePopupHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ClosePopupHandler>();
		}
		return TYPE;
	}

	public void closePopup() {
		// Does nothing by default.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<ClosePopupHandler> getAssociatedType() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(ClosePopupHandler handler) {
		handler.onClosePopup(this);
	}

}
