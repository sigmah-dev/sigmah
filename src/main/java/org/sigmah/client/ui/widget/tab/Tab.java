package org.sigmah.client.ui.widget.tab;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabBar;

/**
 * A simple tab displayable in a {@link TabBar}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class Tab extends Composite implements HasClickHandlers {

	private static final String CSS_TAB = "tab";
	private static final String CSS_TAB_CLOSEABLE = "closeable";
	public static final String CSS_TAB_ACTIVE = "active";
	private static final String CSS_TAB_LEFT = "tab-left";
	private static final String CSS_TAB_MIDDLE = "tab-middle";
	private static final String CSS_TAB_RIGHT = "tab-right";
	private static final String CSS_TAB_CLOSE = "tab-close";

	private final TabId id;
	private final boolean closeable;

	private final Panel panel;
	private final HTML leftPanel;
	private final HTML titlePanel;
	private final HTML rightPanel;
	private FocusPanel closePanel;

	public Tab(TabId id, boolean closeable, String... styleNames) {

		leftPanel = new HTML();
		leftPanel.addStyleName(CSS_TAB_LEFT);

		titlePanel = new HTML();
		titlePanel.addStyleName(CSS_TAB_MIDDLE);

		rightPanel = new HTML();
		rightPanel.addStyleName(CSS_TAB_RIGHT);

		panel = new AbsolutePanel();
		panel.addStyleName(CSS_TAB);
		if (styleNames != null && styleNames.length > 0) {
			for (final String styleName : styleNames) {
				panel.addStyleName(styleName);
			}
		}

		panel.add(leftPanel);
		panel.add(titlePanel);
		panel.add(rightPanel);

		if (closeable) {

			panel.addStyleName(CSS_TAB_CLOSEABLE);

			closePanel = new FocusPanel();
			closePanel.setStyleName(CSS_TAB_CLOSE);

			panel.add(closePanel);

		}

		this.id = id;
		this.closeable = closeable;

		initWidget(panel);

	}

	public TabId getId() {
		return id;
	}

	protected void setTabTitle(String title) {
		setTitle(title);
		titlePanel.setHTML(title);
	}

	public boolean isCloseable() {
		return closeable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		leftPanel.addClickHandler(handler);
		rightPanel.addClickHandler(handler);
		return titlePanel.addClickHandler(handler);
	}

	protected HandlerRegistration addCloseHandler(ClickHandler handler) {
		return closePanel.addClickHandler(handler);
	}

}
