package org.sigmah.client.page.admin;


import org.sigmah.client.ui.StylableVBoxLayout;


import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Displays administration screen.
 * 
 * @author nrebiai
 * 
 */
public class AdminView extends LayoutContainer implements AdminPresenter.View{
	
	private final static int BORDER = 8;
    private final static String STYLE_MAIN_BACKGROUND = "main-background";

    private final ContentPanel rightPanel;
    private final ContentPanel leftNavigationPanel;
    private Widget widget;
	
	@Inject
    public AdminView() { 
		final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST, 150);
        leftLayoutData.setMargins(new Margins(0, BORDER / 2, 0, 0));
		final BorderLayout borderLayout = new BorderLayout();
        borderLayout.setContainerStyle("x-border-layout-ct " + STYLE_MAIN_BACKGROUND);
        setLayout(borderLayout);
		leftNavigationPanel = new ContentPanel(new StylableVBoxLayout("main-background project-top-bar"));
		leftNavigationPanel.setHeaderVisible(false);
		leftNavigationPanel.setCollapsible(true);
        
		rightPanel = new ContentPanel(new FitLayout());
		final BorderLayout rightBorderLayout = new BorderLayout();
        borderLayout.setContainerStyle("x-border-layout-ct " + STYLE_MAIN_BACKGROUND);
        rightPanel.setLayout(rightBorderLayout);
        rightPanel.setHeaderVisible(false);
        rightPanel.setBorders(false);
        
        //rightPanel.setHeaderVisible(false);
        //rightPanel.setSize(300, 300);
        add(leftNavigationPanel,leftLayoutData);
        add(rightPanel,new BorderLayoutData(LayoutRegion.CENTER));
	}

	@Override
	public void setMainPanel(Widget newWidget) {
		
		if(widget != null){
			Log.debug("Old widget " + widget.getTitle());
			rightPanel.remove(widget);
		}
           
		final BorderLayoutData mainLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
        mainLayoutData.setMargins(new Margins(0, 0, 0, BORDER / 2));
        rightPanel.add(newWidget, mainLayoutData);
        widget = newWidget;		
        Log.debug("New widget " + newWidget.getTitle());
        rightPanel.layout();
	}

	@Override
	public ContentPanel getTabPanel() {
		return leftNavigationPanel;
	}
}
