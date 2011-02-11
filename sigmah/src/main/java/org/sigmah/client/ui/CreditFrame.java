package org.sigmah.client.ui;

import org.sigmah.client.i18n.I18N;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

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
     */
    private static void init() {

        final HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.setSpacing(5);

        final Panel versionPanel = getVersionPanel();
        hPanel.add(getVersionPanel());
        hPanel.setCellVerticalAlignment(versionPanel, HasVerticalAlignment.ALIGN_TOP);

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
        p.add(hPanel);

        window.add(p);
    }

    private static Panel getVersionPanel() {

        final Image logo = new Image("image/logo.png");
        final Label version = new Label(I18N.CONSTANTS.sigmah_version_name() + " (v"
                + I18N.CONSTANTS.sigmah_version_number() + ")");
        final Label date = new Label(I18N.CONSTANTS.sigmah_version_releaseDate());

        final VerticalPanel p = new VerticalPanel();

        p.setSpacing(5);

        p.add(logo);
        p.setCellHorizontalAlignment(logo, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(version);
        p.setCellHorizontalAlignment(version, HasHorizontalAlignment.ALIGN_CENTER);
        p.add(date);
        p.setCellHorizontalAlignment(date, HasHorizontalAlignment.ALIGN_CENTER);

        return p;
    }

    private static Window window;

    /**
     * Shows the window.
     */
    public static void show() {
        if (window == null) {
            init();
        }
        window.show();
    }
}
