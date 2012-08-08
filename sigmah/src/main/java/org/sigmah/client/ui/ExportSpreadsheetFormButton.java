package org.sigmah.client.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.shared.dto.ExportUtils;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

public class ExportSpreadsheetFormButton {

	private final Button button;
	private final FormPanel exportForm;
	private final Map<String,String> fieldMap;
	private final List<Widget> optionWidgets;
	private final int formWidth;
	private final int formHeight;
	
	public ExportSpreadsheetFormButton() {
		 this(350, 180);
	}
	
	public ExportSpreadsheetFormButton(int formWidth,int formHeight) {
		this.formWidth=formWidth;
		this.formHeight=formHeight;
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
		optionWidgets=new ArrayList<Widget>();
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
	
  

	public List<Widget> getOptionWidgets() {
		return optionWidgets;
	}

	public void exportButtonClicked(){
		final Window w = new Window();
		w.setPlain(true);
		w.setModal(true);
		w.setBlinkModal(true);
		w.setLayout(new FitLayout());
		w.setSize(formWidth,formHeight);
		w.setHeading(I18N.CONSTANTS.exportData());

		final FormPanel panel=new FormPanel();
		 
		final Radio calcChoice = new Radio();
		calcChoice.setBoxLabel(I18N.CONSTANTS.openDocumentSpreadsheet());
		calcChoice.setName("type");

		final Radio excelChoice = new Radio();
		excelChoice.setValue(true);
		excelChoice.setBoxLabel(I18N.CONSTANTS.msExcel());
		excelChoice.setName("type");

		RadioGroup radioGroup = new RadioGroup();
		radioGroup.setOrientation(Orientation.VERTICAL);
		radioGroup.setFieldLabel(I18N.CONSTANTS.chooseFileType());
		radioGroup.add(excelChoice);
		radioGroup.add(calcChoice);

		final Button export = new Button(I18N.CONSTANTS.export());
 		panel.getButtonBar().add(export);
 	

		export.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// Clears the form.
				exportForm.removeAll();
				if (excelChoice.getValue()) {
					fieldMap.put(ExportUtils.PARAM_EXPORT_FORMAT,
							ExportUtils.ExportFormat.MS_EXCEL.name());
				} else {
					fieldMap.put(ExportUtils.PARAM_EXPORT_FORMAT,
							ExportUtils.ExportFormat.OPEN_DOCUMENT_SPREADSHEET.name());
				}
				
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

				w.hide();
			}
		});		
		
		for(Widget option:optionWidgets){
			panel.add(option);
		}
		panel.add(radioGroup);
		w.add(panel);
		w.show();
	}
}
