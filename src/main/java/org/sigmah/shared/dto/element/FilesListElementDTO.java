package org.sigmah.shared.dto.element;

import java.util.ArrayList;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.reminder.ReminderType;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.client.ui.widget.form.ButtonFileUploadField;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.event.RequiredValueEvent;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.dto.value.ListableValue;
import org.sigmah.shared.file.Cause;
import org.sigmah.shared.file.ProgressListener;
import org.sigmah.shared.file.TransfertManager;
import org.sigmah.shared.servlet.FileUploadResponse;
import org.sigmah.shared.util.ProfileUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.event.DomEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * FilesListElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FilesListElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8520711106031085130L;

	/**
	 * Entity name mapped by the current DTO starting from the "server.domain" package name.
	 */
	public static final String ENTITY_NAME = "element.FilesListElement";

	/**
	 * Current value result updated after each upload to keep the consistency of the widget.
	 */
	private ValueResult currentValueResult;

	/**
	 * The component main panel.
	 */
	private transient ContentPanel mainPanel;

	/**
	 * Files list model data.
	 */
	private transient ListStore<FileDTO> store;

	/**
	 * If a monitored point has been created during the upload.
	 */
	private transient boolean monitoredPointGenerated = false;

	/**
	 * The upload button.
	 */
	private transient ButtonFileUploadField uploadField;

	public Integer getLimit() {
		return get("limit");
	}

	public void setLimit(Integer limit) {
		set("limit", limit);
	}

	private int getAdjustedLimit() {
		final Integer limit = getLimit();

		if (limit == null) {
			return -1;
		}

		if (limit <= 0) {
			return -1;
		}

		return limit;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {

		if (result == null || !result.isValueDefined()) {
			return false;
		}

		return !result.getValuesObject().isEmpty();
	}

	/**
	 * Updates the grid store for the current value.
	 */
	private void updateStore() {

		if (currentValueResult != null && currentValueResult.isValueDefined()) {
			store.removeAll();
			int max = getAdjustedLimit();
			int count = 0;
			for (ListableValue s : currentValueResult.getValuesObject()) {
				store.add((FileDTO) s);
				count++;
				if (count == max) {
					if (uploadField != null) {
						uploadField.setEnabled(false);
						if (uploadField.isRendered()) {
							uploadField.reset();
						}
					}
					break;
				}
			}
			store.sort("date", SortDir.DESC);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() {
		super.init();
		assert transfertManager != null;
		store = new ListStore<FileDTO>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {

		currentValueResult = valueResult;

		// Creates the upload button (with a hidden form panel).
		uploadField = new ButtonFileUploadField();
		uploadField.setButtonCaption(I18N.CONSTANTS.flexibleElementFilesListAddDocument());
		uploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		uploadField.setButtonIcon(IconImageBundle.ICONS.attach());
		uploadField.setEnabled(enabled);

		final FormPanel uploadFormPanel = new FormPanel();
		uploadFormPanel.setLayout(new FitLayout());
		uploadFormPanel.setBodyBorder(false);
		uploadFormPanel.setHeaderVisible(false);
		uploadFormPanel.setPadding(0);
		uploadFormPanel.setEncoding(Encoding.MULTIPART);
		uploadFormPanel.setMethod(Method.POST);
		uploadFormPanel.setAction(GWT.getModuleBaseURL() + "upload");

		final HiddenField<String> elementIdHidden = new HiddenField<String>();
		elementIdHidden.setName(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT);

		final HiddenField<String> projectIdHidden = new HiddenField<String>();
		projectIdHidden.setName(FileUploadUtils.DOCUMENT_PROJECT);

		final HiddenField<String> nameHidden = new HiddenField<String>();
		nameHidden.setName(FileUploadUtils.DOCUMENT_NAME);

		final HiddenField<String> authorHidden = new HiddenField<String>();
		authorHidden.setName(FileUploadUtils.DOCUMENT_AUTHOR);

		final HiddenField<String> pointDateHidden = new HiddenField<String>();
		pointDateHidden.setName(FileUploadUtils.MONITORED_POINT_DATE);

		final HiddenField<String> pointLabelHidden = new HiddenField<String>();
		pointLabelHidden.setName(FileUploadUtils.MONITORED_POINT_LABEL);

		uploadFormPanel.add(uploadField);
		uploadFormPanel.add(nameHidden);
		uploadFormPanel.add(authorHidden);
		uploadFormPanel.add(elementIdHidden);
		uploadFormPanel.add(projectIdHidden);
		uploadFormPanel.add(pointDateHidden);
		uploadFormPanel.add(pointLabelHidden);

		// Creates actions tool bar.
		final ToolBar actionsToolBar = new ToolBar();
		actionsToolBar.setAlignment(HorizontalAlignment.LEFT);

		actionsToolBar.add(uploadFormPanel);

		// Upload the selected file immediately after it's selected.
		uploadField.addListener(Events.OnChange, new Listener<DomEvent>() {

			@Override
			public void handleEvent(DomEvent be) {
				be.getEvent().stopPropagation();

				if (transfertManager.canUpload()) {
					// Set hidden fields values.
					elementIdHidden.setValue(String.valueOf(getId()));
					projectIdHidden.setValue(String.valueOf(currentContainerDTO.getId()));
					nameHidden.setValue(uploadField.getValue());
					authorHidden.setValue(String.valueOf(auth().getUserId()));

					// Debug form hidden values.
					if (Log.isDebugEnabled()) {

						final StringBuilder sb = new StringBuilder();
						sb.append("Upload a new file with parameters: ");
						sb.append("name=");
						sb.append(nameHidden.getValue());
						sb.append(" ; author id=");
						sb.append(authorHidden.getValue());
						sb.append(" ; project id=");
						sb.append(projectIdHidden.getValue());
						sb.append(" ; element id=");
						sb.append(elementIdHidden.getValue());

						Log.debug(sb.toString());
					}

					transfertManager.upload(uploadFormPanel, createUploadProgressListener());

				} else {
					N10N.warn(I18N.CONSTANTS.flexibleElementFilesListUploadUnable());
				}
			}
		});

		updateStore();

		// Creates the grid which contains the files list.
		final FlexibleGrid<FileDTO> filesGrid = new FlexibleGrid<FileDTO>(store, null, getColumnModel(enabled));
		filesGrid.setAutoExpandColumn("name");
		filesGrid.setVisibleElementsCount(5);

		store.setStoreSorter(new StoreSorter<FileDTO>() {

			@Override
			public int compare(Store<FileDTO> store, FileDTO m1, FileDTO m2, String property) {

				if ("date".equals(property)) {

					final FileVersionDTO last1 = m1.getLastVersion();
					final FileVersionDTO last2 = m2.getLastVersion();

					return last1.getAddedDate().compareTo(last2.getAddedDate());
				} else if ("author".equals(property)) {

					final FileVersionDTO last1 = m1.getLastVersion();
					final FileVersionDTO last2 = m2.getLastVersion();

					final String authorM1 = last1.getAuthorFirstName() != null ? last1.getAuthorFirstName() + " " + last1.getAuthorName() : last1.getAuthorName();
					final String authorM2 = last2.getAuthorFirstName() != null ? last2.getAuthorFirstName() + " " + last2.getAuthorName() : last2.getAuthorName();

					return authorM1.compareTo(authorM2);
				} else if ("version".equals(property)) {

					final FileVersionDTO last1 = m1.getLastVersion();
					final FileVersionDTO last2 = m2.getLastVersion();

					return new Integer(last1.getVersionNumber()).compareTo(last2.getVersionNumber());
				} else if ("name".equals(property)) {

					final FileVersionDTO last1 = m1.getLastVersion();
					final FileVersionDTO last2 = m2.getLastVersion();

					final String title1 = last1.getName() + '.' + last1.getExtension();
					final String title2 = last2.getName() + '.' + last2.getExtension();

					return title1.compareTo(title2);
				}

				else {
					return super.compare(store, m1, m2, property);
				}
			}
		});

		// Creates the main panel.
		mainPanel = new ContentPanel();
		mainPanel.setHeaderVisible(true);
		mainPanel.setBorders(true);

		int max = getAdjustedLimit();
		if (max != -1) {
			mainPanel.setHeadingText(getLabel() + " (" + I18N.MESSAGES.flexibleElementFilesListLimitReached(String.valueOf(getAdjustedLimit())) + ")");
		} else {
			mainPanel.setHeadingText(getLabel());
		}

		mainPanel.setTopComponent(actionsToolBar);
		mainPanel.add(filesGrid);

		return mainPanel;
	}

	/**
	 * Refreshes files list
	 */
	public void updateComponent() {

		// Server call to obtain elements value
		dispatch.execute(new GetValue(currentContainerDTO.getId(), getId(), getEntityName()), new CommandResultHandler<ValueResult>() {

			@Override
			public void onCommandFailure(final Throwable throwable) {
				// The widget cannot be refreshed for the new value state.
			}

			@Override
			public void onCommandSuccess(final ValueResult valueResult) {

				currentValueResult = valueResult;
				updateStore();
			}
		});
	}

	private ProgressListener createUploadProgressListener() {
		return new ProgressListener() {

			@Override
			public void onProgress(double progress, double speed) {
			}

			@Override
			public void onFailure(Cause cause) {
				mainPanel.unmask();

				// Displaying an error message.
				final StringBuilder errorMessageBuilder = new StringBuilder();
				errorMessageBuilder.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorDetails()).append("\n");

				switch (cause) {
					case FILE_TOO_LARGE:
						errorMessageBuilder.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorTooBig());
						break;
					default:
						errorMessageBuilder.append(I18N.CONSTANTS.flexibleElementFilesListUploadErrorEmpty());
						break;
				}

				N10N.warn(I18N.CONSTANTS.flexibleElementFilesListUploadError(), errorMessageBuilder.toString());
			}

			@Override
			public void onLoad(String result) {
				// Reset upload fields.
				uploadField.reset();
				handlerManager.fireEvent(new RequiredValueEvent(true, true));
				mainPanel.unmask();

				// Displaying a notification of success
				N10N.validNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.flexibleElementFilesListUploadFileConfirm());

				// Adds the monitored point.
				if (monitoredPointGenerated && currentContainerDTO != null && currentContainerDTO instanceof ProjectDTO) {
					createMonitoredPoint(result);
				}

				updateComponent();
			}
		};
	}

	/**
	 * Creates a new monitored point after a successful upload (if needed).
	 * 
	 * @param value
	 *          Result from an upload.
	 */
	private void createMonitoredPoint(String value) {
		Log.debug("[updateComponentAfterUpload] Adds a monitored point with response '" + value + "'.");

		final FileUploadResponse response = FileUploadResponse.parse(value);
		final MonitoredPointDTO point = response.getMonitoredPoint();

		if (point != null) {
			if (Log.isDebugEnabled()) {
				Log.debug("[updateComponentAfterUpload] Adds a monitored point '" + point.getLabel() + "'");
			}

			N10N.validNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.monitoredPointAddConfirm());

			// Forces the default completion state.
			point.setCompletionDate(null);

			if (eventBus != null) {
				// Sends an event to notify monitored point update.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.REMINDER_UPDATED, ReminderType.MONITORED_POINT));
			}
		}

		monitoredPointGenerated = false;
	}

	/**
	 * Defines the column model for the files list grid.
	 * 
	 * @param enabled
	 *          If the component is enabled.
	 * @return The column model.
	 */
	private ColumnConfig[] getColumnModel(final boolean enabled) {

		// File's add date.
		final ColumnConfig dateColumn = new ColumnConfig();
		dateColumn.setId("date");
		dateColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListDate());
		dateColumn.setWidth(60);
		dateColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			final DateTimeFormat format = DateUtils.DATE_SHORT;

			@Override
			public Object render(FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileDTO> store, Grid<FileDTO> grid) {
				final FileVersionDTO last = model.getLastVersion();
				return format.format(last.getAddedDate());
			}
		});

		// File's name.
		final ColumnConfig nameColumn = new ColumnConfig();
		nameColumn.setId("name");
		nameColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListName());
		nameColumn.setWidth(100);
		nameColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			@Override
			public Object render(final FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileDTO> store, Grid<FileDTO> grid) {
				return createDownloadLink(model, null, transfertManager);
			}
		});

		// File's author.
		final ColumnConfig authorColumn = new ColumnConfig();
		authorColumn.setId("author");
		authorColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListAuthor());
		authorColumn.setWidth(100);
		authorColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			@Override
			public Object render(FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileDTO> store, Grid<FileDTO> grid) {
				final FileVersionDTO last = model.getLastVersion();
				return last.getAuthorFirstName() != null ? last.getAuthorFirstName() + " " + last.getAuthorName() : last.getAuthorName();
			}
		});

		// File's last version number.
		final ColumnConfig versionColumn = new ColumnConfig();
		versionColumn.setId("version");
		versionColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListVersion());
		versionColumn.setWidth(20);
		versionColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			@Override
			public Object render(final FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileDTO> store, Grid<FileDTO> grid) {
				final FileVersionDTO last = model.getLastVersion();
				return last.getVersionNumber();
			}
		});

		// Upload new version.
		final ColumnConfig addVersionColumn = new ColumnConfig();
		addVersionColumn.setId("addVersion");
		addVersionColumn.setHeaderText(null);
		addVersionColumn.setWidth(60);
		addVersionColumn.setSortable(false);
		addVersionColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			@Override
			public Object render(final FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileDTO> store, Grid<FileDTO> grid) {

				final ButtonFileUploadField uploadField = new ButtonFileUploadField();
				uploadField.setButtonCaption(I18N.CONSTANTS.flexibleElementFilesListUploadVersion());
				uploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
				uploadField.setButtonIcon(IconImageBundle.ICONS.attach());
				uploadField.setEnabled(enabled);

				final FormPanel uploadFormPanel = new FormPanel();
				uploadFormPanel.setLayout(new FitLayout());
				uploadFormPanel.setBodyBorder(false);
				uploadFormPanel.setHeaderVisible(false);
				uploadFormPanel.setPadding(0);
				uploadFormPanel.setEncoding(Encoding.MULTIPART);
				uploadFormPanel.setMethod(Method.POST);
				uploadFormPanel.setAction(GWT.getModuleBaseURL() + "upload");

				final HiddenField<String> idHidden = new HiddenField<String>();
				idHidden.setName(FileUploadUtils.DOCUMENT_ID);

				final HiddenField<String> nameHidden = new HiddenField<String>();
				nameHidden.setName(FileUploadUtils.DOCUMENT_NAME);

				final HiddenField<String> authorHidden = new HiddenField<String>();
				authorHidden.setName(FileUploadUtils.DOCUMENT_AUTHOR);

				final HiddenField<String> versionHidden = new HiddenField<String>();
				versionHidden.setName(FileUploadUtils.DOCUMENT_VERSION);

				uploadFormPanel.add(uploadField);
				uploadFormPanel.add(authorHidden);
				uploadFormPanel.add(idHidden);
				uploadFormPanel.add(nameHidden);
				uploadFormPanel.add(versionHidden);

				uploadField.addListener(Events.OnChange, new Listener<DomEvent>() {

					@Override
					public void handleEvent(DomEvent be) {
						be.getEvent().stopPropagation();

						if (transfertManager.canUpload()) {
							// Set hidden fields values.
							idHidden.setValue(String.valueOf(model.getId()));
							nameHidden.setValue(uploadField.getValue());
							authorHidden.setValue(String.valueOf(auth().getUserId()));
							versionHidden.setValue(String.valueOf(model.getLastVersion().getVersionNumber() + 1));

							// Debug form hidden values.
							if (Log.isDebugEnabled()) {

								final StringBuilder sb = new StringBuilder();
								sb.append("Upload a new version with parameters: ");
								sb.append("version number=");
								sb.append(versionHidden.getValue());
								sb.append(" ; file id=");
								sb.append(idHidden.getValue());
								sb.append(" ; file name=");
								sb.append(nameHidden.getValue());
								sb.append(" ; author id=");
								sb.append(authorHidden.getValue());

								Log.debug(sb.toString());
							}

							// Submits the form.
							transfertManager.upload(uploadFormPanel, createUploadProgressListener());

						} else {
							N10N.warn(I18N.CONSTANTS.flexibleElementFilesListUploadUnable());
						}
					}
				});

				return uploadFormPanel;
			}
		});

		// Versions list.
		final ColumnConfig historyColumn = new ColumnConfig();
		historyColumn.setId("history");
		historyColumn.setHeaderText(null);
		historyColumn.setWidth(20);
		historyColumn.setSortable(false);
		historyColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			@Override
			public Object render(final FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileDTO> store, Grid<FileDTO> grid) {

				final com.google.gwt.user.client.ui.Label historyButton = new com.google.gwt.user.client.ui.Label(I18N.CONSTANTS.flexibleElementFilesListHistory());
				historyButton.addStyleName("flexibility-action");
				historyButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent e) {

						final FileDetailsWindow versionsWindow = new FileDetailsWindow(auth(), dispatch, transfertManager, enabled);
						versionsWindow.addListener(new FileDetailsWindow.FileDetailsWindowListener() {

							@Override
							public void versionDeleted(FileVersionDTO version) {
								updateComponent();
							}
						});

						versionsWindow.show(model);
					}
				});

				return historyButton;
			}
		});

		// Delete.
		final ColumnConfig deleteColumn = new ColumnConfig();
		deleteColumn.setId("delete");
		deleteColumn.setHeaderText(null);
		deleteColumn.setWidth(10);
		deleteColumn.setSortable(false);
		deleteColumn.setRenderer(new GridCellRenderer<FileDTO>() {

			@Override
			public Object render(final FileDTO model, String property, ColumnData config, int rowIndex, int colIndex, final ListStore<FileDTO> store,
					Grid<FileDTO> grid) {

				if (!enabled) {
					return new Label("-");
				}

				final Image image = IconImageBundle.ICONS.remove().createImage();
				image.setTitle(I18N.CONSTANTS.remove());
				image.addStyleName("flexibility-action");
				image.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent event) {

						// Asks the client to confirm the file removal.
						N10N.confirmation(I18N.CONSTANTS.flexibleElementFilesListDelete(), I18N.MESSAGES.flexibleElementFilesListConfirmDelete(model.getName()),
							new ConfirmCallback() {

								/**
								 * OK action.
								 */
								@Override
								public void onAction() {

									// Deletes it.
									dispatch.execute(new Delete(model, currentContainerDTO.getId(), getId()), new CommandResultHandler<VoidResult>() {

										@Override
										public void onCommandFailure(final Throwable caught) {
											N10N.error(I18N.CONSTANTS.flexibleElementFilesListDeleteError(), I18N.CONSTANTS.flexibleElementFilesListDeleteErrorDetails());
										}

										@Override
										public void onCommandSuccess(final VoidResult result) {
											store.remove(model);
											uploadField.setEnabled(true);
											uploadField.reset();
											if (store.getCount() == 0) {
												handlerManager.fireEvent(new RequiredValueEvent(false, true));
											}
										}
									}, new LoadingMask(mainPanel));
								}
							});

					}
				});

				return image;
			}
		});

		if (ProfileUtils.isGranted(auth(), GlobalPermissionEnum.REMOVE_FILE)) {
			return new ColumnConfig[] {
																	dateColumn,
																	nameColumn,
																	authorColumn,
																	versionColumn,
																	addVersionColumn,
																	historyColumn,
																	deleteColumn
			};
		} else {
			return new ColumnConfig[] {
																	dateColumn,
																	nameColumn,
																	authorColumn,
																	versionColumn,
																	addVersionColumn,
																	historyColumn
			};
		}
	}

	/**
	 * Builds and shows a window with the file's details.
	 * 
	 * @author tmi
	 */
	private static final class FileDetailsWindow {

		/**
		 * Listener.
		 * 
		 * @author tmi
		 */
		private interface FileDetailsWindowListener {

			/**
			 * Method called when a version is deleted.
			 * 
			 * @param version
			 *          The deleted version.
			 */
			public void versionDeleted(FileVersionDTO version);
		}

		private final DispatchAsync dispatch;

		private final Authentication authentication;

		/**
		 * GXT window.
		 */
		private final Window window;

		/**
		 * Window main panel.
		 */
		private final ContentPanel mainPanel;

		/**
		 * The versiions grid.
		 */
		private final FlexibleGrid<FileVersionDTO> grid;

		/**
		 * The current displayed file.
		 */
		private FileDTO file;

		/**
		 * Versions store.
		 */
		private final ListStore<FileVersionDTO> store;

		/**
		 * Listeners
		 */
		private final ArrayList<FileDetailsWindowListener> listeners = new ArrayList<FileDetailsWindowListener>();

		private final TransfertManager transfertManager;

		/**
		 * Builds the window.
		 * 
		 * @param dispatch
		 * @param enabled
		 *          If the component is enabled.
		 */
		public FileDetailsWindow(final Authentication authentication, final DispatchAsync dispatch, final TransfertManager transfertManager, boolean enabled) {

			this.authentication = authentication;
			this.dispatch = dispatch;
			this.transfertManager = transfertManager;

			store = new ListStore<FileVersionDTO>();

			grid = new FlexibleGrid<FileVersionDTO>(store, null, 10, getColumnModel(enabled));
			grid.setAutoExpandColumn("name");
			grid.setAutoHeight(true);

			store.setStoreSorter(new StoreSorter<FileVersionDTO>() {

				@Override
				public int compare(Store<FileVersionDTO> store, FileVersionDTO m1, FileVersionDTO m2, String property) {

					if ("author".equals(property)) {

						final String authorM1 = m1.getAuthorFirstName() != null ? m1.getAuthorFirstName() + " " + m1.getAuthorName() : m1.getAuthorName();
						final String authorM2 = m2.getAuthorFirstName() != null ? m2.getAuthorFirstName() + " " + m2.getAuthorName() : m2.getAuthorName();

						return authorM1.compareTo(authorM2);
					} else if ("name".equals(property)) {

						final String title1 = m1.getName() + '.' + m1.getExtension();
						final String title2 = m2.getName() + '.' + m2.getExtension();

						return title1.compareTo(title2);
					} else if ("size".equals(property)) {

						return new Long(m1.getSize()).compareTo(m2.getSize());
					}

					else {
						return super.compare(store, m1, m2, property);
					}
				}
			});

			mainPanel = new ContentPanel();
			mainPanel.setHeaderVisible(false);
			mainPanel.setBodyBorder(false);
			mainPanel.add(grid);
			mainPanel.setScrollMode(Scroll.AUTOY);

			// Builds window.
			window = new Window();
			window.setSize(550, 250);
			window.setPlain(true);
			window.setModal(true);
			window.setBlinkModal(true);
			window.setResizable(false);
			window.setLayout(new FitLayout());

			window.add(mainPanel);
		}

		/**
		 * Defines the column model for the versions list grid.
		 * 
		 * @param enabled
		 *          If the component is enabled.
		 * @return The column model.
		 */
		private ColumnConfig[] getColumnModel(final boolean enabled) {

			// Version's number.
			final ColumnConfig versionColumn = new ColumnConfig();
			versionColumn.setId("versionNumber");
			versionColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListVersionNumber());
			versionColumn.setWidth(55);

			// Version's add date.
			final ColumnConfig dateColumn = new ColumnConfig();
			dateColumn.setId("addedDate");
			dateColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListDate());
			dateColumn.setWidth(110);
			dateColumn.setDateTimeFormat(DateUtils.DATE_TIME_SHORT);

			// Version's author.
			final ColumnConfig authorColumn = new ColumnConfig();
			authorColumn.setId("author");
			authorColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListAuthor());
			authorColumn.setWidth(100);
			authorColumn.setRenderer(new GridCellRenderer<FileVersionDTO>() {

				@Override
				public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileVersionDTO> store,
						Grid<FileVersionDTO> grid) {
					return model.getAuthorFirstName() != null ? model.getAuthorFirstName() + " " + model.getAuthorName() : model.getAuthorName();
				}
			});

			// Version's name.
			final ColumnConfig nameColumn = new ColumnConfig();
			nameColumn.setId("name");
			nameColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListName());
			nameColumn.setWidth(100);
			nameColumn.setRenderer(new GridCellRenderer<FileVersionDTO>() {

				@Override
				public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileVersionDTO> store,
						Grid<FileVersionDTO> grid) {
					return createDownloadLink(file, model.getVersionNumber(), transfertManager);
				}
			});

			// Version's size.
			final ColumnConfig sizeColumn = new ColumnConfig();
			sizeColumn.setId("size");
			sizeColumn.setHeaderText(I18N.CONSTANTS.flexibleElementFilesListSize());
			sizeColumn.setWidth(60);
			sizeColumn.setRenderer(new GridCellRenderer<FileVersionDTO>() {

				@Override
				public Object render(FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<FileVersionDTO> store,
						Grid<FileVersionDTO> grid) {
					final Size size = Size.convertToBestUnit(new Size(model.getSize(), Size.SizeUnit.BYTE));
					return Math.round(size.getSize()) + " " + Size.SizeUnit.getTranslation(size.getUnit());
				}
			});

			// Delete.
			final ColumnConfig deleteColumn = new ColumnConfig();
			deleteColumn.setId("delete");
			deleteColumn.setHeaderText(null);
			deleteColumn.setWidth(25);
			deleteColumn.setSortable(false);
			deleteColumn.setRenderer(new GridCellRenderer<FileVersionDTO>() {

				@Override
				public Object render(final FileVersionDTO model, String property, ColumnData config, int rowIndex, int colIndex, final ListStore<FileVersionDTO> store,
						Grid<FileVersionDTO> grid) {

					if (enabled) {
						final Image image = IconImageBundle.ICONS.remove().createImage();
						image.setTitle(I18N.CONSTANTS.remove());
						image.addStyleName("flexibility-action");
						image.addClickHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent event) {

								// Do not delete a single version.
								if (store.getCount() <= 1) {
									N10N.warn(I18N.CONSTANTS.flexibleElementFilesListVersionDeleteForbidden(),
										I18N.CONSTANTS.flexibleElementFilesListVersionDeleteForbiddenDetails());
									return;
								}

								// Asks the client to confirm the version deletion.
								N10N.confirmation(I18N.CONSTANTS.flexibleElementFilesListVersionDelete(),
									I18N.MESSAGES.flexibleElementFilesListConfirmVersionDelete(String.valueOf(model.getVersionNumber())), new ConfirmCallback() {

										@Override
										public void onAction() {

											// Deletes it.
											dispatch.execute(new Delete(model), new CommandResultHandler<VoidResult>() {

												@Override
												public void onCommandFailure(final Throwable caught) {
													N10N.warn(I18N.CONSTANTS.flexibleElementFilesListDeleteError(), I18N.CONSTANTS.flexibleElementFilesListDeleteErrorDetails());
												}

												@Override
												public void onCommandSuccess(final VoidResult result) {
													store.remove(model);
													fireVersionDeleted(model);
												}
											}, new LoadingMask(window));
										}
									});

							}
						});

						return image;
					} else {
						return new Label("-");
					}
				}
			});

			if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.REMOVE_FILE)) {
				return new ColumnConfig[] {
																		versionColumn,
																		dateColumn,
																		authorColumn,
																		nameColumn,
																		sizeColumn,
																		deleteColumn
				};
			} else {
				return new ColumnConfig[] {
																		versionColumn,
																		dateColumn,
																		authorColumn,
																		nameColumn,
																		sizeColumn
				};
			}
		}

		/**
		 * Shows the windows for the given file.
		 * 
		 * @param file
		 *          The file.
		 */
		public void show(FileDTO file) {

			if (file == null) {
				return;
			}

			this.file = file;
			final FileVersionDTO lastVersion = file.getLastVersion();

			// Clears the existing versions.
			store.removeAll();

			// Adds each version to the store to be displayed in the grid.
			for (final FileVersionDTO version : this.file.getVersions()) {
				store.add(version);
			}

			store.sort("versionNumber", SortDir.DESC);

			// Configures the window parameters to be consistent with the new displayed file.
			window.setHeadingText(I18N.CONSTANTS.flexibleElementFilesListHistory() + " - " + lastVersion.getName() + '.' + lastVersion.getExtension());
			window.show();
		}

		/**
		 * Adds a listener to the window.
		 * 
		 * @param listener
		 *          The new listener.
		 */
		public void addListener(FileDetailsWindowListener listener) {
			listeners.add(listener);
		}

		/**
		 * Method called when a version is deleted.
		 * 
		 * @param version
		 *          The deleted version.
		 */
		protected void fireVersionDeleted(FileVersionDTO version) {
			for (final FileDetailsWindowListener listener : listeners) {
				listener.versionDeleted(version);
			}
		}
	}

	/**
	 * Create a download link for the given file.
	 * 
	 * @param file
	 *          The file to download.
	 * @return The download link.
	 */
	private static com.google.gwt.user.client.ui.Label createDownloadLink(final FileDTO file, final Integer version, final TransfertManager transfertManager) {

		final FileVersionDTO versionDTO;
		if (version != null) {
			versionDTO = file.getVersion(version);
		} else {
			versionDTO = file.getLastVersion();
		}

		if (versionDTO == null) {
			return null;
		}
		
		if(!versionDTO.isAvailable()) {
			return new com.google.gwt.user.client.ui.Label(
				versionDTO.getName() + '.' + versionDTO.getExtension() + ' ' + I18N.CONSTANTS.flexibleElementFilesListNotUploadedYet());
		}

		final com.google.gwt.user.client.ui.Label downloadButton = new com.google.gwt.user.client.ui.Label(versionDTO.getName() + '.' + versionDTO.getExtension());
		downloadButton.addStyleName("flexibility-action");

		// Buttons listeners.
		downloadButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent e) {
				transfertManager.canDownload(versionDTO, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						N10N.error(I18N.CONSTANTS.flexibleElementFilesListDownloadError(), I18N.CONSTANTS.flexibleElementFilesListDownloadErrorDetails());
					}

					@Override
					public void onSuccess(Boolean result) {
						if (result) {
							transfertManager.download(versionDTO, new ProgressListener() {

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
			}
		});

		return downloadButton;
	}

	/**
	 * Utility class used to manipulate file's sizes.
	 * 
	 * @author tmi
	 */
	public static final class Size {

		private final double size;

		private final SizeUnit unit;

		public Size(double size, SizeUnit unit) {
			this.size = size;
			this.unit = unit;
		}

		public double getSize() {
			return size;
		}

		public SizeUnit getUnit() {
			return unit;
		}

		@Override
		public String toString() {
			final StringBuilder sb = new StringBuilder();
			sb.append(size);
			sb.append(unit.name());
			return sb.toString();
		}

		/**
		 * Converts a size from one unit to another.
		 * 
		 * @param size
		 *          The size (must be <code>positive</code> and not <code>null</code>).
		 * @param targetUnit
		 *          The desired size unit.
		 * @return The size converted.
		 */
		public static Size convert(Size size, SizeUnit targetUnit) {

			if (targetUnit == null) {
				throw new IllegalArgumentException("Units cannot be null.");
			}

			if (size == null) {
				throw new IllegalArgumentException("Size cannot be null.");
			}

			if (size.size < 0) {
				throw new IllegalArgumentException("Size cannot be negative.");
			}

			if (size.size == 0 || size.unit == targetUnit) {
				return size;
			}

			double computedSize;

			if (size.unit.weight < targetUnit.weight) {
				computedSize = size.size / ((targetUnit.weight - size.unit.weight) * SizeUnit.STEP);
			} else {
				computedSize = size.size * Math.pow(SizeUnit.STEP, size.unit.weight - targetUnit.weight);
			}

			return new Size(computedSize, targetUnit);
		}

		/**
		 * Converts a size to the best appropriate unit (greater than <code>0</code>).
		 * 
		 * @param size
		 *          The size (must be <code>positive</code> and not <code>null</code>).
		 * @return The given {@code size} converted to the best appropriate unit (greater than <code>0</code>).
		 */
		public static Size convertToBestUnit(Size size) {

			if (size == null) {
				throw new IllegalArgumentException("Size cannot be null.");
			}

			if (size.size < 0) {
				throw new IllegalArgumentException("Size cannot be negative.");
			}

			if (size.size == 0) {
				return size;
			}

			double computedSize = 0;
			SizeUnit computedUnit = null;

			double currentSize = size.size;
			SizeUnit currentUnit = size.unit;

			if (size.size > 1) {

				while (currentSize >= 1 && currentUnit != null) {

					computedSize = currentSize;
					computedUnit = currentUnit;

					currentSize = currentSize / SizeUnit.STEP;
					currentUnit = SizeUnit.getPrevUnit(currentUnit);
				}

			} else {

				computedSize = size.size;
				computedUnit = size.unit;

				while (computedSize <= 1 && currentUnit != null) {

					computedSize = currentSize;
					computedUnit = currentUnit;

					currentSize = currentSize * SizeUnit.STEP;
					currentUnit = SizeUnit.getNextUnit(currentUnit);
				}
			}

			return new Size(computedSize, computedUnit);
		}

		/**
		 * Represents size units.
		 * 
		 * @author tmi
		 */
		public static enum SizeUnit {

			BYTE(1),
			KILOBYTE(2),
			MEGABYTE(3),
			GIGABYTE(4),
			TERABYTE(5);

			private static final int STEP = 1024;

			private final int weight;

			private SizeUnit(int weight) {
				this.weight = weight;
			}

			/**
			 * Gets the next unit (the first greater one).
			 * 
			 * @param unit
			 *          The base unit.
			 * @return The next unit.
			 */
			private static SizeUnit getNextUnit(SizeUnit unit) {

				if (unit == null) {
					return null;
				}

				for (final SizeUnit u : SizeUnit.values()) {
					if (u.weight + 1 == unit.weight) {
						return u;
					}
				}

				return null;
			}

			/**
			 * Gets the previous unit (the first lower one).
			 * 
			 * @param unit
			 *          The base unit.
			 * @return The previous unit.
			 */
			private static SizeUnit getPrevUnit(SizeUnit unit) {

				if (unit == null) {
					return null;
				}

				for (final SizeUnit u : SizeUnit.values()) {
					if (u.weight - 1 == unit.weight) {
						return u;
					}
				}

				return null;
			}

			/**
			 * Gets the translation key of this unit specific to the current application.
			 * 
			 * @param unit
			 *          The unit.
			 * @return The translation key.
			 */
			public static String getTranslation(SizeUnit unit) {

				switch (unit) {
					case BYTE:
						return I18N.CONSTANTS.flexibleElementFilesListSizeByteUnit();
					case KILOBYTE:
						return I18N.CONSTANTS.flexibleElementFilesListSizeKByteUnit();
					case MEGABYTE:
						return I18N.CONSTANTS.flexibleElementFilesListSizeMByteUnit();
					case GIGABYTE:
						return I18N.CONSTANTS.flexibleElementFilesListSizeGByteUnit();
					case TERABYTE:
						return I18N.CONSTANTS.flexibleElementFilesListSizeTByteUnit();
					default:
						return "";
				}
			}
		}
	}
}
