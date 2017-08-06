package org.sigmah.client.ui.view;

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

import java.util.Collection;
import java.util.HashSet;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.ApplicationPresenter;
import org.sigmah.client.ui.presenter.zone.AppLoaderPresenter;
import org.sigmah.client.ui.presenter.zone.AuthenticationBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MenuBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MessageBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OfflineBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OrganizationBannerPresenter;
import org.sigmah.client.ui.presenter.zone.SearchPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.HasPageMessage;
import org.sigmah.client.ui.view.zone.MessageBannerView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Singleton;

/**
 * <p>
 * Application frame view.
 * </p>
 * <p>
 * This is the only <em>view</em> that does not inherit {@link AbstractView}.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Claire Yang (cyang@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class ApplicationView implements ApplicationPresenter.View, HasPageMessage {

	/**
	 * <p>
	 * Application viewport displaying {@code com.extjs.gxt.ui} components.
	 * </p>
	 * <p>
	 * Slightly edited version of {@link com.extjs.gxt.ui.client.widget.Viewport} that do not necessarily span to the
	 * entire page dimensions.
	 * A simple inheritance is not possible due to {@link #onAttach()} method implementation.
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	private static class ApplicationViewport extends Viewport {

		/**
		 * The widgets which dimensions should be taken in account during viewport resizing.
		 */
		private final Collection<Widget> widgets;

		private Integer calculatedWidth;
		private Integer calculatedHeight;

		/**
		 * Initializes the application viewport.
		 * 
		 * @param widgets
		 *          The widgets which dimensions should be taken in account during viewport resizing.
		 */
		public ApplicationViewport(final Collection<Widget> widgets) {
			super();

			this.widgets = widgets;

			setLayout(new FitLayout());
			syncSize();
			setBorders(false);
		}

		/**
		 * <p>
		 * {@inheritDoc}
		 * </p>
		 * <p>
		 * Takes care of {@link #widgets} heights and {@link ApplicationView#contentPanel} paddings during update.
		 * </p>
		 */
		@Override
		public void setSize(final int width, final int height) {

			int extraHeight = 0;
			int extraWidth = 0;

			// Calculating dynamically extra dimensions (in pixels).
			if (widgets != null && !isCalculatedSize(width, height)) {
				for (final Widget widget : widgets) {
					if (widget == null || !widget.isAttached()) {
						continue;
					}
					extraHeight += widget.getOffsetHeight();
				}

				extraHeight += CONTENT_PADDING_TOP + CONTENT_PADDING_BOTTOM;
				extraWidth += CONTENT_PADDING_LEFT + CONTENT_PADDING_RIGHT;
			}

			// Updating viewport size.
			calculatedWidth = width - extraWidth;
			calculatedHeight = height - extraHeight;
			super.setSize(calculatedWidth, calculatedHeight);
		}

		/**
		 * Is the given size equals to last calculated size?
		 * 
		 * @param width
		 *          The received width.
		 * @param height
		 *          The received height.
		 * @return {@code true} if the given size equals to last calculated size.
		 */
		private boolean isCalculatedSize(final int width, final int height) {
			return new Integer(width).equals(calculatedWidth) && new Integer(height).equals(calculatedHeight);
		}
	}

	// Content panel dynamic padding values.
	private static final int CONTENT_PADDING_TOP = 10;
	private static final int CONTENT_PADDING_BOTTOM = CONTENT_PADDING_TOP;
	private static final int CONTENT_PADDING_LEFT = 15;
	private static final int CONTENT_PADDING_RIGHT = CONTENT_PADDING_LEFT;

	/**
	 * <p>
	 * Header area widgets which height should be taken in account for viewport dimensions.
	 * </p>
	 * <p>
	 * Make sure to update {@link #viewport} dimensions if one of the referenced widgets dimensions is dynamically
	 * changed.
	 * </p>
	 */
	private final Collection<Widget> headerWidgets;

	private Panel panel;
	private Panel headerPanel;
	private Panel headerLeftPanel;
	private Panel headerMenuPanel;
	private Panel headerMiddlePanel;
	private Panel headerMiddleRightPanel;
	private Panel headerRightPanel;
	private Panel menuPanel;
	private Panel messagePanel;
	private Panel pageMessagePanel;
	private HTML pageMessageLabel;
	private Panel contentPanel;
	private ApplicationViewport viewport;

	private Anchor creditsMenu;
	private Anchor bugReportMenu;
	private Anchor helpMenu;

	/**
	 * Instantiates the application frame.
	 */
	public ApplicationView() {

		buildView();

		headerWidgets = new HashSet<Widget>();
		headerWidgets.add(headerPanel);
		headerWidgets.add(menuPanel);
		headerWidgets.add(messagePanel);

		// Root panel initialization.
		RootPanel.get().add(this);

		// Viewport initialization (after widgets build).
		viewport = new ApplicationViewport(headerWidgets);
		contentPanel.add(viewport);

	}

	/**
	 * Builds the view.
	 */
	private void buildView() {

		// --
		// Header.
		// --

		headerLeftPanel = new FlowPanel();
		headerLeftPanel.getElement().setId("header-left");

		headerMiddlePanel = new FlowPanel();
		headerMiddlePanel.getElement().setId("header-middle");
		
		headerMiddleRightPanel = new HorizontalPanel();
		headerMiddleRightPanel.getElement().setId("header-middle-right");

		headerRightPanel = new FlowPanel();
		headerRightPanel.getElement().setId("header-right");

		headerPanel = new FlowPanel();
		headerPanel.getElement().setId("header");

		headerPanel.add(headerLeftPanel);
		headerPanel.add(headerMiddlePanel);
		headerPanel.add(headerMiddleRightPanel);
		headerPanel.add(headerRightPanel);	

		// Menu.

		creditsMenu = new Anchor(I18N.CONSTANTS.credits());
		bugReportMenu = new Anchor(I18N.CONSTANTS.bugReport());
		helpMenu = new Anchor(I18N.CONSTANTS.help());

		headerMenuPanel = new FlowPanel();
		headerMenuPanel.getElement().setId("header-menu");

		headerMenuPanel.add(creditsMenu);
		headerMenuPanel.add(new InlineLabel(" | "));
		headerMenuPanel.add(bugReportMenu);
		headerMenuPanel.add(new InlineLabel(" | "));
		headerMenuPanel.add(helpMenu);

		// --
		// Tabs.
		// --

		menuPanel = new FlowPanel();
		menuPanel.getElement().setId("menu");

		// --
		// Message.
		// --

		messagePanel = new FlowPanel();
		messagePanel.getElement().setId("app-message");

		pageMessagePanel = new SimplePanel();
		pageMessagePanel.addStyleName(MessageBannerView.CSS_PANEL);
		pageMessagePanel.setVisible(false);

		pageMessageLabel = new HTML();
		pageMessageLabel.addStyleName(MessageBannerView.CSS_MESSAGE);
		pageMessagePanel.add(pageMessageLabel);

		// --
		// Content.
		// --

		contentPanel = new SimplePanel();
		contentPanel.getElement().setId("content");

		// Dynamic paddings.
		contentPanel.getElement().getStyle().setPaddingTop(CONTENT_PADDING_TOP, Unit.PX);
		contentPanel.getElement().getStyle().setPaddingBottom(CONTENT_PADDING_BOTTOM, Unit.PX);
		contentPanel.getElement().getStyle().setPaddingLeft(CONTENT_PADDING_LEFT, Unit.PX);
		contentPanel.getElement().getStyle().setPaddingRight(CONTENT_PADDING_RIGHT, Unit.PX);

		// --
		// Loading screen.
		// --

		// --
		// Main panel.
		// --

		panel = new FlowPanel();
		panel.getElement().setId("application");

		panel.add(headerPanel);
		panel.add(menuPanel);
		panel.add(messagePanel);
		panel.add(contentPanel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return panel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		// Not used (everything is initialized in the constructor).
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Not used.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getLoadables() {
		// Not used.
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFullPage() {
		// Not used.
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void hideLoadingPanel() {

		final RootPanel panel = RootPanel.get("loading");

		if (panel != null) {
			panel.setVisible(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initZones(OrganizationBannerPresenter.View organizationBannerView, AuthenticationBannerPresenter.View authenticationBannerPresenter,
			OfflineBannerPresenter.View offlineBannerPresenter, AppLoaderPresenter.View appLoaderPresenter, MenuBannerPresenter.View menuBannerPresenter,
			MessageBannerPresenter.View messageBannerPresenter, SearchPresenter.View searchView) {

		// Organization logo.
		headerLeftPanel.add(organizationBannerView.getLogoPanel());

		// User name.
		headerMiddlePanel.add(authenticationBannerPresenter.getNamePanel());

		// Organization name.
		headerMiddlePanel.add(organizationBannerView.getNamePanel());
		
		//Search Bar panel
		headerMiddleRightPanel.add(searchView.getSearchBarPanel());

		// User name.
		headerRightPanel.add(authenticationBannerPresenter.getLogoutPanel());

		// Header menu.
		headerRightPanel.add(headerMenuPanel);

		// Offline status.
		headerRightPanel.add(offlineBannerPresenter.getStatusPanel());

		// Loader.
		headerRightPanel.add(appLoaderPresenter.getLoaderPanel());

		// Menu.
		menuPanel.add(menuBannerPresenter.getMenuPanel());
		final HTML clear = new HTML();
		clear.getElement().getStyle().setProperty("clear", "both");
		menuPanel.add(clear); // Ensures 'menuPanel' proper height in DOM tree.

		// Application message.
		messagePanel.add(messageBannerPresenter.getMessagePanel());
		messagePanel.add(pageMessagePanel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showPresenter(final IsWidget presenterWidget, boolean fullPage) {

		viewport.removeFromParent();

		if (fullPage) {
			// View is displayed on entire page.
			viewport = new ApplicationViewport(null);
			RootPanel.get().add(viewport);

		} else {
			// View is displayed within content panel.
			viewport = new ApplicationViewport(headerWidgets);
			contentPanel.add(viewport);
		}
		
		viewport.add(presenterWidget.asWidget());
		viewport.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getCreditsHandler() {
		return creditsMenu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getBugReportHandler() {
		return bugReportMenu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getHelpHandler() {
		return helpMenu;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateViewportSize() {
		viewport.setSize(Window.getClientWidth(), Window.getClientHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessageVisible(boolean visible) {
		pageMessagePanel.setVisible(visible);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPageMessage(String html) {
		setPageMessageVisible(ClientUtils.isNotBlank(html));
		pageMessageLabel.setHTML(html);
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
		MessageType.applyStyleName(pageMessagePanel, type);
	}

}
