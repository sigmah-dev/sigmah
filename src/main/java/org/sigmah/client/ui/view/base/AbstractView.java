package org.sigmah.client.ui.view.base;

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

import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.layout.Layouts;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * <p>
 * Default abstract view.<br/>
 * The abstract view uses a default {@link BorderLayout} container to layout its component(s).
 * </p>
 * <p>
 * <b>Rules to add a new view:</b>
 * <ol>
 * <li>Define a new presenter managing the view ; see {@link AbstractPresenter} javadoc.</li>
 * <li>Create a new class inheriting {@link AbstractView} with {@link com.google.inject.Singleton} annotation
 * (<u>crucial</u>).</li>
 * <li>View implementation should use {@code AbstractView.add(IsWidget)} or
 * {@code AbstractView.add(IsWidget, BorderLayoutData)} in the {@link #initialize()} method.</li>
 * </ol>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public abstract class AbstractView implements ViewInterface {

	/**
	 * View {@code GXT} parent container.
	 */
	protected final LayoutContainer layoutContainer;

	/**
	 * Initializes the view parent container with a border layout.
	 */
	protected AbstractView() {
		layoutContainer = Layouts.border();
	}

	/**
	 * Adds the given {@code widget} into the <b>center</b> region of the parent layout.<br/>
	 * To specify another region, see {@link #add(IsWidget, BorderLayoutData)}.
	 * 
	 * @param widget
	 *          The widget to add.
	 * @see com.extjs.gxt.ui.client.Style.LayoutRegion#CENTER
	 */
	protected final void add(final IsWidget widget) {
		this.add(widget, null);
	}

	/**
	 * Adds the given {@code widget} into the parent layout.
	 * 
	 * @param widget
	 *          The widget to add.
	 * @param layoutData
	 *          The layout data. If {@code null}, default center layout data is set.
	 * @see com.extjs.gxt.ui.client.widget.layout.BorderLayoutData
	 */
	protected final void add(final IsWidget widget, BorderLayoutData layoutData) {

		if (layoutData == null) {
			layoutData = Layouts.borderLayoutData(LayoutRegion.CENTER);
		}

		this.layoutContainer.add(Widget.asWidgetOrNull(widget), layoutData);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return layoutContainer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getLoadables() {
		// Can be overridden by sub views.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Default implementation does nothing.
		// Can be overridden by sub views implementations.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFullPage() {
		// Default implementation returns false.
		return false;
	}

}
