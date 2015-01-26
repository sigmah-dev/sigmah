package org.sigmah.client.ui.view.pivot;

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
