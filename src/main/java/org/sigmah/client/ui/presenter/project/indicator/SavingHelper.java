package org.sigmah.client.ui.presenter.project.indicator;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.base.EntityDTO;

public class SavingHelper {

	public static void save(DispatchAsync dispatcher, final Store<? extends ModelData> store, Component component) {
		dispatcher.execute(createUpdateCommand(store), new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandSuccess(ListResult<Result> result) {
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
