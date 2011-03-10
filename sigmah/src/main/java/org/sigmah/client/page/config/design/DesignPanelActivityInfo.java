package org.sigmah.client.page.config.design;

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
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.inject.Inject;

public class DesignPanelActivityInfo extends DesignPanelBase implements Page  {

	public static final PageId PAGE_ID = new PageId("design");
	
	@Inject
	public DesignPanelActivityInfo(EventBus eventBus, Dispatcher service) {
		super(eventBus, service);
		treeStore = new TreeStore<ModelData>();
	}	

	public void go(UserDatabaseDTO db) {
		this.db = db;
		doLayout(db);
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
