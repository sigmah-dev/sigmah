package org.sigmah.client.ui.presenter.admin.models.orgunit;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.presenter.base.HasForm;
import org.sigmah.client.ui.view.admin.models.orgunit.AddOrgUnitModelAdminView;
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
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter in charge of adding a new OrgUnit model.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class AddOrgUnitModelAdminPresenter extends AbstractPagePresenter<AddOrgUnitModelAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(AddOrgUnitModelAdminView.class)
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
	@Inject
	protected AddOrgUnitModelAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
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
