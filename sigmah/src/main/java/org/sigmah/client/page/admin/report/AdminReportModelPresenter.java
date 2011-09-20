package org.sigmah.client.page.admin.report;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetReportModels;
import org.sigmah.shared.command.UpdateProjectReportModel;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ProjectReportModelResult;
import org.sigmah.shared.command.result.ReportModelsListResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminReportModelPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO projectModel;

	
	public static abstract class View extends ContentPanel {
		public abstract ListStore<ReportModelDTO> getReportModelsStore();
		public abstract Component getMainPanel();
		public abstract MaskingAsyncMonitor getReportModelsLoadingMonitor();
		public abstract Button getAddReportButton();
		public abstract ListStore<ReportModelDTO>getModelsStore();
		public abstract TextField<String> getReportName();
		public abstract Button getSaveReportSectionButton();
		public abstract EditorGrid<ProjectReportModelSectionDTO> getSectionsGrid();
		public abstract ReportModelDTO getCurrentReportModel();
		public abstract ListStore<ProjectReportModelSectionDTO> getReportSectionsStore();
		public abstract void setCurrentReportModel(ReportModelDTO model);
		public abstract ListStore<ProjectReportModelSectionDTO> getReportSectionsComboStore();	
		public abstract Button getAddReportSectionButton();
		public abstract List<ProjectReportModelSectionDTO> getSectionsToBeSaved();
		public abstract Grid<ReportModelDTO> getReportModelsGrid();
		public abstract ComboBox<ProjectReportModelSectionDTO> getParentSectionsCombo();


	}
	
	public AdminReportModelPresenter(Dispatcher dispatcher){
		this.view = new AdminReportModelView(dispatcher);
		this.dispatcher = dispatcher;       
        
        //Add all listeners or click-handlers
		addListeners();
	}

	@Override
	public Component getView() {
		
		dispatcher.execute(new GetReportModels(), 
        		null,
        		new AsyncCallback<ReportModelsListResult>() {

				@Override
	            public void onFailure(Throwable arg0) {
	                AdminUtil.alertPbmData(alert);
	            }
	
	            @Override
	            public void onSuccess(ReportModelsListResult result) {
	            	view.getReportModelsStore().removeAll();
	            	view.getReportModelsStore().clearFilters();
	                if (result.getList() != null && !result.getList().isEmpty()) {
	                	view.getReportModelsStore().add(result.getList());
		                view.getReportModelsStore().commitChanges();
	                }
	                
	            }		
		});
		
		return view.getMainPanel();
	}

	
	
	/**
	 * Add all listeners or click-handlers for view
	 * 
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void addListeners() {
			
		
	//-------------------Add report model button ------------------------------------------------------
		
	
		 view.getAddReportButton().setItemId(UIActions.add);
		 view.getAddReportButton().addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				
				//Begins to create a new report model
				
				//If input is valid			
				if(view.getReportName().getValue() !=  null && view.getModelsStore().findModel("name", view.getReportName().getValue()) == null){
						
					HashMap<String, Object> newReportProperties = new HashMap<String, Object>();
					//Store the report's name in properties variable
					newReportProperties.put(AdminUtil.PROP_REPORT_MODEL_NAME, view.getReportName().getValue());
					newReportProperties.put(AdminUtil.PROP_REPORT_SECTION_MODEL, null);
					
					//RPC 
					dispatcher.execute(new CreateEntity("ProjectReportModel", newReportProperties), null, new AsyncCallback<CreateResult>(){
						
						
						@Override
			             public void onFailure(Throwable caught) {
							
							//RPC failed
			            	 MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
			              			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminReportModelStandard()
			 								+ " '" + view.getReportName().getValue() + "'"), null);
			            	 
			            	 view.getSectionsToBeSaved().clear();
			            	 
			             }

						@Override
						public void onSuccess(CreateResult result) {
							
							//RPC successfull
							
							if(result != null){
								
								//Refresh the report model grid
								view.getModelsStore().add((ReportModelDTO) result.getEntity());
								view.getModelsStore().commitChanges();
								Notification.show(I18N.CONSTANTS.adminReportModelCreationBox(), 
										I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminReportModelStandard()
												+ " '" + result.getEntity().get("name")+"'"));
							    
								//Focus the new created model cell in the grid		
								
								int rowIndex = view.getModelsStore().indexOf((ReportModelDTO) result.getEntity());								
								Element addedRow =view.getReportModelsGrid().getView().getRow(rowIndex);							
								addedRow.scrollIntoView();	
								addedRow.focus();								
							
							    
								
							}					
							else{
								MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
				              			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminReportModelStandard()
				 								+ " '" + view.getReportName().getValue() + "'"), null);
							}	
							
							view.getSectionsToBeSaved().clear();
						}
			         });
					
					
				}else{			
					//Invalid input
					MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(),I18N.CONSTANTS.adminStandardInvalidValues(),null);
					view.getSectionsToBeSaved().clear();
				}
			}			
		});
		
   //--------------------Save report sections button---------------------------------------------------
	
		 view.getSaveReportSectionButton().setItemId(UIActions.save);
		 view.getSaveReportSectionButton().addListener(Events.OnClick, new Listener<ButtonEvent>(){

				@Override
				public void handleEvent(ButtonEvent be) {
				
		                //Map to store the changes
						Map<String, Object> changes = new HashMap<String, Object>();					
						changes.put(AdminUtil.PROP_REPORT_SECTION_MODEL, view.getSectionsToBeSaved());
						
						//Create a update command with the report model DTO
						UpdateProjectReportModel updateCommand = new UpdateProjectReportModel(view.getCurrentReportModel().getId(),changes);
						
						//RPC to save changes
						dispatcher.execute(updateCommand, new MaskingAsyncMonitor(view.getSectionsGrid(), I18N.CONSTANTS.saving()), new AsyncCallback<ProjectReportModelResult>(){

							@Override
							public void onFailure(Throwable caught) {
								
								//RPC failed
					       		 
				       		 	MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
				          			I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminReportModelStandard()
												+ " '" + view.getCurrentReportModel().getName() + "'"), null);
				       		 	
				       		 	//Clear
				       		 	view.getSectionsToBeSaved().clear();
				       		   //Disable the save button 
								view.getSaveReportSectionButton().disable();
								
								//Refresh the section panel
							    view.getReportSectionsStore().removeAll();
								refreshReportModelSectionsPanel(view.getCurrentReportModel());
							}

							@Override
							public void onSuccess(
									ProjectReportModelResult result) {
								
								
								//RPC Successful
								view.getReportSectionsStore().removeAll();
								
								if(result != null && result.getReportModelDTO() != null){																											
									
								//Refresh the section grid.
									refreshReportModelSectionsPanel(result.getReportModelDTO());
									
								//Update the combobox models
									fillComboSections();
									
								//Update the modelStore
									ReportModelDTO reportModelDTO = result.getReportModelDTO();																		
									view.getModelsStore().remove(view.getCurrentReportModel());
									view.getModelsStore().add(reportModelDTO);
									view.getModelsStore().commitChanges();
									
									//Reset the current report model
									view.setCurrentReportModel(reportModelDTO);

									
									Notification.show(I18N.CONSTANTS.adminReportModelCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccess(I18N.CONSTANTS.adminReportModelStandard()
											+ " '" + view.getCurrentReportModel().getName() +"'"));					
								}					
								else{
									MessageBox.alert(I18N.CONSTANTS.adminReportModelCreationBox(), 
						          			I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminReportModelStandard()
														+ " '" + view.getCurrentReportModel().getName() + "'"), null);

								}	
								
								//Clear this list anyway
								view.getSectionsToBeSaved().clear();
								
								//Disable the save button 
								view.getSaveReportSectionButton().disable();
										
							}

					
							
						});
						
			
											
							
				}			
			});
			
			
			
    //------------------------------------Report sections grid edit listener----------------------------------------------
			
			view.getSectionsGrid().addListener(Events.AfterEdit, new Listener<GridEvent<ProjectReportModelSectionDTO>>() {

	            @Override
	            public void handleEvent(GridEvent<ProjectReportModelSectionDTO> be) {
	            	
	            	//Get the section being edited
	            	ProjectReportModelSectionDTO sectionToBeSaved = be.getModel();
  		           	
	            
	            	
	            	if(sectionToBeSaved!=null)
	            	{
	            		Log.debug("Update a section model");
	            	
	            		//Set the row index to record the position
	            		sectionToBeSaved.setRow(be.getRowIndex());
	            		
	            		//When users are editing other fields,not parent selection name field for a new section
	            		if(sectionToBeSaved.getParentSectionModelName().equals(""))
	            		{
	            			//Do nothing
	            		}       
	            		//When users have edited the parent selections fields and select the root section's name.
	            		else if(sectionToBeSaved.getParentSectionModelName().equals(I18N.CONSTANTS.adminReportSectionRoot()))
	            		{
	            			//Root section
	            			sectionToBeSaved.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
	            			//Set the report model id
	            			sectionToBeSaved.setProjectModelId(view.getCurrentReportModel().getId());
	            			//Set null to parent section
	                		sectionToBeSaved.setParentSectionModelId(null);

	            		}
	            		//When users have edited the parent selections fields and select non-root section's name
	            		else if(view.getParentSectionsCombo().getSelection()!=null && view.getParentSectionsCombo().getSelection().size()>0)
	            		{
	            			Log.debug("The size of selection is :"+view.getParentSectionsCombo().getSelection().size());
	            			
	            			//Non-root section
	            			
	            			//Get the parent section DTO object
	            				            			
	            			ProjectReportModelSectionDTO parentSection = view.getParentSectionsCombo().getSelection().get(0);
	            			
	            			Log.debug("You choose the section of parent : "+parentSection.getId()+" name : "+parentSection.getName()+" CopositeName : "+parentSection.getCompositeName());
	            			
     				        //Set null to report model ComboBox<ProjectReportModelSectionDTO> parentSectionsCombo
	            			sectionToBeSaved.setProjectModelId(null);
	            			//Set parent section id
	                		sectionToBeSaved.setParentSectionModelId(parentSection.getId());
	            			
	            		}
	            	
	            	           			         		
	            	}
	            	        	
	            	
	            	
	            boolean alreadyIn=false;	            
	             
	            //Check if it is already in the list
	            alreadyIn = isAlreadyIn(sectionToBeSaved);
	            	
	            if(!alreadyIn)
	            {
	              view.getSectionsToBeSaved().add(sectionToBeSaved);
	            		
	            }	            	
	            	//enable the save button
	            view.getSaveReportSectionButton().enable();
	            	
	            	
	            }
	        });
			

	}
	
	
	/**
	 * 
	 * Check if the section editing is already recored in the list.
	 * 
	 * If it already exists, update it and return true. Or return false.
	 * 
	 * @param sectionToBeSaved
	 * 
	 * @return
	 */
	private boolean isAlreadyIn(ProjectReportModelSectionDTO sectionToBeSaved) 
	{
		if(view.getSectionsToBeSaved().size()>0)
    	{
		
		 try{
    	     for(ProjectReportModelSectionDTO sectionI : view.getSectionsToBeSaved())  //Raise ConcurrentModificationException sometimes
    	      { 
    		     if(sectionI.getRow().equals(sectionToBeSaved.getRow()))
    		     {
    			  view.getSectionsToBeSaved().remove(sectionI);
    			  view.getSectionsToBeSaved().add(sectionToBeSaved);
    			  return true;
    		     }
    	      }
		   }catch (ConcurrentModificationException e)
		 {
			Log.debug(" Catche a ConcurrentModificationException, recall the method isAlreadyIn ! ");
			return isAlreadyIn(sectionToBeSaved);
		 }
		 
    }
		
	return false;
	}

	@Override
	public void discardView() {
	}

	@Override
	public void viewDidAppear() {
	}

	@Override
	public void setCurrentState(AdminPageState currentState) {
	}

	@Override
	public void setModel(Object model) {
		projectModel = (ProjectModelDTO)model;
	}

	@Override
	public Object getModel() {
		return projectModel;
	}
	
	
private void fillComboSections(){
		
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
	
private void recursiveFillSectionsList(ProjectReportModelSectionDTO rootSection) {
	
	view.getReportSectionsStore().add(rootSection);	
	
	if(rootSection.getSubSectionsDTO()==null)
		return;
	
	for (final ProjectReportModelSectionDTO child : setCompositeNames(rootSection
			.getSubSectionsDTO())) {
		child.setParentSectionModelName(rootSection.getCompositeName());
		recursiveFillSectionsList(child);
		
	}
}
	
	
	public static void refreshReportModelsPanel(Dispatcher dispatcher, final View view){
		dispatcher.execute(new GetReportModels(), 
				view.getReportModelsLoadingMonitor(),
        		new AsyncCallback<ReportModelsListResult>() {
        	@Override
            public void onFailure(Throwable arg0) {
        		AdminUtil.alertPbmData(alert);
            }

            @Override
            public void onSuccess(ReportModelsListResult result) {
            	if (result.getList() != null && !result.getList().isEmpty()) {
            		view.getReportModelsStore().removeAll();
            		view.getReportModelsStore().add(result.getList());
                	view.getReportModelsStore().commitChanges();
            	}
            	
            }
        });
	}
	
  private void refreshReportModelSectionsPanel(ReportModelDTO reportModel)
  {
	  //Load all sections into the grid
		if(reportModel.getSectionsDTO() != null && reportModel.getSectionsDTO().size()>0)
		{
          for(ProjectReportModelSectionDTO sectionDTO : setCompositeNames(reportModel.getSectionsDTO()))
          {
                  sectionDTO.setParentSectionModelName(I18N.CONSTANTS.adminReportSectionRoot());
                  recursiveFillSectionsList(sectionDTO);
          }
      } 
		
	//Update the section store
		view.getReportSectionsStore().commitChanges();	
  }
  
  
  

	/**
	 * Set the composite name for each sectionDTO to display in comboBox
	 * 
	 * @author HUZHE (zhe.hu32@gmail.com)
	 * 
	 */
	public static List<ProjectReportModelSectionDTO> setCompositeNames(List<ProjectReportModelSectionDTO> sections)
	{
		if(sections!=null && sections.size()>0)
		{
			List<ProjectReportModelSectionDTO>sectionsReturn = new ArrayList<ProjectReportModelSectionDTO>();
			
			for(ProjectReportModelSectionDTO s:sections)
			{
				//name(id)
				s.setCompositeName(s.getName()+"<i>("+s.getId()+")</i>");
				sectionsReturn.add(s);
			}
			
			return sectionsReturn;
		}
		else if(sections.size()==0)
		{
			return sections;
		}
			
		else
		{
			return null;
		}
		
	}
	
  
}
