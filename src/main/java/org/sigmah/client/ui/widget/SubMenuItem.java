package org.sigmah.client.ui.widget;

import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * Sub-menu item widget.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SubMenuItem extends Composite implements HasClickHandlers {

	private static final String CSS_SUBMENU_ITEM = "sub-menu-item";

	private final PageRequest request;
	private final HTML titlePanel;

	/**
	 * Initializes a new item navigating to the given {@code page}.
	 * 
	 * @param page
	 *          The {@link Page} to navigate to.
	 */
	public SubMenuItem(final Page page) {
		this(page != null ? page.request() : null);
	}

	/**
	 * Initializes a new item navigating to the given {@code request}.
	 * 
	 * @param request
	 *          The {@link PageRequest} to navigate to.
	 */
	public SubMenuItem(final PageRequest request) {

		this.request = request;

		titlePanel = new HTML();
		titlePanel.setStyleName(CSS_SUBMENU_ITEM);

		initWidget(titlePanel);
	}

	/**
	 * Returns the {@link PageRequest} associated to the current sub-menu item.
	 * 
	 * @return The {@link PageRequest} associated to the current sub-menu item, or {@code null}.
	 */
	public PageRequest getRequest() {
		return request;
	}

	/**
	 * Sets the sub-menu item title value (as well as tool-tip value).
	 * 
	 * @param title
	 *          The new title value.
	 */
	public void setMenuItemTitle(final String title) {
		setTitle(title);
		titlePanel.setHTML(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return titlePanel.addClickHandler(handler);
	}

}
