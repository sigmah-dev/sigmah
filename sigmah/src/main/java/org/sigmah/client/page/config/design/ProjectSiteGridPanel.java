package org.sigmah.client.page.config.design;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.entry.SiteGridPanel;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.client.util.state.IStateManager;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.inject.Inject;

public class ProjectSiteGridPanel extends SiteGridPanel implements SubPresenter {

	@Inject
	public ProjectSiteGridPanel(EventBus eventBus, Dispatcher service,
			IStateManager stateMgr) {
		super(eventBus, service, stateMgr);
		setHeaderVisible(false);
		
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public void discardView() {
		
	}

	@Override
	public void viewDidAppear() {
				
	}

}
