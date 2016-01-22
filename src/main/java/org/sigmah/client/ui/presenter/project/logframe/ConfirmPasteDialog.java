package org.sigmah.client.ui.presenter.project.logframe;

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

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

/**
 * Confirm dialog for LogFrame <em>paste</em> action.
 * 
 * @author ??? (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
final class ConfirmPasteDialog extends Dialog {

	/**
	 * Callback for LogFrame <em>paste</em> action confirm dialog.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
	 */
	public static abstract class ConfirmPasteDialogCallback {

		/**
		 * Callback executed on {@code Ok} button click event.
		 * 
		 * @param linkIndicatorsChecked
		 *          The link indicators CheckBox value.
		 */
		abstract void onOk(boolean linkIndicatorsChecked);

	}

	private final ConfirmPasteDialogCallback callback;
	private final CheckBox indicatorLink;

	public ConfirmPasteDialog(final ConfirmPasteDialogCallback callback) {

		this.callback = callback;

		setHeadingText(I18N.CONSTANTS.paste());
		setWidth(350);
		setHeight(200);

		// copied from MessageBox
		addStyleName("x-window-dlg");
		add(new Html("<div class='ext-mb-icon ext-mb-question' style='margin-bottom: 10px'></div>"
			+ "<div class=ext-mb-content><span class=ext-mb-text>"
			+ I18N.CONSTANTS.logFramePasteConfirm()
			+ "</span><br /></div>"
			+ "<div style='clear:both'><br/></div>"));

		indicatorLink = new CheckBox();
		indicatorLink.setBoxLabel(I18N.CONSTANTS.linkIndicators());
		add(indicatorLink);

		final Html explanation = new Html(I18N.CONSTANTS.linkIndicatorsExplanation());
		explanation.addStyleName("ext-mb-text");
		explanation.setStyleAttribute("marginTop", "5px");

		add(explanation);

		setButtons(OKCANCEL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void show() {
		super.show();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onButtonPressed(Button button) {
		if (button.getItemId().equals(OK)) {
			callback.onOk(indicatorLink.getValue());
		}
		hide();
	}

}
