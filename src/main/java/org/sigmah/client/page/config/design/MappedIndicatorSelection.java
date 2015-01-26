package org.sigmah.client.page.config.design;

import org.sigmah.shared.dto.IndicatorDTO;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class MappedIndicatorSelection implements HasValue<IndicatorDTO>  {

	private HandlerManager manager = new HandlerManager(this);
	private IndicatorDTO value;
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<IndicatorDTO> handler) {
		return manager.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		manager.fireEvent(event);
	}

	@Override
	public IndicatorDTO getValue() {
		return value;
	}

	@Override
	public void setValue(IndicatorDTO value) {
		this.value = value;
		ValueChangeEvent.fire(this, value);
	}

	@Override
	public void setValue(IndicatorDTO value, boolean fireEvents) {
		this.value = value;
		if(fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

}
