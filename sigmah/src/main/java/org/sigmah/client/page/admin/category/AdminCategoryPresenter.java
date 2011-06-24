package org.sigmah.client.page.admin.category;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminPageState;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.AdminModelSubPresenter;
import org.sigmah.client.page.admin.model.common.element.ElementTypeEnum;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.DeleteCategories;
import org.sigmah.shared.command.GetCategories;
import org.sigmah.shared.command.GetProjectModels;
import org.sigmah.shared.command.result.CategoriesListResult;
import org.sigmah.shared.command.result.ProjectModelListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.category.CategoryElementDTO;
import org.sigmah.shared.dto.category.CategoryTypeDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.QuestionElementDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminCategoryPresenter implements AdminModelSubPresenter {
	
	private static boolean alert = false;

	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO projectModel;
	
	public static abstract class View extends ContentPanel {
		public abstract ListStore<CategoryTypeDTO> getCategoriesStore();
		public abstract ListStore<CategoryElementDTO>getCategoryElementsStore();
		public abstract Component getMainPanel();
		public abstract MaskingAsyncMonitor getReportModelsLoadingMonitor();
		public abstract Button getDeleteCategoryElementButton();
		public abstract Button getDeleteCategoryTypeButton();
		public abstract Grid<CategoryElementDTO> getCategoryElementsGrid();
		public abstract Grid<CategoryTypeDTO> getCategoriesGrid();
		public abstract CategoryTypeDTO getCurrentCategoryType();
	}
	
	public AdminCategoryPresenter(Dispatcher dispatcher){
		this.view = new AdminCategoryView(dispatcher);
		this.dispatcher = dispatcher;
		//Add listeners
		addDeletionListeners();	
						
	}

	@Override
	public Component getView() {
								
		dispatcher.execute(new GetCategories(), 
        		null,
        		new AsyncCallback<CategoriesListResult>() {

				@Override
	            public void onFailure(Throwable arg0) {
	                AdminUtil.alertPbmData(alert);
	            }
	
	            @Override
	            public void onSuccess(CategoriesListResult result) {
	            	view.getCategoriesStore().removeAll();
	            	view.getCategoriesStore().clearFilters();
	                if (result.getList() !=null && !result.getList().isEmpty()) {
	                	view.getCategoriesStore().add(result.getList());
		                view.getCategoriesStore().commitChanges();
	                }
	                
	            }		
		});
		
						
		
		return view.getMainPanel();
	}

	
	
	/**
	 * Method to add listeners for deletion buttons into View
	 * 
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void addDeletionListeners() {
		
		//Delete category element button
		view.getDeleteCategoryElementButton().addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				onDeleteCategoryElement(view.getCategoryElementsGrid().getSelectionModel().getSelectedItems());	            
			}
			
		});
		
		//Delete category button
		view.getDeleteCategoryTypeButton().addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				onDeleteCategory(view.getCategoriesGrid().getSelectionModel().getSelectedItems());                
			}
			
		});
		
	}

	
	/**
	 * Method to delete a category.
	 * 
	 *Firstly,get all project models for verifying if there is one or more in the selected categories 
	 *
	 *who are being used by one or more project models. If being used, a category should not be deleted.
	 *
	 * @param selectedItems
	 *          The selected categories
	 *          
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	protected void onDeleteCategory(final List<CategoryTypeDTO> selectedItems) {
		
		
		//Check if there is at least one item selected
		if(selectedItems==null || selectedItems.size()==0)
		{
			MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.selectCategoryToDelete(), null);
			return;
		}
						
		GetProjectModels cmdGetProjectModels = new GetProjectModels();		
		cmdGetProjectModels.allProjectModelStatus();
		cmdGetProjectModels.setFullVersion(true);
		
		dispatcher.execute(cmdGetProjectModels, 				
				new MaskingAsyncMonitor(view.getMainPanel(), I18N.CONSTANTS.verfyingAndDeleting()),
        		new AsyncCallback<ProjectModelListResult>() {
        	@Override
            public void onFailure(Throwable arg0) {
        		
        		//RPC failed
        		 MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.deleteCategoryGetProjectModelsError(), null);       		
        		
            }

            @Override
            public void onSuccess(ProjectModelListResult result) {
            	
            	//RPC succeeds,begin to verify and delete
            	deleteCategoryVerify(selectedItems,result.getFullVersionModelList());
            	
            }
        });
				
								
}
	
	
	/**
	 * Method to verify the deletion action. 
	 * 
	 * If the verification passes, try to delete a category, or show a alert window.
	 *
	 * @param selectedItems
	 *         The selected categories
	 *         
	 * @param allProjectModelsList
	 *         The list of all project models 
	 *         
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void deleteCategoryVerify(final List<CategoryTypeDTO> selectedItems,List<ProjectModelDTO>allProjectModelsList)
	{

		//A List to store DeletionError object
		List<DeletionError> deletionErrorList = new ArrayList<DeletionError>();
		
		//Check
		for(ProjectModelDTO projectModelDTO:allProjectModelsList)
        {
        	List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
        	allElements = projectModelDTO.getAllElements();
        	      	       	
        	for(FlexibleElementDTO e: allElements)
        	{
        		if(e.getElementType()==ElementTypeEnum.QUESTION)
        		{
        			QuestionElementDTO questionElement = (QuestionElementDTO) e;
        			
        			if(questionElement.getCategoryTypeDTO()!=null && selectedItems.contains(questionElement.getCategoryTypeDTO()))
        			{
        				deletionErrorList.add(new DeletionError(questionElement.getCategoryTypeDTO().getLabel(),projectModelDTO.getName(),questionElement.getLabel()));
        			}
        		
        		}
        	}
        	
        }
        
                
		//If the category is used by project models,show an alert window
    	if(deletionErrorList.size()>0)
    		{
    		
    		//Create a dialog window to show error message	
    		final Dialog errorDialog = new Dialog();  
		    errorDialog.setHeading(I18N.CONSTANTS.deletionError());  
		    errorDialog.setButtons(Dialog.CANCEL); 
		    errorDialog.setScrollMode(Scroll.AUTO);  
		    errorDialog.setHideOnButtonClick(true); 
		    errorDialog.setModal(true);
		    errorDialog.setWidth(500);
		    errorDialog.setHeight(250);
		   
		    String errorText="";
		    for(DeletionError error:deletionErrorList)
		    {
		    	errorText=errorText+I18N.MESSAGES.categoryBeingUsed(error.getCategoryTypeName(), error.getProjectModelName(), error.getFieldName())+"<br />";
		    	
		    }
			errorDialog.addText(errorText);
			errorDialog.show();				
    			  			 			 
    		}
    		
    	
    	//Else, try to delete
    	else
    		{
    		
    		List<Integer> ids = new ArrayList<Integer>();
    		String names = "";
    		for(CategoryTypeDTO s : selectedItems){
    			ids.add(s.getId());
    			names = s.getLabel() + ", " + names;
    		}
    		
    		final String toDelete = names;
    		final DeleteCategories deactivate = new DeleteCategories(selectedItems, null);
            dispatcher.execute(deactivate, null, new AsyncCallback<VoidResult>() {

                @Override
                public void onFailure(Throwable caught) {
                    MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
                }

                @Override
                public void onSuccess(VoidResult result) {
                	for(CategoryTypeDTO model : selectedItems){               		
                		view.getCategoriesStore().remove(model);
                		view.getCategoryElementsStore().removeAll();
                		view.getCategoriesStore().commitChanges();
                	}   
                	view.getCategoriesStore().commitChanges();
                	Notification.show(I18N.CONSTANTS.adminCategoryTypeCreationBox(), I18N.MESSAGES.adminStandardUpdateSuccessF(I18N.CONSTANTS.adminCategoryTypeStandard()
    						+ " '" + view.getCurrentCategoryType().getLabel() +"'"));	
                }
            });
            
    	}
	
	}

	
	
	/**
	 * Method to delete a element under a category
	 * 
	 * Firstly,get all project models for verifying if the selected category elements are being used
	 *
	 * by one or more project models. If being used, a category element should not be deleted.
	 * 
	 * @param selectedItems
	 *        The selected categories
	 *        
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	private void onDeleteCategoryElement(final List<CategoryElementDTO> selectedItems) {
				
		//Check if there is at least one item selected
		if(selectedItems==null || selectedItems.size()==0)
		{
			MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.selectCategoryElementToDelete(), null);
			return;
		}
		
		GetProjectModels cmdGetProjectModels = new GetProjectModels();		
		cmdGetProjectModels.allProjectModelStatus();
		cmdGetProjectModels.setFullVersion(true);
		
		dispatcher.execute(cmdGetProjectModels, 				
				new MaskingAsyncMonitor(view.getMainPanel(), I18N.CONSTANTS.verfyingAndDeleting()),
        		new AsyncCallback<ProjectModelListResult>() {
        	@Override
            public void onFailure(Throwable arg0) {
        		
        		//RPC failed
        		 MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.deleteCategoryGetProjectModelsError(), null);       		
        		
            }

            @Override
            public void onSuccess(ProjectModelListResult result) {
            	
            	//RPC succeeds,begin to verify and delete
            	deleteCategoryElementVerify(selectedItems,result.getFullVersionModelList());
            	
            }
        });
								
	}

	/**
	 * Method to verify the deletion action. 
	 * 
	 * If the verification passes, try to delete a category element, or show a alert window.
	 * 
	 * @param selectedItems
	 *        The selected category elements
	 *       
	 * @param allProjectModelsList
	 *        The list of all project models
	 *        
	 * @author HUZHE (zhe.hu32@gmail.com)
	 */
	protected void deleteCategoryElementVerify(final List<CategoryElementDTO> selectedItems,List<ProjectModelDTO> allProjectModelsList) {
		
				    
		//Get the parent CategoryTypeDTO object, they have the same parent CategoryTypeDTO object
		CategoryTypeDTO parentCategoryTypeDTO = selectedItems.get(0).getParentCategoryDTO();
					
		//A List to store DeletionError object
		List<DeletionError> deletionErrorList = new ArrayList<DeletionError>();
		
		
		//Check
		for(ProjectModelDTO projectModelDTO:allProjectModelsList)
        {
        	List<FlexibleElementDTO> allElements = new ArrayList<FlexibleElementDTO>();
        	allElements = projectModelDTO.getAllElements();
        	
        	
        	for(FlexibleElementDTO e: allElements)
        	{
        		if(e.getElementType()==ElementTypeEnum.QUESTION)
        		{
        			QuestionElementDTO questionElement = (QuestionElementDTO) e;
        			       			      			       			
       			    if(questionElement.getCategoryTypeDTO()!=null && parentCategoryTypeDTO.getId()==questionElement.getCategoryTypeDTO().getId())
        			{
       			    	//Add a deletion error object
        				deletionErrorList.add(new DeletionError(parentCategoryTypeDTO.getLabel(),projectModelDTO.getName(),questionElement.getLabel()));
        			}
        		
        		}
        	}
        	
        }
        
		//If the category is used by project models,show an alert window
		if(deletionErrorList.size()>0)
		{
		    //Create a dialog window to show error message
			final Dialog errorDialog = new Dialog();  
		    errorDialog.setHeading(I18N.CONSTANTS.deletionError());  
		    errorDialog.setButtons(Dialog.CANCEL); 
		    errorDialog.setScrollMode(Scroll.AUTO);  
		    errorDialog.setHideOnButtonClick(true);  
		    errorDialog.setModal(true);
		    errorDialog.setHeight(250);
		    errorDialog.setWidth(500);
		    

		   
		    String errorText="";
		    for(DeletionError error:deletionErrorList)
		    {
		    	errorText=errorText+I18N.MESSAGES.categoryBeingUsed(error.getCategoryTypeName(), error.getProjectModelName(), error.getFieldName())+"<br />";
		    	
		    }
			errorDialog.addText(errorText);
			errorDialog.show();	
			return;
		}
		
		//Else, try to delete
		else
		{
			List<Integer> ids = new ArrayList<Integer>();
			String names = "";
			for(CategoryElementDTO s : selectedItems){
				ids.add(s.getId());
				names = s.getLabel() + ", " + names;
			}
			
			final String toDelete = names;
			final DeleteCategories deactivate = new DeleteCategories(null, selectedItems);
	        dispatcher.execute(deactivate, null, new AsyncCallback<VoidResult>() {

	            @Override
	            public void onFailure(Throwable caught) {
	                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
	            }

	            @Override
	            public void onSuccess(VoidResult result) {
	            	List<CategoryElementDTO> elements = view.getCurrentCategoryType().getCategoryElementsDTO();
	            	for(CategoryElementDTO model : selectedItems){
	            		view.getCategoryElementsStore().remove(model);
	            		elements.remove((CategoryElementDTO) model);
	            	}   
	            	view.getCategoryElementsStore().commitChanges();
	            	
					view.getCurrentCategoryType().setCategoryElementsDTO(elements);
					view.getCategoriesStore().update(view.getCurrentCategoryType());
					view.getCategoriesStore().commitChanges();
	            }
	        });
		}
		
	}

	public static void refreshCategoryTypePanel(Dispatcher dispatcher, final View view){
		dispatcher.execute(new GetCategories(), 
				view.getReportModelsLoadingMonitor(),
        		new AsyncCallback<CategoriesListResult>() {
        	@Override
            public void onFailure(Throwable arg0) {
        		AdminUtil.alertPbmData(alert);
            }

            @Override
            public void onSuccess(CategoriesListResult result) {
            	if (result.getList() != null && !result.getList().isEmpty()) {
            		view.getCategoriesStore().removeAll();
            		view.getCategoriesStore().add(result.getList());
                	view.getCategoriesStore().commitChanges();
            	}
            	
            }
        });
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
}
