/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.menu.Menu;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.i18n.UIConstants;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.common.grid.ConfirmCallback;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.shared.dto.EntityDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createNiceMock;
import static org.easymock.EasyMock.replay;
/*
 * @author Alex Bertram
 */

public class MockDesignTree implements DesignPresenter.View {

    public ModelData selection = null;
    public Map<String, Object> newEntityProperties = new HashMap<String, Object>();
    public TreeStore<ModelData> ts = null;
    
    public void init(DesignPresenter presenter, UserDatabaseDTO db, TreeStore store) {
    	
    }

    public FormDialogTether showNewForm(EntityDTO entity, FormDialogCallback callback) {

        for (String property : newEntityProperties.keySet()) {
            ((ModelData) entity).set(property, newEntityProperties.get(property));
        }

        FormDialogTether tether = createNiceMock(FormDialogTether.class);
        replay(tether);

        callback.onValidated(tether);
        return tether;
    }

    protected void mockEditEntity(EntityDTO entity) {

    }

    public void setActionEnabled(String actionId, boolean enabled) {

    }

    public void confirmDeleteSelected(ConfirmCallback callback) {

    }

    public ModelData getSelection() {
        return selection;
    }

    public AsyncMonitor getDeletingMonitor() {
        return null;
    }

    public AsyncMonitor getSavingMonitor() {
        return null;
    }

	@Override
	public void init(DesignPresenter presenter, UIConstants msg,
			UserDatabaseDTO db, TreeStore<ModelData> ts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(DesignPresenter presenter, UIConstants msg,
			TreeStore<ModelData> ts) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doLayout(UserDatabaseDTO db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ActionToolBar getToolbar() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initNewMenu(Menu menu, SelectionListener<MenuEvent> listener) {
		// TODO Auto-generated method stub
		
	}

}
