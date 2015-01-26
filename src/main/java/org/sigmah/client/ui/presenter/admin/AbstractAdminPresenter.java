package org.sigmah.client.ui.presenter.admin;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter.SubPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;

/**
 * Abstract <b>functional parent</b> presenter for the administration presenters.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <V>
 *          View type.
 */
public abstract class AbstractAdminPresenter<V extends AbstractAdminPresenter.View> extends AbstractPagePresenter<V> implements SubPresenter<AdminPresenter> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewInterface {

		// No methods yet.

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	protected AbstractAdminPresenter(final V view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AdminPresenter getParentPresenter() {
		return injector.getAdminPresenter();
	}

}
