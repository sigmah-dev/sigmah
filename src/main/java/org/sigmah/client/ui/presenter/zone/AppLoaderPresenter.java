package org.sigmah.client.ui.presenter.zone;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.AppLoaderView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Application loader presenter displaying loading animation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class AppLoaderPresenter extends AbstractZonePresenter<AppLoaderPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(AppLoaderView.class)
	public static interface View extends ViewInterface {

		Panel getLoaderPanel();

		/**
		 * Sets the application loader state.
		 * 
		 * @param loading
		 *          {@code true} to enable application loader.
		 */
		void setLoading(boolean loading);

	}

	@Inject
	public AppLoaderPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.APP_LOADER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(ZoneRequest zoneRequest) {

		final Boolean loading = zoneRequest.getData(RequestParameter.CONTENT);

		if (Log.isTraceEnabled()) {
			Log.trace("Application loader update request with loading value '" + loading + "'.");
		}

		view.setLoading(ClientUtils.isTrue(loading));

	}

}
