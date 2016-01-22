package org.sigmah.shared.dto.element.event;

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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event transmitted to the {@link org.sigmah.client.ui.presenter.orgunit.OrgUnitPresenter OrgUnitPresenter} when a
 * <b>required</b> flexible element value changes.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class RequiredValueEvent extends GwtEvent<RequiredValueHandler> {

	private final static GwtEvent.Type<RequiredValueHandler> TYPE = new GwtEvent.Type<RequiredValueHandler>();

	private boolean valueOn;

	private boolean immediate;

	public RequiredValueEvent(boolean valueOn) {
		this(valueOn, false);
	}

	public RequiredValueEvent(boolean valueOn, boolean immediate) {
		this.valueOn = valueOn;
		this.immediate = immediate;
	}

	@Override
	protected void dispatch(RequiredValueHandler handler) {
		handler.onRequiredValueChange(this);
	}

	@Override
	public GwtEvent.Type<RequiredValueHandler> getAssociatedType() {
		return TYPE;
	}

	public static Type<RequiredValueHandler> getType() {
		return TYPE;
	}

	public boolean isValueOn() {
		return valueOn;
	}

	public void setValueOn(boolean valueOn) {
		this.valueOn = valueOn;
	}

	public boolean isImmediate() {
		return immediate;
	}

	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}
}
