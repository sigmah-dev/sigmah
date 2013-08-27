package org.sigmah.client.page.dashboard;

import java.util.List;

import org.sigmah.client.cache.UserLocalCache;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.GetImportInformation;
import org.sigmah.shared.command.GetImportationSchemes;
import org.sigmah.shared.command.result.ImportInformationResult;
import org.sigmah.shared.command.result.ImportationSchemeListResult;
import org.sigmah.shared.domain.ImportDetails;
import org.sigmah.shared.domain.importation.ImportationSchemeFileFormat;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportWindow extends FormPanel {
	private Dispatcher dispatcher;
	private ComboBox<ImportationSchemeDTO> importationSchemesCombo;
	protected Button submitButton;
	private Window window;
	private FileUploadField uploadField;

	private static final int WINDOW_HEIGHT = 150;
	private static final int WINDOW_WIDTH = 300;

	public ImportWindow(final Dispatcher dispatcher, final Authentication authentication, final  UserLocalCache cache) {
		this.dispatcher = dispatcher;

		setBodyBorder(false);
		setHeaderVisible(false);
		setEncoding(Encoding.MULTIPART);
		setMethod(Method.POST);
		setAction(GWT.getModuleBaseURL() + "import");
		setMethod(FormPanel.Method.POST);
		setPadding(7);
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

		GetImportationSchemes cmd = new GetImportationSchemes();
		MaskingAsyncMonitor formMaskingMonitor = new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading());
		dispatcher.execute(cmd, formMaskingMonitor, new AsyncCallback<ImportationSchemeListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ImportationSchemeListResult result) {
				importationSchemesCombo = new ComboBox<ImportationSchemeDTO>();
				importationSchemesCombo.setFieldLabel(I18N.CONSTANTS.adminImportationScheme());
				importationSchemesCombo.setAllowBlank(false);
				if (result.getList() != null && !result.getList().isEmpty()) {
					ListStore<ImportationSchemeDTO> schemasStore = new ListStore<ImportationSchemeDTO>();
					schemasStore.add(result.getList());
					importationSchemesCombo.setStore(schemasStore);
					ImportWindow.this.add(importationSchemesCombo);
					importationSchemesCombo.setDisplayField("name");
					importationSchemesCombo.setName("schemeId");
					importationSchemesCombo.setValueField("id");
				}

				uploadField = new FileUploadField();
				uploadField.setAllowBlank(false);
				uploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
				uploadField.setFieldLabel(I18N.CONSTANTS.adminFileImport());

				submitButton = new Button(I18N.CONSTANTS.importItem());
				submitButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {
						if (isValid()) {
							submit();
						} else {
							MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
							                I18N.MESSAGES.importFormIncompleteDetails(""), null);
						}
					}

				});
				add(importationSchemesCombo);
				add(uploadField);
				add(submitButton);

				// Window.
				window = new Window();
				window.setWidth(560);

				window.add(ImportWindow.this);
				window.setAutoHeight(true);

				window.setPlain(true);
				window.setModal(true);
				window.setBlinkModal(true);
				window.setLayout(new FitLayout());

				addListener(Events.Submit, new Listener<FormEvent>() {

					@Override
					public void handleEvent(FormEvent be) {
						

						final String result = be.getResultHtml();

						// Import failed.
						if (result.indexOf("HTTP ERROR") != -1) {
							MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminProjectModelImportError(),
							                null);
						}
						// Import succeed.
						else {
							
							if (ImportationSchemeImportType.ROW.equals(importationSchemesCombo.getValue()
							                .getImportType())
							                && ((ImportationSchemeFileFormat.MS_EXCEL.equals(importationSchemesCombo
							                                .getValue().getFileFormat()) || ImportationSchemeFileFormat.ODS
							                                .equals(importationSchemesCombo.getValue().getFileFormat())))) {
								MessageBox.alert(
								                I18N.CONSTANTS.importItem(),
								                "The importation for the import type "
								                                + ImportationSchemeImportType
								                                                .getStringValue(importationSchemesCombo
								                                                                .getValue()
								                                                                .getImportType())
								                                + " combined with the file format "
								                                + ImportationSchemeFileFormat
								                                                .getStringValue(importationSchemesCombo
								                                                                .getValue()
								                                                                .getFileFormat())
								                                + " is not implemented yet.", null);
							} else {
								GetImportInformation cmd = new GetImportInformation();
								cmd.setFileName(result);
								cmd.setScheme(importationSchemesCombo.getValue());
								dispatcher.execute(cmd,
								                new MaskingAsyncMonitor(ImportWindow.this, I18N.CONSTANTS.loading()),
								                new AsyncCallback<ImportInformationResult>() {

									                @Override
									                public void onFailure(Throwable arg0) {
										                // TODO Auto-generated
										                // method stub

									                }

									                @Override
									                public void onSuccess(ImportInformationResult result) {
									                	window.hide();
										                if (result != null) {

											                List<ImportDetails> entitiesToExtracted = result
											                                .getEntitiesToImport();
											                if (entitiesToExtracted.size() != 0) {
												                ImportDetailsGrid importProjectOrgUnitsWindow = new ImportDetailsGrid(
												                                dispatcher, authentication, cache,  entitiesToExtracted);
												                window = new Window();
												                importProjectOrgUnitsWindow.getImportButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

																	@Override
                                                                    public void handleEvent(BaseEvent be) {
																		window.hide();
                                                                    }
												                	
												                });
												                window.add(importProjectOrgUnitsWindow);
												                window.setHeading(I18N.CONSTANTS
												                                .importProjectOrgUnitsWindowTitle());
												                window.setWidth(700);
												                window.setHeight(300);

												                window.setPlain(true);
												                window.setModal(true);
												                window.setBlinkModal(true);
												                window.setLayout(new FitLayout());
												                ImportWindow.this.hide();
												                window.show();
											                } else {
												                MessageBox.alert(I18N.CONSTANTS.importItem(),
												                                I18N.CONSTANTS.importEntitesEmpty(),
												                                null);

											                }

										                } else {
											                MessageBox.alert(I18N.CONSTANTS.error(),
											                                I18N.CONSTANTS.errorOnServer(), null);
										                }

									                }
								                });
								
							}

						}

					}

				});
				window.show();
			}
		});

	}

	public Object getVariableValueForFlexibleElement(Object value, FlexibleElementDTO fleElement) {
		return null;
	}
}
