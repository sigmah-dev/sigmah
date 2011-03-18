package org.sigmah.client.page.project.pivot;

import java.util.Collection;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndicatorFilterCombo extends ComboBox<IndicatorDTO> {

	private final Dispatcher dispatcher;
	private final ListLoader loader;
	private int databaseId;
	
	public IndicatorFilterCombo(Dispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
		this.loader = new BaseListLoader<ListLoadResult<ModelData>>(new IndicatorProxy());
		setStore(new ListStore(loader));
		setDisplayField("name");
		setWidth(150);
		setMinListWidth(400);
	}
	
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
	
	private class IndicatorProxy extends RpcProxy<ListLoadResult<ModelData>> {

		@Override
		protected void load(Object loadConfig,
				final AsyncCallback<ListLoadResult<ModelData>> callback) {
			
			dispatcher.execute(new GetIndicators(databaseId), null, new AsyncCallback<IndicatorListResult>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(IndicatorListResult result) {
					callback.onSuccess((ListLoadResult)result);
				}
			});
			
		}
	}

	public int getSelectedIndicatorId() {
		assert getValue() != null : "No indicator is selected";
	
		return getValue().getId();
		
	}
	
}
