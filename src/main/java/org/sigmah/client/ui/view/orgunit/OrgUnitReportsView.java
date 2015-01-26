package org.sigmah.client.ui.view.orgunit;

import org.sigmah.client.ui.presenter.orgunit.OrgUnitReportsPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.ViewInterface;

import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class OrgUnitReportsView extends AbstractView implements OrgUnitReportsPresenter.View {

	/**
	 * The reports presenter's view.
	 */
	// Provided before current view initialization.
	private ViewInterface view;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void provideReportsView(ViewInterface view) {
		this.view = view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		add(view);
	}

}
