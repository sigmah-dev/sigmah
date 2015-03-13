package org.sigmah.client.ui.presenter.zone;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.AuthenticationBannerView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.page.Page;

/**
 * Authentication banner presenter displaying user's name and logout.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class AuthenticationBannerPresenter extends AbstractZonePresenter<AuthenticationBannerPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(AuthenticationBannerView.class)
	public static interface View extends ViewInterface {

		Panel getNamePanel();

		HasHTML getNameLabel();

		Panel getLogoutPanel();

		HasClickHandlers getLogoutHandler();
		
		InlineLabel getChangePasswordHandler();

	}

	@Inject
	public AuthenticationBannerPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.AUTH_BANNER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Logout action.
		view.getLogoutHandler().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.logout();
			}

		});
		
		// Change password action.
		view.getChangePasswordHandler().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.navigateRequest(Page.CHANGE_OWN_PASSWORD.request());
			}
		});
		
		// Change password handler visibility.
		view.getNamePanel().addDomHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				view.getChangePasswordHandler().setVisible(true);
			}
			
		}, MouseOverEvent.getType());
		
		view.getNamePanel().addDomHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				view.getChangePasswordHandler().setVisible(false);
			}
			
		}, MouseOutEvent.getType());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(final ZoneRequest zoneRequest) {

		// Updates view's widgets.
		view.getNameLabel().setHTML(auth().getUserEmail() + " ▾");
		view.getLogoutPanel().setVisible(!isAnonymous());
	}

}
