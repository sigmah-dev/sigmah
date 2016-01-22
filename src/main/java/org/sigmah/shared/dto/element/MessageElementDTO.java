package org.sigmah.shared.dto.element;

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