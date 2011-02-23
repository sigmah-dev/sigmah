package org.sigmah.client.page.config.design;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.grid.AbstractEditorGridPresenter;
import org.sigmah.client.page.common.grid.GridView;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.Command;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;

public class ProjectIndicatorsPresenter extends AbstractEditorGridPresenter<ModelData> implements Page, SubPresenter{
	
	protected ProjectIndicatorsPresenter(EventBus eventBus, Dispatcher service,
			IStateManager stateMgr, GridView view) {
		super(eventBus, service, stateMgr, view);
	}

	private View view;
	
	protected TreeStore<ModelData> treeStore = null;
	
	public interface View {
					
		public Button getNewIndicatorButton();
		
		public Button getNewGroupButton();
		
		public Button getReloadButton();
		
		public Button getShowSiteMapButton();
		
		public Button getShowSiteTableButton();
		
		public Button getLoadSitesButton();
		
		public TreeStore<ModelData> getTreeStore();
		
	}	
	
	private void wireView() {
		view.getNewIndicatorButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				
			}
		});

		
		view.getNewGroupButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				
			}
		});
		
		view.getReloadButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				
			}
		});
		
		view.getShowSiteMapButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				
			}
		});
		
		view.getLoadSitesButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				
			}
		});
	}
	
	
	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSelectionChanged(ModelData selectedItem) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Component getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void discardView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void viewDidAppear() {
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
	public boolean navigate(PageState place) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected Command createSaveCommand() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Store<ModelData> getStore() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getStateId() {
		// TODO Auto-generated method stub
		return null;
	}	
	
}
