package org.sigmah.client.ui.widget.popup;

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

import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.ClosePopupEvent;
import org.sigmah.client.event.handler.ClosePopupHandler;
import org.sigmah.client.ui.view.zone.MessageBannerView;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

import com.extjs.gxt.ui.client.event.WindowEvent;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Default popup view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class PopupWidget implements IsPopupWidget, IsWidget {

	// CSS.
	private static final String CSS_MAIN_PANEL = "popup-content";

	private final Window window;
	private final ContentPanel contentPanel;
	private final boolean customLayout;

	private final SimplePanel messagePanel;
	private final HTML message;
	private WindowListener windowListener;

	private final LoadingMask loadingMask;
	
	public PopupWidget(boolean modal) {
		this(modal, true, null);
	}

	public PopupWidget(boolean modal, boolean closable) {
		this(modal, closable, null);
	}

	public PopupWidget(boolean modal, Layout layout) {
		this(modal, true, layout);
	}

	public PopupWidget(boolean modal, boolean closable, Layout layout) {

		final Layout nullSafeLayout = layout != null ? layout : new FlowLayout();
		customLayout = !(nullSafeLayout instanceof FlowLayout);

		// GXT window.
		this.window = new Window() {

			@Override
			protected void onRender(Element parent, int pos) {
				super.onRender(parent, pos);
				getBody().addStyleName(CSS_MAIN_PANEL);
			}
		};
		window.setAutoHide(!modal);
		window.setPlain(modal);
		window.setModal(modal);
		window.setBlinkModal(false);
		window.setClosable(closable);
		window.setLayout(nullSafeLayout);

		setWidth("400px");
		setHeight(null);

		// Message (dynamically inserted).

		messagePanel = new SimplePanel();
		messagePanel.setStyleName(MessageBannerView.CSS_PANEL);
		messagePanel.setVisible(false);

		message = new HTML();
		message.addStyleName(MessageBannerView.CSS_MESSAGE);
		messagePanel.add(message);

		// Content panel.

		contentPanel = Panels.content(null, new FlowLayout());

		// Loading mask.

		loadingMask = new LoadingMask(window);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return window;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void center() {
		show();
		window.center();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		window.show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hide() {
		window.hide();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStyleName(String styleName) {
		window.setStyleName(styleName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addStyleName(String styleName) {
		window.addStyleName(styleName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeStyleName(String styleName) {
		window.removeStyleName(styleName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTitle(String title) {
		window.setHeadingHtml(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setContent(Widget widget) {
		if (customLayout) {
			// TODO Try to fix custom layout display with popup message.
			window.add(widget);

		} else {
			contentPanel.add(widget);
			window.add(messagePanel);
			window.add(contentPanel);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(String width) {
		if (ClientUtils.isNotBlank(width)) {
			window.setAutoWidth(false);
			window.setWidth(width);
		} else {
			window.setAutoWidth(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(String height) {
		if (ClientUtils.isNotBlank(height)) {
			window.setAutoHeight(false);
			window.setHeight(height);
		} else {
			window.setAutoHeight(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setZIndex(int zIndex) {
		window.setZIndex(zIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClosePopupHandler(final ClosePopupHandler handler) {

		if (windowListener != null) {
			window.removeWindowListener(windowListener);
			windowListener = null;
		}

		if (handler == null) {
			return;
		}

		windowListener = new WindowListener() {

			@Override
			public void windowHide(WindowEvent we) {
				handler.onClosePopup(new ClosePopupEvent());
			}

		};

		window.addWindowListener(windowListener);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessageVisible(boolean visible) {
		messagePanel.setVisible(visible);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessage(String html) {
		setPageMessageVisible(ClientUtils.isNotBlank(html));
		message.setHTML(html);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessage(String html, MessageType type) {
		setPageMessage(html);
		setPageMessageType(type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessageType(MessageType type) {
		MessageType.applyStyleName(messagePanel, type);
		MessageType.applyStyleName(window, type);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLoading() {
		return loadingMask.isLoading();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(boolean loading) {
		loadingMask.setLoading(loading);
	}

	/**
	 * Add the given button to the window backing this popup.
	 * @param button Button to add to the popup.
	 */
	public void addButton(Button button) {
		window.addButton(button);
	}
}
