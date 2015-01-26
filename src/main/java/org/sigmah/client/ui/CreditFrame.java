package org.sigmah.client.ui;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.result.ApplicationInfo;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The credit window.
 * 
 * @author tmi
 * 
 */
public final class CreditFrame {

    private CreditFrame() {
    }

    /**
     * Builds the all window.
     * 
     * @param info
     *            info.
     */
    private static void build() {

        final Widget versionPanel = getVersionPanel();
        final Widget rolesPanel = getManagersPartnersPanel();
        final Widget devConPanel = getDevelopersContributorsPanel();

        // Top-right v panel.
        final VerticalPanel vPanel = new VerticalPanel();
        vPanel.setSpacing(0);

        vPanel.add(rolesPanel);
        vPanel.setCellVerticalAlignment(rolesPanel, HasVerticalAlignment.ALIGN_TOP);
        vPanel.add(devConPanel);
        vPanel.setCellVerticalAlignment(devConPanel, HasVerticalAlignment.ALIGN_TOP);
        vPanel.setWidth("100%");
        
        // Top h panel.
        final HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(0);

        hPanel.add(versionPanel);
        hPanel.setCellVerticalAlignment(versionPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.setCellHorizontalAlignment(versionPanel, HasHorizontalAlignment.ALIGN_CENTER);
        hPanel.setCellWidth(versionPanel, "0");
        versionPanel.getElement().getStyle().setMarginRight(45, Unit.PX);

        hPanel.add(vPanel);
        hPanel.setCellVerticalAlignment(vPanel, HasVerticalAlignment.ALIGN_TOP);
        hPanel.setWidth("100%");
        
        // Main panel.
        final VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setSpacing(10);

        mainPanel.add(hPanel);
        mainPanel.add(getCreditsPanel());

        // Builds the window.
        window = new Window();
        window.setWidth(735);
        window.setHeight(570);
        window.setHeading(I18N.CONSTANTS.credits());
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());

        // Main panel.
        final ContentPanel p = new ContentPanel();
        p.setLayout(new FitLayout());
        p.setHeaderVisible(false);
        p.add(mainPanel);

        window.add(p);
    }

    /**
     * Builds the logo and version panel.
     * 
     * @return The panel.
     */
    private static Widget getVersionPanel() {

        final Image logo = new Image("image/logo.png");

        // Version name.
        final String vName = info.getVersionName();
        final Label version;
        if (vName != null && !"".equals(vName.trim())) {
            version = new Label(vName + " (v" + info.getVersionNumber() + ")");
        } else {
            version = new Label("v" + info.getVersionNumber());
        }
        version.addStyleName("credits-version");

        final Label date = new Label(info.getVersionReleaseDate());
        date.addStyleName("credits-version-date");
        
        final Label ref = new Label(info.getVersionRef());
        ref.addStyleName("credits-version-ref");

        final VerticalPanel p = new VerticalPanel();
        p.setSpacing(0);

        p.add(logo);
        p.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(version);
        p.setCellHorizontalAlignment(version, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(date);
        p.setCellHorizontalAlignment(date, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(ref);
        p.setCellHorizontalAlignment(ref, HasHorizontalAlignment.ALIGN_CENTER);

        return p;
    }

    /**
     * Builds the managers and partners panel.
     * 
     * @return The panel.
     */
    private static Widget getManagersPartnersPanel() {

        final Label header1 = new Label(I18N.CONSTANTS.sigmah_managers() + ':');
        header1.addStyleName("credits-manager-header");

        final Label header2 = new Label(I18N.CONSTANTS.sigmah_partners() + ':');
        header2.addStyleName("credits-partner-header");

        final VerticalPanel p = new VerticalPanel();
        p.setSpacing(0);

        p.add(header1);
        for (final ApplicationInfo.ApplicationManager manager : info.getManagers()) {
            p.add(buildManagerPartner(manager.getName(), null, manager.getUrl()));
        }

        p.add(header2);
        for (final ApplicationInfo.ApplicationPartner partner : info.getPartners()) {
            p.add(buildManagerPartner(partner.getName(), partner.getRole(), partner.getUrl()));
        }

        return p;
    }

    /**
     * Builds a manager or partner widget.
     * 
     * @param name
     *            The name.
     * @param role
     *            The role
     * @param url
     *            The address.
     * @return The widget.
     */
    private static Widget buildManagerPartner(String name, ApplicationInfo.ApplicationPartnerRole role, String url) {

        final Grid grid = new Grid(1, 2);
        grid.setCellPadding(0);
        grid.setCellSpacing(0);
        grid.addStyleName("credits-partner");

        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append("&nbsp;");

        if (role != null) {
            sb.append('(');
            switch (role) {
            case DEV:
                sb.append(I18N.CONSTANTS.sigmah_partners_role_development());
                break;
            case DES:
                sb.append(I18N.CONSTANTS.sigmah_partners_role_design());
                break;
            case GRA:
                sb.append(I18N.CONSTANTS.sigmah_partners_role_graphic());
                break;
            }
            sb.append(')');
        }
        sb.append("&nbsp;:&nbsp;");

        grid.setHTML(0, 0, sb.toString());

        final Anchor urlA = new Anchor(url, url);
        urlA.addStyleName("credits-partner-url");
        grid.setWidget(0, 1, urlA);

        return grid;
    }

    /**
     * Builds the developpers and contributors panel.
     * 
     * @return The panel.
     */
    private static Widget getDevelopersContributorsPanel() {

        final Grid grid = new Grid(2, 2);
        grid.setCellPadding(0);
        grid.setCellSpacing(0);
        grid.addStyleName("credits-developers");
        grid.setHeight("130px");

        grid.setText(0, 0, I18N.CONSTANTS.sigmah_developers());
        grid.getCellFormatter().addStyleName(0, 0, "credits-developers-header");
        grid.setText(0, 1, I18N.CONSTANTS.sigmah_contributors());
        grid.getCellFormatter().addStyleName(0, 1, "credits-developers-header");

        StringBuilder sb = new StringBuilder();
        for (final ApplicationInfo.ApplicationDeveloper dev : info.getDeveloppers()) {
            sb.append("<div class=\"credits-developer\">");
            sb.append(dev.getName());
            sb.append("</div><div class=\"credits-developer-email\"><a href=\"mailto:");
            sb.append(dev.getEmail());
            sb.append("\">");
            sb.append(dev.getEmail());
            sb.append("</a></div>");
        }
        grid.setHTML(1, 0, sb.toString());
        grid.getCellFormatter().setVerticalAlignment(1, 0, HasVerticalAlignment.ALIGN_TOP);

        sb = new StringBuilder();
        for (final ApplicationInfo.ApplicationContributor con : info.getContributors()) {
            sb.append("<div class=\"credits-developer\">");
            sb.append(con.getName());
            sb.append("</div><div class=\"credits-developer-email\"><a href=\"mailto:");
            sb.append(con.getEmail());
            sb.append("\">");
            sb.append(con.getEmail());
            sb.append("</a></div>");
        }
        grid.setHTML(1, 1, sb.toString());
        grid.getCellFormatter().setVerticalAlignment(1, 1, HasVerticalAlignment.ALIGN_TOP);

        return grid;
    }

    /**
     * Builds a the credit panel.
     * 
     * @return The panel.
     */
    private static Widget getCreditsPanel() {

        final Label label = new Label(I18N.CONSTANTS.sigmah_credits());
        final Image timeline = new Image("image/credits.png");

        final VerticalPanel p = new VerticalPanel();
        p.setSpacing(0);
        p.addStyleName("credits-timeline");

        p.add(label);
        p.add(timeline);

        return p;
    }

    private static ApplicationInfo info;
    private static Window window;

    /**
     * Initializes the window with the given info.
     * 
     * @param info
     *            The info.
     */
    public static void init(ApplicationInfo info) {
        CreditFrame.info = info;
        build();
    }

    /**
     * Shows the window. Calls the {@link #init(ApplicationInfo)} method before.
     */
    public static void show() {
        if (info != null) {
            window.show();
        }
    }
}
