package org.sigmah.client.ui.view.zone;

import org.sigmah.client.ui.presenter.zone.OrganizationBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Organization banner view (not a real view, just a widgets set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class OrganizationBannerView extends AbstractView implements OrganizationBannerPresenter.View {

	private Panel logoPanel;
	private Panel namePanel;
	private HTML nameLabel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		logoPanel = new SimplePanel();
		logoPanel.getElement().setId("orglogo");

		namePanel = new SimplePanel();
		namePanel.getElement().setId("orgname");

		nameLabel = new HTML();
		namePanel.add(nameLabel);

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
	public Panel getLogoPanel() {
		return logoPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Panel getNamePanel() {
		return namePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML getNameLabel() {
		return nameLabel;
	}

}
