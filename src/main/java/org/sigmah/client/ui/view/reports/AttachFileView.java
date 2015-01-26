package org.sigmah.client.ui.view.reports;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.reports.AttachFilePresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.ButtonFileUploadField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.google.inject.Singleton;

/**
 * Attach file frame view used to attach a file to a Project or a OrgUnit.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AttachFileView extends AbstractPopupView<PopupWidget> implements AttachFilePresenter.View {

	private FormPanel uploadFormPanel;
	private ButtonFileUploadField buttonUploadField;
	private LabelField phaseField;
	private LabelField elementField;
	private Button cancelButton;

	private HiddenField<String> elementIdHidden;
	private HiddenField<String> containerIdHidden;
	private HiddenField<String> nameHidden;
	private HiddenField<String> authorHidden;
	private HiddenField<String> pointDateHidden;
	private HiddenField<String> pointLabelHidden;

	/**
	 * Builds the view.
	 */
	public AttachFileView() {
		super(new PopupWidget(true), 400);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		uploadFormPanel = Forms.panel();
		uploadFormPanel.setEncoding(Encoding.MULTIPART);
		uploadFormPanel.setMethod(Method.POST);

		// Phase name field.
		phaseField = Forms.label(I18N.CONSTANTS.reportPhase());

		// Flexible element label field.
		elementField = Forms.label(I18N.CONSTANTS.flexibleElementFilesList());

		// Creates the upload button (with a hidden form panel).
		buttonUploadField = new ButtonFileUploadField();
		buttonUploadField.setButtonCaption(I18N.CONSTANTS.flexibleElementFilesListAddDocument());
		buttonUploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		buttonUploadField.setButtonIcon(IconImageBundle.ICONS.attach());
		buttonUploadField.setFieldLabel(I18N.CONSTANTS.flexibleElementFilesListAddDocument());

		cancelButton = Forms.button(I18N.CONSTANTS.cancel());

		elementIdHidden = Forms.hidden(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT);
		containerIdHidden = Forms.hidden(FileUploadUtils.DOCUMENT_PROJECT);
		nameHidden = Forms.hidden(FileUploadUtils.DOCUMENT_NAME);
		authorHidden = Forms.hidden(FileUploadUtils.DOCUMENT_AUTHOR);
		pointDateHidden = Forms.hidden(FileUploadUtils.MONITORED_POINT_DATE);
		pointLabelHidden = Forms.hidden(FileUploadUtils.MONITORED_POINT_LABEL);

		// Visible fields.
		uploadFormPanel.add(phaseField);
		uploadFormPanel.add(elementField);
		uploadFormPanel.add(buttonUploadField);

		// Hidden fields.
		uploadFormPanel.add(nameHidden);
		uploadFormPanel.add(authorHidden);
		uploadFormPanel.add(elementIdHidden);
		uploadFormPanel.add(containerIdHidden);
		uploadFormPanel.add(pointDateHidden);
		uploadFormPanel.add(pointLabelHidden);

		uploadFormPanel.addButton(cancelButton);

		initPopup(uploadFormPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return uploadFormPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ButtonFileUploadField getFileUploadButtonField() {
		return buttonUploadField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Object> getElementField() {
		return elementField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Object> getPhaseField() {
		return phaseField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getElementIdField() {
		return elementIdHidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getContainerIdField() {
		return containerIdHidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getNameField() {
		return nameHidden;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getAuthorField() {
		return authorHidden;
	}

}
