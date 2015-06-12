package org.sigmah.client.ui.presenter.reports;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.reports.ReportsView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.panel.FoldPanel;
import org.sigmah.client.util.ClientConfiguration;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.GetProjectDocuments.FilesListElement;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.PromoteProjectReportDraft;
import org.sigmah.shared.command.RemoveProjectReportDraft;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.report.KeyQuestionDTO;
import org.sigmah.shared.dto.report.ProjectReportContent;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.report.RichTextElementDTO;
import org.sigmah.shared.file.Cause;
import org.sigmah.shared.file.ProgressListener;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ExportUtils.ExportFormat;
import org.sigmah.shared.util.ExportUtils.ExportType;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import org.sigmah.shared.file.TransfertManager;

/**
 * Reports & Documents widget presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ReportsPresenter extends AbstractPresenter<ReportsPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ReportsView.class)
	public static interface View extends ViewInterface {

		// --
		// Documents (reports / files) lists area.
		// --

		void setDocumentNameColumnActionHandler(DocumentNameColumnActionHandler documentNameColumnActionHandler);

		/**
		 * Sets the reports list buttons enabled state.
		 * 
		 * @param buttonsEnabled
		 *          {@code true} to enabled the reports list buttons.
		 */
		void setReportsListButtonsVisibility(boolean buttonsEnabled);

		Button getReportsListAttachButton();

		Button getReportsListCreateButton();

		ListStore<ReportReference> getReportsStore();

		// --
		// Loaded report area.
		// --

		/**
		 * Returns the main panel, place holder for the current report.
		 * 
		 * @return The main panel, place holder for the current report.
		 */
		Component getMainPanel();

		/**
		 * Loads the given report.
		 * 
		 * @param report
		 *          The report to load.<br>
		 *          If {@code null}, clears the report area ({@code actionsHandler} will not be used in that case).
		 * @param actionsHandler
		 *          The report actions handler implementation.
		 * @return The root section panel.
		 */
		FoldPanel loadReport(ProjectReportDTO report, ReportActionsHandler actionsHandler);

		/**
		 * Adds a new section.<br>
		 * If the level is {@code 0}, a root section is initialized (section name and prefix are ignored).
		 * 
		 * @param sectionName
		 *          The section name.
		 * @param prefix
		 *          The section prefix value.
		 * @param level
		 *          The section level.
		 * @return The section panel.
		 */
		FoldPanel addSection(String sectionName, String prefix, int level);

		/**
		 * Adds a new rich textarea to the given {@code sectionPanel}.
		 * 
		 * @param richTextElement
		 *          The rich text element data.
		 * @param sectionPanel
		 *          The parent section panel.
		 * @param draftMode
		 *          If the section is loaded in draft mode.
		 * @return The {@link HasHTML} instance associated to the rich text element (if <em>draft</em> mode), or
		 *         {@code null}.
		 */
		HasHTML addTextArea(RichTextElementDTO richTextElement, FoldPanel sectionPanel, boolean draftMode);

		/**
		 * Adds a new key question to the given {@code sectionPanel}.
		 * 
		 * @param keyQuestion
		 *          The key question element data.
		 * @param sectionPanel
		 *          The parent section panel.
		 * @param draftMode
		 *          If the section is loaded in draft mode.
		 * @return The {@link HasHTML} instance associated to the key question.
		 */
		HasHTML addKeyQuestion(final KeyQuestionDTO keyQuestion, final FoldPanel sectionPanel, final boolean draftMode);

	}

	/**
	 * Document name column click handler.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
	 */
	public static interface DocumentNameColumnActionHandler {

		/**
		 * Callback executed on document (report / file) name column click event.
		 * 
		 * @param report
		 *          The clicked document.
		 * @param document
		 *          {@code true} if it is an attached file document, {@code false} if it is a report.
		 */
		void onDocumentNameClicked(ReportReference report, boolean document);

	}

	/**
	 * Time in milliseconds between each autosave.
	 */
	private final static int AUTO_SAVE_PERIOD = ClientConfiguration.getReportAutoSaveDelay();

	/**
	 * References to the HTML report text areas.
	 */
	private Map<Integer, HasHTML> textAreas;

	/**
	 * Old text areas values.
	 */
	private Map<Integer, String> oldContents;

	/**
	 * The current opened report id.
	 */
	private Integer currentReportId;

	/**
	 * Auto-save timer.
	 */
	private Timer autoSaveTimer;

	/**
	 * The project current phase name.
	 */
	private String phaseName;
	
	@Inject
	private TransfertManager transfertManager;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected ReportsPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Maps initialization.
		// --

		textAreas = new HashMap<Integer, HasHTML>();
		oldContents = new HashMap<Integer, String>();

		// --
		// Grid cells handler provided to view.
		// --

		view.setDocumentNameColumnActionHandler(new DocumentNameColumnActionHandler() {

			@Override
			public void onDocumentNameClicked(final ReportReference report, final boolean document) {

				if (document) {
					// Attached file download.
					// BUGFIX #699
					transfertManager.canDownload(report.getFileVersion(), new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							N10N.error(I18N.CONSTANTS.flexibleElementFilesListDownloadError(), I18N.CONSTANTS.flexibleElementFilesListDownloadErrorDetails());
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								transfertManager.download(report.getFileVersion(), new ProgressListener() {

									@Override
									public void onProgress(double progress, double speed) {
									}

									@Override
									public void onFailure(Cause cause) {
										N10N.error(I18N.CONSTANTS.flexibleElementFilesListDownloadError(), I18N.CONSTANTS.flexibleElementFilesListDownloadErrorDetails());
									}

									@Override
									public void onLoad(String result) {
									}
								});
							} else {
								N10N.error(I18N.CONSTANTS.flexibleElementFilesListDownloadError(), I18N.CONSTANTS.flexibleElementFilesListDownloadUnable());
							}
						}
					});

				} else {
					// Report loading.
					if (report.getId() != null && report.getId().equals(currentReportId)) {
						N10N.infoNotif(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportAlreadyOpened());
						return;
					}
					loadReport(report.getId());
				}
			}
		});

		// --
		// Report creation event.
		// --

		eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {
				if (event.concern(UpdateEvent.REPORT_DOCUMENTS_UPDATE)) {

					final ReportReference reportReference = event.getParam(0);

					view.getReportsStore().add(reportReference);

					loadReport(reportReference.getId());
				}
			}
		});
	}

	/**
	 * Sets the phase name.
	 * 
	 * @param phaseName
	 *          The new phase name.
	 */
	public void setPhaseName(final String phaseName) {
		this.phaseName = phaseName;
	}

	/**
	 * Loads the given {@code projectId} corresponding documents (reports / files).
	 * 
	 * @param projectId
	 *          The Project id.
	 * @param filesLists
	 *          The files list.
	 */
	public void loadProjectDocuments(final Integer projectId, final List<FilesListElement> filesLists) {
		loadDocuments(projectId, filesLists, false);
	}

	/**
	 * Loads the given {@code orgUnitId} corresponding documents (reports / files).
	 * 
	 * @param orgUnitId
	 *          The OrgUnit id.
	 * @param filesLists
	 *          The files list.
	 */
	public void loadOrgUnitDocuments(final Integer orgUnitId, final List<FilesListElement> filesLists) {
		loadDocuments(orgUnitId, filesLists, true);
	}

	/**
	 * Executes a dispatch command to load the given {@code reportId} corresponding {@link ProjectReportDTO}.
	 * 
	 * @param reportId
	 *          The report id. If the id is {@code null}, no command is executed and the presenter loads a {@code null}
	 *          {@link ProjectReportDTO} instance.
	 */
	public void loadReport(final Integer reportId) {

		if (reportId == null) {
			loadReport((ProjectReportDTO) null);
			return;
		}

		dispatch.execute(new GetProjectReport(reportId), new CommandResultHandler<ProjectReportDTO>() {

			@Override
			protected void onCommandSuccess(final ProjectReportDTO result) {
				loadReport(result);
			}
		}, new LoadingMask(view.getMainPanel()));
	}

	/**
	 * Loads the given report.
	 * 
	 * @param report
	 *          The report.
	 */
	public void loadReport(final ProjectReportDTO report) {

		view.setReportsListButtonsVisibility(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ORG_UNIT));

		// Reset.
		textAreas.clear();
		oldContents.clear();
		currentReportId = null;
		disableAutoSaveTimer();

		if (report == null) {
			view.loadReport(null, null);
			return;
		}

		final FoldPanel root = view.loadReport(report, new ReportActionsHandler() {

			@Override
			public boolean isEditionEnabled() {
				return ProfileUtils.isGranted(auth(), GlobalPermissionEnum.EDIT_ORG_UNIT);
			}

			@Override
			public void onShareReport() {
				ReportsPresenter.this.onShareReport(report, getChanges());
			}

			@Override
			public void onSaveReport() {
				ReportsPresenter.this.onSaveReport(report, getChanges(), true);
			}

			@Override
			public void onExportReport() {
				ReportsPresenter.this.onExportReport(report);
			}

			@Override
			public void onEditReport() {
				ReportsPresenter.this.onEditReport(report);
			}

			@Override
			public void onDeleteReport() {
				ReportsPresenter.this.onDeleteReport(report);
			}

			@Override
			public void onCloseReport() {
				loadReport((ProjectReportDTO) null);
			}

		});

		// --
		// Loads report section.
		// --

		final List<ProjectReportSectionDTO> sections = report.getSections();
		final StringBuilder prefix = new StringBuilder();

		for (int index = 0; index < sections.size(); index++) {

			final ProjectReportSectionDTO section = sections.get(index);

			prefix.append(index + 1).append('.');
			displaySection(section, root, prefix, 1, report.isDraft());

			prefix.setLength(0);
		}

		// --
		// Activates timer.
		// --

		if (report.isDraft()) {

			// Auto save timer
			autoSaveTimer = new Timer() {

				@Override
				public void run() {
					onSaveReport(report, getChanges(), false);
				}
			};

			autoSaveTimer.schedule(AUTO_SAVE_PERIOD);
		}

		// --
		// Stores opened report id.
		// --

		currentReportId = report.getId();
	}

	/**
	 * Returns if a value has changed into the report.
	 * 
	 * @return if a value has changed into the report.
	 */
	public boolean hasValueChanged() {

		if (ClientUtils.isEmpty(textAreas)) {
			return false;
		}

		for (final Entry<Integer, String> entry : oldContents.entrySet()) {
			final HasHTML textArea = textAreas.get(entry.getKey());
			if (!ClientUtils.equals(textArea.getHTML(), entry.getValue())) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Sets the <em>attach file</em> button's menu.<br>
	 * The button is enabled only if the given {@code menu} is enabled.
	 * 
	 * @param menu
	 *          The menu.
	 */
	public void setAttachFileButtonMenu(final Menu menu) {
		view.getReportsListAttachButton().setMenu(menu);
		view.getReportsListAttachButton().setEnabled(menu != null && menu.isEnabled());
	}

	/**
	 * Sets the <em>creates report</em> button's menu.<br>
	 * The button is enabled only if the given {@code menu} is enabled.
	 * 
	 * @param menu
	 *          The menu.
	 */
	public void setCreateReportButtonMenu(final Menu menu) {
		view.getReportsListCreateButton().setMenu(menu);
		view.getReportsListCreateButton().setEnabled(menu != null && menu.isEnabled());
	}

	/**
	 * Sets the <em>enabled</em> state of the given {@code menuItem} component.<br>
	 * Executes a command action to retrieve {@code flexibleElement} corresponding value.
	 * 
	 * @param menuItem
	 *          The menu item component.
	 * @param containerId
	 *          The container id (Project or OrgUnit).
	 * @param flexibleElement
	 *          The menu item corresponding flexible element.
	 * @param createReportMenu
	 *          {@code true} if the {@code menuItem} is a <em>create report</em> menu item, {@code false} if it is an
	 *          <em>attach file</em> menu item.
	 */
	public void setMenuItemEnabled(final Component menuItem, final Integer containerId, final FlexibleElementDTO flexibleElement, final boolean createReportMenu) {

		menuItem.setEnabled(false); // Default state.

		if (!createReportMenu) {
			// If it is an 'attach file' menu, the menu item is always enabled.
			menuItem.setEnabled(true);
			return;
		}

		if (flexibleElement instanceof ReportListElementDTO) {
			// If the flexible element is a report list, the menu item is always enabled.
			menuItem.setEnabled(true);
			return;
		}

		// Checking the value of the report element to decide if the state of the menu item.
		dispatch.execute(new GetValue(containerId, flexibleElement.getId(), flexibleElement.getEntityName()), new CommandResultHandler<ValueResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("Could not retrieves the value for following flexible element: " + flexibleElement);
				}
				throw new RuntimeException(caught);
			}

			@Override
			public void onCommandSuccess(final ValueResult result) {
				if (result == null || !result.isValueDefined()) {
					menuItem.setEnabled(true);
				} else {
					menuItem.setTitle(I18N.CONSTANTS.reportNoCreate());
				}
			}

		});
	}

	/**
	 * Disabled the reports presenter's auto-save timer.
	 */
	public void disableAutoSaveTimer() {
		if (autoSaveTimer == null) {
			return;
		}
		autoSaveTimer.cancel();
		autoSaveTimer = null;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the given {@code projectOrOrgUnitId} corresponding documents (reports / files).
	 * 
	 * @param projectOrOrgUnitId
	 *          The Project <b>or</b> OrgUnit id.
	 * @param filesLists
	 *          The files list.
	 * @param orgUnit
	 *          {@code true} if the {@code projectOrOrgUnitId} corresponds to an <em>OrgUnit</em> id, {@code false} if it
	 *          corresponds to a <em>Project</em> id.
	 */
	private void loadDocuments(final Integer projectOrOrgUnitId, final List<FilesListElement> filesLists, final boolean orgUnit) {

		view.getReportsStore().removeAll();

		// --
		// Retrieves reports.
		// --

		dispatch.execute(new GetProjectReports(orgUnit ? null : projectOrOrgUnitId, orgUnit ? projectOrOrgUnitId : null),
			new CommandResultHandler<ListResult<ReportReference>>() {

				@Override
				public void onCommandSuccess(final ListResult<ReportReference> result) {
					view.getReportsStore().add(result.getData());
				}
			});

		// --
		// Retrieves documents.
		// --

		dispatch.execute(new GetProjectDocuments(projectOrOrgUnitId, filesLists), new CommandResultHandler<ListResult<ReportReference>>() {

			@Override
			public void onCommandSuccess(final ListResult<ReportReference> result) {
				view.getReportsStore().add(result.getData());
				view.getReportsStore().sort(ReportReference.NAME, SortDir.ASC);
			}
		});
	}

	/**
	 * Returns the report sections corresponding HTML changes map.
	 * 
	 * @return The report sections corresponding HTML changes map.
	 */
	private Map<String, Object> getChanges() {

		final Map<String, Object> changes = new HashMap<String, Object>();

		for (final Map.Entry<Integer, HasHTML> textArea : textAreas.entrySet()) {
			changes.put(textArea.getKey().toString(), textArea.getValue().getHTML());
		}

		return changes;
	}

	/**
	 * Displays the given {@code section} and its children.
	 * 
	 * @param section
	 *          The report section.
	 * @param parent
	 *          The parent element.
	 * @param prefix
	 *          The section prefix.
	 * @param level
	 *          The section level.
	 * @param draftMode
	 *          If the section is loaded in draft mode.
	 */
	private void displaySection(final ProjectReportSectionDTO section, final FoldPanel parent, final StringBuilder prefix, int level, final boolean draftMode) {

		final FoldPanel sectionPanel = view.addSection(section.getName(), prefix.toString(), level);

		final int prefixLength = prefix.length();
		int index = 1;

		for (final ProjectReportContent childItem : section.getChildren()) {

			if (childItem.getClass() == ProjectReportSectionDTO.class) {

				// --
				// Sub section : recursive call.
				// --

				prefix.append(index).append('.');

				displaySection((ProjectReportSectionDTO) childItem, sectionPanel, prefix, level + 1, draftMode);

				index++;
				prefix.setLength(prefixLength);

			} else if (childItem.getClass() == RichTextElementDTO.class) {

				// --
				// Rich text element.
				// --

				final RichTextElementDTO richText = (RichTextElementDTO) childItem;
				final HasHTML hasHTML = view.addTextArea(richText, sectionPanel, draftMode);

				if (draftMode && hasHTML != null) {
					textAreas.put(richText.getId(), hasHTML);
					oldContents.put(richText.getId(), richText.getText());
				}

			} else if (childItem.getClass() == KeyQuestionDTO.class) {

				// --
				// Key question element.
				// --

				final KeyQuestionDTO keyQuestion = (KeyQuestionDTO) childItem;
				final RichTextElementDTO richTextElementDTO = keyQuestion != null ? keyQuestion.getRichTextElementDTO() : null;

				final HasHTML hasHTML = view.addKeyQuestion(keyQuestion, sectionPanel, draftMode);

				if (richTextElementDTO != null) {
					textAreas.put(richTextElementDTO.getId(), hasHTML);
					oldContents.put(richTextElementDTO.getId(), richTextElementDTO.getText());

				} else {
					if (Log.isErrorEnabled()) {
						Log.error("No text area is attached to the key question #" + keyQuestion.getId() + ".");
					}
				}

			} else {
				if (Log.isErrorEnabled()) {
					Log.error("Unsupported report section element type '" + childItem.getClass() + "'.");
				}
			}
		}

		parent.add(sectionPanel);
	}

	/**
	 * Method executed on report save action.
	 * 
	 * @param report
	 *          The current report.
	 * @param changes
	 *          The changes.
	 * @param loadingMask
	 *          Set to {@code true} to display a loading mask on the entire report during save action. Set to
	 *          {@code false} to ignore loading mask.
	 */
	private void onSaveReport(final ProjectReportDTO report, final Map<String, Object> changes, final boolean loadingMask) {

		changes.put(ProjectReportDTO.CURRENT_PHASE, phaseName);

		dispatch.execute(new UpdateEntity(ProjectReportDTO.ENTITY_NAME, report.getVersionId(), changes), new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportSaveError());
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {

				N10N.infoNotif(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportSaveSuccess());

				// --
				// Updates store.
				// --

				for (int index = 0; index < view.getReportsStore().getCount(); index++) {

					final ReportReference reference = view.getReportsStore().getAt(index);

					if (reference.getId().equals(report.getId())) {
						view.getReportsStore().remove(reference);
						reference.setEditorName(auth().getUserShortName());
						reference.setPhaseName(phaseName);
						reference.setLastEditDate(new Date());
						view.getReportsStore().add(reference);
						break;
					}
				}

				// --
				// Updates changes.
				// --

				if (ClientUtils.isEmpty(textAreas)) {
					return;
				}

				for (final Entry<Integer, String> entry : oldContents.entrySet()) {
					final HasHTML textArea = textAreas.get(entry.getKey());
					oldContents.put(entry.getKey(), textArea.getHTML());
				}

				// --
				// Cancels timer.
				// --

				autoSaveTimer.cancel();
				autoSaveTimer.schedule(AUTO_SAVE_PERIOD);
			}
		}, loadingMask ? new LoadingMask(view.getMainPanel()) : null);
	}

	/**
	 * Method executed on report share action.
	 * 
	 * @param report
	 *          The current report.
	 * @param changes
	 *          The changes.
	 */
	private void onShareReport(final ProjectReportDTO report, final Map<String, Object> changes) {

		changes.put(ProjectReportDTO.CURRENT_PHASE, phaseName);

		dispatch.execute(new UpdateEntity(ProjectReportDTO.ENTITY_NAME, report.getVersionId(), changes), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(final VoidResult result) {

				dispatch.execute(new PromoteProjectReportDraft(report.getId(), report.getVersionId()), new CommandResultHandler<ProjectReportDTO>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportSaveError());
					}

					@Override
					public void onCommandSuccess(final ProjectReportDTO result) {
						N10N.notification(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportSaveSuccess(), MessageType.INFO);
						loadReport(result);
					}

				}, new LoadingMask(view.getMainPanel()));

			}

		}, new LoadingMask(view.getMainPanel()));
	}

	/**
	 * Method executed on report delete action.
	 * 
	 * @param report
	 *          The current report.
	 */
	private void onDeleteReport(final ProjectReportDTO report) {

		dispatch.execute(new RemoveProjectReportDraft(report.getVersionId()), new CommandResultHandler<VoidResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportEditError());
			}

			@Override
			public void onCommandSuccess(final VoidResult result) {
				N10N.notification(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportEditCancelSuccess(), MessageType.INFO);
				loadReport(report.getId());
			}

		}, new LoadingMask(view.getMainPanel()));
	}

	/**
	 * Method executed on report edit action.
	 * 
	 * @param report
	 *          The current report.
	 */
	private void onEditReport(final ProjectReportDTO report) {

		// Draft creation
		final Map<String, Object> properties = new HashMap<String, Object>();
		properties.put(ProjectReportDTO.REPORT_ID, report.getId());
		properties.put(ProjectReportDTO.PHASE_NAME, phaseName);

		dispatch.execute(new CreateEntity(ProjectReportDTO.ENTITY_NAME_DRAFT, properties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportEditError());
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {
				loadReport(report.getId());
			}

		}, new LoadingMask(view.getMainPanel()));
	}

	/**
	 * Method executed on report export action.
	 * 
	 * @param report
	 *          The current report.
	 */
	private void onExportReport(final ProjectReportDTO report) {

		final ServletUrlBuilder urlBuilder =
				new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_REPORT);

		urlBuilder.addParameter(RequestParameter.ID, report.getId());
		urlBuilder.addParameter(RequestParameter.TYPE, ExportType.PROJECT_REPORT);
		urlBuilder.addParameter(RequestParameter.FORMAT, ExportFormat.MS_WORD);

		final FormElement form = FormElement.as(DOM.createForm());
		form.setAction(urlBuilder.toString());
		form.setTarget("_downloadFrame");
		form.setMethod(Method.POST.name());

		RootPanel.getBodyElement().appendChild(form);

		form.submit();
		form.removeFromParent();
	}

}
