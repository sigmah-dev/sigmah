package org.sigmah.client.ui.view;

import org.sigmah.client.ui.presenter.MockUpPresenter;
import org.sigmah.client.ui.view.base.AbstractView;

import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.Singleton;

/**
 * Mock-up view.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class MockUpView extends AbstractView implements MockUpPresenter.View {

	// CSS.
	private static final String ID_PANEL = "mockup";

	private Panel panel;
	private CaptionPanel currentSection;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		panel = new FlowPanel();
		panel.getElement().setId(ID_PANEL);

		add(panel);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		panel.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addSection(String title) {
		currentSection = new CaptionPanel("&nbsp;" + title + "&nbsp;", true);
		panel.add(currentSection);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWidget(IsWidget w) {
		if (currentSection == null) {
			addSection(null);
		}
		currentSection.add(w);
	}

}
