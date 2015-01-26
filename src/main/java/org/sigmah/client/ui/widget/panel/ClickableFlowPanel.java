package org.sigmah.client.ui.widget.panel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * A clickable flow panel.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ClickableFlowPanel extends FlowPanel implements HasClickHandlers {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

}
