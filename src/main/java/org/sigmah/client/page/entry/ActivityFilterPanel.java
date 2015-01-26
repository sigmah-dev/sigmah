/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;


public class ActivityFilterPanel extends ContentPanel implements SelectionProvider<ActivityDTO> {
	private final Dispatcher service;

	private TreePanel<ModelData> tree;
	private TreeLoader<ModelData> loader;
	private TreeStore<ModelData> store;
	
	private ActivityDTO selection;
	
	
	@Inject
	public ActivityFilterPanel(Dispatcher dispatcher) {
		this.service = dispatcher;
		this.setHeading(I18N.CONSTANTS.activities());
		this.setScrollMode(Scroll.NONE);
		this.setLayout(new FitLayout());

		loader = new BaseTreeLoader<ModelData>(new Proxy()) {
			@Override
			public boolean hasChildren(ModelData parent) {
				return parent instanceof UserDatabaseDTO;
			}
		};

		store = new TreeStore<ModelData>(loader);
		tree = new TreePanel<ModelData>(store);
		tree.setDisplayProperty("name");
		tree.setAutoLoad(true);
		tree.setIconProvider(new ModelIconProvider<ModelData>() {
			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				if(model instanceof UserDatabaseDTO) {
					return IconImageBundle.ICONS.database();
				} else {
					return IconImageBundle.ICONS.activity();
				}
			}
		});
		tree.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

			@Override
			public void selectionChanged(SelectionChangedEvent<ModelData> se) {
				if(se.getSelectedItem() instanceof ActivityDTO &&
				   selection == null ||
				   selection != null && !selection.equals(se.getSelectedItem()))
				{
					selection = (ActivityDTO) se.getSelectedItem();
					fireEvent(Events.SelectionChange, new SelectionChangedEvent<ActivityDTO>(ActivityFilterPanel.this, 
						getSelection()));
				}
			}
		});
		this.add(tree);
	}

	public void addSelectionChangeListener(SelectionChangedListener<ActivityDTO> listener) {
	}
	
	private class Proxy extends RpcProxy<List<ModelData>> {

		@Override
		protected void load(Object loadConfig, final AsyncCallback<List<ModelData>> callback) {

			if(loadConfig == null) {
				service.execute(new GetSchema(), null, new AsyncCallback<SchemaDTO>() {
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					public void onSuccess(SchemaDTO schema) {
						callback.onSuccess((List)schema.getDatabases());
					}
				});
			} else if(loadConfig instanceof UserDatabaseDTO) {
				callback.onSuccess((List)((UserDatabaseDTO)loadConfig).getActivities());
			}
		}
	}

	@Override
	public List<ActivityDTO> getSelection() {
		return (List<ActivityDTO>) (selection == null ? Collections.emptyList() : Collections.singletonList(selection));
	}

	@Override
	public void addSelectionChangedListener(
			SelectionChangedListener<ActivityDTO> listener) {
		
		addListener(Events.SelectionChange, listener);
		
	}

	@Override
	public void removeSelectionListener(
			SelectionChangedListener<ActivityDTO> listener) {

		removeListener(Events.SelectionChange, listener);
	}

	@Override
	public void setSelection(List<ActivityDTO> selection) {
		tree.getSelectionModel().setSelection((List)selection);
	}

	public ActivityDTO getSelectedActivity() {
		return selection;
	}
}
