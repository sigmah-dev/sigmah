package org.sigmah.client.ui.presenter.project.indicator;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.UpdateEntity;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.base.EntityDTO;

public class SavingHelper {

	public static void save(DispatchAsync dispatcher, final Store<? extends ModelData> store, Component component) {
		dispatcher.execute(createUpdateCommand(store), new AsyncCallback<ListResult<Result>>() {

			@Override
			public void onFailure(Throwable caught) {
				// handled by monitor
			}

			@Override
			public void onSuccess(ListResult<Result> result) {
				store.commitChanges();
			}
		}, new LoadingMask(component, I18N.CONSTANTS.saving()));	
	}
	
    public static BatchCommand createUpdateCommand(Store<? extends ModelData> store) {
		BatchCommand batch = new BatchCommand();
		for (Record record : store.getModifiedRecords()) {
			if(record.getModel() instanceof EntityDTO) {
				EntityDTO entity = (EntityDTO) record.getModel();
				batch.add(new UpdateEntity(entity, getChangedProperties(record)));
				
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
