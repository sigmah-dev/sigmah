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

import java.util.HashSet;
import java.util.Set;

import org.sigmah.client.event.ClosePopupEvent;
import org.sigmah.client.event.handler.ClosePopupHandler;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.popup.IsPopupWidget;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.MessageType;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * Abstract pop-up view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public abstract class AbstractPopupView<P extends IsPopupWidget> implements ViewPopupInterface {

	/**
	 * <p>
	 * Visible pop-ups views.
	 * </p>
	 * <p>
	 * Using a {@link Set} ensures pop-up views references uniqueness.
	 * {@link #equals(Object)} and {@link #hashCode()} methods should not be overridden (declared as {@code final}).
	 * </p>
	 */
	private static final Set<IsPopupWidget> visiblePopups = new HashSet<IsPopupWidget>();

	/**
	 * Returns if a pop-up view is currently visible.
	 * 
	 * @return {@code true} if a pop-up is currently visible.
	 */
	public static final boolean isPopupDisplayed() {
		return !visiblePopups.isEmpty();
	}

	/**
	 * The pop-up implementation.
	 */
	protected final P popup;

	/**
	 * Initializes the popup view.
	 * 
	 * @param popup
	 *          The inner {@link PopupWidget} implementation.
	 */
	protected AbstractPopupView(final P popup) {
		this(popup, (String) null, null);
	}

	/**
	 * Initializes the popup view with given {@code width}.
	 * 
	 * @param popup
	 *          The inner {@link PopupWidget} implementation.
	 * @param width
	 *          The popup view width (in pixels). Ignored if {@code null}.
	 */
	protected AbstractPopupView(final P popup, final Integer width) {
		this(popup, width, null);
	}

	/**
	 * Initializes the popup view with given {@code width}.
	 * 
	 * @param popup
	 *          The inner {@link PopupWidget} implementation.
	 * @param width
	 *          The popup view width. Ignored if {@code null}.
	 */
	protected AbstractPopupView(final P popup, final String width) {
		this(popup, width, null);
	}

	/**
	 * Initializes the popup view with given {@code width} and {@code height}.
	 * 
	 * @param popup
	 *          The inner {@link PopupWidget} implementation.
	 * @param width
	 *          The popup view width (in pixels). Ignored if {@code null}.
	 * @param height
	 *          The popup view height (in pixels). Ignored if {@code null}.
	 */
	protected AbstractPopupView(final P popup, final Integer width, final Integer height) {
		this(popup, width != null ? width + Unit.PX.getType() : null, height != null ? height + Unit.PX.getType() : null);
	}

	/**
	 * Constructor with dimensions parameters.
	 * 
	 * @param popup
	 *          The pop-up widget implementation.
	 * @param width
	 *          The width of the pop-up (ignored if {@code null}).
	 * @param height
	 *          The height of the pop-up (ignored if {@code null}).
	 */
	protected AbstractPopupView(final P popup, final String width, final String height) {

		this.popup = popup;

		if (width != null) {
			this.popup.setWidth(width);
		}

		if (height != null) {
			this.popup.setHeight(height);
		}

		setCloseHandler(new ClosePopupHandler() {

			@Override
			public void onClosePopup(ClosePopupEvent event) {
				event.closePopup();
				visiblePopups.remove(popup);
			}

		});
	}

	/**
	 * Initializes the pop-up widget.
	 * 
	 * @param widget
	 *          The pop-up widget.
	 */
	protected final void initPopup(final Widget widget) {
		popup.setContent(widget);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCloseHandler(final ClosePopupHandler handler) {
		this.popup.setClosePopupHandler(handler);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return popup.asWidget();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getLoadables() {
		// Returns null in default implementation.
		// Can be overridden by sub popup views.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isFullPage() {
		// Should always return false.
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Default implementation does nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void center() {
		if (popup != null) {
			popup.center();
			visiblePopups.add(popup);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hide() {
		if (popup != null) {
			popup.hide();
			visiblePopups.remove(popup);
		}
	}

	public void setStyleName(final String styleName) {
		if (popup != null) {
			popup.setStyleName(styleName);
		}
	}

	public void addStyleName(final String styleName) {
		if (popup != null) {
			popup.addStyleName(styleName);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setPopupTitle(final String title) {
		popup.setTitle(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPopupStyleName(final String style) {
		if (popup != null && style != null) {
			popup.addStyleName(style);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removePopupStyleName(final String style) {
		if (popup != null && style != null) {
			popup.removeStyleName(style);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessageVisible(boolean visible) {
		popup.setPageMessageVisible(visible);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessage(final String html) {
		popup.setPageMessage(html);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessage(final String html, final MessageType type) {
		popup.setPageMessage(html, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessageType(final MessageType type) {
		popup.setPageMessageType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		return super.hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLoading() {
		return popup.isLoading();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(boolean loading) {
		popup.setLoading(loading);
	}

	/**
	 * Retrieves the inner popup widget.
	 * 
	 * @return The implentation used by this popup.
	 */
	public P getPopup() {
		return popup;
	}
}
