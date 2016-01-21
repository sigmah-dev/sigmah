package org.sigmah.client.ui.presenter.zone;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


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
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.ProfileUtils;

/**
 * Authentication banner presenter displaying user's name and logout.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class AuthenticationBannerPresenter extends AbstractZonePresenter<AuthenticationBannerPresenter.View> {

	private boolean canChangeOwnPassword;
	
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
                N10N.confirmation(I18N.CONSTANTS.logout(), I18N.CONSTANTS.logoutConfirmation(), new ConfirmCallback() {

                    @Override
                    public void onAction() {
                        eventBus.logout();
                    }
                    
                });
				
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
				view.getChangePasswordHandler().setVisible(canChangeOwnPassword);
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
		
		// BUGFIX #739: Not showing the menu if the user can't edit his password.
		canChangeOwnPassword = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.CHANGE_PASSWORD);
		
		// Updates view's widgets.
		final String html = canChangeOwnPassword ? auth().getUserEmail() + " ▾" : auth().getUserEmail();
		view.getNameLabel().setHTML(html);
		view.getLogoutPanel().setVisible(!isAnonymous());
	}

}
