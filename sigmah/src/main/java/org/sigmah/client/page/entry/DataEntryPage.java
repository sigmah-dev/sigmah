/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.filter.AdminFilterPanel;
import org.sigmah.client.page.common.filter.DateRangePanel;
import org.sigmah.client.page.common.filter.PartnerFilterPanel;
import org.sigmah.client.page.common.widget.CollapsibleTabPanel;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CardLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;

/**
 * 
 * This is the container and Page for the data entry components.
 *  
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class DataEntryPage extends LayoutContainer implements Page {

	public static final PageId ID = new PageId("site-grid");

	private CollapsibleTabPanel tabPanel;
	private LayoutContainer sidePanel;

	private int tabPanelExandedSize = 200;
	private boolean tabPanelCollapsed;
	private BorderLayoutData tabPanelLayout;

	private EventBus eventBus;
	
	private ActivityFilterPanel activityPanel;
	private AdminFilterPanel adminPanel;
	private DateRangePanel datePanel;
	private PartnerFilterPanel partnerPanel;
	
	private SiteGridPanel gridPanel;
	
	private SiteMap siteMap;

	
	@Inject
	public DataEntryPage(
			EventBus eventBus,
			ActivityFilterPanel activityPanel,
			AdminFilterPanel adminPanel, 
			DateRangePanel datePanel, 
			PartnerFilterPanel partnerPanel, 
			SiteGridPanel gridPanel,
			SiteMap siteMap) {
		
		this.eventBus = eventBus;
		this.activityPanel = activityPanel;
		this.adminPanel = adminPanel;
		this.datePanel = datePanel;
		this.partnerPanel = partnerPanel;
		
		this.gridPanel = gridPanel;
		this.siteMap = siteMap;
		
		setLayout(new BorderLayout());
		
		addFilterPane();			
		addSiteGridPanel(gridPanel);
		addMapPanel();
	
		
		sinkEvents();
	}

	
	private void sinkEvents() {
		activityPanel.addSelectionChangedListener(new SelectionChangedListener<ActivityDTO>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<ActivityDTO> se) {
				onActivityChanged(se.getSelectedItem());
			}
		});
	}

	private void addSiteGridPanel(SiteGridPanel gridPanel) {
		gridPanel.getToolBar().add(new SeparatorToolItem());
		add(gridPanel, new BorderLayoutData(LayoutRegion.CENTER));
	}
	
	private void addFilterPane() {
		ContentPanel west = new ContentPanel();
		west.setHeading(I18N.CONSTANTS.filter());
		west.setIcon(IconImageBundle.ICONS.filter());
		west.setLayout(new AccordionLayout());
		west.add(activityPanel);
		west.add(adminPanel);
		west.add(partnerPanel);
		west.add(datePanel);
		
		BorderLayoutData westLayout = new BorderLayoutData(LayoutRegion.WEST);
		westLayout.setSplit(true);
		westLayout.setMargins(new Margins(0, 5, 0, 0));
		westLayout.setCollapsible(true);
		
		add(west, westLayout);
	}
	
	private void addMapPanel() {
		final ToggleButton sideBarButton = new ToggleButton(I18N.CONSTANTS.map(), IconImageBundle.ICONS.map());
		sideBarButton.setToggleGroup("sideBar");
		sideBarButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showEasternComponent(siteMap, sideBarButton);
				if(!activityPanel.getSelection().isEmpty()) {
					siteMap.loadSites(activityPanel.getSelectedActivity());
				}
			}

		});
		gridPanel.getToolBar().add(sideBarButton);
	}

	public void addSouthTab(TabItem tab) {

		if(tabPanel == null) {

			tabPanelLayout = new BorderLayoutData(Style.LayoutRegion.SOUTH);
			tabPanelLayout.setCollapsible(true);
			tabPanelLayout.setSplit(true);
			tabPanelLayout.setMargins(new Margins(5, 0, 0, 0));

			tabPanel = new CollapsibleTabPanel();
			tabPanel.setTabPosition(TabPanel.TabPosition.BOTTOM);
			tabPanel.setAutoSelect(false);
			add(tabPanel, tabPanelLayout);
		}

		tab.getHeader().addListener(Events.BrowserEvent, new Listener<ComponentEvent>() {
			public void handleEvent(ComponentEvent be) {
				if(be.getEventTypeInt() == Event.ONCLICK) {
					onTabClicked((TabItem.HeaderItem) be.getComponent());
				}
			}
		});

		tabPanel.add(tab);
	}

	private void onTabClicked(TabItem.HeaderItem header) {
		if(tabPanel.getSelectedItem()!=null && tabPanel.getSelectedItem().getHeader() == header) {
			if(!tabPanelCollapsed) {
				// "collapse" tab panel - show only the tab strip
				collapseTabs();
			} else {
				// expand tab panel to previous size
				expandTabs();
			}
			getLayout().layout();
		} else if(tabPanelCollapsed) {
			expandTabs();
			getLayout().layout();
		}
	}

	private void collapseTabs() {
		tabPanelExandedSize = (int)tabPanelLayout.getSize();
		tabPanelLayout.setSize(tabPanel.getBar().getHeight());
		tabPanelLayout.setMargins(new Margins(0));
		tabPanel.getBody().setVisible(false);
		tabPanelLayout.setSplit(false);
		tabPanelCollapsed = true;
	}

	private void expandTabs() {
		tabPanel.getBody().setVisible(true);
		tabPanelLayout.setSize(tabPanelExandedSize);
		tabPanelLayout.setMargins(new Margins(5, 0, 0, 0));
		tabPanelLayout.setSplit(true);
		tabPanelCollapsed = false;
	}
	

	private void onActivityChanged(ActivityDTO selectedItem) {
		applyFilter();
		gridPanel.setHeading(selectedItem.getName());
		if(siteMap.isVisible()) {
			siteMap.loadSites(selectedItem);
		}
	}

	private void applyFilter() {

		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Activity, activityPanel.getSelectedActivity().getId());
		
		List<AdminEntityDTO> entities = adminPanel.getSelection();
		for (AdminEntityDTO entity : entities) {
			filter.addRestriction(DimensionType.AdminLevel, entity.getId());
		}

		List<PartnerDTO> partners = partnerPanel.getSelection();
		for (PartnerDTO entity : partners) {
			filter.addRestriction(DimensionType.Partner, entity.getId());
		}

		if (datePanel.getMinDate() != null) {
			filter.setMinDate(datePanel.getMinDate());
		}

		if (datePanel.getMaxDate() != null) {
			filter.setMaxDate(datePanel.getMaxDate());
		}
		
		gridPanel.load(filter);
	}
	
	private void showEasternComponent(final Component component,
			final ToggleButton sideBarButton) {
		BorderLayout borderLayout = (BorderLayout) getLayout();
		if(sideBarButton.isPressed()) {

			if(sidePanel == null) {
				sidePanel = new LayoutContainer();
				sidePanel.setLayout(new CardLayout());

				BorderLayoutData east = new BorderLayoutData(Style.LayoutRegion.EAST, 0.4f);
				east.setSplit(true);
				east.setMargins(new Margins(0, 0, 0, 5));

				add(sidePanel, east);
			} else if(isRendered()) {
				borderLayout.show(Style.LayoutRegion.EAST);
			}
			if(!component.isAttached()) {
				sidePanel.add(component);
			}
			((CardLayout)sidePanel.getLayout()).setActiveItem(component);
			borderLayout.layout();
		} else {
			borderLayout.hide(Style.LayoutRegion.EAST);
		}
	}

	public void go(SiteGridPageState place, ActivityDTO activity) {


//
//		ActivityDTO activity = schema.getActivityById(sgPlace.getActivityId());
//
//		SiteGridPage grid = new SiteGridPage(true, adminPanel, datePanel, partnerPanel);
//		SiteGridPanel editor = new SiteGridPanel(injector.getEventBus(), injector.getService(),
//				injector.getStateManager(), grid);
//
//		if (activity.getReportingFrequency() == ActivityDTO.REPORT_MONTHLY) {
//			MonthlyGrid monthlyGrid = new MonthlyGrid(activity);
//			MonthlyTab monthlyTab = new MonthlyTab(monthlyGrid);
//			MonthlyPresenter monthlyPresenter = new MonthlyPresenter(
//					injector.getEventBus(),
//					injector.getService(),
//					injector.getStateManager(),
//					activity, monthlyGrid);
//			editor.addSubComponent(monthlyPresenter);
//			grid.addSouthTab(monthlyTab);
//		} else {
//
//			DetailsTab detailsTab = new DetailsTab();
//			DetailsPresenter detailsPresenter = new DetailsPresenter(
//					injector.getEventBus(),
//					activity,
//					injector.getMessages(),
//					detailsTab);
//			grid.addSouthTab(detailsTab);
//			editor.addSubComponent(detailsPresenter);
//		}
//		SiteMap map = new SiteMap(injector.getEventBus(), injector.getService(),
//				activity);
//		editor.addSubComponent(map);
//		grid.addSidePanel(I18N.CONSTANTS.map(), IconImageBundle.ICONS.map(), map);
//
//		editor.go((SiteGridPageState) place, activity);
//		callback.onSuccess(editor);
//
//		currentActivity = activity;
//
//		/*
//		 * Define the intial load parameters based on
//		 * the navigation event, the, by previous user state
//		 * and then by sensible defaults (sorted by date)
//		 */
//
//		 initLoaderDefaults(loader, place, new SortInfo("date2", Style.SortDir.DESC));
//		Filter filter = view.getFilter();
//		if(filter == null) {
//			filter = new Filter();
//		}
//		if (currentActivity != null)
//			filter.addRestriction(DimensionType.Activity, currentActivity.getId());
//
//		GetSites cmd = new GetSites();
//		cmd.setFilter(filter);
//
//		loader.setCommand(cmd);
//		if (currentActivity != null)
//			view.init(SiteGridPanel.this, currentActivity, store);
//
//		view.setActionEnabled(UIActions.add, currentActivity.getDatabase().isEditAllowed());
//		view.setActionEnabled(UIActions.edit, false);
//		view.setActionEnabled(UIActions.delete, false);
//
//		loader.load();
	}


	public boolean navigate(final PageState place) {
//
//		if (!(place instanceof SiteGridPageState)) {
//			return false;
//		}
//
//		final SiteGridPageState gridPlace = (SiteGridPageState) place;
//
//		if (currentActivity.getId() != gridPlace.getActivityId()) {
//			return false;
//		}
//
//		handleGridNavigation(loader, gridPlace);
//
//		return true;
		return true;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PageId getPageId() {
		return ID;
	}

	@Override
	public Object getWidget() {
		return this;
	}

	@Override
	public void requestToNavigateAway(PageState place,
			NavigationCallback callback) {
		callback.onDecided(true);
		
	}

	@Override
	public String beforeWindowCloses() {
		// TODO Auto-generated method stub
		return null;
	}
}
