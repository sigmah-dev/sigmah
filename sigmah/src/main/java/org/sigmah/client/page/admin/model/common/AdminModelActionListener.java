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
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.ProjectModelDTOLight;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminModelActionListener implements ActionListener {
	private final ContentPanel view;
	private final Dispatcher dispatcher;
	private final Boolean isProject;
	private Boolean isReport;
	private Boolean isOrgUnit;

	private int modelId;
	
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
				}			
			});
			window.add(form);
	        window.show();
		}
	}
	
	/**
	 * Export models.
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
	 * Import model.
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
		importDialog.setSize(400, 100);
		importDialog.setButtons(Dialog.YESNO);

		final FormPanel importPanel = new FormPanel();
		importPanel.setBodyBorder(false);
		importPanel.setHeaderVisible(false);
		importPanel.setEncoding(Encoding.MULTIPART);
		importPanel.setMethod(Method.POST);
		importPanel.setAction(GWT.getModuleBaseURL() + "models");
		importPanel.setMethod(FormPanel.Method.POST);
		importPanel.setPadding(5);
		importPanel.setLabelWidth(120);
		importPanel.setFieldWidth(250);
		importPanel.setSize(400, 70);

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
		if (isProject) {
			// Type project model
			final TextField<String> projectmodelType = new TextField<String>();
			projectmodelType.setName("project-model-type");
			projectmodelType.setAllowBlank(false);
			projectmodelType.setFieldLabel(I18N.CONSTANTS.createProjectType());
			importPanel.add(projectmodelType);
		}

		importPanel.addListener(Events.Submit, new Listener<FormEvent>() {
			@Override
			public void handleEvent(FormEvent be) {
				refreshDataModelList();
				importDialog.hide();
			}

		});
		importDialog.add(importPanel);

		importDialog.getButtonById(Dialog.NO).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						importDialog.hide();
					}
				});

		importDialog.getButtonById(Dialog.YES).addSelectionListener(
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						importPanel.submit();
					}
				});

		importDialog.show();
	}

	public int getModelId() {
		return modelId;
	}

	public void setModelId(int projectModelId) {
		this.modelId = projectModelId;
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
