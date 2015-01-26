package org.sigmah.client.ui.view.pivot;

import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.SiteResult;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;

/**
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class SiteFilterCombo extends ComboBox<SiteDTO> {

	private final DispatchAsync dispatcher;
	private final ListLoader loader;
	private int databaseId;
	
	public SiteFilterCombo(DispatchAsync dispatcher) {
		super();
		this.dispatcher = dispatcher;
		this.loader = new BaseListLoader<ListLoadResult<ModelData>>(new SiteProxy());
		setStore(new ListStore(loader));
		setDisplayField("locationName");
	}
	
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
	
	private class SiteProxy extends RpcProxy<ListLoadResult<ModelData>> {

		@Override
		protected void load(Object loadConfig,
				final AsyncCallback<ListLoadResult<ModelData>> callback) {
			
			Filter filter = new Filter();
			filter.addRestriction(DimensionType.Database, databaseId);
			
			GetSites request = new GetSites();
			request.setFilter(filter);
			
			dispatcher.execute(request, new AsyncCallback<SiteResult>() {

				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(SiteResult result) {
					callback.onSuccess((ListLoadResult)result);
				}
			});
			
		}
	}

	public int getSelectedSiteId() {
		assert getValue() != null : "no selection!";
		
		return (Integer)getValue().get("id");
	}

}
