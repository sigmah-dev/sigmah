package org.sigmah.shared.dto.element;

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

		if (valueResult != null && valueResult.getValueObject() != null) {
			String value = valueResult.getValueObject();
			checkbox.setValue(value.equalsIgnoreCase("true"));
		}

		checkbox.addListener(Events.OnClick, new CheckBoxListener());

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
}
