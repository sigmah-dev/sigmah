/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.client.ui;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.google.gwt.core.client.GWT;


/*
 * Utility class that provide button to export spreadsheet documents
 * from UI
 * 
 * @author sherzod
 */
public class ExportSpreadsheetFormButton {

	private final Button button;
	private final FormPanel exportForm;
	private final Map<String,String> fieldMap;
 	 	 	
	public ExportSpreadsheetFormButton() {
		 button = new Button(I18N.CONSTANTS.export(),
				IconImageBundle.ICONS.excel());
		exportForm = new FormPanel();
		exportForm.setBodyBorder(false);
		exportForm.setHeaderVisible(false);
		exportForm.setPadding(0);
		exportForm.setEncoding(Encoding.URLENCODED);
		exportForm.setMethod(Method.POST);
		exportForm.setAction(GWT.getModuleBaseURL() + "export");
		fieldMap=new HashMap<String, String>();
 	}
	

	public FormPanel getExportForm() {
		return exportForm;
	}


	public Button getButton() {
		return button;
	}

	 
	public Map<String, String> getFieldMap() {
		return fieldMap;
	}
	
   	public void triggerExport(){
   		exportForm.removeAll();
		// Adds parameters.
		for (String name : fieldMap.keySet()) {
			final HiddenField<String> hiddenField = new HiddenField<String>();
			hiddenField.setName(name);
			hiddenField.setValue(fieldMap.get(name));
			exportForm.add(hiddenField);
		}

		exportForm.layout();

		// Submits the form.
		exportForm.submit();

	}
 
}
