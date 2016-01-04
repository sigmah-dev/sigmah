package org.sigmah.shared.dto.element;

import org.sigmah.shared.command.result.ValueResult;

import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Html;

/**
 * MessageElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class MessageElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;
	
	private static final String STYLE_MESSAGE_ELEMENT = "messageElement";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		// Gets the entity name mapped by the current DTO starting from the "server.domain" package name.
		return "element.MessageElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		// The label for a message can be considered as HTML code.
		final Html message = new Html(getLabel());
		message.addStyleName(STYLE_MESSAGE_ELEMENT);
		return message;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		// A message element cannot be a required element.
		return false;
	}

}