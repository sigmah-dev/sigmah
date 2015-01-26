package org.sigmah.client.ui.view.pivot;

import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.DispatchAsync;

/**
 * Layout where indicator is fixed (sites x time)
 * 
 * @author alexander
 *
 */
public class IndicatorLayout extends PivotLayout {

	private final IndicatorDTO indicator;

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

	public static void deserializeIndicator(DispatchAsync dispatcher, int projectId, String text, final AsyncCallback<PivotLayout> callback) {
		final int id = Integer.parseInt(text); 
		dispatcher.execute(new GetIndicators(projectId), new AsyncCallback<IndicatorListResult>() {

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
				callback.onFailure(new IllegalArgumentException("Indicator '" + id + "' was not found."));
			}
		});
	}

}
