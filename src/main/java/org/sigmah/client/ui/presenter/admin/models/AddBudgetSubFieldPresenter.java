package org.sigmah.client.ui.presenter.admin.models;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
