package org.sigmah.client.page.entry;

import org.sigmah.client.EventBus;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.store.ListStore;

public class SiteStoreHelper {

	private final EventBus eventBus;
	private final ListStore<SiteDTO> store;
	
	public SiteStoreHelper(EventBus eventBus, final ListStore<SiteDTO> store) {
		super();
		this.eventBus = eventBus;
		this.store = store;
	
//
//        siteChangedListener = new Listener<SiteEvent>() {
//            public void handleEvent(SiteEvent se) {
//
//                SiteDTO ourCopy = store.findModel("id", se.getSite().getId());
//                if (ourCopy != null) {
//                    ourCopy.setProperties(se.getSite().getProperties());
//                }
//                store.update(ourCopy);
//
//            }
//        };
//        this.eventBus.addListener(AppEvents.SiteChanged, siteChangedListener);
//
//        siteCreatedListener = new Listener<SiteEvent>() {
//            public void handleEvent(SiteEvent se) {
//                onSiteCreated(se);
//            }
//        };
//        this.eventBus.addListener(AppEvents.SiteCreated, siteCreatedListener);
//
//        siteSelectedListner = new Listener<SiteEvent>() {
//            public void handleEvent(SiteEvent se) {
//                // check to see if this site is on the current page
//        	
//                if (se.getSource() != SiteGridPanel.this) {
//                    SiteDTO site = store.findModel("id", se.getSiteId());
//                    if (site != null) {
//                        view.setSelection(se.getSiteId());
//                    }
//                }
//            }
//        };
//        this.eventBus.addListener(AppEvents.SiteSelected, siteSelectedListner);
		
	}
	
	
	
}
