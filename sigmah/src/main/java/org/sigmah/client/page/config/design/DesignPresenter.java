/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import java.util.List;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.grid.AbstractEditorGridPresenter;
import org.sigmah.client.page.common.grid.TreeGridView;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.DbPageState;
import org.sigmah.client.page.project.ProjectPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.data.Loader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;


/**
 * Presenter for the Design Page, which enables the user to define UserDatabases and their
 * Activities, Attributes, and Indicators.
 *
 * @author Alex Bertram
 */
public class DesignPresenter extends AbstractEditorGridPresenter<ModelData> implements Page, SubPresenter {
	
    public static final PageId PAGE_ID = new PageId("design");

    @ImplementedBy(DesignView.class)
    public interface View extends TreeGridView<DesignPresenter, ModelData> {
    	
        public void init(DesignPresenter presenter, UIConstants msg, UserDatabaseDTO db, TreeStore<ModelData> ts);
        
        public void init(DesignPresenter presenter, UIConstants msg, TreeStore<ModelData> ts);
        
        public void doLayout(UserDatabaseDTO db);
        
        public FormDialogTether showNewForm(EntityDTO entity, FormDialogCallback callback);
      
    }
    
    private View view;
    private final EventBus eventBus;
    private final Dispatcher service;
    private final UIConstants messages;
    protected TreeStore<ModelData> treeStore;
    protected final ProjectPresenter projectPresenter; 
	protected UserDatabaseDTO db;

    @Inject
    public DesignPresenter(EventBus eventBus, Dispatcher service, IStateManager stateMgr,
                    View view, UIConstants messages) {
        super(eventBus, service, stateMgr, view);
        this.eventBus = eventBus;
        this.service = service;
        this.view = view;
        this.messages = messages;
        this.projectPresenter = null;
    }
    
     
    public DesignPresenter(EventBus eventBus, Dispatcher service, UIConstants messages,  View view, ProjectPresenter projectPresenter, TreeStore<ModelData> tree) {
        super(eventBus, service, null, view);
        this.eventBus = eventBus;
        this.service = service;
        this.messages = messages;
        this.projectPresenter = projectPresenter;
        this.treeStore = tree;
        this.view = view;
    }

    public void go(UserDatabaseDTO db) {
    	this.db = db;
    	this.view.doLayout(db);
    	
    	// this is used by activity info
    	if (treeStore == null) { 
    			this.treeStore = new TreeStore<ModelData>();
    			fillStore(messages);
    			this.view.setActionEnabled(UIActions.delete, false);
    	}
    	initListeners(treeStore, null);

    }
    
    @Override
    public Store<ModelData> getStore() {
        return this.treeStore;
    }

    public TreeStore<ModelData> getTreeStore() {
        return this.treeStore;
    }

    public boolean navigate(PageState place) {
        return place instanceof DbPageState &&
                place.getPageId().equals(PAGE_ID) &&
                ((DbPageState) place).getDatabaseId() == projectPresenter.getCurrentProjectDTO().getId();
    }

    public void onNodeDropped(ModelData source) {

        // update sortOrder
        ModelData parent = treeStore.getParent(source);
        List<ModelData> children = parent == null ? treeStore.getRootItems() : treeStore.getChildren(parent);

        for (int i = 0; i != children.size(); ++i) {
            Record record = treeStore.getRecord(children.get(i));
            record.set("sortOrder", i);
        }

    }

    public void onNew(String entityName) {

        final EntityDTO newEntity;
        ModelData parent;

        ModelData selected = view.getSelection();

        if ("Activity".equals(entityName)) {
        	UserDatabaseDTO d = new UserDatabaseDTO();
        	d.setId(db.getId());
            newEntity = new ActivityDTO(d);
            newEntity.set("databaseId", db.getId());
            parent = null;

        } else if ("AttributeGroup".equals(entityName)) {
            ActivityDTO activity = findActivityFolder(selected);

            newEntity = new AttributeGroupDTO();
            newEntity.set("activityId", activity.getId());
            parent = treeStore.getChild(activity, 0);

        } else if ("Attribute".equals(entityName)) {
            AttributeGroupDTO group = findAttributeGroupNode(selected);

            newEntity = new AttributeDTO();
            newEntity.set("attributeGroupId", group.getId());
            parent = group;

        } else if ("Indicator".equals(entityName)) {
            ActivityDTO activity = findActivityFolder(selected);

            IndicatorDTO newIndicator = new IndicatorDTO();
            newIndicator.setCollectIntervention(true);
            newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);

            newEntity = newIndicator;
            newEntity.set("activityId", activity.getId());

            parent = treeStore.getChild(activity, 1);

        } else {
            return; // TODO log error
        }

        createEntity(parent, newEntity);
    }

    private void createEntity(final ModelData parent, final EntityDTO newEntity) {
        view.showNewForm(newEntity, new FormDialogCallback() {
            @Override
            public void onValidated(final FormDialogTether tether) {

                service.execute(new CreateEntity(newEntity), tether, new AsyncCallback<CreateResult>() {
                    public void onFailure(Throwable caught) {

                    }

                    public void onSuccess(CreateResult result) {
                        newEntity.set("id", result.getNewId()); // todo add setId to EntityDTO interface

                        if (parent == null) {
                        	treeStore.add(newEntity, false);
                        } else {
                        	treeStore.add(parent, newEntity, false);
                        }

                        if (newEntity instanceof ActivityDTO) {
                        	treeStore.add(newEntity, new AttributeGroupFolder((ActivityDTO) newEntity, messages.attributes()), false);
                        	treeStore.add(newEntity, new IndicatorFolder((ActivityDTO) newEntity, messages.indicators()), false);
                        }

                        tether.hide();

                        eventBus.fireEvent(AppEvents.SchemaChanged);
                    }
                });
            }
        });
    }

    protected ActivityDTO findActivityFolder(ModelData selected) {

        while (!(selected instanceof ActivityDTO)) {
            selected = treeStore.getParent(selected);
        }

        return (ActivityDTO) selected;
    }

    protected AttributeGroupDTO findAttributeGroupNode(ModelData selected) {
        if (selected instanceof AttributeGroupDTO) {
            return (AttributeGroupDTO) selected;
        }
        if (selected instanceof AttributeDTO) {
            return (AttributeGroupDTO) treeStore.getParent(selected);
        }
        throw new AssertionError("not a valid selection to add an attribute !");

    }

    @Override
    protected void onDeleteConfirmed(final ModelData model) {
        service.execute(new Delete((EntityDTO) model), view.getDeletingMonitor(), new AsyncCallback<VoidResult>() {
            public void onFailure(Throwable caught) {

            }

            public void onSuccess(VoidResult result) {
            	treeStore.remove(model);
                eventBus.fireEvent(AppEvents.SchemaChanged);
            }
        });
    }

    @Override
	protected void initListeners(Store store, Loader loader) {
		super.initListeners(store, loader);
	}

	@Override
    protected String getStateId() {
        return "Design" + projectPresenter.getCurrentProjectDTO().getId();
    }

    @Override
    protected Command createSaveCommand() {
        BatchCommand batch = new BatchCommand();

        for (ModelData model : treeStore.getRootItems()) {
            prepareBatch(batch, model);
        }
        return batch;
    }

    protected void prepareBatch(BatchCommand batch, ModelData model) {
        if (model instanceof EntityDTO) {
            Record record = treeStore.getRecord(model);
            if (record.isDirty()) {
                batch.add(new UpdateEntity((EntityDTO) model, this.getChangedProperties(record)));
            }
        }

        for (ModelData child : treeStore.getChildren(model)) {
            prepareBatch(batch, child);
        }
    }

    public void onSelectionChanged(ModelData selectedItem) {
    	
    	view.setActionEnabled(UIActions.delete, db.isDesignAllowed() &&
    			selectedItem instanceof EntityDTO);
    
    }

    public PageId getPageId() {
        return PAGE_ID;
    }

    public Object getWidget() {
        return view;
    }

    @Override
    protected void onSaved() {
        eventBus.fireEvent(AppEvents.SchemaChanged);
    }

	@Override
	public Component getView() {
		view.init(this, messages, treeStore);
		view.setActionEnabled(UIActions.delete, false); 
		return (Component)view;
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
	public void shutdown() {
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
	
}
