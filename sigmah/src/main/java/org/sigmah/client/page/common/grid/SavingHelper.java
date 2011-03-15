package org.sigmah.client.page.common.grid;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SavingHelper {

	public static void save(Dispatcher dispatcher, final Store<? extends ModelData> store, Component component) {
		dispatcher.execute(createUpdateCommand(store), 
				new MaskingAsyncMonitor(component, I18N.CONSTANTS.saving()), new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// handled by monitor
			}

			@Override
			public void onSuccess(BatchResult result) {
				store.commitChanges();
			}
		});	
	}
	
    public static BatchCommand createUpdateCommand(Store<? extends ModelData> store) {
		BatchCommand batch = new BatchCommand();
		for (Record record : store.getModifiedRecords()) {
			if(record.getModel() instanceof EntityDTO) {
				EntityDTO entity = (EntityDTO) record.getModel();
				batch.add(new UpdateEntity(entity.getEntityName(), entity.getId(), getChangedProperties(record)));
				
			}
		}
		return batch;
    }
    
 
    public static Map<String, Object> getChangedProperties(Record record) {
        Map<String, Object> changes = new HashMap<String, Object>();

        for (String property : record.getChanges().keySet()) {
            changes.put(property, record.get(property));
        }
        return changes;
    }
    
}
