package org.sigmah.client.page.common.grid;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.UpdateEntity;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;

public class SavingHelper {

    public static Map<String, Object> getChangedProperties(Record record) {
        Map<String, Object> changes = new HashMap<String, Object>();

        for (String property : record.getChanges().keySet()) {
            changes.put(property, record.get(property));
        }
        return changes;
    }
	
    public static BatchCommand createUpdateCommand(ListStore<? extends ModelData> store) {
		BatchCommand batch = new BatchCommand();
		for (Record record : store.getModifiedRecords()) {
			batch.add(new UpdateEntity("Site", (Integer) record.get("id"), getChangedProperties(record)));
		}
		return batch;
    }
    
}
