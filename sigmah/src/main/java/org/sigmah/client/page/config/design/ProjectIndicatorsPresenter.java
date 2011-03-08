package org.sigmah.client.page.config.design;

import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.entry.SiteEditor;
import org.sigmah.client.page.entry.SiteGrid;
import org.sigmah.client.page.entry.SiteGridPageState;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.DataReader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ProjectIndicatorsPresenter implements SubPresenter {

	private final ProjectPresenter projectPresenter;
	private final Dispatcher service;
	private final EventBus eventBus;

	private SiteEditor siteEditor;
	private DesignPresenter designPresenter;
	private SchemaDTO schema;
	private UserDatabaseDTO db;
	private View view;

	protected TreeStore<ModelData> treeStore = null;

	public ProjectIndicatorsPresenter(Dispatcher dispatcher, EventBus eventBus,
			ProjectPresenter projectPresenter) {
		this.service = dispatcher;
		this.projectPresenter = projectPresenter;
		this.eventBus = eventBus;
	}

	public interface View {
		
		public ActionToolBar getDesignTreeToolBar();

		public Button getNewIndicatorButton();

		public Button getNewGroupButton();

		public Button getReloadButton();

		public Button getShowSiteMapButton();

		public Button getShowSiteTableButton();

		public Button getLoadSitesButton();

		public TreeStore<ModelData> getTreeStore();

	}

	private ActivityDTO getCurrentActivity() {
		if (db.getActivities() != null && db.getActivities().size() > 0) {
			// TODO fix me
			return db.getActivities().get(0);
		}
		return null;
	}

	private void wireViews() {
		
		view.getNewIndicatorButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		view.getNewGroupButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		view.getReloadButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		view.getShowSiteMapButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});

		view.getLoadSitesButton().addListener(Events.OnClick,
				new Listener<ButtonEvent>() {
					@Override
					public void handleEvent(ButtonEvent be) {

					}
				});
	}

	@Override
	public Component getView() {
		if (view == null) {
			// create a tree store
			treeStore = new TreeStore<ModelData>(new BaseTreeLoader<ModelData>(
					new Proxy()));
			// treeStore.getLoader().load();

			SiteGrid siteGrid = new SiteGrid();
			siteGrid.setHeaderVisible(false);

			this.siteEditor = new SiteEditor(eventBus, service, null, siteGrid);
			this.designPresenter = new DesignPresenter(eventBus, service,
					I18N.CONSTANTS, new DesignView(service), projectPresenter,
					treeStore);
			this.view = new ProjectIndicatorsView(siteEditor, designPresenter);
		
		}
		return (Component) this.view;
	}

	@Override
	public void discardView() {
		view = null;
	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub
	}

	private void fillStore(UIConstants messages) {
		for (ActivityDTO activity : db.getActivities()) {
			ActivityDTO activityNode = new ActivityDTO(activity);
			treeStore.add(activityNode, false);

			AttributeGroupFolder attributeFolder = new AttributeGroupFolder(
					activityNode, messages.attributes());
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
					messages.indicators());
			treeStore.add(activityNode, indicatorFolder, false);

			for (IndicatorDTO indicator : activity.getIndicators()) {
				IndicatorDTO indicatorNode = new IndicatorDTO(indicator);
				treeStore.add(indicatorFolder, indicatorNode, false);
			}
		}
	}

	private void finishLoad(UserDatabaseDTO db) {
		fillStore(I18N.CONSTANTS);
		this.designPresenter.go(db);
		SiteGridPageState state = new SiteGridPageState();
		state.setPageNum(1);
		this.siteEditor.go(state, getCurrentActivity());
	}

	private class Proxy implements DataProxy<List<ModelData>> {

		@Override
		public void load(DataReader<List<ModelData>> reader, Object loadConfig,
				AsyncCallback<List<ModelData>> callback) {
			if (db == null) {
				service.execute(new GetSchema(), null,
						new AsyncCallback<SchemaDTO>() {

							public void onSuccess(SchemaDTO result) {
								schema = result;
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
}
