package org.sigmah.client.page.project.pivot;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Layout fixed by site (indicators x time)
 * 
 * @author alexander
 *
 */
public class SiteLayout extends PivotLayout {
	
	private SiteDTO site;

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

	public static void deserializeSite(Dispatcher dispatcher, String text, final AsyncCallback<PivotLayout> callback) {
		int siteId = Integer.parseInt(text);
		dispatcher.execute(GetSites.byId(siteId), null, new AsyncCallback<SiteResult>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(SiteResult result) {
				if(result.getData().size() != 1) {
					callback.onFailure(new RuntimeException("site does not exist"));
				} else {
					callback.onSuccess(new SiteLayout(result.getData().get(0)));
				}
			}
		});
	}
		


}
