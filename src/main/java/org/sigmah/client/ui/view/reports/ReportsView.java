package org.sigmah.client.ui.view.reports;

import java.util.Arrays;
import java.util.Date;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.reports.ReportActionsHandler;
import org.sigmah.client.ui.presenter.reports.ReportsPresenter;
import org.sigmah.client.ui.presenter.reports.ReportsPresenter.DocumentNameColumnActionHandler;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.reports.ToolbarImages;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.layout.Layouts;
import org.sigmah.client.ui.widget.layout.Layouts.Margin;
import org.sigmah.client.ui.widget.panel.FoldPanel;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.DateUtils;
import org.sigmah.shared.dto.report.KeyQuestionDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.report.RichTextElementDTO;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RichTextArea;

/**
 * Reports & Documents widget view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class ReportsView extends AbstractView implements ReportsPresenter.View {

	// CSS style names.
	private static final String STYLE_TOOL_CLOSE_ICON = "x-tool-close";
	private static final String STYLE_PROJECT_REPORT_DRAFT = "project-report-draft";
	private static final String STYLE_PROJECT_REPORT_PERSONALDRAFT = "project-report-personalDraft";
	private static final String STYLE_PROJECT_REPORT_DRAFT_BUTTON = "project-report-draft-button";
	private static final String STYLE_PROJECT_REPORT_LEVEl = "project-report-level-"; // Prefix.
	private static final String STYLE_PROJECT_REPORT = "project-report";
	private static final String STYLE_PROJECT_REPORT_FIELD_EMPTY = "project-report-field-empty";
	private static final String STYLE_PROJECT_REPORT_FIELD = "project-report-field";
	private static final String STYLE_FLEXIBILITY_ACTION = "flexibility-action";

	private ContentPanel reportsListPanel;
	private Grid<ReportReference> reportsListGrid;
	private ToolBar reportsListToolbar;
	private Button attachButton;
	private Button createReportButton;

	private LayoutContainer mainPanel;
	private KeyQuestionState keyQuestionState;

	/**
	 * The global rich text formatter instance.
	 */
	private RichTextArea.Formatter globalRichTextFormatter;

	/**
	 * The document column name
	 */
	private DocumentNameColumnActionHandler documentNameColumnActionHandler;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		globalRichTextFormatter = null;
		keyQuestionState = new KeyQuestionState();

		add(createReportsListPanel(), Layouts.borderLayoutData(LayoutRegion.WEST, 400f, true));
		add(createMainPanel(), Layouts.borderLayoutData(LayoutRegion.CENTER, Margin.LEFT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDocumentNameColumnActionHandler(final DocumentNameColumnActionHandler documentNameColumnActionHandler) {
		this.documentNameColumnActionHandler = documentNameColumnActionHandler;
	}

	/**
	 * Creates the main container.
	 * 
	 * @return The component.
	 */
	private Component createMainPanel() {

		mainPanel = Layouts.border(false);

		return mainPanel;
	}

	/**
	 * Creates the reports list panel.
	 * 
	 * @return The component.
	 */
	private Component createReportsListPanel() {

		reportsListPanel = Panels.content(I18N.CONSTANTS.projectTabReports());

		// Toolbar.
		reportsListToolbar = new ToolBar();
		reportsListToolbar.setAlignment(HorizontalAlignment.RIGHT);
		reportsListPanel.setTopComponent(reportsListToolbar);

		// Buttons.
		createReportButton = Forms.button(I18N.CONSTANTS.reportCreateReport(), IconImageBundle.ICONS.add());
		attachButton = Forms.button(I18N.CONSTANTS.flexibleElementFilesListAddDocument(), IconImageBundle.ICONS.attach());

		// Grid and columns model.
		final ColumnConfig editDate = new ColumnConfig(ReportReference.LAST_EDIT_DATE, I18N.CONSTANTS.reportLastEditDate(), 200);
		editDate.setDateTimeFormat(DateUtils.DATE_SHORT);
		final ColumnConfig editorName = new ColumnConfig(ReportReference.EDITOR_NAME, I18N.CONSTANTS.reportEditor(), 200);
		final ColumnConfig iconColumn = new ColumnConfig("icon", "", 20);
		final ColumnConfig reportName = new ColumnConfig(ReportReference.NAME, I18N.CONSTANTS.reportName(), 200);
		final ColumnConfig typeColumn = new ColumnConfig(ReportReference.FLEXIBLE_ELEMENT_LABEL, I18N.CONSTANTS.reportType(), 200);

		iconColumn.setRenderer(new GridCellRenderer<ReportReference>() {

			@Override
			public Object render(final ReportReference model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ReportReference> store, final Grid<ReportReference> grid) {

				if (model.isDocument()) {
					return IconImageBundle.ICONS.attach().createImage();

				} else {
					return IconImageBundle.ICONS.report().createImage();
				}
			}
		});

		reportName.setRenderer(new GridCellRenderer<ReportReference>() {

			@Override
			public Object render(final ReportReference model, final String property, final ColumnData config, final int rowIndex, final int colIndex,
					final ListStore<ReportReference> store, final Grid<ReportReference> grid) {

				final com.google.gwt.user.client.ui.Label documrentButton = new com.google.gwt.user.client.ui.Label((String) model.get(property));
				documrentButton.addStyleName(STYLE_FLEXIBILITY_ACTION);

				// Button click handler.
				documrentButton.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(final ClickEvent e) {
						documentNameColumnActionHandler.onDocumentNameClicked(model, model.isDocument());
					}
				});

				return documrentButton;
			}
		});

		final ColumnModel reportColumnModel = new ColumnModel(Arrays.asList(editDate, editorName, iconColumn, reportName, typeColumn));
		reportsListGrid = new Grid<ReportReference>(new ListStore<ReportReference>(), reportColumnModel);
		reportsListGrid.setAutoExpandColumn(ReportReference.NAME);
		reportsListGrid.getView().setForceFit(true);

		reportsListPanel.add(reportsListGrid);

		return reportsListPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReportsListButtonsVisibility(final boolean buttonsVisibility) {

		reportsListToolbar.removeAll();

		if (buttonsVisibility) {
			reportsListToolbar.add(attachButton);
			reportsListToolbar.add(new SeparatorToolItem());
			reportsListToolbar.add(createReportButton);
		}

		reportsListToolbar.layout();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getReportsListAttachButton() {
		return attachButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getReportsListCreateButton() {
		return createReportButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<ReportReference> getReportsStore() {
		return reportsListGrid.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Component getMainPanel() {
		return mainPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FoldPanel loadReport(final ProjectReportDTO report, final ReportActionsHandler actionsHandler) {

		// --
		// Reset.
		// --

		mainPanel.removeAll();
		keyQuestionState.clear();

		if (report == null) {
			return null;
		}

		// --
		// Report loading.
		// --

		final ContentPanel reportPanel = new ContentPanel(new FitLayout());
		reportPanel.setScrollMode(Scroll.AUTOY);
		reportPanel.setHeadingHtml(report.getName());
		reportPanel.getHeader().addTool(new ToolButton(STYLE_TOOL_CLOSE_ICON, new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(final IconButtonEvent be) {
				actionsHandler.onCloseReport();
			}
		}));

		// Report container
		final FlowPanel flowPanel = new FlowPanel();

		// Report
		final FoldPanel root = addSection(null, null, 0);

		// Toolbar
		final ToolBar toolBar = new ToolBar();

		if (report.isDraft()) {

			// Draft banner
			final HorizontalPanel header = new HorizontalPanel();
			header.addStyleName(STYLE_PROJECT_REPORT_DRAFT);

			// The "Personal Draft"
			final Label personalDraft = new Label(I18N.MESSAGES.personalDraft());
			personalDraft.addStyleName(STYLE_PROJECT_REPORT_PERSONALDRAFT);

			final DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_MEDIUM);
			final DateTimeFormat timeFormat = DateTimeFormat.getFormat(PredefinedFormat.TIME_MEDIUM);

			// The label showing the last changed time
			final Label draftLastChangedTime =
					new Label(I18N.MESSAGES.reportDraftLastChanged(dateFormat.format(report.getLastEditDate()), timeFormat.format(report.getLastEditDate())));

			// Add the two labels
			header.add(personalDraft);
			header.add(draftLastChangedTime);

			final HorizontalPanel buttons = new HorizontalPanel();
			buttons.setSpacing(5);
			buttons.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			buttons.addStyleName(STYLE_PROJECT_REPORT_DRAFT_BUTTON);

			// --
			// Delete button.
			// --

			buttons.add(Forms.button(I18N.CONSTANTS.delete(), new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(final ButtonEvent ce) {
					actionsHandler.onDeleteReport();
				}
			}));

			// --
			// Send report button.
			// --

			buttons.add(Forms.button(I18N.CONSTANTS.sendReportDraft(), new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(final ButtonEvent ce) {
					actionsHandler.onShareReport();
				}
			}));

			header.add(buttons);
			header.setCellHorizontalAlignment(buttons, HasHorizontalAlignment.ALIGN_RIGHT);

			flowPanel.add(header);

			// --
			// Save button.
			// --

			toolBar.add(Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save(), new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(final ButtonEvent be) {

					actionsHandler.onSaveReport();

					final Date now = new Date();
					header.clear();
					draftLastChangedTime.setTitle(I18N.MESSAGES.reportDraftLastChanged(dateFormat.format(now), timeFormat.format(now)));
					personalDraft.setTitle(I18N.MESSAGES.personalDraft());
					header.add(personalDraft);
					header.add(draftLastChangedTime);
					header.add(buttons);
				}
			}));

			toolBar.add(new SeparatorToolItem());

		} else {

			// --
			// Edit report button.
			// --

			if (actionsHandler.isEditionEnabled()) {
				toolBar.add(Forms.button(I18N.CONSTANTS.edit(), IconImageBundle.ICONS.editPage(), new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(final ButtonEvent ce) {
						actionsHandler.onEditReport();
					}
				}));
			}

			// --
			// Word export button.
			// --

			toolBar.add(Forms.button(I18N.CONSTANTS.exportToWord(), IconImageBundle.ICONS.msword(), new SelectionListener<ButtonEvent>() {

				@Override
				public void componentSelected(final ButtonEvent ce) {
					actionsHandler.onExportReport();
				}
			}));

			toolBar.add(new SeparatorToolItem());
		}

		// Key question info
		final Label keyQuestionLabel = keyQuestionState.getLabel();
		toolBar.add(keyQuestionLabel);
		toolBar.add(new SeparatorToolItem());

		// Overview mode
		final Button foldButton = Forms.button(I18N.CONSTANTS.reportOverviewMode());
		foldButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				root.expand(true);
				root.fold(true);
			}
		});
		// Expanded mode
		final Button expandButton = Forms.button(I18N.CONSTANTS.reportFullMode());
		expandButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				root.expand(true);
			}
		});

		toolBar.add(foldButton);
		toolBar.add(expandButton);

		if (report.isDraft()) {
			toolBar.add(new SeparatorToolItem());
			createRichTextToolbar(toolBar);
		}

		flowPanel.add(root);
		reportPanel.add(flowPanel);
		reportPanel.setTopComponent(toolBar);

		// Display
		mainPanel.add(reportPanel, new BorderLayoutData(LayoutRegion.CENTER));
		mainPanel.layout();

		mainPanel.unmask();

		return root;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FoldPanel addSection(final String sectionName, final String prefix, final int level) {

		final FoldPanel sectionPanel = new FoldPanel();

		if (level > 0) {
			sectionPanel.setHeading(prefix.toString() + ' ' + sectionName);
			sectionPanel.addStyleName(STYLE_PROJECT_REPORT_LEVEl + level);

		} else {
			sectionPanel.addStyleName(STYLE_PROJECT_REPORT);
		}

		return sectionPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML addTextArea(final RichTextElementDTO richTextElement, final FoldPanel sectionPanel, final boolean draftMode) {

		if (draftMode) {

			final RichTextArea textArea = new RichTextArea();

			textArea.setHTML(richTextElement.getText());
			textArea.addFocusHandler(new FocusHandler() {

				@Override
				public void onFocus(FocusEvent event) {
					globalRichTextFormatter = textArea.getFormatter();
				}
			});

			sectionPanel.add(textArea);
			return textArea;

		} else {

			final HTML html = new HTML();
			final String value = richTextElement.getText();

			if (ClientUtils.isBlank(value)) {
				html.setText(I18N.CONSTANTS.reportEmptySection());
				html.addStyleName(STYLE_PROJECT_REPORT_FIELD_EMPTY);

			} else {
				html.setHTML(value);
				html.addStyleName(STYLE_PROJECT_REPORT_FIELD);
			}

			sectionPanel.add(html);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasHTML addKeyQuestion(final KeyQuestionDTO keyQuestion, final FoldPanel sectionPanel, final boolean draftMode) {

		keyQuestionState.increaseCount();

		keyQuestion.setNumber(keyQuestionState.getCount());

		// Rich text field.
		final RichTextArea textArea = new RichTextArea();
		final RichTextElementDTO richTextElementDTO = keyQuestion.getRichTextElementDTO();

		if (richTextElementDTO != null) {
			textArea.setHTML(richTextElementDTO.getText());
		}

		// Compas icon.
		final ImageResource icon;
		if (ClientUtils.isBlank(textArea.getText())) {
			icon = ToolbarImages.IMAGES.compasRed();

		} else {
			icon = ToolbarImages.IMAGES.compasGreen();
			keyQuestionState.increaseValids();
		}

		sectionPanel.addToolButton(icon, new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				KeyQuestionDialog.getDialog(keyQuestion, textArea, sectionPanel, sectionPanel.getToolButtonCount(), keyQuestionState, draftMode).show();
			}
		});

		return textArea;
	}

	// -------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------------------------

	/**
	 * Creates a rich text toolbar responding to the {@link #globalRichTextFormatter} events.
	 * 
	 * @param toolbar
	 *          The toolbar to which the rich text toolbar actions buttons should be append.
	 */
	private void createRichTextToolbar(final ToolBar toolbar) {
		createRichTextToolbar(toolbar, globalRichTextFormatter);
	}

	/**
	 * Creates a rich text toolbar responding to the given {@code formatter} events.
	 * 
	 * @param toolbar
	 *          The toolbar to which the rich text toolbar actions buttons should be append.
	 * @param formatter
	 *          The rich text formatter instance.
	 */
	static void createRichTextToolbar(final ToolBar toolbar, final RichTextArea.Formatter formatter) {

		// Fonts
		final ListBox fontListBox = new ListBox();
		fontListBox.addItem(I18N.CONSTANTS.font());
		fontListBox.addItem("Arial");
		fontListBox.addItem("Times New Roman");
		fontListBox.addItem("Courier New");
		fontListBox.addItem("Georgia");
		fontListBox.addItem("Trebuchet");
		fontListBox.addItem("Verdana");
		fontListBox.addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {
				formatter.setFontName(fontListBox.getValue(fontListBox.getSelectedIndex()));
			}
		});
		final LayoutContainer fontListBoxWrapper = new LayoutContainer(new FitLayout());
		fontListBoxWrapper.add(fontListBox);
		toolbar.add(fontListBoxWrapper);

		// Bold
		final Button boldButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textBold()));
		boldButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.toggleBold();
			}
		});
		toolbar.add(boldButton);

		// Italic
		final Button italicButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textItalic()));
		italicButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.toggleItalic();
			}
		});
		toolbar.add(italicButton);

		// Underline
		final Button underlineButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textUnderline()));
		underlineButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.toggleUnderline();
			}
		});
		toolbar.add(underlineButton);

		// Strike
		final Button strikeButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textStrike()));
		strikeButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.toggleStrikethrough();
			}
		});
		toolbar.add(strikeButton);

		// Align left
		final Button alignLeftButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textAlignLeft()));
		alignLeftButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.setJustification(RichTextArea.Justification.LEFT);
			}
		});
		toolbar.add(alignLeftButton);

		// Align center
		final Button alignCenterButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textAlignCenter()));
		alignCenterButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.setJustification(RichTextArea.Justification.CENTER);
			}
		});
		toolbar.add(alignCenterButton);

		// Align right
		final Button alignRightButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textAlignRight()));
		alignRightButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.setJustification(RichTextArea.Justification.RIGHT);
			}
		});
		toolbar.add(alignRightButton);

		// Justify
		final Button alignJustifyButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textAlignJustify()));
		alignJustifyButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.setJustification(RichTextArea.Justification.FULL);
			}
		});
		toolbar.add(alignJustifyButton);

		// List with numbers
		final Button listNumbersButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textListNumbers()));
		listNumbersButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.insertOrderedList();
			}
		});
		toolbar.add(listNumbersButton);

		// List with bullets
		final Button listBulletsButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.textListBullets()));
		listBulletsButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent be) {
				formatter.insertUnorderedList();
			}
		});
		toolbar.add(listBulletsButton);

		// Images
		final Button imageAddButton = Forms.button(null, AbstractImagePrototype.create(ToolbarImages.IMAGES.imageAdd()));
		imageAddButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			private Dialog imageAddDialog;
			private TextField<String> imageURLField;

			@Override
			public void componentSelected(ButtonEvent be) {
				if (imageAddDialog == null) {
					imageAddDialog = new Dialog();

					imageAddDialog.setButtons(Dialog.OKCANCEL);
					imageAddDialog.setHeadingHtml(I18N.CONSTANTS.reportAddImageDialogTitle());
					imageAddDialog.setModal(true);

					imageAddDialog.setResizable(false);
					imageAddDialog.setWidth("340px");

					imageAddDialog.setLayout(new FormLayout());

					// Report name
					imageURLField = new TextField<String>();
					imageURLField.setFieldLabel(I18N.CONSTANTS.reportImageURL());
					imageURLField.setAllowBlank(false);
					imageURLField.setName("url");
					imageAddDialog.add(imageURLField);

					// OK button
					imageAddDialog.getButtonById(Dialog.OK).addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							formatter.insertImage(imageURLField.getValue());
							imageAddDialog.hide();
						}
					});

					// Cancel button
					imageAddDialog.getButtonById(Dialog.CANCEL).addSelectionListener(new SelectionListener<ButtonEvent>() {

						@Override
						public void componentSelected(ButtonEvent ce) {
							imageAddDialog.hide();
						}
					});
				}

				imageURLField.setValue(null);
				imageAddDialog.show();
			}
		});
		toolbar.add(imageAddButton);
	}

}
