package org.sigmah.client.ui.presenter.admin.orgunits;

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

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.admin.orgunits.AddOrgUnitAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.shared.command.AddOrgUnit;
import org.sigmah.shared.command.GetOrgUnitModels;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Admin create OrgUnit Presenter which manages {@link AddOrgUnitAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AddOrgUnitAdminPresenter extends AbstractPagePresenter<AddOrgUnitAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(AddOrgUnitAdminView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<String> getNameField();

		Field<String> getFullNameField();

		ComboBox<CountryDTO> getCountryField();

		ComboBox<OrgUnitModelDTO> getModelField();

		Button getSaveButton();

	}

	/**
	 * The parent OrgUnit id, should never be {@code null}.
	 */
	private Integer parentId;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	protected AddOrgUnitAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_ORG_UNIT;
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
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onSaveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		parentId = request.getParameterInteger(RequestParameter.ID);

		if (parentId == null) {
			hideView();
			throw new IllegalArgumentException("Invalid required parent OrgUnit id.");
		}

		view.getForm().clearAll();

		setPageTitle(I18N.CONSTANTS.adminOrgUnitAdd());

		loadCountries();
		loadModels();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the countries from the local cache and populates the corresponding form field.
	 */
	@SuppressWarnings("deprecation")
	private void loadCountries() {
		view.getCountryField().getStore().removeAll();
		view.getCountryField().getStore().add(injector.getClientCache().getCountryCache().get());
	}

	/**
	 * Loads the org units models using a command and populates the corresponding form field.
	 */
	private void loadModels() {

		view.getModelField().getStore().removeAll();

		// Retrieves the models.
		dispatch.execute(new GetOrgUnitModels(OrgUnitModelDTO.Mode.BASE), new CommandResultHandler<ListResult<OrgUnitModelDTO>>() {

			@Override
			public void onCommandFailure(final Throwable e) {
				hideView();
				N10N.warn(I18N.CONSTANTS.adminOrgUnitAddUnavailable(), I18N.CONSTANTS.adminOrgUnitAddMissingModel());
			}

			@Override
			public void onCommandSuccess(final ListResult<OrgUnitModelDTO> result) {

				if (result == null || result.isEmpty()) {
					hideView();
					N10N.warn(I18N.CONSTANTS.adminOrgUnitAddUnavailable(), I18N.CONSTANTS.adminOrgUnitAddMissingModel());
					return;
				}

				view.getModelField().getStore().add(result.getList());
			}
		});
	}

	/**
	 * Callback executed on save button action.
	 */
	private void onSaveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		final OrgUnitDTO unit = new OrgUnitDTO();
		unit.setName(view.getNameField().getValue());
		unit.setFullName(view.getFullNameField().getValue());
		unit.setOfficeLocationCountry(view.getCountryField().getValue());

		final Integer modelId = view.getModelField().getValue().getId();

		dispatch.execute(new AddOrgUnit(parentId, modelId, I18N.CONSTANTS.calendarDefaultName(), unit, OrgUnitDTO.Mode.WITH_TREE),
			new CommandResultHandler<CreateResult>() {

				@Override
				public void onCommandFailure(final Throwable caught) {
					N10N.warn(I18N.CONSTANTS.adminOrgUnitAddFailed(), I18N.CONSTANTS.adminOrgUnitAddFailedDetails());
				}

				@Override
				public void onCommandSuccess(final CreateResult result) {

					N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminOrgUnitAddSucceed());

					eventBus.fireEvent(new UpdateEvent(UpdateEvent.ORG_UNIT_UPDATE));

					hideView();
				}
			}, view.getSaveButton());
	}

}
