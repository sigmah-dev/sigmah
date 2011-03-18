package org.sigmah.client.page.admin.model.common;


import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Displays models administration screen.
 * 
 * @author nrebiai
 * 
 */
public class AdminOneModelView extends LayoutContainer implements AdminOneModelPresenter.View{
	
	private final static int BORDER = 8;
    private final static String STYLE_MAIN_BACKGROUND = "main-background";

    private TabPanel tabPanelParameters;
    private LayoutContainer panelSelectedTab;
    
	@Inject
    public AdminOneModelView() { 
		final BorderLayoutData topLayoutData = new BorderLayoutData(LayoutRegion.NORTH, 50);
        topLayoutData.setMargins(new Margins(0, BORDER / 2, 0, BORDER / 2));
		final BorderLayout borderLayout = new BorderLayout();
        borderLayout.setContainerStyle("x-border-layout-ct " + STYLE_MAIN_BACKGROUND);
        setLayout(borderLayout);
        
        tabPanelParameters = new TabPanel();
        tabPanelParameters.setPlain(true);
        
        panelSelectedTab = new LayoutContainer(new BorderLayout());
        panelSelectedTab.setBorders(false);
        panelSelectedTab.addStyleName("project-current-phase-panel");
        
        final BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0, 0, 4, 4));

        add(tabPanelParameters, centerData);
	}

	@Override
	public Widget getMainPanel() {
		this.setTitle("one model");
		return this;
	}

	@Override
	public TabPanel getTabPanelParameters() {
		return tabPanelParameters;
	}
	
	@Override
    public LayoutContainer getPanelSelectedTab() {
        return panelSelectedTab;
    }
}
