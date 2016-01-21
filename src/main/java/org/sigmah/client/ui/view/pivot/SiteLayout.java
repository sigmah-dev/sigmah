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
import org.sigmah.shared.dto.SiteDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.DispatchAsync;

/**
 * Layout fixed by site (indicators x time)
 * 
 * @author alexander
 *
 */
public class SiteLayout extends PivotLayout {
	
	private final SiteDTO site;

	public SiteLayout(SiteDTO siteModel) {
		super();
		this.site = siteModel;
	}
	
	@Override
	public String serialize() {
		return "S" + site.getId();
	}

	public SiteDTO getSite() {
		return site;
	}

	public static void deserializeSite(DispatchAsync dispatcher, String text, final AsyncCallback<PivotLayout> callback) {
		final int siteId = Integer.parseInt(text);
		dispatcher.execute(GetSites.byId(siteId), new AsyncCallback<SiteResult>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(SiteResult result) {
				if(result.getData().size() != 1) {
					callback.onFailure(new IllegalArgumentException("Site '" + siteId + "' does not exist."));
				} else {
					callback.onSuccess(new SiteLayout(result.getData().get(0)));
				}
			}
		});
	}

}
