package org.sigmah.client.ui.view;

import org.sigmah.client.ui.presenter.HelpPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.popup.PopupWidget;

import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Singleton;

/**
 * Credits frame view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class HelpView extends AbstractPopupView<PopupWidget> implements HelpPresenter.View {

	// CSS.
	private static final String ID_HELP = "help";
	private static final String CSS_HELP_CONTENT = "help-content";

	private IFrameElement iframe;

	/**
	 * Builds the view.
	 */
	public HelpView() {
		super(new PopupWidget(true));
		popup.setWidth("700px");
		popup.setHeight("557px");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		iframe = IFrameElement.as(DOM.createIFrame());

		final SimplePanel panel = new SimplePanel();
		panel.addStyleName(CSS_HELP_CONTENT);
		panel.getElement().appendChild(iframe);

		final ScrollPanel mainPanel = new ScrollPanel(panel);
		mainPanel.getElement().setId(ID_HELP);
		mainPanel.setAlwaysShowScrollBars(false);

		initPopup(mainPanel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHelpURL(String url) {
		iframe.setSrc(url);
	}

}
