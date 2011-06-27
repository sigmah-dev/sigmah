package org.sigmah.client.page.admin.report;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.model.common.AdminModelActionListener;
import org.sigmah.client.page.admin.report.AdminReportModelPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.shared.command.DeleteReportModels;
import org.sigmah.shared.command.GetReportElements;
import org.sigmah.shared.command.result.ProjectReportModelResult;
import org.sigmah.shared.command.result.ReportElementsResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminReportModelView extends View {

	private final Grid<ReportModelDTO> reportModelsGrid;
	private ListStore<ReportModelDTO> modelsStore;
	private ListStore<ProjectReportModelSectionDTO> reportSectionsStore;
	private ListStore<ProjectReportModelSectionDTO> reportSectionsComboStore;
	private EditorGrid<ProjectReportModelSectionDTO> sectionsGrid;
	private final Dispatcher dispatcher;
	private Button saveReportSectionButton;
	private Button addReportSectionButton;
	private ComboBox<ProjectReportModelSectionDTO> parentSectionsCombo;

	private ReportModelDTO currentReportModel;
	private final ContentPanel reportPanel;
	private Button addReportButton;
	private TextField<String> reportName;
    private List<ProjectReportModelSectionDTO> sectionsToBeSaved = new ArrayList<ProjectReportModelSectionDTO>();

	public AdminReportModelView(Dispatcher dispatcher) {

		this.dispatcher = dispatcher;

		setLayout(new BorderLayout());
		setHeaderVisible(false);
		setBorders(false);
		setBodyBorder(false);

		ContentPanel sidePanel = new ContentPanel(new FitLayout());
		sidePanel.setHeaderVisible(false);
		sidePanel.setWidth(350);
		sidePanel.setScrollMode(Scroll.AUTOY);
		reportModelsGrid = buildModelsListGrid();
		sidePanel.add(reportModelsGrid);
		sidePanel.setTopComponent(reportModelToolBar());
		

		reportPanel = new ContentPanel(new FitLayout());
		reportPanel.setHeaderVisible(false);
		reportPanel.setBorders(true);
		reportPanel.add(buildReportSectionsGrid());
		reportPanel.setTopComponent(reportSectionToolBar());
		reportPanel.setScrollMode(Scroll.AUTOY);

		final BorderLayoutData leftLayoutData = new BorderLayoutData(
				LayoutRegion.WEST, 350);
		leftLayoutData.setMargins(new Margins(0, 4, 0, 0));
		add(sidePanel, leftLayoutData);
		final BorderLayoutData mainLayoutData = new BorderLayoutData(
				LayoutRegion.CENTER);
		mainLayoutData.setMargins(new Margins(0, 0, 0, 4));
		add(reportPanel, mainLayoutData);
	}

	private EditorGrid<ProjectReportModelSectionDTO> buildReportSectionsGrid() {

		reportSectionsStore = new ListStore<ProjectReportModelSectionDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("id");
		column.setWidth(50);
		column.setHeader(I18N.CONSTANTS.adminFlexibleFieldId());
		configs.add(column);

		column = new ColumnConfig();
		column.setId("index");
		column.setWidth(50);
		column.setHeader(I18N.CONSTANTS.adminReportSectionIndex());
		NumberField index = new NumberField();
		index.setAllowBlank(false);
		column.setEditor(new CellEditor(index) {
			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((Number) value).intValue();
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("name");
		column.setWidth(400);
		column.setHeader(I18N.CONSTANTS.adminReportSectionName());
		TextField<String> name = new TextField<String>();
		name.setAllowBlank(false);
		column.setEditor(new CellEditor(name));
		configs.add(column);

		column = new ColumnConfig();
		column.setId("numberOfTextarea");
		column.setWidth(75);
		column.setHeader(I18N.CONSTANTS.adminReportSectionNbText());
		NumberField nbTextAreas = new NumberField();
		nbTextAreas.setAllowBlank(false);
		column.setEditor(new CellEditor(nbTextAreas) {
			@Override
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				return ((Number) value).intValue();
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setId("parentSectionModelName");
		column.setWidth(400);
		column.setHeader(I18N.CONSTANTS.adminReportSectionParentSection());
		parentSectionsCombo = new ComboBox<ProjectReportModelSectionDTO>();
		parentSectionsCombo.setTriggerAction(TriggerAction.ALL);
		parentSectionsCombo.setEditable(false);
		reportSectionsComboStore = new ListStore<ProjectReportModelSectionDTO>();
		parentSectionsCombo.setStore(reportSectionsComboStore);
		parentSectionsCombo.setDisplayField("name");
		column.setEditor(new CellEditor(parentSectionsCombo) {
			@Override
			// Get the ProjectReportModelSection equivalent to the value
			// displayed when there's one
			public Object preProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				ProjectReportModelSectionDTO section = null;
				for (ProjectReportModelSectionDTO sectionI : reportSectionsStore
						.getModels()) {
					if (sectionI.getName().equals(value.toString())) {
						section = sectionI;
					}
				}
				return section;
			}

			@Override
			// Get the field to display if a ProjectReportModelSection has been
			// chosen
			public Object postProcessValue(Object value) {
				if (value == null) {
					return value;
				}
				Log.debug("value "
						+ ((ProjectReportModelSectionDTO) value).getName());
				// parentSection = ((ProjectReportModelSectionDTO) value);
				// Log.debug("parentSection " + parentSection);
				return ((ProjectReportModelSectionDTO) value).getName();
			}
		});
		configs.add(column);

		column = new ColumnConfig();
		column.setWidth(75);
		column.setAlignment(Style.HorizontalAlignment.RIGHT);
		column.setRenderer(new GridCellRenderer<ProjectReportModelSectionDTO>() {

			@Override
			public Object render(final ProjectReportModelSectionDTO model,
					final String property, ColumnData config, int rowIndex,
					int colIndex,
					ListStore<ProjectReportModelSectionDTO> store,
					Grid<ProjectReportModelSectionDTO> grid) {

				Button deleteSectionButton = new Button(I18N.CONSTANTS.delete());
				deleteSectionButton.setItemId(UIActions.delete);
				deleteSectionButton.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {

							@Override
							public void handleEvent(ButtonEvent be) {

								

								//Check if the section is already saved into database
								if(model.getId()==-1)
								{
									//In this case, just delete the section locally
									sectionsGrid.getStore().remove(model);
									sectionsGrid.getStore().commitChanges();
									
									//Clear
									sectionsToBeSaved.clear();
									return;
								}		
								
								
								//First,check if the section can be deleted
								dispatcher.execute(new GetReportElements(),  new MaskingAsyncMonitor(sectionsGrid, I18N.CONSTANTS.verfyingAndDeleting()), new AsyncCallback<ReportElementsResult>(){

									@Override
									public void onFailure(Throwable caught) {										
									
										MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(model.getName()), null);																				
										
										sectionsToBeSaved.clear();
										
										return;
									}

									@Override
									public void onSuccess(
											ReportElementsResult result) {
																	
										
										List<ReportElementDTO> reportElements = result.getReportElements();
										List<ReportListElementDTO>reportListElements = result.getReportListElements();
										
										
										//If it is used as a report element
										for(ReportElementDTO reportElement:reportElements)
										{
											if(reportElement.getModelId()==currentReportModel.getId())
											{
												MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(model.getName()), null);
												return;
											}
										}
										
										
										//If it is used as a report list element
										for(ReportListElementDTO reportListElement:reportListElements)
										{
											if(reportListElement.getModelId()==currentReportModel.getId())
											{
												MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(model.getName()), null);
												return;
											}
										}
										
										
										
										//If goes this far, begins to delete
										final List<ProjectReportModelSectionDTO> sectionsToBeDeleted = new ArrayList<ProjectReportModelSectionDTO>();
										sectionsToBeDeleted.add(model);
										DeleteReportModels deleteCommand = new DeleteReportModels(null,sectionsToBeDeleted);
										deleteCommand.setReportModelId(currentReportModel.getId());
										dispatcher.execute(deleteCommand, new MaskingAsyncMonitor(sectionsGrid, null), new AsyncCallback<ProjectReportModelResult>(){

											@Override
											public void onFailure(
													Throwable caught) {
												
												
												sectionsToBeSaved.clear();
												
												MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(model.getName()), null);																				
												
												return;
																						
											}

											@Override
											public void onSuccess(
													ProjectReportModelResult result) {
												
												if(result==null || result.getReportModelDTO()==null)
												{
													MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(model.getName()), null);	
													sectionsToBeSaved.clear();
													return;
												}
												
												//Refresh the section grid
												reportSectionsStore.removeAll();
												//Reload all sections
												 for(ProjectReportModelSectionDTO sectionDTO : result.getReportModelDTO().getSectionsDTO()){
					                                    sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
					                                    recursiveFillSectionsList(sectionDTO);
					                            }											
												reportSectionsStore.commitChanges();
												
												//Refresh 
												sectionsToBeSaved.clear();
												fillComboSections();
												
												//Reset the report model
												reportModelsGrid.getStore().remove(currentReportModel);
												currentReportModel = result.getReportModelDTO();
												reportModelsGrid.getStore().add(result.getReportModelDTO());
												reportModelsGrid.getStore().commitChanges();
																						
												
												
											}
											
											
										});
									
										
										
									}
									
								});
								
								
								
								

							}

						});

				return deleteSectionButton;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		sectionsGrid = new EditorGrid<ProjectReportModelSectionDTO>(
				reportSectionsStore, cm);
		sectionsGrid.setAutoHeight(true);

		sectionsGrid.hide();
		sectionsGrid.enable();
		sectionsGrid.getView().setForceFit(true);
		return sectionsGrid;
	}

	private Grid<ReportModelDTO> buildModelsListGrid() {

		modelsStore = new ListStore<ReportModelDTO>();

		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig("name",
				I18N.CONSTANTS.adminReportName(), 280);
		column.setRenderer(new GridCellRenderer<ReportModelDTO>() {

			@Override
			public Object render(final ReportModelDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ReportModelDTO> store, Grid<ReportModelDTO> grid) {
				final ToggleAnchor anchor = new ToggleAnchor(model.getName());
				anchor.setAnchorMode(true);

				anchor.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {

						currentReportModel = model;
						sectionsGrid.show();
						reportSectionsStore.removeAll();			
						
						//Load all sections 
						if(model.getSectionsDTO() != null){
                            for(ProjectReportModelSectionDTO sectionDTO : model.getSectionsDTO()){
                                    sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
                                    recursiveFillSectionsList(sectionDTO);
                            }
                    }      
						
						reportSectionsStore.commitChanges();

						for(ProjectReportModelSectionDTO p:reportSectionsStore.getModels())
						{
							Log.debug("After loading all sections ID: "+p.getId()+" Name: "+p.getName()+"\n");
						}
						
						//Clear the sectionsToBeSaved
						sectionsToBeSaved.clear();
						
						// Update the store of comboBox
						fillComboSections();
												

						// Enable the add button in section grid
						addReportSectionButton.enable();

						// Disable the save button in section grid
						saveReportSectionButton.disable();
					}

				});
				return anchor;
			}

		});
		configs.add(column);

		column = new ColumnConfig();
		column.setWidth(70);
		column.setAlignment(HorizontalAlignment.CENTER);
		column.setRenderer(new GridCellRenderer<ReportModelDTO>() {
			@Override
			public Object render(final ReportModelDTO model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ReportModelDTO> store, Grid<ReportModelDTO> grid) {

				Button buttonExport = new Button(I18N.CONSTANTS.export());
				buttonExport.setItemId(UIActions.exportModel);
				buttonExport.addListener(Events.OnClick,
						new Listener<ButtonEvent>() {
							@Override
							public void handleEvent(ButtonEvent be) {
								AdminModelActionListener listener = new AdminModelActionListener(
										AdminReportModelView.this, dispatcher,
										false);
								listener.setModelId(model.getId());
								listener.setIsOrgUnit(false);
								listener.setIsReport(true);// the model is a
															// project report
								listener.onUIAction(UIActions.exportModel);
							}
						});
				return buttonExport;
			}
		});
		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);

		Grid<ReportModelDTO> grid = new Grid<ReportModelDTO>(modelsStore, cm);
		grid.setAutoHeight(true);
		grid.setAutoWidth(false);
		grid.getView().setForceFit(true);
		grid.setAutoWidth(true);
		return grid;
	}

	private ToolBar reportModelToolBar() {
		ToolBar toolbar = new ToolBar();

		reportName = new TextField<String>();
		reportName.setFieldLabel(I18N.CONSTANTS.adminReportName());
		toolbar.add(reportName);

		addReportButton = new Button(I18N.CONSTANTS.addItem(),
				IconImageBundle.ICONS.add());

		Button deleteReportButton = new Button(I18N.CONSTANTS.delete(),
				IconImageBundle.ICONS.delete());
		deleteReportButton.addListener(Events.OnClick,
				new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {
						
						final List<ReportModelDTO> reportsToBeDeleted = reportModelsGrid.getSelectionModel().getSelectedItems();
						
						//First,check if the report model can be deleted
						dispatcher.execute(new GetReportElements(),  new MaskingAsyncMonitor(reportModelsGrid, I18N.CONSTANTS.verfyingAndDeleting()), new AsyncCallback<ReportElementsResult>(){

							@Override
							public void onFailure(Throwable caught) {
								
								sectionsToBeSaved.clear();
								
								String modelNames="";
								for(ReportModelDTO r:reportsToBeDeleted)
								{
									modelNames+=r.getName()+" ";
								}
							
								MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(modelNames), null);
								
								return;
							}

							@Override
							public void onSuccess(
									ReportElementsResult result) {
								
								
								
								List<ReportElementDTO> reportElements = result.getReportElements();
								List<ReportListElementDTO>reportListElements = result.getReportListElements();
								
								for(ReportModelDTO reportModelToDelete:reportsToBeDeleted)
								{
									//If it is used as a report element
								   for(ReportElementDTO reportElement:reportElements)
								   {
		
									if(reportElement.getModelId()==reportModelToDelete.getId())
									{
										MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(reportModelToDelete.getName()), null);
										return;
									}
								   }
								   
								   //If it is used as a report list element
								  for(ReportListElementDTO reportListElement:reportListElements)
								   {
									if(reportListElement.getModelId()==reportModelToDelete.getId())
									{
										MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.reportModelAlreadyUsed(reportModelToDelete.getName()), null);
										return;
									}
								   }
								}
								
								
								//If goes this far, begins to delete
								
								dispatcher.execute(new DeleteReportModels(reportsToBeDeleted,null), new MaskingAsyncMonitor(reportModelsGrid, null), new AsyncCallback<ProjectReportModelResult>(){

									@Override
									public void onFailure(
											Throwable caught) {
										
										
										sectionsToBeSaved.clear();

										String modelNames="";
										for(ReportModelDTO r:reportsToBeDeleted)
										{
											modelNames+=r.getName()+" ";
										}
									
										MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(modelNames), null);
										
										return;
										
									}

									@Override
									public void onSuccess(
											ProjectReportModelResult result) {
										
										
										//Refresh the section grid
										for(ReportModelDTO p: reportsToBeDeleted)
										{
											reportModelsGrid.getStore().remove(p);
										}
										
										reportModelsGrid.getStore().commitChanges();
										
										//Refresh 
										sectionsToBeSaved.clear();
										sectionsGrid.getStore().removeAll();
										currentReportModel=null;
										
									}
									
									
								});
							
								
								
							}
							
						});
						
						
						
						
					}

				});

		Button buttonImport = new Button(I18N.CONSTANTS.importItem());
		buttonImport.setItemId(UIActions.importModel);
		buttonImport.setEnabled(true);
		buttonImport.addListener(Events.Select, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				AdminModelActionListener listener = new AdminModelActionListener(
						AdminReportModelView.this, dispatcher, false);
				listener.setIsOrgUnit(false);
				listener.setIsReport(true);
				listener.onUIAction(UIActions.importModel);
			}

		});

		toolbar.add(deleteReportButton);

		toolbar.add(addReportButton);

		toolbar.add(buttonImport);

		return toolbar;
	}

	private ToolBar reportSectionToolBar() {
		ToolBar toolbar = new ToolBar();

		addReportSectionButton = new Button(I18N.CONSTANTS.addItem(),
				IconImageBundle.ICONS.add());
		addReportSectionButton.disable();
		addReportSectionButton.setItemId(UIActions.add);
		addReportSectionButton.addListener(Events.OnClick,
				new Listener<ButtonEvent>() {

					@Override
					public void handleEvent(ButtonEvent be) {

						ProjectReportModelSectionDTO section = new ProjectReportModelSectionDTO();

						section.setId(null);
						section.setIndex(0);
						section.setName(I18N.CONSTANTS.adminEditGrid());
						section.setNumberOfTextarea(0);
						section.setParentSectionModelName(I18N.CONSTANTS
								.adminReportSectionRoot());
						section.setProjectModelId(currentReportModel.getId());
						section.setParentSectionModelId(null);
						if (!reportSectionsStore.contains(section))
							reportSectionsStore.add(section);

					}

				});
		toolbar.add(addReportSectionButton);

		saveReportSectionButton = new Button(I18N.CONSTANTS.save(),
				IconImageBundle.ICONS.save());
		saveReportSectionButton.disable();
		toolbar.add(saveReportSectionButton);

		return toolbar;
	}

	@Override
	public ListStore<ReportModelDTO> getReportModelsStore() {
		return modelsStore;
	}

	@Override
	public Component getMainPanel() {
		return this;
	}

	/**
	 * Method to fill the combbox of the parent section selection.
	 * 
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void fillComboSections() {

		getReportSectionsComboStore().removeAll();
		ProjectReportModelSectionDTO dummyRootSection = new ProjectReportModelSectionDTO();
		dummyRootSection.setIndex(0);
		dummyRootSection.setName(I18N.CONSTANTS.adminReportSectionRoot());
		dummyRootSection.setNumberOfTextarea(0);
		dummyRootSection.setParentSectionModelName("");
		dummyRootSection.setProjectModelId(0);
		getReportSectionsComboStore().add(dummyRootSection);
		getReportSectionsComboStore().add(getReportSectionsStore().getModels());
		getReportSectionsComboStore().commitChanges();
	}

	
	/**
	 * Add all sections into report section grid recursively.
	 * 
	 * @param rootSection
	 * 
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void recursiveFillSectionsList(ProjectReportModelSectionDTO rootSection) {
		
	    reportSectionsStore.add(rootSection);	
	    
	    if(rootSection.getSubSectionsDTO()==null)
	    	return;
	    
		for (final ProjectReportModelSectionDTO child : rootSection
				.getSubSectionsDTO()) {
			child.setParentSectionModelName(rootSection.getName());
			recursiveFillSectionsList(child);
			
		}
	}

	@Override
	public MaskingAsyncMonitor getReportModelsLoadingMonitor() {
		return new MaskingAsyncMonitor(reportModelsGrid,
				I18N.CONSTANTS.loading());
	}

	@Override
	public Button getAddReportButton() {

		return this.addReportButton;

	}

	@Override
	public ListStore<ReportModelDTO> getModelsStore() {

		return this.modelsStore;
	}

	@Override
	public TextField<String> getReportName() {
		return this.reportName;
	}

	@Override
	public Button getSaveReportSectionButton() {
		return this.saveReportSectionButton;
	}

	@Override
	public EditorGrid<ProjectReportModelSectionDTO> getSectionsGrid() {

		return this.sectionsGrid;
	}

	@Override
	public ReportModelDTO getCurrentReportModel() {
		return this.currentReportModel;
	}

	@Override
	public void setCurrentReportModel(ReportModelDTO model) {
		this.currentReportModel = model;
	}

	@Override
	public ListStore<ProjectReportModelSectionDTO> getReportSectionsStore() {
		return this.reportSectionsStore;
	}

	@Override
	public ListStore<ProjectReportModelSectionDTO> getReportSectionsComboStore() {
		return this.reportSectionsComboStore;
	}

	@Override
	public Button getAddReportSectionButton() {
		return this.addReportSectionButton;
	}

	@Override
	public List<ProjectReportModelSectionDTO> getSectionsToBeSaved() {
		return this.sectionsToBeSaved;
	}



}
