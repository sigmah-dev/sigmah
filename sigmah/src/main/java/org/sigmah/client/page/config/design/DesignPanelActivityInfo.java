package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.config.DbPageState;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.google.inject.Inject;

public class DesignPanelActivityInfo extends DesignPanelBase implements Page  {

	public static final PageId PAGE_ID = new PageId("design");
	
	@Inject
	public DesignPanelActivityInfo(EventBus eventBus, Dispatcher service) {
		this.eventBus=eventBus;
		this.service=service;
		treeStore = new TreeStore<ModelData>();
	}	

	public void go(UserDatabaseDTO db) {
		this.db = db;
		toolBar.setDirty(false);
		if (this.isRendered()) {
			this.layout();
		}
		// setLayout(new BorderLayout());
		setIcon(IconImageBundle.ICONS.design());
		setHeading(I18N.CONSTANTS.design() + " - " + db.getFullName());
		fillStore();
		//setActionEnabled(UIActions.delete, false);
		
		ActionToolBar bar = getToolbar();
		bar.addSaveSplitButton();
		
        SelectionListener<MenuEvent> listener = new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                onNew(ce.getItem().getItemId());
            }
        };

        Menu newMenu = new Menu();
        initNewMenu(newMenu, listener);

        Button newButtonMenu = new Button(I18N.CONSTANTS.newText(), IconImageBundle.ICONS.add());
        newButtonMenu.setMenu(newMenu);
        newButtonMenu.setEnabled(db.isDesignAllowed());
        newButtonMenu.setEnabled(true);

        bar.add(newButtonMenu);
        bar.addDeleteButton();
		//initListeners(treeStore);
	}
	
	@Override
	protected void initNewMenu(Menu menu, SelectionListener<MenuEvent> listener) {
		MenuItem newActivity = new MenuItem(I18N.CONSTANTS.newActivity(),
				IconImageBundle.ICONS.activity(), listener);
		newActivity.setItemId("Activity");
		menu.add(newActivity);

		final MenuItem newAttributeGroup = new MenuItem(
				I18N.CONSTANTS.newAttributeGroup(),
				IconImageBundle.ICONS.attributeGroup(), listener);
		newAttributeGroup.setItemId("AttributeGroup");
		menu.add(newAttributeGroup);

		final MenuItem newAttribute = new MenuItem(
				I18N.CONSTANTS.newAttribute(),
				IconImageBundle.ICONS.attribute(), listener);
		newAttribute.setItemId("Attribute");
		menu.add(newAttribute);

		final MenuItem newIndicator = new MenuItem(
				I18N.CONSTANTS.newIndicator(),
				IconImageBundle.ICONS.indicator(), listener);
		newIndicator.setItemId("Indicator");
		menu.add(newIndicator);

		menu.addListener(Events.BeforeShow, new Listener<BaseEvent>() {
			public void handleEvent(BaseEvent be) {

				ModelData sel = getSelection();
				newAttributeGroup.setEnabled(sel != null);
				newAttribute.setEnabled(sel instanceof AttributeGroupDTO
						|| sel instanceof AttributeDTO);
				newIndicator.setEnabled(sel != null);
			}
		});
	}
	
	@Override
	protected void fillStore() {
		for (ActivityDTO activity : db.getActivities()) {
			ActivityDTO activityNode = new ActivityDTO(activity);
			treeStore.add(activityNode, false);
	
			AttributeGroupFolder attributeFolder = new AttributeGroupFolder(
					activityNode, I18N.CONSTANTS.attributes());
			treeStore.add(activityNode, attributeFolder, false);
	
			for (AttributeGroupDTO group : activity.getAttributeGroups()) {
				AttributeGroupDTO groupNode = new AttributeGroupDTO(group);
				treeStore.add(attributeFolder, groupNode, false);
	
				for (AttributeDTO attribute : group.getAttributes()) {
					AttributeDTO attributeNode = new AttributeDTO(attribute);
					treeStore.add(groupNode, attributeNode, false);
				}
			}
	
			IndicatorFolder indicatorFolder = new IndicatorFolder(activityNode,
					I18N.CONSTANTS.indicators());
			treeStore.add(activityNode, indicatorFolder, false);
	
			for (IndicatorDTO indicator : activity.getIndicators()) {
				IndicatorDTO indicatorNode = new IndicatorDTO(indicator);
				treeStore.add(indicatorFolder, indicatorNode, false);
			}
		}
	}
	
	@Override
	protected ColumnModel createColumnModel() {
		List<ColumnConfig> columns = new ArrayList<ColumnConfig>();

		TextField<String> nameField = new TextField<String>();
		nameField.setAllowBlank(false);

		ColumnConfig nameColumn = new ColumnConfig("name",
				I18N.CONSTANTS.name(), 150);
		nameColumn.setEditor(new CellEditor(nameField));
		nameColumn.setRenderer(new TreeGridCellRenderer());
		columns.add(nameColumn);
		return new ColumnModel(columns);
	}
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PageId getPageId() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Object getWidget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void requestToNavigateAway(PageState place,
			NavigationCallback callback) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String beforeWindowCloses() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean navigate(PageState place) {
		return place instanceof DbPageState
				&& place.getPageId().equals(PAGE_ID)
				&& ((DbPageState) place).getDatabaseId() == db.getId();
	}

}
