package org.sigmah.shared.dto.element;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.FlexibleGrid;
import org.sigmah.client.util.DateUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Image;
import org.sigmah.shared.dto.referential.ValueEventChangeType;

/**
 * Flexible element that can contain a list of project reports.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class ReportListElementDTO extends FlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 4984099670087438625L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.ReportListElement";

	// DTO attributes keys.
	public static final String MODEL_ID = ReportElementDTO.MODEL_ID;

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
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(MODEL_ID, getModelId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		final boolean canAdd = enabled && userCanPerformChangeType(ValueEventChangeType.ADD);
		
		final ContentPanel component = new ContentPanel();
		component.setHeadingText(getLabel());

		// Setting up the report store
		final List<?> reports = valueResult.getValuesObject();

		final ListStore<ReportReference> store = new ListStore<ReportReference>();
		if (reports != null)
			store.add((List<ReportReference>) reports);

		// Creating the toolbar
		if (canAdd) {
			component.setTopComponent(createToolbar(store));
		}

		// Creating the grid
		final FlexibleGrid<ReportReference> reportGrid = new FlexibleGrid<ReportReference>(store, null, createColumnModel(enabled));
		reportGrid.setAutoExpandColumn("name");
		reportGrid.setVisibleElementsCount(5);

		component.add(reportGrid);

		return component;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCorrectRequiredValue(ValueResult result) {
		final List<?> reports = result.getValuesObject();
		return reports != null && !reports.isEmpty();
	}

	private PageRequest createPageRequest(final Integer reportId) {
		// Report & Document path
		final PageRequest request;

		if (currentContainerDTO instanceof ProjectDTO) {
			// This element is displayed in a project
			request = new PageRequest(Page.PROJECT_REPORTS);
			request.addParameter(RequestParameter.ID, currentContainerDTO.getId());
			request.addParameter(RequestParameter.REPORT_ID, reportId);

		} else if (currentContainerDTO instanceof OrgUnitDTO) {
			// This element is displayed in a project
			request = new PageRequest(Page.ORGUNIT_REPORTS);
			request.addParameter(RequestParameter.ID, currentContainerDTO.getId());
			request.addParameter(RequestParameter.REPORT_ID, reportId);

		} else {
			if (Log.isDebugEnabled()) {
				Log.debug("ReportElementDTO does not know how to render properly from a '" + currentContainerDTO.getClass() + "' container.");
			}
			request = null;
		}

		return request;
	}

	/**
	 * Creates and configure the column model for the grid contained in this component.
	 * 
	 * @param enabled
	 *          <code>true</code> to enable the delete column, <code>false</code> to disable it.
	 * @return A new array of column configs.
	 */
	private ColumnConfig[] createColumnModel(final boolean enabled) {
		final boolean canRemove = enabled && userCanPerformChangeType(ValueEventChangeType.REMOVE);
		
		// Creating columns
		final ColumnConfig lastEditDateColumn = new ColumnConfig("lastEditDate", I18N.CONSTANTS.reportLastEditDate(), 60);
		final ColumnConfig nameColumn = new ColumnConfig("name", I18N.CONSTANTS.reportName(), 100);
		final ColumnConfig editorNameColumn = new ColumnConfig("editorName", I18N.CONSTANTS.reportEditor(), 100);
		final ColumnConfig deleteColumn = new ColumnConfig("delete", "", 10);

		// Date column specificities
		lastEditDateColumn.setDateTimeFormat(DateUtils.DATE_SHORT);

		// Name column specificities
		nameColumn.setRenderer(new GridCellRenderer<ReportReference>() {

			@Override
			public Object render(final ReportReference model, String property, ColumnData config, int rowIndex, int colIndex, ListStore store, Grid grid) {
				final Anchor anchor = new Anchor((String) model.get(property));
				anchor.addStyleName("flexibility-link");
				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						eventBus.navigateRequest(createPageRequest(model.getId()));
					}
				});
				return anchor;
			}
		});

		// Delete column specificities
		deleteColumn.setSortable(false);
		deleteColumn.setRenderer(new GridCellRenderer<ReportReference>() {

			@Override
			public Object render(final ReportReference model, String property, ColumnData config, int rowIndex, int colIndex, final ListStore store, Grid grid) {
				if (!canRemove) {
					return "-";
				}
				
				final Image image = IconImageBundle.ICONS.remove().createImage();
				image.setTitle(I18N.CONSTANTS.remove());
				image.addStyleName("flexibility-action");

				// Action
				image.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						N10N.confirmation(I18N.CONSTANTS.remove(), I18N.MESSAGES.reportRemoveConfirm(model.getName()), new ConfirmCallback() {

							@Override
							public void onAction() {
								// TODO: Delete the report
								if (Log.isDebugEnabled()) {
									Log.debug("Removing '" + model.getName() + "' report...");
								}

								dispatch.execute(new Delete(ProjectReportDTO.ENTITY_NAME, model.getId()), new CommandResultHandler<VoidResult>() {

									@Override
									public void onCommandSuccess(final VoidResult result) {
										store.remove(model);
										N10N.validNotif("OK", "OK");
									}

									@Override
									public void onCommandFailure(final Throwable caught) {
										N10N.warn("ERROR", "ERROR");
									}

								});
							}
						});
					}
				});

				return image;
			}
		});

		return new ColumnConfig[] {
			lastEditDateColumn,
			nameColumn,
			editorNameColumn,
			deleteColumn
		};
	}

	/**
	 * Creates the toolbar of this component.
	 * 
	 * @param enabled
	 *          <code>true</code> to enable the buttons of this toolbar, <code>false</code> to disable them.
	 * @return A new toolbar.
	 */
	private ToolBar createToolbar(final ListStore<ReportReference> store) {
		final ToolBar toolbar = new ToolBar();

		// Creating buttons
		final Button createReportButton = new Button(I18N.CONSTANTS.reportCreateReport(), IconImageBundle.ICONS.add());

		// "Create" button action
		createReportButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				MessageBox.prompt(I18N.CONSTANTS.reportCreateReport(), I18N.CONSTANTS.reportName(), new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent be) {
						if (Dialog.OK.equals(be.getButtonClicked().getItemId())) {
							final String name = be.getValue();

							final HashMap<String, Serializable> properties = new HashMap<String, Serializable>();
							properties.put("name", name);
							properties.put("flexibleElementId", getId());
							properties.put("reportModelId", getModelId());
							properties.put("containerId", currentContainerDTO.getId());
							if (currentContainerDTO instanceof ProjectDTO)
								properties.put("projectId", currentContainerDTO.getId());

							if (currentContainerDTO instanceof OrgUnitDTO)
								properties.put("orgUnitId", currentContainerDTO.getId());

							properties.put("multiple", true);

							if (currentContainerDTO instanceof ProjectDTO)
								properties.put("phaseName", ((ProjectDTO) currentContainerDTO).getCurrentPhase().getPhaseModel().getName());

							if (currentContainerDTO instanceof OrgUnitDTO)
								properties.put("phaseName", null);

							dispatch.execute(new CreateEntity(ProjectReportDTO.ENTITY_NAME, properties), new CommandResultHandler<CreateResult>() {

								@Override
								public void onCommandFailure(final Throwable caught) {
									N10N.error(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportCreateError());
								}

								@Override
								public void onCommandSuccess(final CreateResult result) {

									final ProjectReportDTO createdProjetReport = (ProjectReportDTO) result.getEntity();

									final ReportReference reference = new ReportReference();
									reference.setId(createdProjetReport.getId());
									reference.setName(name);
									reference.setLastEditDate(new Date());
									reference.setEditorName(auth().getUserShortName());
									store.add(reference);

									N10N.validNotif(I18N.CONSTANTS.projectTabReports(), I18N.CONSTANTS.reportCreateSuccess());
								}

							});
						}
					}
				});
			}
		});

		// Adding buttons to the toolbar
		toolbar.add(createReportButton);

		return toolbar;
	}

	public Integer getModelId() {
		return get(MODEL_ID);
	}

	public void setModelId(Integer modelId) {
		set(MODEL_ID, modelId);
	}

}
