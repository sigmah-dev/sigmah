package org.sigmah.client.page.admin.management;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.management.AdminExportManagementPresenter.View;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.inject.Inject;

/**
 * View for setting the export feature
 * 
 * @author Sherzod
 * @author Aurélien Ponçon
 */
public class AdminExportManagementView extends ContentPanel implements View {

    private final Radio calcChoice;
    private final Radio excelChoice;
    private final Button saveButton;

    @Inject
    public AdminExportManagementView() {
        this.setHeading(I18N.CONSTANTS.defaultExportFormat());
       final FormPanel panel = new FormPanel();
        panel.setHeaderVisible(false);
        FormLayout layout = new FormLayout();
        layout.setLabelWidth(150);

        panel.setLayout(layout);
        // file format
        calcChoice = new Radio();
        calcChoice.setBoxLabel(I18N.CONSTANTS.openDocumentSpreadsheet());
        calcChoice.setName("type");

        excelChoice = new Radio();
        excelChoice.setValue(true);
        excelChoice.setBoxLabel(I18N.CONSTANTS.msExcel());
        excelChoice.setName("type");

        RadioGroup radioGroup = new RadioGroup();
        radioGroup.setOrientation(Orientation.VERTICAL);
        radioGroup.setFieldLabel(I18N.CONSTANTS.chooseFileType());
        radioGroup.add(calcChoice);
        radioGroup.add(excelChoice);        
        panel.add(radioGroup);

        // button
        saveButton = new Button(I18N.CONSTANTS.saveExportConfiguration());
        panel.getButtonBar().add(saveButton);
        panel.setButtonAlign(HorizontalAlignment.CENTER);
        panel.getButtonBar().setEnableOverflow(false);
        this.setBodyStyleName("width-of-parent");
        this.add(panel);

    }

    @Override
    public ContentPanel getContentPanel() {
        return this;
    }

    @Override
    public Radio getExcelRadioButton() {
        return excelChoice;
    }

    @Override
    public Radio getCalcRadioButton() {
        return calcChoice;
    }
 
    @Override
    public Button getSaveButton() {
        return saveButton;
    }

}
