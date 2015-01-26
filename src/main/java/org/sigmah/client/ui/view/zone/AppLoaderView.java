package org.sigmah.client.ui.view.zone;

import org.sigmah.client.ui.presenter.zone.AppLoaderPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Application loader view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class AppLoaderView extends AbstractView implements AppLoaderPresenter.View {

	// CSS.
	private static final String CSS_LOADING = "loading";

	private Panel loaderPanel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		loaderPanel = new SimplePanel();
		loaderPanel.getElement().setId("app-loader");

	}

	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLoading(boolean loading) {
		if (loading) {
			loaderPanel.addStyleName(CSS_LOADING);
		} else {
			loaderPanel.removeStyleName(CSS_LOADING);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getLoaderPanel() {
		return loaderPanel;
	}

}
