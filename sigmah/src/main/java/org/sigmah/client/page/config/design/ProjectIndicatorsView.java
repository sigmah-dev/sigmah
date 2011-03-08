package org.sigmah.client.page.config.design;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;

public class ProjectIndicatorsView extends LayoutContainer implements
		ProjectIndicatorsPresenter.View {

	protected ContentPanel mapContainer;

	private Button newIndicatorButton;
	private Button newGroupButton;
	private Button reloadButton;
	private Button showSiteMapButton;
	private Button showSiteTableButton;
	private Button loadSitesButton;
	private TreeStore<ModelData> treeStore;
	private TabPanel tabPanel;
	private TabItem mapTabItem;
	private TabItem sitesTabItem;
	private SubPresenter siteEditor;
	private DesignPresenter designPresenter;

	public ProjectIndicatorsView(SubPresenter siteEditor,
			final DesignPresenter designPresenter) {

		this.siteEditor = siteEditor;
		this.designPresenter = designPresenter;

		BorderLayout borderLayout = new BorderLayout();
		borderLayout.setContainerStyle("x-border-layout-ct main-background");
		setLayout(borderLayout);

		ContentPanel mainPanel = new ContentPanel();
		mainPanel.setIcon(null);

		// setIcon(IconImageBundle.ICONS.design());

		// map tab panel
		tabPanel = new TabPanel();
		tabPanel.setPlain(true);

		// map tab item
		mapTabItem = new TabItem("map");
		mapTabItem.setLayout(new FitLayout());
		mapTabItem.setEnabled(false);
		mapTabItem.setAutoHeight(true);
		mapTabItem.setEnabled(true);

		tabPanel.add(mapTabItem);

		// sites tab item
		sitesTabItem = new TabItem("sites");
		sitesTabItem.setLayout(new FitLayout());
		sitesTabItem.setEnabled(false);
		sitesTabItem.setAutoHeight(true);
		sitesTabItem.setEnabled(true);

		sitesTabItem.add(siteEditor.getView());
		tabPanel.add(sitesTabItem);
		
		// "tab" buttons for map view
		showSiteMapButton = new Button("show sitemap");
		showSiteTableButton = new Button("show site table");

		// mapContainer.add(showSiteMapButton);
		// mapContainer.add(showSiteTableButton);

		// buttons for indicator view
		newIndicatorButton = new Button("new indicator");
		newGroupButton = new Button("new group");
		reloadButton = new Button("reload button");

		mainPanel.add(newIndicatorButton);
		mainPanel.add(newGroupButton);
		mainPanel.add(reloadButton);

		// reload button for map view
		loadSitesButton = new Button("load site button");
		// mapContainer.add(loadSitesButton);

		BorderLayoutData centerLayout = new BorderLayoutData(
				Style.LayoutRegion.CENTER);
		centerLayout.setMargins(new Margins(0, 0, 0, 0));
		centerLayout.setSplit(true);
		centerLayout.setCollapsible(true);

		BorderLayoutData layout = new BorderLayoutData(Style.LayoutRegion.EAST);
		layout.setSplit(true);
		layout.setCollapsible(true);
		layout.setSize(375);
		layout.setMargins(new Margins(0, 0, 0, 5));

		add(designPresenter.getView(), centerLayout);
		add(tabPanel, layout);
		// setHeading(I18N.CONSTANTS.design() + " - " );
		final DesignPresenter.View designView = (DesignPresenter.View) designPresenter
				.getView();
		ActionToolBar bar = designView.getToolbar();
		Menu newMenu = new Menu();

		Button newButtonMenu = new Button(I18N.CONSTANTS.newText(),
				IconImageBundle.ICONS.add());
		newButtonMenu.setMenu(newMenu);
		newButtonMenu.setEnabled(true);
		bar.add(newButtonMenu);

		SelectionListener<MenuEvent> listener = new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				designPresenter.onNew(ce.getItem().getItemId());
			}
		};
		
		final MenuItem newIndicatorGroup = new MenuItem(
				I18N.CONSTANTS.newIndicatorGroup(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicatorGroup.setItemId("IndicatorGroup");
		newMenu.add(newIndicatorGroup);
		
		final MenuItem newIndicator = new MenuItem(
				I18N.CONSTANTS.newIndicator(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicator.setItemId("Indicator");
		newMenu.add(newIndicator);
		
		Button reloadButtonMenu = new Button(I18N.CONSTANTS.refresh(),
				IconImageBundle.ICONS.refresh());
		reloadButtonMenu.setEnabled(true);
		bar.add(reloadButtonMenu);		
	}

	
	@Override
	public Button getNewIndicatorButton() {
		return this.newIndicatorButton;
	}

	@Override
	public Button getNewGroupButton() {
		return this.newGroupButton;
	}

	@Override
	public Button getReloadButton() {
		return this.reloadButton;
	}

	@Override
	public Button getShowSiteMapButton() {
		return this.showSiteMapButton;
	}

	@Override
	public Button getShowSiteTableButton() {
		return this.showSiteTableButton;
	}

	@Override
	public Button getLoadSitesButton() {
		return this.loadSitesButton;
	}

	@Override
	public TreeStore<ModelData> getTreeStore() {
		return this.treeStore;
	}

	@Override
	public ActionToolBar getDesignTreeToolBar() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * @Override public void init(ProjectIndicatorsPresenter
	 * projectIndicatorsPresenter, TreeStore<ModelData> treeStore) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 */

}
