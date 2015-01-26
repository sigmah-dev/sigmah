package org.sigmah.client.ui.view.zone;

import org.sigmah.client.ui.presenter.zone.MessageBannerPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * MEssage banner view (not a real view, just a widgets set).
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class MessageBannerView extends AbstractView implements MessageBannerPresenter.View {

	// CSS.
	public static final String CSS_PANEL = "message-panel";
	public static final String CSS_MESSAGE = "message";

	private Panel messagePanel;
	private HTML messageLabel;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		messagePanel = new SimplePanel();
		messagePanel.addStyleName(CSS_PANEL);
		messagePanel.setVisible(false);

		messageLabel = new HTML();
		messageLabel.addStyleName(CSS_MESSAGE);
		messagePanel.add(messageLabel);

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
	public Panel getMessagePanel() {
		return messagePanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMessage(String html, MessageType type) {

		messagePanel.setVisible(ClientUtils.isNotBlank(html));
		messageLabel.setHTML(html);

		MessageType.applyStyleName(messagePanel, type);

	}

}
