package org.sigmah.client.page.project.pivot;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Layout where indicator is fixed (sites x time)
 * 
 * @author alexander
 *
 */
public class IndicatorLayout extends PivotLayout {

	private IndicatorDTO indicator;


	public IndicatorLayout(IndicatorDTO indicator) {
		super();
		this.indicator = indicator;
	}


	@Override
	public String serialize() {
		return "I" + indicator.getId();
	}
	
	public IndicatorDTO getIndicator() {
		return indicator;
	}


	public static void deserializeIndicator(Dispatcher dispatcher, int projectId, String text, final AsyncCallback<PivotLayout> callback) {
		final int id = Integer.parseInt(text); 
		dispatcher.execute(GetIndicators.forDatabase(projectId), null, new AsyncCallback<IndicatorListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			@Override
			public void onSuccess(IndicatorListResult result) {
				for(IndicatorDTO indicator : result.getData()) {
					if(indicator.getId() == id) {
						callback.onSuccess(new IndicatorLayout(indicator));
						return;
					}
				}
				callback.onFailure(new RuntimeException());
			}
		});
	}
	
	
	

}
