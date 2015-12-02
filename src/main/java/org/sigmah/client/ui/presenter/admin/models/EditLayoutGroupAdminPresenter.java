package org.sigmah.client.ui.presenter.admin.models;

import java.util.HashMap;
import java.util.Map;

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
import org.sigmah.client.ui.view.admin.models.EditLayoutGroupAdminView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitDetailsDTO;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Presenter in charge of creating/editing a layout group.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class EditLayoutGroupAdminPresenter extends AbstractPagePresenter<EditLayoutGroupAdminPresenter.View> implements HasForm {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(EditLayoutGroupAdminView.class)
	public static interface View extends ViewPopupInterface {

		FormPanel getForm();

		Field<String> getNameField();

		ComboBox<BaseModelData> getContainerField();

		SimpleComboBox<Integer> getRowField();

		Button getSaveButton();

		Button getDeleteButton();

	}

	/**
	 * The edited {@link LayoutGroupDTO}, or {@code null} in case of creation.
	 */
	private LayoutGroupDTO layoutGroup;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by the presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	protected EditLayoutGroupAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Container field change events handler.
		// --

		view.getContainerField().addListener(Events.Change, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent event) {

				final BaseModelData selectedContainer = view.getContainerField().getValue();

				setRowFieldValues(selectedContainer, null);
			}
		});

		// --
		// Save button handler.
		// --

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onSaveForm();
			}
		});

		// --
		// Delete button handler.
		// --

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onDelete();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		view.getForm().clearAll();
		view.getRowField().disable();
		setPageTitle(I18N.CONSTANTS.adminFlexibleGroup());

		final FlexibleElementDTO flexibleElement = request.getData(RequestParameter.DTO);
		final IsModel currentModel = request.getData(RequestParameter.MODEL);

		if (currentModel == null) {
			hideView();
			throw new IllegalArgumentException("Missing required model.");
		}

		// --
		// Loads containers.
		// --

		view.getContainerField().getStore().removeAll();
		if (ClientUtils.isNotEmpty(currentModel.getHasLayoutElements())) {
			for (final AbstractModelDataEntityDTO<?> hasLayout : currentModel.getHasLayoutElements()) {
				if (hasLayout == null) {
					continue;
				}
				view.getContainerField().getStore().add(hasLayout);
			}
		}

		// --
		// Loads the edited element.
		// --

//		view.getDeleteButton().setVisible(flexibleElement != null);
		
		if (flexibleElement != null) {
			layoutGroup = flexibleElement.getGroup();

			view.getNameField().setValue(layoutGroup.getTitle());
			view.getContainerField().setValue(flexibleElement.getContainerModel());
			setRowFieldValues(flexibleElement.getContainerModel(), layoutGroup.getRow());
		} else {
			layoutGroup = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] { view.getForm()
		};
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Populates the row field with the given {@code hasLayout} corresponding layout row counts.
	 * 
	 * @param hasLayout
	 *          The component with a layout.
	 * @param selectedValue
	 *          The optional selected value.
	 */
	private void setRowFieldValues(final BaseModelData hasLayout, final Integer selectedValue) {

		view.getRowField().removeAll();
		view.getRowField().setEnabled(hasLayout != null);

		if (hasLayout == null) {
			return;
		}

		final LayoutDTO container = getLayout(hasLayout);

		if (container != null) {
			view.getRowField().removeAll();

			for (int i = 0; i < container.getRowsCount(); i++) {
				view.getRowField().add(i);
			}
			
			if (layoutGroup == null) {
				view.getRowField().add(container.getRowsCount());
			}
		}

		view.getRowField().setSimpleValue(selectedValue);
	}

	/**
	 * Retrieves the given {@code hasLayout} corresponding {@link LayoutDTO}.
	 * 
	 * @param hasLayout
	 *          The container, may be {@code null}.
	 * @return The given {@code hasLayout} corresponding {@link LayoutDTO}, or {@code null}.
	 */
	static LayoutDTO getLayout(final BaseModelData hasLayout) {

		if (hasLayout instanceof ProjectDetailsDTO) {
			return ((ProjectDetailsDTO) hasLayout).getLayout();

		} else if (hasLayout instanceof PhaseModelDTO) {
			return ((PhaseModelDTO) hasLayout).getLayout();

		} else if (hasLayout instanceof OrgUnitDetailsDTO) {
			return ((OrgUnitDetailsDTO) hasLayout).getLayout();

		} else {
			return null;
		}
	}

	/**
	 * Callback executed on <em>save</em> button action.
	 */
	private void onSaveForm() {

		if (!view.getForm().isValid()) {
			return;
		}

		final String name = view.getNameField().getValue();
		final Integer row = view.getRowField().getSimpleValue();
		final Integer column = 0;
		final LayoutDTO container = getLayout(view.getContainerField().getValue());

		final LayoutGroupDTO layoutGroupDTO = layoutGroup != null ? layoutGroup : new LayoutGroupDTO();
		layoutGroupDTO.setTitle(name);
		layoutGroupDTO.setRow(row);
		layoutGroupDTO.setColumn(column);
		layoutGroupDTO.setParentLayout(container);

		final Map<String, Object> newGroupProperties = new HashMap<String, Object>();
		newGroupProperties.put(AdminUtil.PROP_NEW_GROUP_LAYOUT, layoutGroupDTO);

		dispatch.execute(new CreateEntity(LayoutGroupDTO.ENTITY_NAME, newGroupProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminFlexibleGroup(),
					I18N.MESSAGES.adminStandardCreationFailure(I18N.MESSAGES.adminStandardLayoutGroup() + " '" + name + "'"));
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null) {
					N10N.warn(I18N.CONSTANTS.adminFlexibleGroup(), I18N.MESSAGES.adminStandardCreationNull(I18N.MESSAGES.adminStandardLayoutGroup() + " '" + name + "'"));
					return;
				}

				N10N.infoNotif(I18N.CONSTANTS.adminFlexibleGroup(),
					I18N.MESSAGES.adminStandardUpdateSuccess(I18N.MESSAGES.adminStandardLayoutGroup() + " '" + name + "'"));

				hideView();

				// Send an update event to reload necessary data.
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.LAYOUT_GROUP_UPDATE, result.getEntity()));
			}
		}, view.getSaveButton(), view.getDeleteButton());
	}

	/**
	 * Callback executed on <em>delete</em> button action.
	 */
	private void onDelete() {
		
		dispatch.execute(new Delete(layoutGroup), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				hideView();

				// Send an update event to reload necessary data.
				// eventBus.fireEvent(new UpdateEvent(UpdateEvent.LAYOUT_GROUP_UPDATE, result.getEntity()));
			}
		}, view.getSaveButton(), view.getDeleteButton());
	}

}
