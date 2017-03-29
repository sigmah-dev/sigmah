package org.sigmah.client.ui.presenter.admin.models.orgunit;

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

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;

/**
 * Presenter in charge of adding a new OrgUnit model.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class AddOrgUnitModelAdminPresenter extends AbstractPagePresenter<AddOrgUnitModelAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<Boolean> getHasBudgetField();

		Field<Boolean> getCanContainProjectsField();

		Field<String> getTitleField();

		Field<String> getNameField();

		Button getAddButton();

	}

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by the presenter.
	 * @param injector
	 *          The application injector.
	 */
	public AddOrgUnitModelAdminPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_ORG_UNIT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onSaveForm();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		view.getForm().clearAll();

		setPageTitle(I18N.CONSTANTS.adminOrgUnitsModelCreationBox());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { view.getForm()
		};
	}

	/**
	 * Callback executed on <em>create</em> button action.
	 */
	public void onSaveForm() {

		if (!view.getForm().isValid()) {
			return;
		}

		final String name = view.getNameField().getValue();
		final String title = view.getTitleField().getValue();
		final Boolean hasBudget = view.getHasBudgetField().getValue();
		final Boolean containsProjects = view.getCanContainProjectsField().getValue();

		final Map<String, Object> newOrgUnitModelProperties = new HashMap<String, Object>();
		newOrgUnitModelProperties.put(AdminUtil.PROP_OM_NAME, name);
		newOrgUnitModelProperties.put(AdminUtil.PROP_OM_TITLE, title);
		newOrgUnitModelProperties.put(AdminUtil.PROP_OM_HAS_BUDGET, hasBudget);
		newOrgUnitModelProperties.put(AdminUtil.PROP_OM_CONTAINS_PROJECTS, containsProjects);

		final OrgUnitModelDTO model = new OrgUnitModelDTO();
		model.setStatus(ProjectModelStatus.DRAFT);
		newOrgUnitModelProperties.put(AdminUtil.ADMIN_ORG_UNIT_MODEL, model);

		dispatch.execute(new CreateEntity(OrgUnitModelDTO.ENTITY_NAME, newOrgUnitModelProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminOrgUnitsModelStandard() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				final OrgUnitModelDTO orgUnitModel = (OrgUnitModelDTO) result.getEntity();
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.ORG_UNIT_MODEL_ADD, orgUnitModel));

				N10N.infoNotif(I18N.CONSTANTS.adminOrgUnitsModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationSuccess(I18N.CONSTANTS.adminOrgUnitsModelStandard() + " '" + name + "'"));

				hideView();
			}
		});
	}
}
