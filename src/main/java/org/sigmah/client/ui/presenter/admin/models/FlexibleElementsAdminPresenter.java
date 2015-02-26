package org.sigmah.client.ui.presenter.admin.models;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.base.IsModelTabPresenter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.admin.models.FlexibleElementsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.DeleteFlexibleElements;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.DefaultFlexibleElementType;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Model's flexible elements administration presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FlexibleElementsAdminPresenter<E extends IsModel> extends AbstractPresenter<FlexibleElementsAdminPresenter.View>
																																																															implements
																																																															IsModelTabPresenter<E, FlexibleElementsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(FlexibleElementsAdminView.class)
	public static interface View extends ViewInterface, HasGrid<FlexibleElementDTO> {

		/**
		 * Provides the current loaded model corresponding status to the view.
		 * 
		 * @param status
		 *          The current loaded model corresponding status.
		 */
		void setModelStatus(ProjectModelStatus status);

		void setToolbarEnabled(final boolean enabled);

		Button getAddButton();

		Button getAddGroupButton();

		Button getDeleteButton();

	}

	/**
	 * "On flexible element group" click event key used to differentiate flexible elements grid events.
	 */
	public static final String ON_GROUP_CLICK_EVENT_KEY = "_GROUP_EVENT_";

	/**
	 * The provided current model.
	 */
	private E currentModel;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by this presenter.
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	public FlexibleElementsAdminPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Grid selection change handler.
		// --

		view.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<FlexibleElementDTO>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<FlexibleElementDTO> event) {
				view.getDeleteButton().setEnabled(ClientUtils.isNotEmpty(event.getSelection()));
			}
		});

		// --
		// Grid events handler.
		// --

		view.setGridEventHandler(new GridEventHandler<FlexibleElementDTO>() {

			@Override
			public void onRowClickEvent(final FlexibleElementDTO rowElement) {
				if (ClientUtils.isTrue(rowElement.get(ON_GROUP_CLICK_EVENT_KEY))) {
					// Group label event.
					eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.DTO, rowElement));

				} else {
					eventBus.navigateRequest(Page.ADMIN_EDIT_FLEXIBLE_ELEMENT.request().addData(RequestParameter.MODEL, currentModel)
						.addData(RequestParameter.DTO, rowElement));
				}
			}
		});

		// --
		// Add button handler.
		// --

		view.getAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_FLEXIBLE_ELEMENT.request().addData(RequestParameter.MODEL, currentModel));
			}
		});

		// --
		// Add Group button handler.
		// --

		view.getAddGroupButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				eventBus.navigateRequest(Page.ADMIN_EDIT_LAYOUT_GROUP_MODEL.request().addData(RequestParameter.MODEL, currentModel));
			}
		});

		// --
		// Delete button handler.
		// --

		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onFlexibleElementDeleteAction(view.getGrid().getSelectionModel().getSelection());
			}
		});

		// --
		// Flexible element creation/update event handler.
		// --

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.FLEXIBLE_ELEMENT_UPDATE)) {
					final boolean update = event.getParam(1);
					final FlexibleElementDTO updatedOrCreatedElement = event.getParam(2);
					onFlexibleElementUpdate(update, updatedOrCreatedElement);
				}
			}
		}));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminProjectModelFields();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadTab(final E model) {

		this.currentModel = model;

		view.setToolbarEnabled(model.getStatus() != null && model.getStatus().isEditable());

		view.setModelStatus(model.getStatus());

		view.getStore().removeAll();
		view.getStore().add(model.getAllElements());
		view.getStore().commitChanges();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasValueChanged() {
		return false;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Callback executed on flexible element creation/update event.
	 * 
	 * @param udpate
	 *          Flag set to {@code true} in case of update.
	 * @param updatedOrCreatedElement
	 *          The updated or created flexible element.
	 */
	private void onFlexibleElementUpdate(final boolean udpate, final FlexibleElementDTO updatedOrCreatedElement) {

		if (udpate) {
			view.getStore().update(updatedOrCreatedElement);

		} else {
			view.getStore().add(updatedOrCreatedElement);
		}

		view.getStore().commitChanges();
	}

	/**
	 * Callback executed on flexible element delete action.<br>
	 * Handles confirmation message.
	 * 
	 * @param selection
	 *          The selected flexible element(s).
	 */
	private void onFlexibleElementDeleteAction(final List<FlexibleElementDTO> selection) {

		if (ClientUtils.isEmpty(selection)) {
			N10N.warn(I18N.CONSTANTS.delete(), I18N.MESSAGES.adminFlexibleDeleteNone());
			return;
		}

		final List<String> elementNames = new ArrayList<String>();
		final List<String> defaultElementNames = new ArrayList<String>();

		for (final FlexibleElementDTO element : selection) {
			elementNames.add(element.getFormattedLabel());

			if (element instanceof DefaultFlexibleElementDTO) {
				defaultElementNames.add(DefaultFlexibleElementType.getName(((DefaultFlexibleElementDTO) element).getType()));
			}
		}

		if (ClientUtils.isNotEmpty(defaultElementNames)) {
			N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminErrorDeleteDefaultFlexible(), defaultElementNames);
			return;
		}

		N10N.confirmation(I18N.CONSTANTS.delete(), I18N.CONSTANTS.adminFlexibleConfirmDelete(), elementNames, new ConfirmCallback() {

			@Override
			public void onAction() {

				dispatch.execute(new DeleteFlexibleElements(selection), new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {

						final StringBuilder builder = new StringBuilder();
						for (final String deleted : elementNames) {
							if (builder.length() > 0) {
								builder.append(", ");
							}
							builder.append(deleted);
						}

						N10N.warn(I18N.CONSTANTS.error(), I18N.MESSAGES.entityDeleteEventError(builder.toString()));
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						// Updates the store.
						for (final FlexibleElementDTO element : selection) {
							view.getStore().remove(element);
						}

						// Notification.
						N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.adminFlexibleDeleteFlexibleElementsConfirm());

						// FIXME (v1.3) update model
					}
				});
			}
		});
	}

}
