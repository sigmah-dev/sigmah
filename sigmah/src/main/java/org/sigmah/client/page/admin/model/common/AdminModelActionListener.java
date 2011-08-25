package org.sigmah.client.page.admin.model.common;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.category.AdminCategoryPresenter;
import org.sigmah.client.page.admin.model.orgunit.AdminOrgUnitModelsPresenter;
import org.sigmah.client.page.admin.model.orgunit.OrgUnitModelForm;
import org.sigmah.client.page.admin.model.project.AdminProjectModelsPresenter;
import org.sigmah.client.page.admin.report.AdminReportModelPresenter;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetOrgUnitModelCopy;
import org.sigmah.shared.command.GetProjectModelCopy;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.ProjectModelDTOLight;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ListBox;

public class AdminModelActionListener implements ActionListener {
	
	private final ContentPanel view;
	
	private final Dispatcher dispatcher;
	
	private final Boolean isProject;
	
	private Boolean isReport;
	
	private Boolean isOrgUnit;

	private int modelId;
		
	private ProjectModelDTOLight projectModel;
	
	private OrgUnitModelDTO orgUnit;
	
	
	
	/**
	 * @return the orgUnit
	 */
	public OrgUnitModelDTO getOrgUnit() {
		return orgUnit;
	}

	/**
	 * @param orgUnit the orgUnit to set
	 */
	public void setOrgUnit(OrgUnitModelDTO orgUnit) {
		this.orgUnit = orgUnit;
	}

	/**
	 * @return the projectModel
	 */
	public ProjectModelDTOLight getProjectModel() {
		return projectModel;
	}

	/**
	 * @param projectModel the projectModel to set
	 */
	public void setProjectModel(ProjectModelDTOLight projectModel) {
		this.projectModel = projectModel;
	}



	public Boolean getIsRepport() {
		return isReport;
	}

	public void setIsReport(Boolean isRepport) {
		this.isReport = isRepport;
	}
	
	public Boolean getIsOrgUnit() {
		return isOrgUnit;
	}

	public void setIsOrgUnit(Boolean isOrgUnit) {
		this.isOrgUnit = isOrgUnit;
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int projectModelId) {
		this.modelId = projectModelId;
	}
	
	public AdminModelActionListener(ContentPanel view, Dispatcher dispatcher, Boolean isProject){
		this.view = view;
		this.dispatcher = dispatcher;
		this.isProject = isProject;
	}
	
	@Override
	public void onUIAction(String actionId) {
		/*if (UIActions.delete.equals(actionId)) {
            view.confirmDeleteSelected(new ConfirmCallback() {
                public void confirmed() {
                    onDeleteConfirmed(view.getPrivacyGroupsSelection());
                }
            });
        }else*/  if (UIActions.add.equals(actionId)) {
            onAdd();
        }
        if(UIActions.exportModel.equals(actionId)){
        	onExportModel();
        }
        if(UIActions.importModel.equals(actionId)){
        	onImportModel();
        }
        if(UIActions.copyModel.equals(actionId)){
        	onCopyModel();
        }
        if(UIActions.deleteModel.equals(actionId)){
        	onDeleteModel();
        }
		
	}
	
	/**
	 * Method to try to delete a draft project model or a draft orgunit model
	 * 
	 * @author HUZHE(zhe.hu32@gmail.com)
	 */
	private void onDeleteModel() {
		
	  if(isProject)
	  {//Delete draft project model
		  
		//If the status of the project model is not "Draft", can not be deleted
		if(getProjectModel()==null || !ProjectModelStatus.DRAFT.equals(getProjectModel().getStatus()))
		{
			MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.CONSTANTS.deleteNotDraftProjectModelError(), null);
			return;
		}
		
		//Show confirm window		
		Listener<MessageBoxEvent> l=new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(
					MessageBoxEvent be) {
				if (Dialog.YES.equals(be
						.getButtonClicked()
						.getItemId())) {
					deleteProjectModel();
				}
			}
		};
		
		MessageBox deleteConfirmMsgBox = MessageBox.confirm(I18N.CONSTANTS.deleteConfirm(), I18N.CONSTANTS.deleteDraftProjectModelConfirm(), l);		
		deleteConfirmMsgBox.show();
		
	  }
	  else if(isOrgUnit)
	  {//Delete draft OrgUnit model
		  
		 
		  //Check the status of OrgUnit model to delete
		  if(getOrgUnit()==null || !ProjectModelStatus.DRAFT.equals(getOrgUnit().getStatus()))
		  {
			 
			  MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.CONSTANTS.deleteNotDraftOrgUnitModelError(), null);
			  return;
		  }
		  
		  //Show confirm window		
			Listener<MessageBoxEvent> l=new Listener<MessageBoxEvent>() {
				@Override
				public void handleEvent(
						MessageBoxEvent be) {
					if (Dialog.YES.equals(be
							.getButtonClicked()
							.getItemId())) {
						
						deleteOrgUnitModel();
					}
				}
			};
			
			MessageBox deleteConfirmMsgBox = MessageBox.confirm(I18N.CONSTANTS.deleteConfirm(), I18N.CONSTANTS.deleteDraftOrgUnitModelConfirm(), l);		
			deleteConfirmMsgBox.show();
		  
		  
	  }
		
	}

	/**
	 * RPC to execute the deletion of a project model
	 * 
	 * Note: Only "Draft" is allowed to delete
	 * 
	 * @author HUZHE(zhe.hu32@gmail.com)
	 */
	private void deleteProjectModel()
	{
		
		//RPC to try to delete the project model
		Delete deleteCommand = new Delete("ProjectModel", getModelId());
		deleteCommand.setProjectModelStatus(getProjectModel().getStatus());
		dispatcher.execute(deleteCommand, null, new AsyncCallback<VoidResult>(){

			@Override
			public void onFailure(Throwable caught) {
				
				MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(getProjectModel().getName()), null);
				
			}

			@Override
			public void onSuccess(VoidResult result) {
				
				//Remove the project locally
				((AdminProjectModelsPresenter.View) view).getAdminModelsStore().remove(getProjectModel());
				((AdminProjectModelsPresenter.View) view).getAdminModelsStore().commitChanges();

				// Show notification.
				Notification.show(I18N.CONSTANTS
						.adminProjectModelDelete(),
						I18N.CONSTANTS
								.adminProjectModelDeleteDetail());
			}
			
		});
	}
	
	/**
	 * RPC to execute the deletion of a OrgUnit model
	 * 
	 * Note: Only "Draft" is allowed to delete
	 * 
	 * @author HUZHE(zhe.hu32@gmail.com)	
	 */
	private void deleteOrgUnitModel()
	{
		
		Log.debug("DeleteOrgUnitModel: Id is "+getOrgUnit().getId());
		
		Delete deleteCommand = new Delete("OrgUnitModel", getOrgUnit().getId());
		deleteCommand.setProjectModelStatus(getOrgUnit().getStatus());
		
		dispatcher.execute(deleteCommand, null, new AsyncCallback<VoidResult>(){

			@Override
			public void onFailure(Throwable caught) {
				
				MessageBox.alert(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(getOrgUnit().getName()), null);
				
			}

			@Override
			public void onSuccess(VoidResult result) {
				
				//Remove the project locally
				((AdminOrgUnitModelsPresenter.View) view).getAdminModelsStore().remove(getOrgUnit());
				((AdminOrgUnitModelsPresenter.View) view).getAdminModelsStore().commitChanges();

				// Show notification.
				Notification.show(I18N.CONSTANTS
						.adminOrgUnitModelDelete(),
						I18N.CONSTANTS
								.adminOrgUnitModelDeleteDetail());
			}
			
		});
		
			}
	
	
	
	/*protected void onDeleteConfirmed(final List<PrivacyGroupDTO> selection) {
		
		List<Integer> ids = new ArrayList<Integer>();
		String names = "";
		for(PrivacyGroupDTO s : selection){
			ids.add(s.getId());
			names = s.getTitle() + ", " + names;
		}
		
		final String toDelete = names;
		final DeleteList delete = new DeleteList(PrivacyGroup.class, ids);
        dispatcher.execute(delete, null, new AsyncCallback<VoidResult>() {

            @Override
            public void onFailure(Throwable caught) {
                MessageBox.alert(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(toDelete), null);
            }

            @Override
            public void onSuccess(VoidResult result) {
            	for(PrivacyGroupDTO model : selection){
            		 view.getAdminPrivacyGroupsStore().remove(model);
            	}    
            }
        });
	}*/
	
	private void onAdd() {	

		if(isProject){
			int width = 700;
			int height = 550;
			String title = I18N.CONSTANTS.adminProjectModelCreationBox();
			final Window window = new Window();		
			window.setHeading(title);
	        window.setSize(width, height);
	        window.setPlain(true);
	        window.setModal(true);
	        window.setBlinkModal(true);
	        window.setLayout(new FitLayout());
			final ProjectModelForm form = new ProjectModelForm(dispatcher, new AsyncCallback<CreateResult>(){

				@Override
				public void onFailure(Throwable arg0) {
					window.hide();				
				}

				@Override
				public void onSuccess(CreateResult result) {
					window.hide();
					ProjectModelDTO pM = (ProjectModelDTO)result.getEntity();
					ProjectModelDTOLight pMLight = new ProjectModelDTOLight();
					pMLight.setName(pM.getName());
					pMLight.setId(pM.getId());
					pMLight.setStatus(ProjectModelStatus.DRAFT);
					pMLight.setVisibilities(pM.getVisibilities());
					((AdminProjectModelsPresenter.View)view).getAdminModelsStore().add(pMLight);
					((AdminProjectModelsPresenter.View)view).getAdminModelsStore().commitChanges();
					
					//Focus the new created model cell in the grid						
					int rowIndex =((AdminProjectModelsPresenter.View)view).getAdminModelsStore().indexOf(pMLight);	
					Element addedRow =((AdminProjectModelsPresenter.View)view).getProjectModelGrid().getView().getRow(rowIndex);							
					addedRow.scrollIntoView();	
					addedRow.focus();
				}			
			});
			window.add(form);
	        window.show();		
		}else{
			int width = 700;
			int height = 550;
			String title = I18N.CONSTANTS.adminOrgUnitsModelCreationBox();
			final Window window = new Window();		
			window.setHeading(title);
	        window.setSize(width, height);
	        window.setPlain(true);
	        window.setModal(true);
	        window.setBlinkModal(true);
	        window.setLayout(new FitLayout());
			final OrgUnitModelForm form = new OrgUnitModelForm(dispatcher, new AsyncCallback<CreateResult>(){

				@Override
				public void onFailure(Throwable arg0) {
					window.hide();				
				}

				@Override
				public void onSuccess(CreateResult result) {
					window.hide();
					OrgUnitModelDTO oM = (OrgUnitModelDTO)result.getEntity();
					((AdminOrgUnitModelsPresenter.View)view).getAdminModelsStore().add(oM);
					((AdminOrgUnitModelsPresenter.View)view).getAdminModelsStore().commitChanges();
					
					//Focus the new created model cell in the grid						
					int rowIndex =((AdminOrgUnitModelsPresenter.View)view).getAdminModelsStore().indexOf(oM);	
					Element addedRow =((AdminOrgUnitModelsPresenter.View)view).getOrgUnitModelGrid().getView().getRow(rowIndex);							
					addedRow.scrollIntoView();	
					addedRow.focus();
					
					
				}			
			});
			window.add(form);
	        window.show();
		}
	}
	
	/**
	 * Export project, org-unit, repport and category type models.
	 */
	private void onExportModel() {
		String parametres = "type=" + URL.encodeComponent("project-model")
				+ "&id=" + URL.encodeComponent(String.valueOf(getModelId()));

		if (!isProject) {
			if (isReport) {
				parametres = "type="
						+ URL.encodeComponent("project-report-model") + "&id="
						+ URL.encodeComponent(String.valueOf(getModelId()));
			} else {
				if (isOrgUnit) {
					parametres = "type="
							+ URL.encodeComponent("org-unit-model") + "&id="
							+ URL.encodeComponent(String.valueOf(getModelId()));
				} else {
					parametres = "type=" + URL.encodeComponent("category-type")
							+ "&id="
							+ URL.encodeComponent(String.valueOf(getModelId()));
				}
			}
		}
		com.google.gwt.user.client.Window.Location.replace(GWT
				.getModuleBaseURL()
				+ "models?" + parametres);
	}

	/**
	 * Import project, org-unit, report and category type model.
	 */
	private void onImportModel() {
		final Dialog importDialog = new Dialog();
		importDialog.setAutoHeight(true);
		importDialog.setPlain(true);
		importDialog.setModal(true);
		importDialog.setBlinkModal(true);

		if (isProject) {
			importDialog.setHeading(I18N.CONSTANTS.adminProjectModelImport());
		} else {
			if (isReport) {
				importDialog
						.setHeading(I18N.CONSTANTS.adminReportModelImport());
			} else {
				if (isOrgUnit) {
					importDialog.setHeading(I18N.CONSTANTS
							.adminOrgUnitsModelImport());
				} else {
					importDialog.setHeading(I18N.CONSTANTS
							.adminCategoryImport());
				}
			}
		}

		importDialog.setLayout(new FitLayout());
		importDialog.setButtons(Dialog.OKCANCEL);
		importDialog.setWidth(420);
		importDialog.setAutoHeight(true);
		
		final FormPanel importPanel = new FormPanel();
		importPanel.setBodyBorder(false);
		importPanel.setHeaderVisible(false);
		importPanel.setEncoding(Encoding.MULTIPART);
		importPanel.setMethod(Method.POST);
		importPanel.setAction(GWT.getModuleBaseURL() + "models");
		importPanel.setMethod(FormPanel.Method.POST);
		importPanel.setPadding(5);
		
		if(isProject){
			importPanel.setLabelWidth(120);
			importPanel.setFieldWidth(270);
		} else{			
			importPanel.setLabelWidth(120);
			importPanel.setFieldWidth(250);
		}
		
		importPanel.setAutoHeight(true);
		importPanel.setAutoWidth(true);

		final FileUploadField uploadField = new FileUploadField();
		uploadField.setAllowBlank(false);
		uploadField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		uploadField.setFieldLabel(I18N.CONSTANTS.adminFileImport());
		importPanel.add(uploadField);
		// Type Handler
		final TextField<String> typeHandler = new TextField<String>();
		String type = "project-model";
		if (!isProject) {
			if (isReport) {
				type = "project-report-model";
			} else {
				if (isOrgUnit) {
					type = "org-unit-model";
				} else {
					type = "category-type";
				}
			}
		}
		typeHandler.setValue(type);
		typeHandler.setName("type");
		typeHandler.setVisible(false);
		importPanel.add(typeHandler);
		//Add project model type choice
		if (isProject) {
			Label labelProjectModelType = new Label(I18N.CONSTANTS.adminProjectModelType());
			labelProjectModelType.setStyleAttribute("font-size", "12px");
			labelProjectModelType.setStyleAttribute("margin-right", "30px");
			
			ListBox projectModelTypeList = new ListBox();
			projectModelTypeList.setName("project-model-type");
			projectModelTypeList.setVisibleItemCount(1);
			projectModelTypeList.addItem(ProjectModelType.getName(ProjectModelType.NGO), "NGO");
			projectModelTypeList.addItem(ProjectModelType.getName(ProjectModelType.FUNDING), "FUNDING");
			projectModelTypeList.addItem(ProjectModelType.getName(ProjectModelType.LOCAL_PARTNER), "LOCAL_PARTNER");
			
			importPanel.add(labelProjectModelType);
			importPanel.add(projectModelTypeList);
		}

		importPanel.addListener(Events.Submit, new Listener<FormEvent>() {
			@Override
			public void handleEvent(FormEvent be) {
				refreshDataModelList();
				importDialog.hide();
				
				final String result = be.getResultHtml();
				
				// Import failed.
				if(result.indexOf("HTTP ERROR") != -1) {
				    MessageBox.alert(I18N.CONSTANTS.error(), 
				            I18N.CONSTANTS.adminProjectModelImportError(), null);
				}
				// Import succeed.
				else {
				  //Show notification
	                if(isProject)
	                    Notification.show(I18N.CONSTANTS.adminProjectModelImport(), I18N.CONSTANTS.adminProjectModelImportDetail());
	                else if(isOrgUnit)
	                        Notification.show(I18N.CONSTANTS.adminOrgUnitsModelImport(), I18N.CONSTANTS.adminOrgUnitsModelImportDetail());
	                    else if(isReport)
	                            Notification.show(I18N.CONSTANTS.adminReportModelImport(), I18N.CONSTANTS.adminReportModelImportDetail());
	                        else
	                            Notification.show(I18N.CONSTANTS.adminCategoryImport(), I18N.CONSTANTS.adminCategoryImportDetailt());   
				}
				
			}

		});
		importDialog.add(importPanel);

		importDialog.getButtonById(Dialog.CANCEL).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						importDialog.hide();
					}
				});

		importDialog.getButtonById(Dialog.OK).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						if (importPanel.isValid()) {
							importPanel.submit();
						}else{
							if(isProject)
								MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
					                    I18N.MESSAGES.importFormIncompleteDetails(I18N.CONSTANTS.adminProjectModelStandard()), null);
							else if(isOrgUnit)
								MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
					                    I18N.MESSAGES.importFormIncompleteDetails(I18N.CONSTANTS.adminOrgUnitsModelStandard()), null);
							else if(isReport)
								MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
					                    I18N.MESSAGES.importFormIncompleteDetails(I18N.CONSTANTS.adminReportModelStandard()), null);
							else
								MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
					                    I18N.MESSAGES.importFormIncompleteDetails(I18N.CONSTANTS.adminCategoryTypeStandard()), null);
						}
					}
				});

		importDialog.show();
	}

	/**
	 * Duplicates project and org-unit models.
	 */
	private void onCopyModel() {
		if (isProject) {
			GetProjectModelCopy copyProjectModelCommand = new GetProjectModelCopy(getModelId());
			copyProjectModelCommand.setNewModelName(I18N.MESSAGES.copyOf(getProjectModel().getName()));
			dispatcher.execute(copyProjectModelCommand, null,
					new AsyncCallback<ProjectModelDTOLight>() {

						@Override
						public void onFailure(Throwable arg0) {
							MessageBox.alert(I18N.CONSTANTS
									.adminProjectModelCopy(), I18N.CONSTANTS
									.adminProjectModelCopyError(), null);
						}

						@Override
						public void onSuccess(ProjectModelDTOLight result) {
							if (result != null) {
								((AdminProjectModelsPresenter.View) view)
										.getAdminModelsStore().add(result);
								((AdminProjectModelsPresenter.View) view)
										.getAdminModelsStore().commitChanges();
								// Show notification.
								Notification.show(I18N.CONSTANTS
										.adminProjectModelCopy(),
										I18N.CONSTANTS
												.adminProjectModelCopyDetail());
							}

						}
					});
		}

		if (isOrgUnit) {
			GetOrgUnitModelCopy copyOrgUnitModelCommand = new GetOrgUnitModelCopy(getModelId());
			copyOrgUnitModelCommand.setNewModelName(I18N.MESSAGES.copyOf(getOrgUnit().getName()));
			dispatcher.execute(copyOrgUnitModelCommand, null,
					new AsyncCallback<OrgUnitModelDTO>() {

						@Override
						public void onFailure(Throwable arg0) {
							MessageBox.alert(I18N.CONSTANTS
									.adminOrgUnitsModelCopy(), I18N.CONSTANTS
									.adminOrgUnitsModelCopyError(), null);
						}

						@Override
						public void onSuccess(OrgUnitModelDTO result) {
							if (result != null) {
								((AdminOrgUnitModelsPresenter.View) view)
										.getAdminModelsStore().add(result);
								((AdminOrgUnitModelsPresenter.View) view)
										.getAdminModelsStore().commitChanges();
								// Show notification.
								Notification.show(
												I18N.CONSTANTS.adminOrgUnitsModelCopy(),
												I18N.CONSTANTS.adminOrgUnitsModelCopyDetail());
							}
						}
					});
		}
	}

	/**
	 * Refresh the model list.
	 */
	private void refreshDataModelList(){
		if (isProject) {// refresh project models list
			AdminProjectModelsPresenter
					.refreshProjectModelsPanel(dispatcher, (AdminProjectModelsPresenter.View)view);
		} else {
			if (isOrgUnit) {// refresh org-unit models list
				AdminOrgUnitModelsPresenter.refreshOrgUnitModelsPanel(dispatcher, (AdminOrgUnitModelsPresenter.View)view);
			} else {
				if (isReport) { // refresh reports model list
					AdminReportModelPresenter.refreshReportModelsPanel(dispatcher, (AdminReportModelPresenter.View)view);
				} else {// refresh category type list
					AdminCategoryPresenter.refreshCategoryTypePanel(dispatcher, (AdminCategoryPresenter.View)view);
				}
			}
		}
	}
}
