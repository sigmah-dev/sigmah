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
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.element.event.ValueEvent;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

/**
 * CheckboxElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 */
public class CheckboxElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		// Gets the entity name mapped by the current DTO starting from the "server.domain" package name.
		return "element.CheckboxElement";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		final CheckBox checkbox = new CheckBox();
		checkbox.setBoxLabel(getLabel());
		checkbox.setHideLabel(true);

		if (valueResult != null) {
			final String value = valueResult.getValueObject();
			checkbox.setValue(Boolean.parseBoolean(value));
		}

		checkbox.addListener(Events.Change, new CheckBoxListener());

		checkbox.setEnabled(enabled);

		return checkbox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {

		if (result == null || result.getValueObject() == null) {
			return false;
		}

		try {
			final String value = result.getValueObject();
			return value.equalsIgnoreCase("true");
		} catch (ClassCastException e) {
			return false;
		}
	}

	private class CheckBoxListener implements Listener<BaseEvent> {

		@Override
		public void handleEvent(BaseEvent be) {
			final CheckBox checkbox = (CheckBox) be.getSource();
			boolean value = checkbox.getValue();

			handlerManager.fireEvent(new ValueEvent(CheckboxElementDTO.this, String.valueOf(value)));

			// Required element ?
			if (getValidates()) {
				handlerManager.fireEvent(new RequiredValueEvent(value));
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {

		ensureHistorable();

		final CheckBox c = new CheckBox();
		c.setHeight(16);
		c.setReadOnly(true);
		c.setValue(Boolean.valueOf(token.getTokens().get(0).getValue()));
		return c;
	}

	@Override
	public String toHTML(String value) {
		if(value == null) {
			return "";
		}
		
		return new StringBuilder("<input type=\"checkbox\" readonly=\"readonly\" checked=\"")
			.append(Boolean.parseBoolean(value) ? "checked" : "")
			.append("\">").toString();
	}
	
}
