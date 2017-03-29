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
