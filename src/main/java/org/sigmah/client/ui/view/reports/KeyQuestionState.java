package org.sigmah.client.ui.view.reports;

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

import org.sigmah.client.i18n.I18N;

import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Count and display the number of key questions and how many are valids.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) (v1.3)
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
final class KeyQuestionState implements IsWidget {

	private int valids;
	private int count;
	private Label label;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Widget asWidget() {
		return getLabel();
	}

	public void clear() {
		valids = 0;
		count = 0;
		label = null;
	}

	public Label getLabel() {
		if (label == null) {
			label = new Label();
			updateLabel();
		}
		return label;
	}

	public void increaseCount() {
		this.count++;
	}

	public int getCount() {
		return count;
	}

	public void increaseValids() {
		this.valids++;

		if (label != null) {
			updateLabel();
		}
	}

	public void decreaseValids() {
		this.valids--;

		if (label != null) {
			updateLabel();
		}
	}

	private void updateLabel() {
		label.setHtml(I18N.MESSAGES.reportKeyQuestions(Integer.toString(valids), Integer.toString(count)));
	}
}
