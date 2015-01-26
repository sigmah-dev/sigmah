/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.orgunit.reports;

import java.util.Date;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.ui.ButtonFileUploadField;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTO.LocalizedElement;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;

/**
 * Creates and maintains the "attach file" dialog.
 * 
 * @author Kristela Macaj(kmacaj@ideia.fr)
 */
public class AttachFileHandler implements AttachMenuBuilder.AttachDocumentHandler {
	private Dialog dialog;

	private FormPanel uploadFormPanel;
	private ButtonFileUploadField uploadField;
	private LabelField phaseField;
	private LabelField elementField;

	private ReportReference currentCreatedDocument;
	private boolean monitoredPointGenerated;

	private HiddenField<String> elementIdHidden;
	private HiddenField<String> orgUnitIdHidden;
	private HiddenField<String> nameHidden;
	private HiddenField<String> authorHidden;
	private HiddenField<String> emptyHidden;
	private HiddenField<String> pointDateHidden;
	private HiddenField<String> pointLabelHidden;

	@Override
	public Dialog getDialog(final ListStore<ReportReference> documentsStore, final OrgUnitDTO orgUnit,
	                final FlexibleElementDTO flexibleElement, final MenuItem menuItem, final String phaseName,
	                final Authentication authentication, final Dispatcher dispatcher, final EventBus eventBus) {

		if (dialog == null) {
			dialog = createDialog();
		}

		uploadFormPanel.reset();
		phaseField.setText(phaseName);
		elementField.setText(flexibleElement.getLabel());

		// Upload the selected file immediately after it's selected.
		uploadField.removeAllListeners();
		uploadField.addListener(Events.OnChange, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {

				dialog.mask(I18N.CONSTANTS.loading());

				// Set hidden fields values.
				elementIdHidden.setValue(String.valueOf(flexibleElement.getId()));
				orgUnitIdHidden.setValue(String.valueOf(orgUnit.getId()));
				nameHidden.setValue(uploadField.getValue());
				authorHidden.setValue(String.valueOf(authentication.getUserId()));
				emptyHidden.setValue("true");

				// Create the local document.
				currentCreatedDocument = new ReportReference();
				currentCreatedDocument.setName(uploadField.getValue());
				currentCreatedDocument.setLastEditDate(new Date());
				currentCreatedDocument.setEditorName(authentication.getUserShortName());
				currentCreatedDocument.setDocument(true);
				currentCreatedDocument.setFlexibleElementLabel(elementField.getText());
				currentCreatedDocument.setPhaseName(phaseField.getText());

				// Debug form hidden values.
				if (Log.isDebugEnabled()) {

					final StringBuilder sb = new StringBuilder();
					sb.append("Upload a new file with parameters: ");
					sb.append("name=");
					sb.append(nameHidden.getValue());
					sb.append(" ; author id=");
					sb.append(authorHidden.getValue());
					sb.append(" ; orgUnit id=");
					sb.append(orgUnitIdHidden.getValue());
					sb.append(" ; element id=");
					sb.append(elementIdHidden.getValue());
					sb.append(" ; allow empty=");
					sb.append(emptyHidden.getValue());

					Log.debug(sb.toString());
				}
				uploadFormPanel.submit();

			}
		});

		uploadFormPanel.removeAllListeners();
		uploadFormPanel.addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent be) {

				// Updates the widget for the new value.
				updateComponentAfterUpload(be, orgUnit, documentsStore);

				// Reset upload fields.
				uploadField.reset();

				dialog.unmask();
			}
		});

		return dialog;
	}

	/**
	 * Builds the dialog box.
	 */
	private Dialog createDialog() {
		// TO DO

		// Phase name field.
		phaseField = new LabelField();
		phaseField.setFieldLabel(I18N.CONSTANTS.reportPhase());

		// Flexible element label field.
		elementField = new LabelField();
		elementField.setFieldLabel(I18N.CONSTANTS.flexibleElementFilesList());

		// Creates the upload button (with a hidden form panel).
		uploadField = new ButtonFileUploadField();
		uploadField.setButtonCaption(I18N.CONSTANTS.flexibleElementFilesListAddDocument());
		uploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		uploadField.setButtonIcon(IconImageBundle.ICONS.attach());

		elementIdHidden = new HiddenField<String>();
		elementIdHidden.setName(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT);

		orgUnitIdHidden = new HiddenField<String>();
		orgUnitIdHidden.setName(FileUploadUtils.DOCUMENT_PROJECT);

		nameHidden = new HiddenField<String>();
		nameHidden.setName(FileUploadUtils.DOCUMENT_NAME);

		authorHidden = new HiddenField<String>();
		authorHidden.setName(FileUploadUtils.DOCUMENT_AUTHOR);

		emptyHidden = new HiddenField<String>();
		emptyHidden.setName(FileUploadUtils.CHECK_EMPTY);

		pointDateHidden = new HiddenField<String>();
		pointDateHidden.setName(FileUploadUtils.MONITORED_POINT_DATE);

		pointLabelHidden = new HiddenField<String>();
		pointLabelHidden.setName(FileUploadUtils.MONITORED_POINT_LABEL);

		uploadFormPanel = new FormPanel() {

			@Override
			public void reset() {
				phaseField.reset();
				elementField.reset();
				nameHidden.reset();
				authorHidden.reset();
				elementIdHidden.reset();
				orgUnitIdHidden.reset();
				emptyHidden.reset();
				pointDateHidden.reset();
				pointLabelHidden.reset();
			}
		};

		uploadFormPanel.setLayout(new FitLayout());
		uploadFormPanel.setBodyBorder(false);
		uploadFormPanel.setHeaderVisible(false);
		uploadFormPanel.setPadding(0);
		uploadFormPanel.setEncoding(Encoding.MULTIPART);
		uploadFormPanel.setMethod(Method.POST);
		uploadFormPanel.setAction(GWT.getModuleBaseURL() + "upload");

		uploadFormPanel.add(uploadField);
		uploadFormPanel.add(nameHidden);
		uploadFormPanel.add(authorHidden);
		uploadFormPanel.add(elementIdHidden);
		uploadFormPanel.add(orgUnitIdHidden);
		uploadFormPanel.add(emptyHidden);
		uploadFormPanel.add(pointDateHidden);
		uploadFormPanel.add(pointLabelHidden);

		// Dialog box.
		final Dialog dialogBox = new Dialog();
		dialogBox.setButtons(Dialog.CLOSE);
		dialogBox.setHeading(I18N.CONSTANTS.flexibleElementFilesListAddDocumentDetails());
		dialogBox.setModal(true);

		dialogBox.setResizable(false);
		dialogBox.setWidth("340px");

		dialogBox.setLayout(new FormLayout());
		dialogBox.add(phaseField);
		dialogBox.add(elementField);
		dialogBox.add(uploadFormPanel);

		return dialogBox;
	}

	/**
	 * Update files list after an upload.
	 * 
	 * @param be
	 *            Form event after the upload.
	 */
	private void updateComponentAfterUpload(FormEvent be, final OrgUnitDTO orgUnit,
	                final ListStore<ReportReference> documentsStore) {

		final String code = FileUploadUtils.parseUploadResultCode(be.getResultHtml());

		if (FileUploadUtils.isError(code)) {

			// If an error occurred, informs the user.
			final StringBuilder sb = new StringBuilder();
			sb.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorDetails());

			if (FileUploadUtils.EMPTY_DOC_ERROR_CODE.equals(code)) {
				sb.append("\n");
				sb.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorEmpty());
			} else if (FileUploadUtils.TOO_BIG_DOC_ERROR_CODE.equals(code)) {
				final String[] errorDetail = FileUploadUtils.parseTooBigDocDetail(be.getResultHtml());
				String message = I18N.CONSTANTS.flexibleElementFilesListUploadErrorTooBig();
				if (errorDetail != null && errorDetail.length >= 2) {
					try {
						String fileSize = String.valueOf(Integer.parseInt(errorDetail[0]) / (1024 * 1024));
						String fileMaxSize = String.valueOf(Integer.parseInt(errorDetail[1]) / (1024 * 1024));
						message = I18N.MESSAGES.flexibleElementFilesListUploadErrorTooBig(fileSize, fileMaxSize);
					} catch (NumberFormatException nf) {
						message = I18N.CONSTANTS.flexibleElementFilesListUploadErrorTooBig();
					} catch (NullPointerException np) {
						message = I18N.CONSTANTS.flexibleElementFilesListUploadErrorTooBig();
					}
				}
				sb.append("\n");
				sb.append(message);
			}

			MessageBox.alert(I18N.CONSTANTS.flexibleElementFilesListUploadError(), sb.toString(), null);
		} else {

			dialog.hide();

			currentCreatedDocument.setId(Integer.valueOf(code));

			Notification.show(I18N.CONSTANTS.infoConfirmation(),
			                I18N.CONSTANTS.flexibleElementFilesListUploadFileConfirm());

			// Adds the monitored point.
			if (monitoredPointGenerated) {

				final MonitoredPointDTO point = FileUploadUtils.parseUploadResultMonitoredPoint(be.getResultHtml());

				if (point != null) {
					if (Log.isDebugEnabled()) {
						Log.debug("[updateComponentAfterUpload] Adds a monitored point '" + point.getLabel() + "'");
					}

					Notification.show(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointAddConfirm());
					// TO DO
					// orgUnit.addMonitoredPoint(point);
				}

				monitoredPointGenerated = false;
			}

			// Refreshes files list
			documentsStore.add(currentCreatedDocument);
		}

		currentCreatedDocument = null;
	}

	@Override
	public boolean shouldEnableMenuItem(MenuItem menuItem, int containerId, LocalizedElement element,
	                Dispatcher dispatcher) {
		return true;
	}

}
