package org.sigmah.client.ui.view.pivot;

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
