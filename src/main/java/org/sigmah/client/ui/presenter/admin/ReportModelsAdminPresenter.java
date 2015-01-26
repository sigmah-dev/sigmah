package org.sigmah.client.ui.presenter.admin;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.admin.ReportModelsAdminView;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.DeleteReportModels;
import org.sigmah.shared.command.GetReportElements;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.UpdateProjectReportModel;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ReportElementsResult;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.dom.client.Element;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin report models Presenter which manages {@link ReportModelsAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class ReportModelsAdminPresenter extends AbstractAdminPresenter<ReportModelsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ReportModelsAdminView.class)
	public static interface View extends AbstractAdminPresenter.View {

		LoadingMask getReportModelsLoadingMonitor();

		LoadingMask getReportModelsSectionsLoadingMonitor();

		Button getAddReportButton();

		ListStore<ReportModelDTO> getModelsStore();

		TextField<String> getReportName();

		Button getSaveReportSectionButton();

		EditorGrid<ProjectReportModelSectionDTO> getSectionsGrid();

		ListStore<ProjectReportModelSectionDTO> getReportSectionsStore();

		ListStore<ProjectReportModelSectionDTO> getReportSectionsComboStore();

		Button getAddReportSectionButton();

		List<ProjectReportModelSectionDTO> getSectionsToBeSaved();

		Grid<ReportModelDTO> getReportModelsGrid();

		ComboBox<ProjectReportModelSectionDTO> getParentSectionsCombo();

		void setReportModelPresenterHandler(ReportModelPresenterHandler handler);

		void setReportModelSectionPresenterHandler(ReportModelSectionPresenterHandler handler);

		Button getDeleteReportModelButton();

		Button getButtonImport();

		ContentPanel getReportModelPanel();

		ContentPanel getReportModelSectionsPanel();

	}

	public interface ReportModelPresenterHandler {

		void onClickHandler(ReportModelDTO reportModelDTO);

		void onSelectHandler(ReportModelDTO reportModelDTO);
	}

	public interface ReportModelSectionPresenterHandler {

		void onClickHandler(final ProjectReportModelSectionDTO projectReportModelSectionDTO);
	}

	private ReportModelDTO currentReportModel;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected ReportModelsAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_REPORTS_MODELS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// clean view
		view.getReportName().clear();

		// reload report model
		refreshReportModelsPanel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		/**
		 * ****************** Reports Model Section Panel **********************
		 */

		// ADD

		reportModelSectionAddButtonListener();

		// SAVE

		reportModelSectionSaveButtonListner();

		// DELETE

		reportModelSectionDeleteButtonListener();

		/**
		 * ******************** Report Model Panel *****************
		 */
		// ADD
		reportModelAddButtonListener();
		// DELETE

		reportModelDeleteButtonListener();

		// IMPORT

		reportModelImportButtonListener();

		// SHOW // EXPORT
		view.setReportModelPresenterHandler(new ReportModelPresenterHandler() {

			// SHOW SECTION
			@Override
			public void onSelectHandler(ReportModelDTO reportModelDTO) {

				currentReportModel = reportModelDTO;
				view.getSectionsGrid().show();
				view.getReportSectionsStore().removeAll();
				// Load all sections
				if (reportModelDTO.getSections() != null && reportModelDTO.getSections().size() > 0) {
					for (ProjectReportModelSectionDTO sectionDTO : setCompositeNames(reportModelDTO.getSections())) {
						sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
						recursiveFillSectionsList(sectionDTO);
					}
				}
				view.getReportSectionsStore().commitChanges();

				// Clear the sectionsToBeSaved
				view.getSectionsToBeSaved().clear();
				// Update the store of comboBox
				fillComboSections();
				// Enable the add button in section grid
				view.getAddReportSectionButton().enable();
				// Disable the save button in section grid
				view.getSaveReportSectionButton().disable();

			}

			// EXPORT
			@Override
			public void onClickHandler(ReportModelDTO reportModelDTO) {
				final ServletUrlBuilder urlBuilder =
						new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_MODEL_REPORT);

				urlBuilder.addParameter(RequestParameter.ID, reportModelDTO.getId());

				ClientUtils.launchDownload(urlBuilder.toString());
			}
		});

		// Handler

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.REPORT_MODEL_IMPORT)) {

					refreshReportModelsPanel();

				}
			}
		}));
	}

	private void reportModelSectionSaveButtonListner() {

		// --------------------Save report sections button---------------------------------------------------

		view.getSaveReportSectionButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {

				// Map to store the changes
				Map<String, Object> changes = new HashMap<String, Object>();
				changes.put(AdminUtil.PROP_REPORT_SECTION_MODEL, view.getSectionsToBeSaved());

				// Create a update command with the report model DTO
				UpdateProjectReportModel updateCommand = new UpdateProjectReportModel(currentReportModel.getId(), changes);

				// save changes
				// , new MaskingAsyncMonitor(view.getSectionsGrid(), I18N.CONSTANTS.saving())
				dispatch.execute(updateCommand, new CommandResultHandler<ReportModelDTO>() {

					@Override
					public void onCommandFailure(Throwable caught) {

						N10N.warn(I18N.CONSTANTS.adminReportModelCreationBox(),
							I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminReportModelStandard() + " '" + currentReportModel.getName() + "'"));

						// Clear
						view.getSectionsToBeSaved().clear();
						// Disable the save button
						view.getSaveReportSectionButton().disable();

						// Refresh the section panel
						view.getReportSectionsStore().removeAll();
						refreshReportModelSectionsPanel(currentReportModel);
					}

					@Override
					public void onCommandSuccess(ReportModelDTO result) {

						view.getReportSectionsStore().removeAll();

						if (result != null) {

							// Refresh the section grid.
							refreshReportModelSectionsPanel(result);

							// Update the combobox models
							fillComboSections();

							// Update the modelStore
							ReportModelDTO reportModelDTO = result;
							view.getModelsStore().remove(currentReportModel);
							view.getModelsStore().add(reportModelDTO);
							view.getModelsStore().commitChanges();

							// Reset the current report model
							currentReportModel = reportModelDTO;

							N10N.infoNotif(I18N.CONSTANTS.adminReportModelCreationBox(),
								I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminReportModelStandard() + " '" + currentReportModel.getName() + "'"));
						} else {
							N10N.warn(I18N.CONSTANTS.adminReportModelCreationBox(),
								I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminReportModelStandard() + " '" + currentReportModel.getName() + "'"));

						}

						// Clear this list anyway
						view.getSectionsToBeSaved().clear();

						// Disable the save button
						view.getSaveReportSectionButton().disable();

					}

				});

			}
		});

		// ------------------------------------Report sections grid edit
		// listener----------------------------------------------

		view.getSectionsGrid().addListener(Events.AfterEdit, new Listener<GridEvent<ProjectReportModelSectionDTO>>() {

			@Override
			public void handleEvent(GridEvent<ProjectReportModelSectionDTO> be) {

				// Get the section being edited
				ProjectReportModelSectionDTO sectionToBeSaved = be.getModel();

				if (sectionToBeSaved != null) {

					// Set the row index to record the position
					sectionToBeSaved.setRow(be.getRowIndex());

					if (sectionToBeSaved.getParentSectionModelName() != null) {

						// When users have edited the parent selections fields and select the root section's name.
						if (sectionToBeSaved.getParentSectionModelName().equals(I18N.CONSTANTS.adminReportSectionRoot())) {
							// Root section
							sectionToBeSaved.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
							// Set the report model id
							sectionToBeSaved.setProjectModelId(currentReportModel.getId());
							// Set null to parent section
							sectionToBeSaved.setParentSectionModelId(null);

						}
						// When users have edited the parent selections fields and select non-root section's name
						else if (view.getParentSectionsCombo().getSelection() != null && view.getParentSectionsCombo().getSelection().size() > 0) {

							// Get the parent section DTO object

							ProjectReportModelSectionDTO parentSection = view.getParentSectionsCombo().getSelection().get(0);

							Log.debug("You choose the section of parent : "
								+ parentSection.getId()
								+ " name : "
								+ parentSection.getName()
								+ " CopositeName : "
								+ parentSection.getCompositeName());

							// Set null to report model ComboBox<ProjectReportModelSectionDTO> parentSectionsCombo
							sectionToBeSaved.setProjectModelId(null);
							// Set parent section id
							sectionToBeSaved.setParentSectionModelId(parentSection.getId());

						}
					}
				}

				boolean alreadyIn = false;

				// Check if it is already in the list
				alreadyIn = isAlreadyIn(sectionToBeSaved);

				if (!alreadyIn) {
					view.getSectionsToBeSaved().add(sectionToBeSaved);

				}
				// enable the save button
				view.getSaveReportSectionButton().enable();

			}
		});

	}

	private void reportModelSectionDeleteButtonListener() {

		view.setReportModelSectionPresenterHandler(new ReportModelSectionPresenterHandler() {

			@Override
			public void onClickHandler(final ProjectReportModelSectionDTO projectReportModelSectionDTO) {

				// Check if the section is already saved into database
				if (projectReportModelSectionDTO.getId() == null) { // In this case, just delete the section locally
					view.getSectionsGrid().getStore().remove(projectReportModelSectionDTO);
					view.getSectionsGrid().getStore().commitChanges(); // Clear

					if (view.getSectionsToBeSaved() != null && view.getSectionsToBeSaved().contains(projectReportModelSectionDTO))
						view.getSectionsToBeSaved().remove(projectReportModelSectionDTO);
					return;
				}
				// First,check if the section can be deleted
				dispatch.execute(new GetReportElements(), new CommandResultHandler<ReportElementsResult>() {

					@Override
					public void onCommandFailure(Throwable caught) {

						N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(projectReportModelSectionDTO.getName()));
						view.getSectionsToBeSaved().clear();
						return;
					}

					@Override
					public void onCommandSuccess(ReportElementsResult result) {
						List<ReportElementDTO> reportElements = result.getReportElements();
						List<ReportListElementDTO> reportListElements = result.getReportListElements();
						// If it is used as a report element
						for (ReportElementDTO reportElement : reportElements) {
							if (reportElement.getModelId().equals(currentReportModel.getId())) {
								N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(projectReportModelSectionDTO.getName()));
								return;
							}
						} // If it is used as a report list element
						for (ReportListElementDTO reportListElement : reportListElements) {
							if (reportListElement.getModelId().equals(currentReportModel.getId())) {
								N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(projectReportModelSectionDTO.getName()));
								return;
							}
						} // If goes this far, begins to delete
						final List<ProjectReportModelSectionDTO> sectionsToBeDeleted = new ArrayList<ProjectReportModelSectionDTO>();
						sectionsToBeDeleted.add(projectReportModelSectionDTO);
						DeleteReportModels deleteCommand = new DeleteReportModels(null, sectionsToBeDeleted);
						deleteCommand.setReportModelId(currentReportModel.getId());

						dispatch.execute(deleteCommand, new CommandResultHandler<ReportModelDTO>() {

							@Override
							public void onCommandFailure(Throwable caught) {
								view.getSectionsToBeSaved().clear();
								N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(projectReportModelSectionDTO.getName()));
								return;
							}

							@Override
							public void onCommandSuccess(ReportModelDTO result) {
								if (result == null) {
									N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(projectReportModelSectionDTO.getName()));
									view.getSectionsToBeSaved().clear();
									return;
								}
								// Refresh the section grid
								view.getReportSectionsStore().removeAll(); // Reload all sections if this report has sections
								List<ProjectReportModelSectionDTO> sectionsWithCompositeNames = setCompositeNames(result.getSections());
								if (result.getSections() != null && sectionsWithCompositeNames != null) {
									for (ProjectReportModelSectionDTO sectionDTO : sectionsWithCompositeNames) {
										sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
										recursiveFillSectionsList(sectionDTO);
									}
								}
								view.getReportSectionsStore().commitChanges(); // Refresh
								view.getSectionsToBeSaved().clear();
								fillComboSections(); // Reset the report model
								view.getReportModelsGrid().getStore().remove(currentReportModel);
								currentReportModel = result;
								view.getReportModelsGrid().getStore().add(result);
								view.getReportModelsGrid().getStore().commitChanges();
							}
						}, new LoadingMask(view.getReportModelSectionsPanel()));
					}
				}, new LoadingMask(view.getReportModelSectionsPanel(), I18N.CONSTANTS.verfyingAndDeleting()));
			}
		});

	}

	private void reportModelSectionAddButtonListener() {

		view.getAddReportSectionButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {
				ProjectReportModelSectionDTO section = new ProjectReportModelSectionDTO();
				section.setId(null);
				section.setIndex(0);
				section.setName(I18N.CONSTANTS.adminEditGrid());
				section.setNumberOfTextarea(0);
				section.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
				section.setProjectModelId(currentReportModel.getId());
				section.setParentSectionModelId(null);
				if (!view.getReportSectionsStore().contains(section))
					view.getReportSectionsStore().add(section);
			}
		});

	}

	private void reportModelImportButtonListener() {

		view.getButtonImport().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				eventBus.navigateRequest(Page.IMPORT_MODEL.requestWith(RequestParameter.TYPE, AdminUtil.ADMIN_REPORT_MODEL));
			}
		});

	}

	private void reportModelDeleteButtonListener() {

		view.getDeleteReportModelButton().addListener(Events.OnClick, new Listener<ButtonEvent>() {

			@Override
			public void handleEvent(ButtonEvent be) {

				final List<ReportModelDTO> reportsToBeDeleted = view.getReportModelsGrid().getSelectionModel().getSelectedItems();

				dispatch.execute(new GetReportElements(), new CommandResultHandler<ReportElementsResult>() {

					@Override
					public void onCommandFailure(Throwable caught) {
						view.getSectionsToBeSaved().clear();
						String modelNames = "";
						for (ReportModelDTO r : reportsToBeDeleted) {
							modelNames += r.getName() + " ";
						}
						N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(modelNames), null);
						return;
					}

					@Override
					public void onCommandSuccess(final ReportElementsResult result) {

						List<ReportElementDTO> reportElements = result.getReportElements();
						List<ReportListElementDTO> reportListElements = result.getReportListElements();

						for (ReportModelDTO reportModelToDelete : reportsToBeDeleted) {
							// If it is used as a report element
							for (ReportElementDTO reportElement : reportElements) {
								if (reportElement.getModelId().equals(reportModelToDelete.getId())) {
									N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(reportModelToDelete.getName()));
									return;
								}
							}
							// If it is used as a report list element
							for (ReportListElementDTO reportListElement : reportListElements) {
								if (reportListElement.getModelId().equals(reportModelToDelete.getId())) {
									N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(reportModelToDelete.getName()));
									return;
								}
							}
						}
						// If goes this far,begins to delete
						dispatch.execute(new DeleteReportModels(reportsToBeDeleted, null), new CommandResultHandler<ReportModelDTO>() {

							@Override
							public void onCommandFailure(final Throwable caught) {
								view.getSectionsToBeSaved().clear();
								String modelNames = "";
								for (ReportModelDTO r : reportsToBeDeleted) {
									modelNames += r.getName() + " ";
								}
								N10N.warn(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(modelNames));
								return;
							}

							@Override
							public void onCommandSuccess(final ReportModelDTO result) { // Refresh the section grid
								for (ReportModelDTO p : reportsToBeDeleted) {
									view.getReportModelsGrid().getStore().remove(p);
								}
								view.getReportModelsGrid().getStore().commitChanges(); // Refresh sectionsToBeSaved.clear();
								view.getSectionsGrid().getStore().removeAll();
								currentReportModel = null;
								view.getAddReportSectionButton().disable();
								view.getSaveReportSectionButton().disable();
							}
						}, new LoadingMask(view.getReportModelPanel()));
					}
				}, new LoadingMask(view.getReportModelPanel(), I18N.CONSTANTS.verfyingAndDeleting()));
			}
		});

	}

	private void reportModelAddButtonListener() {

		view.getAddReportButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				if (view.getReportName().getValue() != null && view.getModelsStore().findModel("name", view.getReportName().getValue()) == null) {

					HashMap<String, Object> newReportProperties = new HashMap<String, Object>();
					// Store the report's name in properties variable
					newReportProperties.put(AdminUtil.PROP_REPORT_MODEL_NAME, view.getReportName().getValue());
					newReportProperties.put(AdminUtil.PROP_REPORT_SECTION_MODEL, null);

					dispatch.execute(new CreateEntity(ReportModelDTO.ENTITY_NAME, newReportProperties), new CommandResultHandler<CreateResult>() {

						@Override
						public void onCommandFailure(Throwable caught) {

							N10N.warn(I18N.CONSTANTS.adminReportModelCreationBox(),
								I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminReportModelStandard() + " '" + view.getReportName().getValue() + "'"));

							view.getSectionsToBeSaved().clear();

						}

						@Override
						public void onCommandSuccess(CreateResult result) {

							if (result != null) {

								// Refresh the report model grid
								view.getModelsStore().add((ReportModelDTO) result.getEntity());
								view.getModelsStore().commitChanges();
								N10N.infoNotif(I18N.CONSTANTS.adminReportModelCreationBox(),
									I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminReportModelStandard() + " '" + result.getEntity().getEntityName() + "'"));

								// Create a selected model list
								List<ReportModelDTO> selectedModel = new ArrayList<ReportModelDTO>();
								selectedModel.add((ReportModelDTO) result.getEntity());

								// Focus the new created model cell in the grid

								int rowIndex = view.getModelsStore().indexOf((ReportModelDTO) result.getEntity());
								Element addedRow = view.getReportModelsGrid().getView().getRow(rowIndex);

								view.getReportModelsGrid().getSelectionModel().setSelection(selectedModel);

								addedRow.setScrollTop(addedRow.getScrollTop());
								addedRow.scrollIntoView();
								view.getReportName().clear();

							} else {
								N10N.warn(I18N.CONSTANTS.adminReportModelCreationBox(),
									I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminReportModelStandard() + " '" + view.getReportName().getValue() + "'"));
							}

							view.getSectionsToBeSaved().clear();
						}
					}, view.getReportModelsLoadingMonitor());

				} else {
					// Invalid input
					N10N.warn(I18N.CONSTANTS.adminReportModelCreationBox(), I18N.CONSTANTS.adminStandardInvalidValues());
					view.getSectionsToBeSaved().clear();
				}

			};
		});

	}

	/**
	 * Load Report Model
	 */
	public void refreshReportModelsPanel() {

		dispatch.execute(new GetReportModels(), new CommandResultHandler<ListResult<ReportModelDTO>>() {

			@Override
			public void onCommandFailure(Throwable arg0) {
				N10N.warn(I18N.CONSTANTS.adminboard(), I18N.CONSTANTS.adminProblemLoading());
			}

			@Override
			public void onCommandSuccess(ListResult<ReportModelDTO> result) {
				if (result.getList() != null && !result.getList().isEmpty()) {
					view.getModelsStore().removeAll();
					view.getModelsStore().add(result.getList());
					view.getModelsStore().commitChanges();
				}

			}
		}, view.getReportModelsLoadingMonitor());
	}

	public void refreshReportModelSectionsPanel(ReportModelDTO reportModel) {
		// Load all sections into the grid
		if (reportModel.getSections() != null && reportModel.getSections().size() > 0) {
			for (ProjectReportModelSectionDTO sectionDTO : setCompositeNames(reportModel.getSections())) {
				sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
				recursiveFillSectionsList(sectionDTO);
			}
		}

		// Update the section store
		view.getReportSectionsStore().commitChanges();
	}

	/**
	 * Set the composite name for each sectionDTO to display in comboBox
	 */
	public static List<ProjectReportModelSectionDTO> setCompositeNames(List<ProjectReportModelSectionDTO> sections) {
		if (sections != null && sections.size() > 0) {
			List<ProjectReportModelSectionDTO> sectionsReturn = new ArrayList<ProjectReportModelSectionDTO>();

			for (ProjectReportModelSectionDTO s : sections) {
				// name(id)
				s.setCompositeName(s.getName() + "<i>(" + s.getId() + ")</i>");
				sectionsReturn.add(s);
			}

			return sectionsReturn;
		} else if (sections.size() == 0) {
			return sections;
		}

		else {
			return null;
		}

	}

	/**
	 * Method to fill the combbox of the parent section selection.
	 */

	private void fillComboSections() {
		view.getReportSectionsComboStore().removeAll();
		ProjectReportModelSectionDTO dummyRootSection = new ProjectReportModelSectionDTO();
		dummyRootSection.setIndex(0);
		dummyRootSection.setName(I18N.CONSTANTS.adminReportSectionRoot());
		dummyRootSection.setNumberOfTextarea(0);
		dummyRootSection.setParentSectionModelName("");
		dummyRootSection.setProjectModelId(0);
		view.getReportSectionsComboStore().add(dummyRootSection);
		view.getReportSectionsComboStore().add(view.getReportSectionsStore().getModels());
		view.getReportSectionsComboStore().commitChanges();
	}

	/**
	 * Add all sections into report section grid recursively.
	 * 
	 * @param rootSection
	 */

	private void recursiveFillSectionsList(ProjectReportModelSectionDTO rootSection) {
		view.getReportSectionsStore().add(rootSection);
		if (rootSection.getSubSections() == null)
			return;
		for (final ProjectReportModelSectionDTO child : setCompositeNames(rootSection.getSubSections())) {
			child.setParentSectionModelName(rootSection.getCompositeName());
			recursiveFillSectionsList(child);
		}
	}

	/**
	 * Check if the section editing is already recored in the list. If it already exists, update it and return true. Or
	 * return false.
	 * 
	 * @param sectionToBeSaved
	 * @return boolean
	 */
	public boolean isAlreadyIn(ProjectReportModelSectionDTO sectionToBeSaved) {
		if (view.getSectionsToBeSaved().size() > 0) {

			try {
				for (ProjectReportModelSectionDTO sectionI : view.getSectionsToBeSaved()) // Raise
				// ConcurrentModificationException
				// sometimes
				{
					if (sectionI.getRow().equals(sectionToBeSaved.getRow())) {
						view.getSectionsToBeSaved().remove(sectionI);
						view.getSectionsToBeSaved().add(sectionToBeSaved);
						return true;
					}
				}
			} catch (ConcurrentModificationException e) {
				Log.debug(" Catche a ConcurrentModificationException, recall the method isAlreadyIn ! ");
				return isAlreadyIn(sectionToBeSaved);
			}

		}

		return false;
	}

}
