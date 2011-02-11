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
    public static void init(ApplicationInfo info) {

        CreditFrame.info = info;

        final Widget versionPanel = getVersionPanel();
        final Widget rolesPanel = getRolesPanel();

        // Top-right v panel.
        final VerticalPanel vPanel = new VerticalPanel();
        vPanel.setSpacing(0);

        vPanel.add(rolesPanel);
        vPanel.setCellVerticalAlignment(rolesPanel, HasVerticalAlignment.ALIGN_TOP);

        // Top h panel.
        final HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(0);

        hPanel.add(versionPanel);
        hPanel.setCellVerticalAlignment(versionPanel, HasVerticalAlignment.ALIGN_MIDDLE);
        hPanel.setCellHorizontalAlignment(versionPanel, HasHorizontalAlignment.ALIGN_CENTER);
        hPanel.setCellWidth(versionPanel, "0");
        versionPanel.getElement().getStyle().setMarginRight(20, Unit.PX);

        hPanel.add(vPanel);
        hPanel.setCellVerticalAlignment(vPanel, HasVerticalAlignment.ALIGN_TOP);

        // Main panel.
        final VerticalPanel mainPanel = new VerticalPanel();
        mainPanel.setSpacing(10);

        mainPanel.add(hPanel);
        mainPanel.add(new Label(""));

        // Builds the window.
        window = new Window();
        window.setHeading(I18N.CONSTANTS.credits());
        window.setWidth(750);
        window.setHeight(400);
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

    private static Widget getVersionPanel() {

        final Image logo = new Image("image/logo.png");
        final Label version = new Label(info.getVersionName() + " (v" + info.getVersionNumber() + ")");
        version.addStyleName("credits-version");
        final Label date = new Label(info.getVersionReleaseDate());
        date.addStyleName("credits-version-date");

        final VerticalPanel p = new VerticalPanel();
        p.setSpacing(0);

        p.add(logo);
        p.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(version);
        p.setCellHorizontalAlignment(version, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(date);
        p.setCellHorizontalAlignment(date, HasHorizontalAlignment.ALIGN_CENTER);

        return p;
    }

    private static Widget getRolesPanel() {

        final Label header1 = new Label(I18N.CONSTANTS.sigmah_managers() + ':');
        header1.addStyleName("credits-manager-header");

        final Label header2 = new Label(I18N.CONSTANTS.sigmah_partners() + ':');
        header2.addStyleName("credits-partner-header");

        final VerticalPanel p = new VerticalPanel();
        p.setSpacing(0);

        p.add(header1);
        for (final ApplicationInfo.ApplicationManager manager : info.getManagers()) {
            p.add(buildActor(manager.getName(), null, manager.getUrl()));
        }

        p.add(header2);
        for (final ApplicationInfo.ApplicationPartner partner : info.getPartners()) {
            p.add(buildActor(partner.getName(), partner.getRole(), partner.getUrl()));
        }

        return p;
    }

    private static Widget buildActor(String name, ApplicationInfo.ApplicationPartnerRole role, String url) {

        final Grid grid = new Grid(1, 2);
        grid.setCellPadding(0);
        grid.setCellSpacing(0);
        grid.addStyleName("credits-partner");

        final StringBuilder sb = new StringBuilder();
        sb.append(name);
        sb.append(' ');

        if (role != null) {
            sb.append('(');
            switch (role) {
            case DEVELOPPER:
                sb.append(I18N.CONSTANTS.sigmah_partners_role_development());
                break;
            case DESIGN:
                sb.append(I18N.CONSTANTS.sigmah_partners_role_design());
                break;
            case GRAPHISM:
                sb.append(I18N.CONSTANTS.sigmah_partners_role_graphic());
                break;
            }
            sb.append(')');
        }
        sb.append(": ");

        grid.setText(0, 0, sb.toString());

        final Anchor urlA = new Anchor(url, url);
        urlA.addStyleName("credits-partner-url");
        grid.setWidget(0, 1, urlA);

        return grid;
    }

    private static ApplicationInfo info;
    private static Window window;

    /**
     * Shows the window.
     * 
     */
    public static void show() {
        if (window != null) {
            window.show();
        }
    }
}
