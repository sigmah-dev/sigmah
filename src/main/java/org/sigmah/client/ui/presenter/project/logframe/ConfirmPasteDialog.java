package org.sigmah.client.ui.presenter.project.logframe;

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
