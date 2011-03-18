package org.sigmah.client.page.admin;

import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.users.AdminUsersPresenter;
import org.sigmah.client.page.admin.users.AdminUsersView;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.form.ProfileSigmahForm;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.profile.ProfileDTO;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AdminUtil {
	
	public final static String ADMIN_PROJECT_MODEL = "projectModel";
	
	public static void alertPbmData(boolean alert, String title, String msg) {
        if (alert)
            return;
        alert = true;
        MessageBox.alert(I18N.CONSTANTS.adminUsers(), I18N.CONSTANTS.adminProblemLoading(), null);
    }
	
	public static Object createUserGridText(String content) {
        final Text label = new Text(content);
        label.addStyleName("project-grid-leaf");
        
        return label;
    }
	
	public static CheckBox createCheckBox(String property, String label) {
		CheckBox box = new CheckBox();
		box.setName(property);
		box.setBoxLabel(label);
		return box;
	}
	
	public static Object getInList(List<String> list, String defaultLabel) {
		
		String content = "";
		
		if(list!= null){																							
			for(Object element : list){
				content = element + ", " + content;
			}
		}else{
			content = defaultLabel;
		}				
		return createUserGridText(content);
		
	}
	
	public static Object getEditButton(final FormPanel form, 
			final String title, final int width, final int height) {
		
		Button button = new Button(I18N.CONSTANTS.edit());
        button.setItemId(UIActions.edit);
        button.addListener(Events.OnClick, new Listener<ButtonEvent>(){

			@Override
			public void handleEvent(ButtonEvent be) {
				final Window window = new Window();		
				window.setHeading(title);
		        window.setSize(width, height);
		        window.setPlain(true);
		        window.setModal(true);
		        window.setBlinkModal(true);
		        window.setLayout(new FitLayout());
				window.add(form);
		        window.show();
			}
		});		        		        
		return button;				
	}
	
	public static void showForm(final Window window, final FormPanel form, 
			final String title, final int width, final int height) {
	
		window.setHeading(title);
        window.setSize(width, height);
        window.setPlain(true);
        window.setModal(true);
        window.setBlinkModal(true);
        window.setLayout(new FitLayout());
		window.add(form);
        window.show();

	}
	
	

}
