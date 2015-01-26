package org.sigmah.client.ui.presenter.admin.models;

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.models.AddBudgetSubFieldView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class AddBudgetSubFieldPresenter extends AbstractPagePresenter<AddBudgetSubFieldPresenter.View> {

	private BudgetSubFieldDTO currentBudgetSubField;

	@ImplementedBy(AddBudgetSubFieldView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		TextField<String> getNameField();

		Button getSaveButton();

	}

	@Inject
	public AddBudgetSubFieldPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public Page getPage() {
		return Page.ADMIN_EDIT_FLEXIBLE_ELEMENT_ADD_BUDGETSUBFIELD;
	}

	@Override
	public void onBind() {

		view.getSaveButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {

				if (view.getForm().isValid()) {
					currentBudgetSubField.setLabel(view.getNameField().getValue());
					eventBus.fireEvent(new UpdateEvent(UpdateEvent.EDIT_FLEXIBLEELEMNT_EDIT_BUDGETSUBFIELD));
					view.hide();
				}

			}
		});

	}

	@Override
	public void onPageRequest(PageRequest request) {

		view.getForm().clearAll();

		currentBudgetSubField = request.getData(RequestParameter.DTO);

		view.getNameField().setValue(currentBudgetSubField.getLabel());

		setPageTitle(I18N.CONSTANTS.adminAddBudgetSubField());

	}

}
