package org.sigmah.client;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.orgunit.OrgUnitState;
import org.sigmah.client.page.project.ProjectState;
import org.sigmah.shared.Cookies;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * The help window.
 * 
 * @author tmi
 */
public class SigmahHelpWindow {

    /**
     * The manuals directory.
     */
    private static final String MANUALS_DIR = "manuals";

    /**
     * The manual HTML file name.
     */
    private static final String HTML_MANUAL_FILE = "manual.html";

    /**
     * The pop up window.
     */
    private final Window window;

    /**
     * The content.
     */
    private final IFrameElement iframe;

    /**
     * Builds the help window.
     */
    private SigmahHelpWindow() {

        // The content.
        iframe = IFrameElement.as(DOM.createIFrame());
        iframe.addClassName("help-content");

        final SimplePanel panel = new SimplePanel();
        panel.setWidth("100%");
        panel.getElement().appendChild(iframe);

        // Main window panel.
        final ScrollPanel sp = new ScrollPanel(panel);
        sp.setAlwaysShowScrollBars(false);
        sp.setWidth("100%");

        // Main window panel.
        final ContentPanel mainPanel = new ContentPanel();
        mainPanel.setHeaderVisible(false);
        mainPanel.setLayout(new FitLayout());
        mainPanel.add(sp);

        // Window.
        window = new Window();
        window.setHeading(I18N.CONSTANTS.help());
        window.setSize(700, 557);
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
        window.add(mainPanel);
    }

    /**
     * Singleton.
     */
    private static SigmahHelpWindow instance;

    /**
     * Builds the unique instance of this class if necessary.
     */
    private static void ensureInstance() {
        if (instance == null) {
            instance = new SigmahHelpWindow();
        }
    }

    /**
     * Shows the help window.
     * 
     * @param pageState
     *            The current page state.
     */
    public static void show(PageState pageState) {

        ensureInstance();

        instance.window.hide();

        // Builds the anchor.
        final PageId pageId;
        if (pageState instanceof ProjectState) {
            final ProjectState state = (ProjectState) pageState;
            pageId = state.getManualPageId();
        } else if (pageState instanceof OrgUnitState) {
            final OrgUnitState state = (OrgUnitState) pageState;
            pageId = state.getManualPageId();
        } else if (pageState instanceof AdminPageState) {
            final AdminPageState state = (AdminPageState) pageState;
            pageId = state.getManualPageId();
        } else {
            pageId = pageState.getPageId();
        }

        // Builds manual URL.
        final String manualURL = buildManualURL(Cookies.getUserLocale());

        // Tries to reach the manual index file.
        final RequestBuilder rb = new RequestBuilder(RequestBuilder.GET, manualURL);
        rb.setCallback(new RequestCallback() {

            @Override
            public void onResponseReceived(Request request, Response response) {

                // Success.
                if (response.getStatusCode() == 200) {
                    show(manualURL, pageId);
                }
                // Missing manual for this locale.
                else if (response.getStatusCode() == 404) {

                    MessageBox.alert(I18N.CONSTANTS.manualOpeningErrorTitle(), I18N.CONSTANTS.manualExistingError(),
                            new Listener<MessageBoxEvent>() {

                                @Override
                                public void handleEvent(MessageBoxEvent be) {
                                    show(buildManualURL("fr"), pageId);
                                }
                            });
                } else {
                    MessageBox.alert(I18N.CONSTANTS.manualOpeningErrorTitle(), I18N.CONSTANTS.manualOpeningError(),
                            null);
                }

            }

            @Override
            public void onError(Request request, Throwable exception) {
                MessageBox.alert(I18N.CONSTANTS.manualOpeningErrorTitle(), I18N.CONSTANTS.manualOpeningError(), null);
            }
        });

        try {
            rb.send();
        } catch (RequestException e) {
            MessageBox.alert(I18N.CONSTANTS.manualOpeningErrorTitle(), I18N.CONSTANTS.manualOpeningError(), null);
        }
    }

    private static String buildManualURL(String locale) {

        final StringBuilder sb = new StringBuilder();
        sb.append(MANUALS_DIR);
        sb.append("/");
        sb.append(locale);
        sb.append("/");
        sb.append(HTML_MANUAL_FILE);

        return sb.toString();
    }

    private static void show(String manualURL, PageId anchor) {

        if (anchor != null) {
            manualURL += "#" + anchor;
        }

        instance.iframe.setSrc(manualURL);
        instance.window.show();
    }
}
