package org.sigmah.client.ui.view.reports;

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
