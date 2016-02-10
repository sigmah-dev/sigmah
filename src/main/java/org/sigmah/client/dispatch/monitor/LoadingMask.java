package org.sigmah.client.dispatch.monitor;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.Loadable;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;

/**
 * <p>
 * Uses a GXT loading mask on a component to keep the user updated on the progress of an asynchronous call.
 * </p>
 * <p>
 * The monitor allows a limited number of retries (defaults to two) before giving up.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LoadingMask implements Loadable {

	private final String loadingText;
	private final Component component;

	private boolean loading;

	/**
	 * <p>
	 * Initializes a new loading mask for the given {@code component}.
	 * </p>
	 * <p>
	 * Default loading text will be used. To specify custom loading text, see {@link #LoadingMask(Component, String)}.
	 * </p>
	 * 
	 * @param component
	 *          The GXT component.
	 */
	public LoadingMask(final Component component) {
		this(component, I18N.CONSTANTS.loading());
	}

	/**
	 * Initializes a new loading mask for the given {@code component} with the given {@code loadingText}.
	 * 
	 * @param component
	 *          The GXT component.
	 * @param loadingText
	 *          The custom loading text.
	 */
	public LoadingMask(final Component component, final String loadingText) {
		this.component = component;
		this.loadingText = loadingText;
	}

	protected final Component getComponent() {
		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setLoading(final boolean loading) {

		this.loading = loading;

		if (loading) {
			mask();

		} else {
			unmask();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isLoading() {
		return loading;
	}

	protected final void mask() {

		if (component == null) {
			return;
		}

		if (component.isRendered()) {
			// If the component is already rendered, apply mask immediately.
			handleRenderEvent(null, null);

		} else {
			// If the component is not rendered yet, wait until after it is all layed out before applying the mask.
			final EventType eventType = component instanceof Container ? Events.AfterLayout : Events.Render;
			component.addListener(eventType, new Listener<ComponentEvent>() {

				@Override
				public void handleEvent(final ComponentEvent be) {
					handleRenderEvent(eventType, this);
				}
			});
		}
	}

	protected final void unmask() {

		if (component == null) {
			return;
		}

		if (component.isRendered()) {
			component.unmask();
			onUnmask();
		}
	}

	private void handleRenderEvent(final EventType eventType, final Listener<? extends BaseEvent> listener) {

		if (isLoading()) {
			// If the call is still in progress, apply the mask.
			component.el().mask(loadingText);
		}

		if (listener != null && eventType != null) {
			// If a listener has been registered, remove it.
			component.removeListener(eventType, listener);
		}

		onMask();
	}

	/**
	 * Method called when the component is masked.<br>
	 * <em>Default implementation does nothing.</em>
	 */
	protected void onMask() {
		// Default implementation does nothing.
	}

	/**
	 * Method called when the component is unmasked.<br>
	 * <em>Default implementation does nothing.</em>
	 */
	protected void onUnmask() {
		// Default implementation does nothing.
	}
}
