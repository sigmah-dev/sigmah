package org.sigmah.client.page.admin;

import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.toolbar.UIActions;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class AdminUtil {
	
	public final static String ADMIN_PROJECT_MODEL = "projectModel";
	public final static String ADMIN_ORG_UNIT_MODEL = "orgUnitModel";
	public static final String PROP_LOG_FRAME = "log_frame";
	
	public static final String PROP_LOG_FRAME_NAME = "logframe name";
	public static final String PROP_OBJ_MAX = "objectives_max";
	public static final String PROP_OBJ_MAX_PER_GROUP = "objectives_max_per_group";
	public static final String PROP_OBJ_ENABLE_GROUPS = "objectives_enable_groups";
	public static final String PROP_OBJ_MAX_GROUPS = "objectives_max_groups";
	public static final String PROP_A_MAX = "activities_max";
	public static final String PROP_A_ENABLE_GROUPS = "activities_enable_groups";
	public static final String PROP_A_MAX_PER_RESULT = "activities_max_per_result";
	public static final String PROP_A_MAX_GROUPS = "activities_max_groups";
	public static final String PROP_A_MAX_PER_GROUP = "activities_max_per_group";
	public static final String PROP_R_MAX = "results_max";
	public static final String PROP_R_ENABLE_GROUPS = "results_enable_groups";
	public static final String PROP_R_MAX_PER_OBJ = "results_max_per_obj";
	public static final String PROP_R_MAX_GROUPS = "results_max_groups";
	public static final String PROP_R_MAX_PER_GROUP = "results_max_per_group";
	public static final String PROP_P_MAX = "prerequisites_max";
	public static final String PROP_P_ENABLE_GROUPS = "prerequisites_enable_groups";
	public static final String PROP_P_MAX_GROUPS = "prerequisites_max_groups";
	public static final String PROP_P_MAX_PER_GROUP = "prerequisites_max_per_group";
	
	public final static String PROP_FX_REPORT_MODEL = "FlexibleElementReportModel";
	public final static String PROP_FX_MAX_LIMIT = "FlexibleElementMaxLimit";
	public final static String PROP_FX_MIN_LIMIT = "FlexibleElementMinLimit";
	public final static String PROP_FX_TEXT_TYPE = "FlexibleElementTextType";
	public final static String PROP_FX_LENGTH = "FlexibleElementLength";
	public final static String PROP_FX_DECIMAL = "FlexibleElementDecimal";
	public final static String PROP_FX_Q_MULTIPLE = "FlexibleElementQuestionMultiple";
	public final static String PROP_FX_Q_QUALITY = "FlexibleElementQuestionQuality";
	public final static String PROP_FX_Q_CATEGORY = "FlexibleElementQuestionCategory";
	public final static String PROP_FX_Q_CHOICES = "FlexibleElementQuestionChoices";
	
	public final static String PROP_FX_NAME = "name";
	public final static String PROP_FX_TYPE = "type";
	public final static String PROP_FX_GROUP = "group";
	public final static String PROP_FX_ORDER_IN_GROUP = "orderInGroup";
	public final static String PROP_FX_IN_BANNER = "inBanner";
	public final static String PROP_FX_POS_IN_BANNER = "posBanner";
	public final static String PROP_FX_IS_COMPULSARY = "isCompulsory";
	public final static String PROP_FX_PRIVACY_GROUP = "privacyGroup";
	public final static String PROP_FX_AMENDABLE = "amendable";
	
	public final static String PROP_FX_LC_BANNER = "layoutConstraintBanner";
	public final static String PROP_FX_LC = "layoutConstraint";
	
	public final static String PROP_FX_FLEXIBLE_ELEMENT = "flexibleElement";
	
	public final static String PROP_FX_OLD_FIELDS = "oldFieldProperties";
	
	public final static String PROP_NEW_GROUP_LAYOUT = "NewLayoutGroup";
	
	public final static String PROP_PHASE_MODEL = "NewPhaseModel";
	public final static String PROP_PHASE_ROWS = "PhaseRows";
	public final static String PROP_PHASE_ROOT = "PhaseRoot";
	public final static String PROP_PHASE_ORDER = "PhaseDisplayOrder";
	public final static String PROP_PHASE_GUIDE = "PhaseGuide";
	
	public final static String PROP_REPORT_MODEL_NAME = "ProjectReportModelName";
	public final static String PROP_REPORT_SECTION_MODEL = "ProjectReportModelSection";
	
	public final static String PROP_CATEGORY_TYPE = "NewCategoryType";
	public final static String PROP_CATEGORY_TYPE_NAME = "NewCategoryTypeName";
	public final static String PROP_CATEGORY_TYPE_ICON = "NewCategoryTypeIcon";
	public final static String PROP_CATEGORY_ELEMENT = "NewCategoryElement";
	public final static String PROP_CATEGORY_ELEMENT_NAME = "NewCategoryElementName";
	public final static String PROP_CATEGORY_ELEMENT_COLOR = "NewCategoryElementColor";
	
	public final static String PROP_PM_NAME = "ProjectModelName";
	public final static String PROP_PM_USE = "ProjectModelUse";
	public final static String PROP_PM_STATUS = "ProjectModelStatus";
	
	public final static String PROP_OM_NAME = "OrgUnitModelName";
	public final static String PROP_OM_STATUS = "OrgUnitModelStatus";
	public final static String PROP_OM_TITLE = "OrgUnitModelTitle";
	public final static String PROP_OM_HAS_BUDGET = "OrgUnitModelBudget";
	public final static String PROP_OM_HAS_SITE = "OrgUnitModelSite";
	public final static String PROP_OM_CONTAINS_PROJECTS = "OrgUnitModelContainProjects";
	
	public static void alertPbmData(boolean alert) {
        if (alert)
            return;
        alert = true;
        MessageBox.alert(I18N.CONSTANTS.adminboard(), I18N.CONSTANTS.adminProblemLoading(), null);
    }
	
	public static Text createGridText(String content) {
        final Text label = new Text(content);
        label.addStyleName("label-small");
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
		return createGridText(content);
		
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
