package org.sigmah.client.page.admin.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.callback.Deleted;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.AdminModelActionListener;
import org.sigmah.client.page.admin.report.AdminReportModelPresenter.View;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.ui.ToggleAnchor;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
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
	private final HashMap<String, Object> newReportProperties = new HashMap<String, Object>();
	private final Dispatcher dispatcher;
	private Button saveReportSectionButton;
	private Button addReportSectionButton;
	private ComboBox<ProjectReportModelSectionDTO>  parentSectionsCombo;
	private List<ProjectReportModelSectionDTO> sectionsToBeSaved = new ArrayList<ProjectReportModelSectionDTO>();
	private ReportModelDTO currentReportModel;
	private final ContentPanel reportPanel ;

	public AdminReportModelView(Dispatcher dispatcher){		
		
		this.dispatcher = dispatcher;
		
        setLayout(new BorderLayout());
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);
        
        ContentPanel sidePanel = new ContentPanel(new VBoxLayout());
        sidePanel.setHeaderVisible(false);
        sidePanel.setWidth(350);
        sidePanel.setScrollMode(Scroll.NONE);
        reportModelsGrid = buildModelsListGrid();
        sidePanel.add(reportModelsGrid);
        sidePanel.setTopComponent(reportModelToolBar());
        
        reportPanel = new ContentPanel(new FitLayout());
        reportPanel.setHeaderVisible(false);
        reportPanel.setBorders(true);
        reportPanel.add(buildReportSectionsGrid());
        reportPanel.setTopComponent(reportSectionToolBar());
        
        final BorderLayoutData leftLayoutData = new BorderLayoutData(LayoutRegion.WEST, 350);
        leftLayoutData.setMargins(new Margins(0, 4, 0, 0));
		add(sidePanel, leftLayoutData);	
		 final BorderLayoutData mainLayoutData = new BorderLayoutData(LayoutRegion.CENTER);
	        mainLayoutData.setMargins(new Margins(0, 0, 0, 4));
		add(reportPanel, mainLayoutData);		
	}

	private EditorGrid<ProjectReportModelSectionDTO> buildReportSectionsGrid(){	
		
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
        column.setEditor(new CellEditor(index){
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
        column.setEditor(new CellEditor(nbTextAreas){
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
        	//Get the ProjectReportModelSection equivalent to the value displayed when there's one
        	public Object preProcessValue(Object value) {    
        		if (value == null) {    
        			return value;    
        		}    
        		ProjectReportModelSectionDTO section = null;
        		for(ProjectReportModelSectionDTO sectionI : reportSectionsStore.getModels()){
        			if(sectionI.getName().equals(value.toString())){
        				section = sectionI;
        			}
        		}
        		return section;    
        	}    
        	  
        	@Override    
        	//Get the field to display if a ProjectReportModelSection has been chosen
        	public Object postProcessValue(Object value) {           		
        		if (value == null) {    
        			return value;    
        		}    
        		Log.debug("value " + ((ProjectReportModelSectionDTO) value).getName());
        		//parentSection = ((ProjectReportModelSectionDTO) value);
        		//Log.debug("parentSection " + parentSection);
        		return ((ProjectReportModelSectionDTO) value).getName();    
        	}    
        });
        configs.add(column);
        
        column = new ColumnConfig();    
		column.setWidth(75);  
		column.setAlignment(Style.HorizontalAlignment.RIGHT);
	    column.setRenderer(new GridCellRenderer<ProjectReportModelSectionDTO>(){

			@Override
			public Object render(final ProjectReportModelSectionDTO model, final String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<ProjectReportModelSectionDTO> store, Grid<ProjectReportModelSectionDTO> grid) {
				
				Button button = new Button(I18N.CONSTANTS.delete());
		        button.setItemId(UIActions.delete);
		        button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

					@Override
					public void handleEvent(ButtonEvent be) {
						dispatcher.execute(new Delete(model), null, new Deleted() {
	                        @Override
	                        public void deleted() {
	                        	reportSectionsStore.remove(model);
	                        }
	                    });
		                
					}
		        	
		        });
		        return button;
			}
	    });
	    configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);		
		
		sectionsGrid = new EditorGrid<ProjectReportModelSectionDTO>(reportSectionsStore, cm); 
		sectionsGrid.setAutoHeight(true);
		sectionsGrid.addListener(Events.AfterEdit, new Listener<GridEvent<ProjectReportModelSectionDTO>>() {

            @Override
            public void handleEvent(GridEvent<ProjectReportModelSectionDTO> be) {
            	Log.debug("report " + currentReportModel.getName());
            	ProjectReportModelSectionDTO sectionToBeSaved = be.getModel();
            	sectionToBeSaved.setRow(be.getRowIndex());
            	if(sectionToBeSaved != null && I18N.CONSTANTS.adminReportSectionRoot().equals(sectionToBeSaved.getParentSectionModelName())){           		
            		sectionToBeSaved.setProjectModelId(currentReportModel.getId());
            		sectionToBeSaved.setReportModelName(currentReportModel.getName());
            		sectionToBeSaved.setParentSectionModelName(null);
            	}else if(sectionToBeSaved != null){
            		ProjectReportModelSectionDTO parentSection = reportSectionsComboStore.findModel("name", sectionToBeSaved.getParentSectionModelName());
            		Log.debug("parentSection " + parentSection);
            		if(parentSection != null){
            			sectionToBeSaved.setParentSectionModelName(parentSection.getName());
                		sectionToBeSaved.setParentSectionModelId(parentSection.getId());
            		} 
            	}
            	Boolean alreadyIn = false;
            	for(ProjectReportModelSectionDTO sectionI : sectionsToBeSaved){
            		if(sectionI.getRow().equals(sectionToBeSaved.getRow())){
            			sectionsToBeSaved.remove(sectionI);
            			sectionsToBeSaved.add(sectionToBeSaved);
            			alreadyIn = true;
            		}
            	}
            	if(!alreadyIn){
            		sectionsToBeSaved.add(sectionToBeSaved);
            	}
            	Log.debug("Section Id : " + sectionToBeSaved.getId());
    			Log.debug("Name : " + sectionToBeSaved.getName());
    			Log.debug("Index : " + sectionToBeSaved.getIndex());
    			Log.debug("Section nbText : " + sectionToBeSaved.getNumberOfTextarea());
    			Log.debug("Section parent : " + sectionToBeSaved.getParentSectionModelId());
    			Log.debug("Section parent name : " + sectionToBeSaved.getParentSectionModelName());
    			Log.debug("Section report model: " + sectionToBeSaved.getProjectModelId());
            	
            }

        });
		sectionsGrid.hide();
		sectionsGrid.enable();		
		sectionsGrid.getView().setForceFit(true);
		return sectionsGrid;
	}
	
	private Grid<ReportModelDTO> buildModelsListGrid(){		
		
		modelsStore = new ListStore<ReportModelDTO>();
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		
        
        
        ColumnConfig column = new ColumnConfig("name",I18N.CONSTANTS.adminReportName(), 280);  
		column.setRenderer(new GridCellRenderer<ReportModelDTO>(){

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
						if(model.getSectionsDTO() != null){
							for(ProjectReportModelSectionDTO sectionDTO : model.getSectionsDTO()){
								sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
								recursiveFillSectionsList(sectionDTO);
							}
						}							
						reportSectionsStore.commitChanges();
						fillComboSections();
						addReportSectionButton.enable();
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
								listener.setIsReport(true);// the model is a project report
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
		
		final TextField<String> reportName = new TextField<String>();
		reportName.setFieldLabel(I18N.CONSTANTS.adminReportName());
		toolbar.add(reportName);
    	
		Button addReportButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
        addReportButton.setItemId(UIActions.add);
		addReportButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				if(reportName.getValue() !=  null && modelsStore.findModel("name", reportName.getValue()) == null){
										
					newReportProperties.put(AdminUtil.PROP_REPORT_MODEL_NAME, reportName.getValue());
					dispatcher.execute(new CreateEntity("ProjectReportModel", newReportProperties), null, new AsyncCallback<CreateResult>(){
			             public void onFailure(Throwable caught) {
			            	 MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
			              			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminReportModelStandard()
			 								+ " '" + reportName.getValue() + "'"), null);
			             }

						@Override
						public void onSuccess(CreateResult result) {
							if(result != null){
								modelsStore.add((ReportModelDTO) result.getEntity());
								modelsStore.commitChanges();
								Notification.show(I18N.CONSTANTS.adminReportModelCreationBox(), 
										I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminReportModelStandard()
												+ " '" + result.getEntity().get("name")+"'"));
							}					
							else{
								MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
				              			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminReportModelStandard()
				 								+ " '" + reportName.getValue() + "'"), null);
							}		
						}
			         });
				}else{
					MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(),I18N.CONSTANTS.adminStandardInvalidValues(),null);
				}
			}			
		});
		Button deleteReportButton = new Button(I18N.CONSTANTS.delete(), IconImageBundle.ICONS.delete());
		deleteReportButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				dispatcher.execute(new Delete(reportModelsGrid.getSelectionModel().getSelectedItem()), null, new Deleted() {
                    @Override
                    public void deleted() {
                        modelsStore.remove(reportModelsGrid.getSelectionModel().getSelectedItem());
                    }
                });
			}
			
		});		

		Button buttonImport = new Button(I18N.CONSTANTS.importItem());
		buttonImport.setItemId(UIActions.importModel);
		buttonImport.setEnabled(true);
		buttonImport.addListener(Events.OnClick, new Listener<ButtonEvent>() {
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
		
		addReportSectionButton = new Button(I18N.CONSTANTS.addItem(), IconImageBundle.ICONS.add());
		addReportSectionButton.disable();
		addReportSectionButton.setItemId(UIActions.add);
		addReportSectionButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				
				ProjectReportModelSectionDTO section = new ProjectReportModelSectionDTO();
				
				section.setIndex(0);
				section.setName(I18N.CONSTANTS.adminEditGrid());
				section.setNumberOfTextarea(0);
				section.setParentSectionModelName("");
				section.setProjectModelId(0);
				if(!reportSectionsStore.contains(section))
					reportSectionsStore.add(section);
				
			}
			
		});
		toolbar.add(addReportSectionButton);
		
		saveReportSectionButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		saveReportSectionButton.setItemId(UIActions.save);
		saveReportSectionButton.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				if(sectionsToBeSaved.size() > 0){
					String sections = "";
					for(ProjectReportModelSectionDTO sectionToBeSaved : sectionsToBeSaved){
						sections += sectionToBeSaved.getName() + ", ";
					}
					
					final String name = sections;
					Map<String, Object> newReportModelSectionProperties = new HashMap<String, Object>();
					newReportModelSectionProperties.put(AdminUtil.PROP_REPORT_MODEL_NAME, currentReportModel.getName());
					newReportModelSectionProperties.put(AdminUtil.PROP_REPORT_SECTION_MODEL, sectionsToBeSaved);
					dispatcher.execute(new CreateEntity("ProjectReportModel", newReportModelSectionProperties), null, new AsyncCallback<CreateResult>(){

				       	 public void onFailure(Throwable caught) {
				       		 	MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
				          			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminReportModelStandard()
												+ " '" + currentReportModel.getName() + "'"), null);
				       		 	sectionsToBeSaved.clear();
				             }

							@Override
							public void onSuccess(CreateResult result) {
								if(result != null && result.getEntity() != null){										
									reportSectionsStore.removeAll();
									for(ProjectReportModelSectionDTO sectionDTO : ((ReportModelDTO)result.getEntity()).getSectionsDTO()){
										sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());										
										recursiveFillSectionsList(sectionDTO);										
									}
									reportSectionsStore.commitChanges();
									fillComboSections();
									modelsStore.remove(currentReportModel);
									modelsStore.add(((ReportModelDTO)result.getEntity()));
									modelsStore.commitChanges();
									Notification.show(I18N.CONSTANTS.adminReportModelCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminReportModelStandard()
											+ " '" + currentReportModel.getName() +"'"));					
								}					
								else{
									MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
						          			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminReportModelStandard()
														+ " '" + currentReportModel.getName() + "'"), null);
								}	
								sectionsToBeSaved.clear();
							}
				        });
										
				}			
			}			
		});
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
	
	private void recursiveFillSectionsList(ProjectReportModelSectionDTO rootSection) {
		reportSectionsStore.add(rootSection);
        for (final ProjectReportModelSectionDTO child : rootSection.getSubSectionsDTO()) {
        	child.setParentSectionModelName(rootSection.getName());
            recursiveFillSectionsList(child);
        }
    }
	
	private void fillComboSections(){
		
		reportSectionsComboStore.removeAll();
		ProjectReportModelSectionDTO dummyRootSection = new ProjectReportModelSectionDTO();		
		dummyRootSection.setIndex(0);
		dummyRootSection.setName(I18N.CONSTANTS.adminReportSectionRoot());
		dummyRootSection.setNumberOfTextarea(0);
		dummyRootSection.setParentSectionModelName("");
		dummyRootSection.setProjectModelId(0);
		reportSectionsComboStore.add(dummyRootSection);
		reportSectionsComboStore.add(reportSectionsStore.getModels());
		reportSectionsComboStore.commitChanges();
	}

	@Override
	public MaskingAsyncMonitor getReportModelsLoadingMonitor() {
		return new MaskingAsyncMonitor(reportModelsGrid, I18N.CONSTANTS.loading());
	}
}
