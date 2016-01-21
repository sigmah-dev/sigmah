package org.sigmah.client.ui.widget;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.history.HistoryTokenDTO;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.google.gwt.user.client.ui.HTML;

/**
 * A simple text element (with a div inner-tag) to display a history token.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class HistoryTokenText extends HTML {

	public HistoryTokenText() {
		super();
		addStyleName("history-textarea");
		setSize("100%", "100%");
	}

	public HistoryTokenText(HistoryTokenListDTO token) {
		this();
		setHistoryToken(token);
	}

	public HistoryTokenText(String value) {
		this();
		setHistoryTokenValue(value);
	}

	public HistoryTokenText(List<String> values) {
		this();
		setHistoryTokenValue(values);
	}

	public HistoryTokenText(String... values) {
		this();
		setHistoryTokenValue(values);
	}

	public HistoryTokenText(Object value) {
		this();
		setHistoryTokenValue(value);
	}

	/**
	 * Display a history token in this element.
	 * 
	 * @param token
	 *          The history token.
	 */
	public void setHistoryToken(HistoryTokenListDTO token) {
		if (token != null) {
			if (token.getTokens().size() == 1) {
				setHistoryTokenValue(token.getTokens().get(0));
			} else {
				setHistoryTokenValue2(token.getTokens());
			}
		}
	}

	/**
	 * Display a history value in this element.
	 * 
	 * @param value
	 *          The value.
	 */
	public void setHistoryTokenValue(HistoryTokenDTO value) {
		if (value != null) {
			setHistoryTokenValue(value.getValue());
		}
	}

	/**
	 * Display a history values list in this element.
	 * 
	 * @param values
	 *          The values list.
	 */
	public void setHistoryTokenValue2(List<HistoryTokenDTO> values) {
		if (values != null) {
			final ArrayList<String> strings = new ArrayList<String>();
			for (final HistoryTokenDTO token : values) {
				strings.add(token.getValue());
			}
			setHistoryTokenValue(strings);
		}
	}

	/**
	 * Display a history value in this element.
	 * 
	 * @param value
	 *          The value.
	 */
	public void setHistoryTokenValue(String value) {
		setHTML(value);
	}

	/**
	 * Display a history values list in this element.
	 * 
	 * @param values
	 *          The values list.
	 */
	public void setHistoryTokenValue(List<String> values) {
		if (values != null) {
			setHistoryTokenValue(values.toArray(new String[values.size()]));
		}
	}

	/**
	 * Display a history values list in this element.
	 * 
	 * @param values
	 *          The values list.
	 */
	public void setHistoryTokenValue(String... values) {

		final StringBuilder sb = new StringBuilder();
		for (final String s : values) {
			sb.append("- ");
			sb.append(s);
			sb.append('\n');
		}

		setHTML(sb.toString());
	}

	/**
	 * Display a history value in this element.
	 * 
	 * @param value
	 *          The value.
	 */
	public void setHistoryTokenValue(Object value) {
		if (value != null) {
			setHTML(value.toString());
		}
	}

	@Override
	public void setHTML(String html) {

		// Prepares HTML string.
		if (html == null || "".equals(html)) {
			html = I18N.CONSTANTS.historyEmptyString();
			addStyleName("history-empty-string");
		} else {
			html = html.replaceAll("\n", "<br/>");
		}

		super.setHTML(html);
	}
}
