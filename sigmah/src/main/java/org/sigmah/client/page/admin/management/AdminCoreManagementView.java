package org.sigmah.client.page.admin.management;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.management.AdminCoreManagementPresenter.View;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TextBox;

/**
 * View for managing the organization
 * 
 * @author Aurélien Ponçon
 *
 */
public class AdminCoreManagementView extends ContentPanel implements View {

    private TextBox organizationNameTextBox;
    private FileUpload logoFileUpload;
    private Image logoImage;
    private Button saveButton;
    private FlexTable formFlexTable;
    private FormPanel formPanel;

    public AdminCoreManagementView() {
        formFlexTable = new FlexTable();
        formFlexTable.setCellSpacing(10);
        formFlexTable.addStyleName("form-text width-of-parent");

        formPanel = new FormPanel();
        formPanel.setMethod(FormPanel.METHOD_POST);
        formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);

        int y = 0;

        organizationNameTextBox = new TextBox();
        organizationNameTextBox.setName("organizationName");
        organizationNameTextBox.setWidth("100%");
        formFlexTable.setText(y, 0, I18N.CONSTANTS.organizationManagementOrganizationName());
        formFlexTable.setWidget(y, 1, organizationNameTextBox);
        formFlexTable.getFlexCellFormatter().setColSpan(y, 1, 2);
        y++;

        logoFileUpload = new FileUpload();
        logoFileUpload.setName("logoFile");
        logoFileUpload.setWidth("100%");
        formPanel.add(logoFileUpload);
        formFlexTable.setText(y, 0, I18N.CONSTANTS.organizationManagementLogoUpload());
        formFlexTable.setWidget(y, 1, formPanel);
        formFlexTable.getFlexCellFormatter().setColSpan(y, 1, 2);
        y++;

        logoImage = new Image();
        logoImage.addStyleName("form-logo");
        formFlexTable.setText(y, 0, I18N.CONSTANTS.organizationManagementActualLogo());
        formFlexTable.setWidget(y, 1, logoImage);
        formFlexTable.getFlexCellFormatter().setRowSpan(y, 0, 2);
        formFlexTable.getCellFormatter().setVerticalAlignment(y, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        formFlexTable.getFlexCellFormatter().setColSpan(y, 1, 2);
        formFlexTable.getFlexCellFormatter().setRowSpan(y, 1, 2);
        formFlexTable.getCellFormatter().setHorizontalAlignment(y, 1, HasHorizontalAlignment.ALIGN_CENTER);
        y += 2;

        saveButton = new Button();
        formFlexTable.setWidget(y, 0, saveButton);
        saveButton.setText(I18N.CONSTANTS.organizationManagementSaveChanges());
        formFlexTable.getFlexCellFormatter().setColSpan(y, 0, 3);
        formFlexTable.getCellFormatter().setHorizontalAlignment(y, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        y++;

        int height = 33 * y;
        formFlexTable.setHeight(height + "px");

        this.add(formFlexTable);
        this.setLayout(new FlowLayout());
        this.setHeading(I18N.CONSTANTS.organizationManagementTitle());
        this.setBodyStyleName("width-of-parent height-of-parent");

    }

    @Override
    public TextBox getOrganizationNameTextBox() {
        return organizationNameTextBox;
    }

    @Override
    public FileUpload getLogoFileUpload() {
        return logoFileUpload;
    }

    @Override
    public Image getLogoImage() {
        return logoImage;
    }

    @Override
    public Button getSaveButton() {
        return saveButton;
    }

    @Override
    public ContentPanel getContentPanel() {
        return this;
    }

    @Override
    public FormPanel getFileFormPanel() {
        return formPanel;
    }

}
