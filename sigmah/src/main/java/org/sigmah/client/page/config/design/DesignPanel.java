/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.page.entry.SiteGridPageState;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class DesignPanel extends DesignPanelBase implements SubPresenter {
	
	private ProjectPresenter projectPresenter;
	
	@Inject
	DesignPanel(EventBus eventBus, Dispatcher service) {
		super(eventBus, service);
		treeStore = new TreeStore<ModelData>(new BaseTreeLoader<ModelData>(new Proxy()));
		toolBar.addSaveSplitButton();
	}
	
	DesignPanel(EventBus eventBus, Dispatcher service, ProjectPresenter projectPresenter) {
		super(eventBus, service);
		this.projectPresenter = projectPresenter;
	}
	
	@Override
	public Component getView() {
		return this;
	}

	@Override
	public void discardView() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub
		
	}

	private class Proxy implements DataProxy<List<ModelData>> {

		private void finishLoad(UserDatabaseDTO db) {
			fillStore();
			SiteGridPageState state = new SiteGridPageState();
			state.setPageNum(1);
		}
		
		@Override
		public void load(DataReader<List<ModelData>> reader, Object loadConfig,
				AsyncCallback<List<ModelData>> callback) {
			if (db == null) {
				service.execute(new GetSchema(), null,
						new AsyncCallback<SchemaDTO>() {
	
							public void onSuccess(SchemaDTO result) {
								SchemaDTO schema = result;
								db = schema.getDatabaseById(projectPresenter
										.getCurrentProjectDTO().getId());
								finishLoad(db);
							}
	
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}
						});
			} else {
				finishLoad(db);
			}
		}
	}

	@Override
	protected void fillStore() {
		// TODO Auto-generated method stub
		
	}
}
