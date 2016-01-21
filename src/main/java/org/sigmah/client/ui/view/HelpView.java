package org.sigmah.client.ui.view;

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
