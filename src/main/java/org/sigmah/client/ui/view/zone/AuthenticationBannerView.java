package org.sigmah.client.ui.view.zone;

import com.google.gwt.dom.client.Style;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.zone.AuthenticationBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Authentication banner view (not a real view, just a widgets set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class AuthenticationBannerView extends AbstractView implements AuthenticationBannerPresenter.View {

	private Panel usernamePanel;
	private HTML usernameLabel;
	private Panel logoutPanel;
	private Anchor logoutHandler;
	private InlineLabel changePasswordHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		usernamePanel = new FlowPanel();
		usernamePanel.getElement().setId("username");

		usernameLabel = new HTML();
		usernamePanel.add(usernameLabel);
		
		changePasswordHandler = new InlineLabel(I18N.CONSTANTS.changePassword());
		changePasswordHandler.setVisible(false);
		usernamePanel.add(changePasswordHandler);

		logoutPanel = new SimplePanel();
		logoutPanel.getElement().setId("userlogout");

		logoutHandler = new Anchor(I18N.CONSTANTS.logout());
		logoutPanel.add(logoutHandler);

		// initWidget(); Useless.

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onViewRevealed() {
		// Nothing to do here.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getNamePanel() {
		return usernamePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML getNameLabel() {
		return usernameLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getLogoutPanel() {
		return logoutPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getLogoutHandler() {
		return logoutHandler;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InlineLabel getChangePasswordHandler() {
		return changePasswordHandler;
	}

}
