package org.sigmah.client.page.admin.management;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.management.AdminBackupManagementPresenter.View;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.inject.Inject;

/**
 * View for do some backups of the files for a selected org unit
 * 
 * @author Aurélien Ponçon
 *
 */
public class AdminBackupManagementView extends ContentPanel implements View {

    private RadioButton allVersionsRadioButton;
    private RadioButton lastVersionRadioButton;
    private ListBox orgUnitListBox;
    private Button backupButton;

    private FlexTable formFlexTable;

    @Inject
    public AdminBackupManagementView() {
        formFlexTable = new FlexTable();
        formFlexTable.setCellSpacing(10);
        formFlexTable.addStyleName("form-text");

        int y = 0;

        allVersionsRadioButton = new RadioButton("version_group", I18N.CONSTANTS.backupManagementAllVersion());
        allVersionsRadioButton.addStyleName("form-radio");
        formFlexTable.setText(y, 0, I18N.CONSTANTS.backupManagementDownload());
        formFlexTable.getFlexCellFormatter().setRowSpan(y, 0, 2);
        formFlexTable.setWidget(y, 1, allVersionsRadioButton);
        y++;

        lastVersionRadioButton = new RadioButton("version_group", I18N.CONSTANTS.backupManagementOneVersion());
        lastVersionRadioButton.addStyleName("form-radio");
        formFlexTable.setWidget(y, 0, lastVersionRadioButton);
        y++;

        orgUnitListBox = new ListBox();
        formFlexTable.setText(y, 0, I18N.CONSTANTS.backupManagementRootOrganization());
        formFlexTable.setWidget(y, 1, orgUnitListBox);
        y++;

        backupButton = new Button(I18N.CONSTANTS.backupManagementBackupAllFiles());
        formFlexTable.setWidget(y, 0, backupButton);
        formFlexTable.getFlexCellFormatter().setColSpan(y, 0, 2);
        formFlexTable.getCellFormatter().setHorizontalAlignment(y, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        y++;
        
        int height = 37 * y < 180 ? 180 : 37 * y;
        formFlexTable.setHeight(height + "px");

        this.add(formFlexTable);
        this.setHeading(I18N.CONSTANTS.backupManagementTitle());
        this.setBodyStyleName("width-of-parent height-of-parent");
    }

    @Override
    public ContentPanel getContentPanel() {
        return this;
    }

    @Override
    public RadioButton getAllVersionsRadioButton() {
        return allVersionsRadioButton;
    }

    @Override
    public RadioButton getLastVersionRadioButton() {
        return lastVersionRadioButton;
    }

    @Override
    public ListBox getOrgUnitListBox() {
        return orgUnitListBox;
    }

    @Override
    public Button getBackupButton() {
        return backupButton;
    }

}
