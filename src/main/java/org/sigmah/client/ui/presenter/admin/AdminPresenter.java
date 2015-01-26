package org.sigmah.client.ui.presenter.admin;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.presenter.base.HasSubPresenter;
import org.sigmah.client.ui.view.admin.AdminView;
import org.sigmah.client.ui.view.base.HasSubView;
import org.sigmah.client.ui.widget.SubMenuItem;
import org.sigmah.client.ui.widget.SubMenuWidget;
import org.sigmah.client.ui.widget.SubMenuWidget.SubMenuListener;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * <p>
 * <b>UI parent</b> presenter which manages the {@link AdminView}.
 * </p>
 * <p>
 * Does not respond to a page token. Manages sub-presenters.
 * </p>
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AdminPresenter extends AbstractPresenter<AdminPresenter.View> implements HasSubPresenter<AdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(AdminView.class)
	public static interface View extends HasSubView {

		/**
		 * Returns the sub-menu widget.
		 * 
		 * @return The sub-menu widget.
		 */
		SubMenuWidget getSubMenuWidget();

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected AdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// SubMenu listener.
		view.getSubMenuWidget().addListener(new SubMenuListener() {

			@Override
			public void onSubMenuClick(final SubMenuItem menuItem) {

				final PageRequest currentPageRequest = injector.getPageManager().getCurrentPageRequest(false);
				eventBus.navigateRequest(menuItem.getRequest().addAllParameters(currentPageRequest.getParameters(true)));
			}

		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSubPresenterRequest(final PageRequest subPageRequest) {
		view.getSubMenuWidget().initializeMenu(subPageRequest.getPage());
	}

}
