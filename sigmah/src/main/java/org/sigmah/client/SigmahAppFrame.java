/*
 * All Sigmah code is released under the GNU General Public License v3 See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client;

import java.util.Date;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.event.NavigationEvent;
import org.sigmah.client.event.NavigationEvent.NavigationError;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.SigmahAuthProvider;
import org.sigmah.client.offline.sigmah.DispatchOperator;
import org.sigmah.client.offline.sigmah.OfflineLabelFactory;
import org.sigmah.client.offline.sigmah.OnlineMode;
import org.sigmah.client.page.Frame;
import org.sigmah.client.page.HasTab;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.TabPage;
import org.sigmah.client.page.common.widget.LoadingPlaceHolder;
import org.sigmah.client.page.dashboard.DashboardPageState;
import org.sigmah.client.page.login.LoginView;
import org.sigmah.client.ui.CreditFrame;
import org.sigmah.client.ui.SigmahViewport;
import org.sigmah.client.ui.Tab;
import org.sigmah.client.ui.TabBar;
import org.sigmah.client.ui.TabBar.TabAction;
import org.sigmah.client.ui.TabModel;
import org.sigmah.shared.command.GetApplicationInfo;
import org.sigmah.shared.command.GetHostServerInfo;
import org.sigmah.shared.command.result.ApplicationInfo;
import org.sigmah.shared.command.result.HostServerInfo;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.http.client.URL;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Main frame of Sigmah.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SigmahAppFrame implements Frame {

    public static final int HEADER_DEFAULT_HEIGHT = 90;

    private Page activePage;

    private SigmahViewport view;

    private PageState activePageState;

    @Inject
    public SigmahAppFrame(final EventBus eventBus, final Authentication auth, final TabModel tabModel, final Dispatcher dispatcher, final UserLocalCache cache, final OnlineMode onlineMode) {

        if (auth == null) {
            RootPanel.get().add(new LoginView());
            RootPanel.get("loading").getElement().removeFromParent();

        } else {

            // Init cache first.
            cache.init();

            // The user is already logged in
            RootPanel.get("username").add(new Label(auth.getEmail()));

            final Anchor reportButton = new Anchor(I18N.CONSTANTS.bugReport());

            dispatcher.execute(new GetHostServerInfo(), null, new AsyncCallback<HostServerInfo>() {

                @Override
                public void onFailure(Throwable caught) {
                    // Do nothing

                }

                @Override
                public void onSuccess(HostServerInfo result) {

                    configureReportAnchor(reportButton, auth, result.getHostUrl());

                }

            });

            RootPanel.get("bugreport").add(reportButton);

            final Anchor helpButton = new Anchor(I18N.CONSTANTS.help());
            helpButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    SigmahHelpWindow.show(activePageState);
                }
            });
            RootPanel.get("help").add(helpButton);

            // Logout action
            final Anchor logoutButton = new Anchor(I18N.CONSTANTS.logout());

            if (RootPanel.get("logout") != null) {
                RootPanel.get("logout").add(logoutButton);
            }

            // Offline
            if (dispatcher instanceof DispatchOperator && Factory.getInstance() != null)
                RootPanel.get("offline-status").add(
                    OfflineLabelFactory.getLabel((DispatchOperator) dispatcher, onlineMode));

            // Credit
            final Anchor creditButton = new Anchor(I18N.CONSTANTS.credits());
            creditButton.addClickHandler(new ClickHandler() {

                boolean initalized = false;

                @Override
                public void onClick(ClickEvent event) {

                    if (initalized) {
                        CreditFrame.show();
                    } else {
                        dispatcher.execute(new GetApplicationInfo(), null, new AsyncCallback<ApplicationInfo>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                // nothing.
                            }

                            @Override
                            public void onSuccess(ApplicationInfo result) {
                                CreditFrame.init(result);
                                CreditFrame.show();
                            }
                        });
                    }
                }
            });

            if (RootPanel.get("credit") != null) {
                RootPanel.get("credit").add(creditButton);
            }

            // Tab bar
            final TabBar tabBar = new TabBar(tabModel, eventBus);
            activePageState = new DashboardPageState();
            final Tab dashboardTab = tabModel.add(I18N.CONSTANTS.dashboard(), activePageState, false);
            tabBar.addTabStyleName(tabModel.indexOf(dashboardTab), "home");

            final RootPanel tabs = RootPanel.get("tabs");
            tabs.add(tabBar);

            eventBus.addListener(NavigationHandler.NavigationAgreed, new Listener<NavigationEvent>() {

                @Override
                public void handleEvent(NavigationEvent be) {
                    if (be.getParentObject() instanceof TabBar
                        && be.getNavigationError() == NavigationError.NONE
                        && be.getParentEvent().getNavigationError() == NavigationError.WORK_NOT_SAVED) {
                        if (tabBar.getLastAction() == TabAction.REMOVE) {
                            final Tab currentTab = tabModel.get(tabBar.getSelectedIndex());
                            tabModel.remove(currentTab);
                        } else if (tabBar.getLastAction() == TabAction.NAVIGATE) {
                            final Tab targetTab = tabModel.get(be.getPlace());
                            NavigationError navigationError = tabBar.displayTab(targetTab);
                            if (navigationError == NavigationError.NONE) {
                                tabBar.setSelectedIndex(tabModel.indexOf(targetTab));
                            }
                        }
                    } else if (be.getParentObject() == logoutButton
                        && be.getNavigationError() == NavigationError.NONE
                        && be.getParentEvent().getNavigationError() == NavigationError.WORK_NOT_SAVED) {
                        logOut();
                    } else {
                        final PageState state = be.getPlace();
                        activePageState = state;
                        final String title;
                        if (state instanceof TabPage)
                            title = ((TabPage) state).getTabTitle();
                        else
                            title = I18N.CONSTANTS.title();

                        final Tab targetTab = tabModel.add(title, be.getPlace(), true);

                        if (state instanceof HasTab)
                            ((HasTab) state).setTab(targetTab);
                    }

                }
            });

            // ClickHandler for the logout button
            logoutButton.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    Tab targetTab = null;
                    if (tabBar.getSelectedIndex() > 0) {
                        targetTab = tabModel.get(tabBar.getSelectedIndex() - 1);
                    } else if (tabModel.size() > 1) {
                        targetTab = tabModel.get(tabBar.getSelectedIndex() - 1);
                    } // else the actual page is the main page that does not require to verify if there are data that
                      // should be saved

                    if (targetTab != null) {
                        NavigationEvent navigationEvent =
                                new NavigationEvent(NavigationHandler.NavigationRequested, targetTab.getState(), null,
                                    logoutButton);
                        eventBus.fireEvent(navigationEvent);
                        if (navigationEvent.getNavigationError() == NavigationError.NONE) {
                            logOut();
                        }
                    } else {
                        logOut();
                    }
                }
            });

            int clutterHeight = getDecorationHeight(HEADER_DEFAULT_HEIGHT);

            // Configure Ext-GWT viewport
            this.view = new SigmahViewport(0, clutterHeight);
            this.view.setLayout(new FitLayout());
            this.view.syncSize();
            this.view.setBorders(true);

            RootPanel.get("content").add(this.view);

            cache.getOrganizationCache().getOrganization(new AsyncCallback<OrganizationDTO>() {

                @Override
                public void onSuccess(OrganizationDTO result) {

                    if (result != null) {
                        // Sets organization parameters.
                        RootPanel.get("orgname").getElement().setInnerHTML(result.getName().toUpperCase());
                        RootPanel
                            .get("orglogo")
                            .getElement()
                            .setAttribute(
                                "style",
                                "background-image: url("
                                    + GWT.getModuleBaseURL()
                                    + "image-provider?"
                                    + FileUploadUtils.IMAGE_URL
                                    + "="
                                    + result.getLogo()
                                    + ")");
                    }
                }

                @Override
                public void onFailure(Throwable e) {
                    Log.error("[execute] Error while getting the organization for user #id " + auth.getUserId() + ".",
                        e);
                }
            });
        }
    }

    private native int getDecorationHeight(int defaultHeight) /*-{
		var height = 0;

		if (!$wnd.document.getElementsByClassName && !$wnd.getComputedStyle)
			return defaultHeight;

		var elements = $wnd.document.getElementsByClassName("decoration");
		for ( var index = 0; index < elements.length; index++) {
			var style = $wnd.getComputedStyle(elements[index], null);
			height += parseInt(style.height) + parseInt(style.borderTopWidth)
					+ parseInt(style.borderBottomWidth)
					+ parseInt(style.marginTop) + parseInt(style.marginBottom)
					+ parseInt(style.paddingTop)
					+ parseInt(style.paddingBottom);
		}

		return height;
    }-*/;

    private void configureReportAnchor(final Anchor reportButton, final Authentication auth, String hostUrl) {

        final String versionNumber =
                Dictionary.getDictionary(SigmahAuthProvider.DICTIONARY_NAME).get(SigmahAuthProvider.VERSION_NUMBER);

        // The current date
        Date now = new Date();
        DateTimeFormat dtf = DateTimeFormat.getFormat("dd-MM-yyyy");
        final String dateString = dtf.format(now);

        String subject = I18N.MESSAGES.bugReportMailObject(auth.getEmail(), hostUrl, dateString);

        final StringBuilder hrefBuilder = new StringBuilder("mailto:");
        hrefBuilder.append(I18N.CONSTANTS.bugReportSupportAddress()).append("?subject=")
            .append(URL.encodeComponent(subject, false)).append("&body=")
            .append(URL.encodeComponent(I18N.MESSAGES.bugReportBody(getUserAgent(), versionNumber), false));

        reportButton.setHref(hrefBuilder.toString());

    }

    private native String getUserAgent() /*-{
		return navigator.userAgent;
    }-*/;

    private void logOut() {
        Cookies.removeCookie(org.sigmah.shared.Cookies.AUTH_TOKEN_COOKIE, "/");
        Window.Location.reload();
    }

    @Override
    public void setActivePage(Page page) {
        final Widget widget = (Widget) page.getWidget();
        view.removeAll();
        view.add(widget);
        view.layout();

        activePage = page;
    }

    @Override
    public Page getActivePage() {
        return activePage;
    }

    @Override
    public AsyncMonitor showLoadingPlaceHolder(PageId pageId, PageState loadingPlace) {
        activePage = null;
        LoadingPlaceHolder placeHolder = new LoadingPlaceHolder();
        return placeHolder;
    }

    @Override
    public PageId getPageId() {
        return null;
    }

    @Override
    public Object getWidget() {
        return view;
    }

    @Override
    public void requestToNavigateAway(PageState place, final NavigationCallback callback) {
        callback.onDecided(NavigationError.NONE);
    }

    @Override
    public String beforeWindowCloses() {
        return null;
    }

    @Override
    public boolean navigate(PageState place) {
        return true;
    }

    @Override
    public void shutdown() {

    }

}
