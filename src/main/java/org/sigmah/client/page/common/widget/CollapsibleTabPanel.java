/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.common.widget;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.Event;

/**
 * A tab panel that can be collapsed by double-clicking the tab.
 *
 * TODO: move the logic from {@link org.sigmah.client.page.entry.DataEntryPage} to this class
 *
 * @author Alex Bertram
 */
public class CollapsibleTabPanel extends TabPanel {

	private final LayoutContainer parent;
	private final BorderLayout parentLayout;
	
	private int tabPanelExandedSize = 200;
	private boolean tabPanelCollapsed;
	private BorderLayoutData tabPanelLayout;
	
	public CollapsibleTabPanel(LayoutContainer parent) {
		assert parent.getLayout() instanceof BorderLayout : "CollapsibleTabPanel only works within a BorderLayout";
		
		this.parent = parent;
		this.parentLayout = (BorderLayout)parent.getLayout();
		
		setTabPosition(TabPanel.TabPosition.BOTTOM);
		setAutoSelect(false);
		
		// Add ourselves to the parent layout right away

		tabPanelLayout = new BorderLayoutData(Style.LayoutRegion.SOUTH);
		tabPanelLayout.setCollapsible(true);
		tabPanelLayout.setSplit(true);
		tabPanelLayout.setMargins(new Margins(5, 0, 0, 0));

		parent.add(this, tabPanelLayout);
		
	}
	
    public El getBody() {
        if (getTabPosition() == TabPosition.TOP) {
            return el().getChild(1);
        } else {
            return el().getChild(0);
        }
    }

    public El getBar() {
       if (getTabPosition() == TabPosition.TOP) {
            return el().getChild(0);
        } else {
            return el().getChild(1);
        }
    }

	@Override
	public boolean add(TabItem tab) {
		if(super.add(tab)) {
			tab.getHeader().addListener(Events.BrowserEvent, new Listener<ComponentEvent>() {
				public void handleEvent(ComponentEvent be) {
					if(be.getEventTypeInt() == Event.ONCLICK) {
						onTabClicked((TabItem.HeaderItem) be.getComponent());
					}
				}
			});
			return true;
			
		} else {
			return false;
		}
	}


	private void onTabClicked(TabItem.HeaderItem header) {
		if(getSelectedItem()!=null && getSelectedItem().getHeader() == header) {
			if(!tabPanelCollapsed) {
				// "collapse" tab panel - show only the tab strip
				collapseTabs();
			} else {
				// expand tab panel to previous size
				expandTabs();
			}
			parentLayout.layout();
			
		} else if(tabPanelCollapsed) {
			expandTabs();
			parentLayout.layout();
		}
	}

	private void collapseTabs() {
		tabPanelExandedSize = (int)tabPanelLayout.getSize();
		tabPanelLayout.setSize(getBar().getHeight());
		tabPanelLayout.setMargins(new Margins(0));
		getBody().setVisible(false);
		tabPanelLayout.setSplit(false);
		tabPanelCollapsed = true;
	}

	private void expandTabs() {
		getBody().setVisible(true);
		tabPanelLayout.setSize(tabPanelExandedSize);
		tabPanelLayout.setMargins(new Margins(5, 0, 0, 0));
		tabPanelLayout.setSplit(true);
		tabPanelCollapsed = false;
	}
    
}
