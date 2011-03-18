package org.sigmah.client.page.admin.model.common.element;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.client.page.admin.model.common.element.AdminFlexibleElementsPresenter.View;
import org.sigmah.client.page.admin.model.project.phase.PhaseSigmahForm;
import org.sigmah.client.page.admin.users.form.PrivacyGroupSigmahForm;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.UpdateModelResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.TextAreaElementDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminFlexibleElementActionListener implements ActionListener {
	private final View view;
	private final Dispatcher dispatcher;
	private ProjectModelDTO projectModel;
	
	public AdminFlexibleElementActionListener(View view, Dispatcher dispatcher, ProjectModelDTO projectModel){
		this.view = view;
		this.dispatcher = dispatcher;
		Log.debug("Init action "+ projectModel);
		this.projectModel = projectModel;
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

		int width = 700;
		int height = 550;
		String title = I18N.CONSTANTS.adminFlexible();
		final Window window = new Window();		
		window.setHeading(title);
        window.setSize(width, height);
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
		final ElementForm form = new ElementForm(dispatcher, new AsyncCallback<UpdateModelResult>(){

			@Override
			public void onFailure(Throwable arg0) {
				window.hide();
				
			}

			@Override
			public void onSuccess(UpdateModelResult result) {
				window.hide();
				view.getFieldsStore().add((FlexibleElementDTO)result.getAnnexEntity());
				view.getFieldsStore().commitChanges();
			}			
		}, null, projectModel, null);
		window.add(form);
        window.show();		
	}
}
