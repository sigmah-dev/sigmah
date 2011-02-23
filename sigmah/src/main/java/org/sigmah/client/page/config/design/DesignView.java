/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.AttributeGroupDTO;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;


public class DesignView extends Component implements DesignPresenter.View {

    final protected Dispatcher service;
    protected EditorTreeGrid<ModelData> tree;
    protected ContentPanel formContainer;
    protected DesignGrid grid;
    
    DesignPresenter presenter;
	protected UserDatabaseDTO db;
	
  
    public DesignView(Dispatcher service) {
    	this.service = service;
    	grid = new DesignGrid();
    }
    

    @Override
    public void init(DesignPresenter presenter,UIConstants messages, TreeStore<ModelData> tree) {
    	this.presenter = presenter;
    	grid.init(presenter, presenter.treeStore);
    }
    
    
    @Override
    public void init(DesignPresenter presenter,UIConstants messages, UserDatabaseDTO db, TreeStore<ModelData> tree) {
    	init(presenter, messages, db, tree);
    	grid.init(presenter, presenter.treeStore);
    	doLayout(db);
    }
   
    
    @Override
    public void doLayout(UserDatabaseDTO db) {
    	this.db = db;
    	grid.setLayout(new BorderLayout());
		grid.setIcon(IconImageBundle.ICONS.design());
    	createFormContainer();
    	grid.setHeading(I18N.CONSTANTS.design() + " - " + db.getFullName());
    	presenter.initListeners(presenter.treeStore, null); 
    }
    
    
    private void createFormContainer() {
        formContainer = new ContentPanel();
        formContainer.setHeaderVisible(false);
        formContainer.setBorders(false);
        formContainer.setFrame(false);

        BorderLayoutData layout = new BorderLayoutData(Style.LayoutRegion.EAST);
        layout.setSplit(true);
        layout.setCollapsible(true);
        layout.setSize(375);
        layout.setMargins(new Margins(0, 0, 0, 5));

        grid.add(formContainer, layout);
    }

    
    protected Class formClassForSelection(ModelData sel) {
        if (sel instanceof ActivityDTO) {
            return ActivityForm.class;
        } else if (sel instanceof AttributeGroupDTO) {
            return AttributeGroupForm.class;
        } else if (sel instanceof IndicatorDTO) {
            return IndicatorForm.class;
        } else if (sel instanceof AttributeDTO) {
            return AttributeForm.class;
        }
        return null;
    }

    
    protected AbstractDesignForm createForm(ModelData sel) {
        if (sel instanceof ActivityDTO) {
            return new ActivityForm(service, db);
        } else if (sel instanceof AttributeGroupDTO) {
            return new AttributeGroupForm();
        } else if (sel instanceof AttributeDTO) {
            return new AttributeForm();
        } else if (sel instanceof IndicatorDTO) {
            return new IndicatorForm();
        }
        return null;
    }


    public void showForm(ModelData model) {
        // do we have the right form?
        Class formClass = formClassForSelection(model);

        AbstractDesignForm currentForm = null;
        if (formContainer.getItemCount() != 0) {
            currentForm = (AbstractDesignForm) formContainer.getItem(0);
        }

        if (formClass == null) {
            if (currentForm != null) {
                currentForm.getBinding().unbind();
                formContainer.removeAll();
            }
            return;
        } else {

            if (currentForm == null ||
                    (currentForm != null && !formClass.equals(currentForm.getClass()))) {

                if (currentForm != null) {
                    formContainer.removeAll();
                    currentForm.getBinding().unbind();
                }
                currentForm = createForm(model);
                currentForm.setReadOnly(!db.isDesignAllowed());
                currentForm.setHeaderVisible(false);
                currentForm.setBorders(false);
                currentForm.setFrame(false);
                currentForm.getBinding().setStore(tree.getStore());
                formContainer.add(currentForm);
                formContainer.layout();
            }
        }
        currentForm.getBinding().bind(model);
    }

    
    public FormDialogTether showNewForm(EntityDTO entity, FormDialogCallback callback) {
        AbstractDesignForm form = createForm(entity);
        form.getBinding().bind(entity);

        for (FieldBinding field : form.getBinding().getBindings()) {
            field.getField().clearInvalid();
        }
        
        FormDialogImpl dlg = new FormDialogImpl(form);
        dlg.setWidth(form.getPreferredDialogWidth());
        dlg.setHeight(form.getPreferredDialogHeight());
        dlg.setScrollMode(Style.Scroll.AUTOY);

        if (entity instanceof ActivityDTO) {
            dlg.setHeading(I18N.CONSTANTS.newActivity());
        } else if (entity instanceof AttributeGroupDTO) {
            dlg.setHeading(I18N.CONSTANTS.newAttributeGroup());
        } else if (entity instanceof AttributeDTO) {
            dlg.setHeading(I18N.CONSTANTS.newAttribute());
        } else if (entity instanceof IndicatorDTO) {
            dlg.setHeading(I18N.CONSTANTS.newIndicator());
        }

        dlg.show(callback);
        return dlg;
    }


	@Override
	public void setActionEnabled(String actionId, boolean enabled) {
		grid.setActionEnabled(actionId, enabled);
		
	}


	@Override
	public void confirmDeleteSelected(ConfirmCallback callback) {
		grid.confirmDeleteSelected(callback);
		
	}


	@Override
	public ModelData getSelection() {
		return grid.getSelection();
	}


	public AsyncMonitor getDeletingMonitor() {
		return grid.getDeletingMonitor();
	}


	public AsyncMonitor getSavingMonitor() {
		return grid.getSavingMonitor();
	}

}
