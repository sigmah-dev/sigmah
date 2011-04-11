package org.sigmah.client.page.project.pivot;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.shared.dto.element.handler.ValueEvent;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

public class HistorySelector implements HasValue<PivotLayout> {

	private Button prevButton;
	private Button nextButton;
	private HandlerManager handlerManager;
	private List<PivotLayout> layouts = new ArrayList<PivotLayout>();
	private PivotLayout value;
	
	public HistorySelector() {
		
		prevButton = new Button(null, IconImageBundle.ICONS.back(), new SelectionListener<ButtonEvent>() {
			
			@Override
			public void componentSelected(ButtonEvent ce) {
				prev();
			}
		});
		
		nextButton = new Button(null, IconImageBundle.ICONS.forward(), new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				next();
			}
		});
		
		prevButton.disable();
		nextButton.disable();
		
		handlerManager = new HandlerManager(this);
		
	}
	
	public List<PivotLayout> getLayouts() {
		return layouts;
	}

	public void setLayouts(List<PivotLayout> layouts) {
		this.layouts = layouts;
	}
	
	private int getValueIndex() {
		int curIndex = layouts.indexOf(value);
		assert curIndex != -1 : "currentValue is not included in list of layouts!";
		return curIndex;
	}

	private void prev() {
		int curIndex = getValueIndex();
		if(curIndex == 0) {
			setValueByIndex(layouts.size()-1);
		} else {
			setValueByIndex(curIndex-1);
		}
		enableButtons();
	}
	
	private void next() {
		int curIndex = getValueIndex();
		if(curIndex != layouts.size()-1) {
			setValueByIndex(curIndex+1);
		} else {
			setValueByIndex(0);
		}
		enableButtons();
	}
	
	private void enableButtons() {
		if(value == null || layouts.isEmpty()) {
			nextButton.disable();
			prevButton.disable();
		} else {
			int curIndex = getValueIndex();
			prevButton.setEnabled(curIndex > 0);
			nextButton.setEnabled(curIndex+1 != layouts.size());
		}
	}

	public Button getPrevButton() {
		return prevButton;
	}

	public void setPrevButton(Button prevButton) {
		this.prevButton = prevButton;
	}

	public Button getNextButton() {
		return nextButton;
	}

	public void setNextButton(Button nextButton) {
		this.nextButton = nextButton;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<PivotLayout> handler) {
		return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	@Override
	public PivotLayout getValue() {
		return value;
	}

	@Override
	public void setValue(PivotLayout value) {
		setValue(value, true);
	}

	@Override
	public void setValue(PivotLayout value, boolean fireEvents) {
		ValueChangeEvent.fireIfNotEqual(this, this.value, value);
		this.value = value;
	}

	public void setValueByIndex(int i) {
		setValue(layouts.get(i));
	}
	
	public void onNewLayout(PivotLayout layout) {
		layouts.add(layout);
		value = layout;
		enableButtons();
	}
}
