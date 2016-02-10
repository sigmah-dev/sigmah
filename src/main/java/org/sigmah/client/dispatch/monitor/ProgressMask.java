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

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ProgressBar;

/**
 * Uses a GXT loading mask (with a progress bar) on a component to keep the user updated on the progress of an
 * asynchronous call.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ProgressMask extends LoadingMask {

	/**
	 * Progress bar style name.
	 */
	private static final String PROGRESS_BAR_STYLE = "monitor-progress-bar";

	private final ProgressBar bar;
	private int counter;
	private Integer max;

	public ProgressMask(final Component component) {

		super(component, null);

		this.bar = buildBar(new Listener<ComponentEvent>() {

			@Override
			public void handleEvent(final ComponentEvent be) {

				if (bar.getValue() >= 1) {
					ProgressMask.super.unmask();
				}

			}
		});
	}

	private static ProgressBar buildBar(final Listener<ComponentEvent> listener) {

		final ProgressBar bar = new ProgressBar();
		bar.addStyleName(PROGRESS_BAR_STYLE);

		// When the bar is complete, the monitor is disabled.
		if (listener != null) {
			bar.addListener(Events.Update, listener);
		}

		return bar;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMask() {
		bar.render(getComponent().el().dom);
		bar.el().center(getComponent().el().dom);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onUnmask() {
		bar.el().remove();
	}

	/**
	 * Sets the number of elements.
	 * 
	 * @param max
	 *          The number of elements.
	 */
	public void initCounter(final int max) {
		this.max = max;
		updateProgressBar();
	}

	/**
	 * Add a loaded element.
	 */
	public void increment() {
		increment(1);
	}

	/**
	 * Add n loaded elements.
	 * 
	 * @param count
	 *          The number of loaded elements.
	 */
	public void increment(int count) {
		counter += count;
		updateProgressBar();
	}

	/**
	 * Update the progress bar state.
	 */
	private void updateProgressBar() {
		if (max == null) {
			bar.updateProgress(1, I18N.CONSTANTS.loading());

		} else {
			bar.updateProgress(new Double(counter) / new Double(max), counter + " / " + max + " " + I18N.CONSTANTS.refreshProjectListProjectLoaded() + ".");
		}
	}
}
