package org.sigmah.client.ui.presenter.admin.models.project;

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
import org.sigmah.client.ui.view.admin.models.project.AddProjectModelAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Presenter in charge of adding a new Project model.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class AddProjectModelAdminPresenter extends AbstractPagePresenter<AddProjectModelAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(AddProjectModelAdminView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<String> getNameField();

		Field<ProjectModelType> getProjectModelTypeField();

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
	protected AddProjectModelAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_ADD_PROJECT_MODEL;
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

		setPageTitle(I18N.CONSTANTS.adminProjectModelAdd());
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
		final ProjectModelType modelType = view.getProjectModelTypeField().getValue();

		// Creates a new 'DRAFT' project model.
		final Map<String, Object> newProjectModelProperties = new HashMap<String, Object>();
		newProjectModelProperties.put(AdminUtil.PROP_PM_USE, modelType);
		newProjectModelProperties.put(AdminUtil.PROP_PM_NAME, name);
		newProjectModelProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, new ProjectModelDTO(ProjectModelStatus.DRAFT));

		dispatch.execute(new CreateEntity(ProjectModelDTO.ENTITY_NAME, newProjectModelProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminProjectModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.CONSTANTS.adminProjectModelStandard() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null || result.getEntity() == null) {
					N10N.warn(I18N.CONSTANTS.adminProjectModelCreationBox(),
						I18N.MESSAGES.adminStandardCreationNull(I18N.CONSTANTS.adminProjectModelStandard() + " '" + name + "'"));
					return;
				}

				final ProjectModelDTO createdProjectModel = (ProjectModelDTO) result.getEntity();

				eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_MODEL_ADD, createdProjectModel));

				N10N.infoNotif(I18N.CONSTANTS.adminProjectModelCreationBox(),
					I18N.MESSAGES.adminStandardCreationSuccess(I18N.CONSTANTS.adminProjectModelStandard() + " '" + name + "'"));

				hideView();
			}
		});
	}
}
