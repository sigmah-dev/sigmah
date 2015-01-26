package org.sigmah.client.page.project.logframe;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;

public class ConfirmPasteDialog extends Dialog {
	
	private FormDialogCallback callback;
	private CheckBox indicatorLink;
	
	public ConfirmPasteDialog() {

		setHeading(I18N.CONSTANTS.paste());
		setWidth(350);
		setHeight(200);
		
		// copied from MessageBox
        addStyleName("x-window-dlg");
        add(new Html("<div class='ext-mb-icon ext-mb-question' style='margin-bottom: 10px'></div>" + 
        		"<div class=ext-mb-content><span class=ext-mb-text>" + 
        		I18N.CONSTANTS.logFramePasteConfirm() + "</span><br /></div>" + 
        		"<div style='clear:both'><br/></div>"));
        
		
		indicatorLink = new CheckBox();
		indicatorLink.setBoxLabel(I18N.CONSTANTS.linkIndicators());
		add(indicatorLink);
	
		Html explanation = new Html(I18N.CONSTANTS.linkIndicatorsExplanation());
		explanation.addStyleName("ext-mb-text");
		explanation.setStyleAttribute("marginTop", "5px");
		
		add(explanation);
		
		setButtons(OKCANCEL);
	}

	public void show(FormDialogCallback callback) {
		this.callback = callback;
		super.show();
	}

	@Override
	protected void onButtonPressed(Button button) {
		if(button.getItemId().equals(OK)) {
			callback.onValidated();
		}
		hide();
	}
	
	public boolean isLinkIndicatorsChecked() {
		return indicatorLink.getValue();
		
	}
	
	
}
