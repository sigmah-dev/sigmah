package org.sigmah.client.ui.presenter.admin.models.importer;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.models.importer.AddMatchingRuleImportationShemeModelsAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class AddMatchingRuleImportationShemeModelsAdminPresenter extends AbstractPagePresenter<AddMatchingRuleImportationShemeModelsAdminPresenter.View> {

	private EntityDTO<Integer> currentModel;

	/**
	 * Description of the view managed by this presenter.
	 */

	@ImplementedBy(AddMatchingRuleImportationShemeModelsAdminView.class)
	public static interface View extends ViewPopupInterface {

		ComboBox<VariableDTO> getVariablesCombo();

		ComboBox<FlexibleElementDTO> getFlexibleElementsCombo();

		CheckBox getIsKeyCheckBox();

		Text getIdKeyText();

		Button getSubmitButton();

		FlexTable getBudgetSubFlexTable();

	}

	@Inject
	protected AddMatchingRuleImportationShemeModelsAdminPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_IMPORTATION_SCHEME_MODEL_MATCHING_RULE;
	}

	@Override
	public void onPageRequest(PageRequest request) {

		currentModel = request.getData(RequestParameter.DTO);

		initForm(currentModel);

		setPageTitle(I18N.CONSTANTS.adminAddKeyVariableFlexibleElementHeading());

	}

	@Override
	public void onBind() {

		// on change combo champs (case budget)

		// Save Matching rule

		view.getSubmitButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				// Update Store of matching rule
				// eventBus.fireEvent(event);

			}

		});

	}

	private void initForm(EntityDTO<Integer> currentModel) {

		if (currentModel instanceof OrgUnitModelDTO) {

		}
		if (currentModel instanceof ProjectModelDTO) {

		}

	}
}
