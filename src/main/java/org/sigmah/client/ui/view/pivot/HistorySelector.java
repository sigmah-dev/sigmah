package org.sigmah.client.ui.view.pivot;

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

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import org.sigmah.client.ui.res.icon.IconImageBundle;

public class HistorySelector implements HasValue<Integer> {

	private Button prevButton;
	private Button nextButton;
	private HandlerManager handlerManager;
	private List<PivotLayout> layouts = new ArrayList<PivotLayout>();
	private int curIndex = 0;
	
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
	
	private void prev() {
		setValue(curIndex-1);
		enableButtons();
	}
	
	private void next() {
		setValue(curIndex+1);
		enableButtons();
	}
	
	private void enableButtons() {
		if(layouts.isEmpty()) {
			nextButton.disable();
			prevButton.disable();
		} else {
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
			ValueChangeHandler<Integer> handler) {
		return handlerManager.addHandler(ValueChangeEvent.getType(), handler);
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		handlerManager.fireEvent(event);
	}

	@Override
	public Integer getValue() {
		return curIndex;
	}
	
	public PivotLayout getCurrentLayout() {
		return layouts.isEmpty() ? null : layouts.get(curIndex);
	}

	@Override
	public void setValue(Integer value) {
		setValue(value, true);
	}

	@Override
	public void setValue(Integer value, boolean fireEvents) {
		ValueChangeEvent.fireIfNotEqual(this, this.curIndex, value);
		this.curIndex = value;
		enableButtons();
	}
	
	public void onNewLayout(PivotLayout layout) {
		while(layouts.size() > curIndex+1) {
			layouts.remove(curIndex+1);
		}
		layouts.add(layout);
		curIndex = layouts.size()-1;
		enableButtons();
	}

}
