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
import org.sigmah.client.page.Page;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

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
		
		// --
		// Sub menu visibility.
		// --
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_USERS, GlobalPermissionEnum.MANAGE_USERS);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_ORG_UNITS, GlobalPermissionEnum.MANAGE_ORG_UNITS);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_PROJECTS_MODELS, GlobalPermissionEnum.MANAGE_PROJECT_MODELS);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_ORG_UNITS_MODELS, GlobalPermissionEnum.MANAGE_ORG_UNIT_MODELS);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_REPORTS_MODELS, GlobalPermissionEnum.MANAGE_REPORT_MODELS);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_CATEGORIES, GlobalPermissionEnum.MANAGE_CATEGORIES);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_IMPORTATION_SCHEME, GlobalPermissionEnum.MANAGE_IMPORTATION_SCHEMES);
		view.getSubMenuWidget().setRequiredPermissions(Page.ADMIN_PARAMETERS, GlobalPermissionEnum.MANAGE_SETTINGS);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSubPresenterRequest(final PageRequest subPageRequest) {
		view.getSubMenuWidget().initializeMenu(subPageRequest.getPage(), auth());
	}

}
