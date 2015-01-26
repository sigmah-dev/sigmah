package org.sigmah.client.page.admin.management;

import org.sigmah.client.page.admin.management.AdminManagementPresenter.View;
import org.sigmah.client.ui.StylableHBoxLayout;
import org.sigmah.client.ui.StylableVBoxLayout;

import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayoutData;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayoutData;

/**
 * Page which display some windows for managing the organization or export some data
 * 
 * @author Aurélien Ponçon
 */
public class AdminManagementView extends View {

    private LayoutContainer mainContainer;
    private LayoutContainer secondaryContainer;

    public AdminManagementView() {

        this.setHeaderVisible(false);
        final VBoxLayout mainPanelLayout = new StylableVBoxLayout("secondary-background");
        mainPanelLayout.setVBoxLayoutAlign(VBoxLayout.VBoxLayoutAlign.STRETCH);
        this.setLayout(mainPanelLayout);

        final HBoxLayout layout2 = new StylableHBoxLayout("secondary-background");
        layout2.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.TOP);
        mainContainer = new LayoutContainer(layout2);

        final HBoxLayout layout3 = new StylableHBoxLayout("secondary-background admin-export-top");
        layout3.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.TOP);
        secondaryContainer = new LayoutContainer(layout3);

        VBoxLayoutData layoutData = new VBoxLayoutData();
        layoutData.setFlex(0.0);
        this.add(mainContainer, layoutData);
        VBoxLayoutData layoutData2 = new VBoxLayoutData();
        layoutData2.setFlex(0.5);
        this.add(secondaryContainer, layoutData2);

    }

    @Override
    public void addPanelToMainContainer(ContentPanel contentPanel) {
        addPanelToContainer(mainContainer, contentPanel);

    }

    @Override
    public void addPanelToSecondaryContainer(ContentPanel contentPanel) {
        addPanelToContainer(secondaryContainer, contentPanel);

    }

    private void addPanelToContainer(LayoutContainer dest, ContentPanel panel) {
        HBoxLayoutData hBoxLayoutData = new HBoxLayoutData();
        hBoxLayoutData.setMargins(new Margins(0, 5, 0, 5));
        hBoxLayoutData.setFlex(0.0);

        dest.add(panel, hBoxLayoutData);

        dest.layout(true);
        this.layout(true);
    }
}
