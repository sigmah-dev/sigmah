package org.sigmah.client.ui.presenter.admin.models.base;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.AbstractAdminPresenter;
import org.sigmah.client.ui.view.admin.models.base.AbstractModelsAdminView;
import org.sigmah.client.ui.widget.HasGrid;
import org.sigmah.client.ui.widget.HasGrid.GridEventHandler;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetAvailableStatusForModel;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.IsModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.Pair;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Abstract layer for models administration presenters.
 * 
 * @param <E>
 *          The models grid entity type.
 * @param <V>
 *          The view interface type.
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 * @see AbstractModelsAdminView
 */
public abstract class AbstractModelsAdminPresenter<E extends IsModel, V extends AbstractModelsAdminPresenter.View<E>> extends AbstractAdminPresenter<V> {

	/**
	 * Abstract description of the view managed by this presenter.
	 */
	public static interface View<E extends IsModel> extends AbstractAdminPresenter.View, HasGrid<E> {

		// --
		// Grid components.
		// --

		Button getGridAddButton();

		Button getGridImportButton();

		Button getGridDeleteButton();

		Button getGridDuplicateButton();

		Button getGridExportButton();

		Loadable getGridMask();

		// --
		// Details components.
		// --

		Loadable getDetailsPanelMask();

		IconButton getDetailsCloseButton();

		FormPanel getHeaderForm();

		ComboBox<EnumModel<ProjectModelStatus>> getHeaderStatusField();

		Button getHeaderSaveButton();

		void loadModel(E model);

		/**
		 * Initializes a new tab with the given arguments.<br>
		 * Should be executed only once (inside {@code onBind()} method most-likely).
		 * 
		 * @param tabTitle
		 *          The tab title.
		 * @param tabView
		 *          The tab view component.
		 * @param selectionListener
		 *          The tab selection listener.
		 */
		void addTab(String tabTitle, Widget tabView, Listener<ComponentEvent> selectionListener);

		/**
		 * Selects the first tab.
		 */
		void selectFirstTab();
		
		/**
		 * Collapse or expand the model grid panel.
		 * 
		 * @param expanded <code>true</code> to expand and show the panel, 
		 * <code>false</code> to collapse and hide it.
		 */
		void setModelGridPanelExpanded(boolean expanded);
	}

	/**
	 * The tab presenters.
	 */
	private final IsModelTabPresenter<E, ?>[] tabPresenters;

	/**
	 * The current selected tab presenter.
	 */
	private IsModelTabPresenter<E, ?> currentTabPresenter;

	/**
	 * The current loaded model.
	 */
	private E currentModel;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 * @param tabPresenters
	 *          The tab presenters.
	 */
	@SafeVarargs
	@Inject
	protected AbstractModelsAdminPresenter(final V view, final Injector injector, final IsModelTabPresenter<E, ?>... tabPresenters) {
		super(view, injector);
		this.tabPresenters = tabPresenters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Grid selection change event.
		// --

		view.getGrid().getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<E>() {

			@Override
			public void selectionChanged(final SelectionChangedEvent<E> event) {
				final boolean singleItemSelected = event.getSelection() != null && event.getSelection().size() == 1;
				view.getGridDeleteButton().setEnabled(singleItemSelected);
				view.getGridExportButton().setEnabled(singleItemSelected);
				view.getGridDuplicateButton().setEnabled(singleItemSelected);
			}
		});

		// --
		// Grid events handler implementation.
		// --

		view.setGridEventHandler(new GridEventHandler<E>() {

			@Override
			public void onRowClickEvent(final E model) {

				// Selects the row.
				view.getGrid().getSelectionModel().select(model, false);

				// Loads the corresponding model.
				loadModel(model.getId());
			}
		});

		// --
		// Add button handler.
		// --

		view.getGridAddButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigate(getAddModelPage(), view.getGridAddButton());
			}
		});

		// --
		// Import button handler.
		// --

		view.getGridImportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				eventBus.navigateRequest(Page.IMPORT_MODEL.requestWith(RequestParameter.TYPE, getModelToImport()));
			}

		});

		// --
		// Delete button handler.
		// --

		view.getGridDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onDeleteAction(view.getGrid().getSelectionModel().getSelectedItem());
			}
		});

		// --
		// Export button handler.
		// --

		view.getGridExportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onExportAction(view.getGrid().getSelectionModel().getSelectedItem());
			}
		});

		// --
		// Duplicate button handler.
		// --

		view.getGridDuplicateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {
				onDuplicateAction(view.getGrid().getSelectionModel().getSelectedItem());
			}
		});

		// --
		// Close button handler.
		// --

		view.getDetailsCloseButton().addSelectionListener(new SelectionListener<IconButtonEvent>() {

			@Override
			public void componentSelected(final IconButtonEvent event) {
				loadModel(null);
			}
		});

		// --
		// Save header form button handler.
		// --

		view.getHeaderSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent event) {

				if (!view.getHeaderForm().isValid()) {
					return;
				}

				onSaveAction(currentModel, new AsyncCallback<E>() {

					@Override
					public void onFailure(final Throwable caught) {
						// Does nothing, error message is already handled by child presenters.
					}

					@Override
					public void onSuccess(final E updatedModel) {

						// Updates the current model.
						updateModel(updatedModel);
						view.getStore().update(updatedModel);

						// Updates tab view content.
						currentTabPresenter.loadTab(updatedModel);
					}
				});
			}
		});

		// --
		// Save header status field change event handler.
		// --

		view.getHeaderStatusField().addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(final FieldEvent event) {

				final ProjectModelStatus currentStatus = currentModel.getStatus();
				final ProjectModelStatus targetStatus = EnumModel.getEnum(view.getHeaderStatusField().getValue());

				if (targetStatus == null) {
					return;
				}

				final AsyncCallback<Void> callback = new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						view.getHeaderStatusField().setValue(new EnumModel<ProjectModelStatus>(currentStatus));
					}

					@Override
					public void onSuccess(Void result) {
						// Select event is allowed, nothing to do.
					}
				};

				if (currentStatus != ProjectModelStatus.UNAVAILABLE) {

					final Pair<Boolean, String> validation = ProjectModelStatus.isValidStatusChange(currentStatus, targetStatus);

					if (ClientUtils.isNotTrue(validation.left)) {
						// Invalid status change.
						N10N.warn(I18N.CONSTANTS.error(), validation.right);
						callback.onFailure(null);
						return;
					}
				}

				onStatusChangeEvent(currentModel.getId(), currentStatus, targetStatus, callback);
			}
		});

		// --
		// Tabs initialization.
		// --

		for (final IsModelTabPresenter<E, ?> tabPresenter : tabPresenters) {

			tabPresenter.initialize(); // CRUCIAL!

			view.addTab(tabPresenter.getTabTitle(), Widget.asWidgetOrNull(tabPresenter.getView()), new Listener<ComponentEvent>() {

				@Override
				public void handleEvent(final ComponentEvent event) {
					currentTabPresenter = tabPresenter;
					tabPresenter.loadTab(currentModel);
				}
			});
		}

		// Handler

		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(getUpdateEventKey())) {
					// Model update event.
					final E createdModel = event.getParam(0);

					if (createdModel == null) {
						return;
					}

					view.getStore().add(createdModel);
					view.getStore().commitChanges();
					view.getStore().applyFilters(null);
					view.getGrid().getSelectionModel().select(createdModel, false);

				} else if (event.concern(UpdateEvent.LAYOUT_GROUP_UPDATE)) {
					// Layout group update event.
					loadModel(currentModel.getId()); // Reloads the entire model and updates the view.

				} else if (event.concern(UpdateEvent.FLEXIBLE_ELEMENT_UPDATE) && event.getParam(0) instanceof IsModel) {
					// Flexible element (field) update event.
					final E updatedModel = event.getParam(0);
					updateModel(updatedModel);

				} else if (event.concern(UpdateEvent.PROJECT_MODEL_IMPORT) || event.concern(UpdateEvent.ORG_UNIT_MODEL_IMPORT)) {
					// Import update event.
					loadModels();
				}
			}
		}));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void onPageRequest(final PageRequest request) {

		// --
		// Loads the models grid.
		// --

		loadModels();

		// --
		// Loads the details of a specific model or resets the view.
		// --

		loadModel(request.getParameterInteger(RequestParameter.ID));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final boolean hasValueChanged() {

		if (view.getHeaderForm().isValueHasChanged()) {
			return true;
		}

		for (final IsModelTabPresenter<E, ?> tabPresenter : tabPresenters) {
			if (tabPresenter.hasValueChanged()) {
				return true;
			}
		}

		return false;
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// ABSTRACT METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Returns the type of model to export
	 * 
	 * @return AdminUtil
	 */
	protected abstract String getModelToImport();

	/**
	 * Returns the page corresponding to the <em>add model</em> presenter.
	 * 
	 * @return The page corresponding to the <em>add model</em> presenter.
	 */
	protected abstract Page getAddModelPage();

	/**
	 * Returns the presenter corresponding update event key.
	 * 
	 * @return The presenter corresponding update event key.
	 */
	protected abstract String getUpdateEventKey();

	/**
	 * Returns the command loading the models entities with <b>light</b> mapping.
	 */
	protected abstract Command<ListResult<E>> getLoadModelsCommand();

	/**
	 * Returns the command loading the given {@code modelId} corresponding model entity with <b>full</b> mapping.
	 * 
	 * @param modelId
	 *          The model id.
	 */
	protected abstract Command<E> getLoadModelCommand(final Integer modelId);

	/**
	 * Callback executed on status change event.<br>
	 * Global status change validation has already been processed (see
	 * {@link ProjectModelStatus#isValidStatusChange(ProjectModelStatus, ProjectModelStatus)}).
	 * 
	 * @param modelId
	 *          The current model id.
	 * @param currentStatus
	 *          The current model status.
	 * @param targetStatus
	 *          The target selected status.
	 * @param callback
	 *          The callback to execute in case the select event is denied. If so, execute
	 *          {@code callback.onFailure(null)} (throwable is not used).
	 */
	protected abstract void onStatusChangeEvent(final Integer modelId, final ProjectModelStatus currentStatus, final ProjectModelStatus targetStatus,
			final AsyncCallback<Void> callback);

	/**
	 * Method executed on header form save button action.<br>
	 * Form validation has already been processed.
	 * 
	 * @param currentModel
	 *          The current model to save.
	 * @param callback
	 *          The callback that should be executed once save action has been processed (never {@code null}). The success
	 *          argument should be the updated model.
	 */
	protected abstract void onSaveAction(final E currentModel, final AsyncCallback<E> callback);

	/**
	 * Method executed on model <b>duplicate</b> action.
	 * 
	 * @param model
	 *          The model to duplicate.
	 */
	protected abstract void onDuplicateAction(final E model);

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the models and populates the corresponding store.
	 */
	private void loadModels() {

		view.getStore().removeAll();

		dispatch.execute(getLoadModelsCommand(), new CommandResultHandler<ListResult<E>>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminboard(), I18N.CONSTANTS.adminProblemLoading());
			}

			@Override
			public void onCommandSuccess(final ListResult<E> result) {

				if (result == null || result.isEmpty()) {
					return;
				}

				view.getStore().add(result.getList());
				view.getStore().commitChanges();
			}
		}, view.getGridMask());
	}

	/**
	 * Updates the current model with the given {@code updatedModel}.
	 * 
	 * @param updatedModel
	 *          The updated model.
	 */
	protected final void updateModel(final E updatedModel) {
		currentModel = updatedModel;
	}

	/**
	 * Loads the given {@code modelId} corresponding model.
	 * 
	 * @param modelId
	 *          The model id. If {@code null}, resets the view.
	 */
	private void loadModel(final Integer modelId) {

		if (modelId == null) {
			onModelLoaded(null);
			return;
		}

		dispatch.execute(getLoadModelCommand(modelId), new CommandResultHandler<E>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminboard(), I18N.CONSTANTS.adminProblemLoading());
			}

			@Override
			public void onCommandSuccess(final E result) {
				onModelLoaded(result);
			}
		}, view.getDetailsPanelMask());
	}

	/**
	 * Callback executed once the given {@code model} has been loaded.
	 * 
	 * @param model
	 *          The loaded model (with full mapping). If {@code null}, clears the loaded data and view.
	 */
	protected void onModelLoaded(final E model) {

		currentModel = model;
		
		view.setModelGridPanelExpanded(model == null);
		view.loadModel(currentModel);

		if (currentModel == null) {
			return;
		}
		
		loadStatus(currentModel.getStatus());
	}

	/**
	 * Loads the status data based on the given current model {@code status} and populates the corresponding form field.
	 * 
	 * @param status
	 *          The current model status.
	 */
	private void loadStatus(final ProjectModelStatus status) {

		view.getHeaderStatusField().clear();
		view.getHeaderStatusField().getStore().removeAll();

		dispatch.execute(new GetAvailableStatusForModel(currentModel.getModelType(), currentModel.getId(), status),
			new CommandResultHandler<ListResult<ProjectModelStatus>>() {

				@Override
				public void onCommandFailure(final Throwable caught) {
					N10N.warn(I18N.CONSTANTS.adminModelCheckError(), I18N.CONSTANTS.adminModelCheckErrorDetails());
				}

				@Override
				public void onCommandSuccess(final ListResult<ProjectModelStatus> result) {

					boolean onlyDraft = true;

					final List<EnumModel<ProjectModelStatus>> values = new ArrayList<EnumModel<ProjectModelStatus>>();
					for (final ProjectModelStatus status : result.getList()) {
						values.add(new EnumModel<ProjectModelStatus>(status));
						onlyDraft &= status == ProjectModelStatus.DRAFT;
					}

					view.getHeaderStatusField().getStore().add(values);
					view.getHeaderStatusField().setValue(new EnumModel<ProjectModelStatus>(status));

					if (onlyDraft) {
						view.getHeaderStatusField().setTitle(I18N.CONSTANTS.adminOrgUnitModelOfRoot());
					} else {
						view.getHeaderStatusField().setTitle(null);
					}
				}
			});
	}

	/**
	 * Method executed on model <b>export</b> action.
	 * 
	 * @param model
	 *          The model to export.
	 */
	private void onExportAction(final E model) {

		if (model instanceof ProjectModelDTO) {

			final ServletUrlBuilder urlBuilder =
					new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_MODEL_PROJECT);

			urlBuilder.addParameter(RequestParameter.ID, model.getId());

			ClientUtils.launchDownload(urlBuilder.toString());

		} else if (model instanceof OrgUnitModelDTO) {

			final ServletUrlBuilder urlBuilder =
					new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.EXPORT, ServletMethod.EXPORT_MODEL_ORGUNIT);

			urlBuilder.addParameter(RequestParameter.ID, model.getId());

			ClientUtils.launchDownload(urlBuilder.toString());

		}

	}

	/**
	 * Method executed on model <b>delete</b> action.
	 * 
	 * @param model
	 *          The model to delete.
	 */
	private void onDeleteAction(final E model) {

		if (model == null) {
			return;
		}

		final String errorMessage, confirmationMessage, notificationTitle, notificationContent;

		switch (model.getModelType()) {

			case ProjectModel:
				errorMessage = I18N.CONSTANTS.deleteNotDraftProjectModelError();
				confirmationMessage = I18N.CONSTANTS.deleteDraftProjectModelConfirm();
				notificationTitle = I18N.CONSTANTS.adminProjectModelDelete();
				notificationContent = I18N.CONSTANTS.adminProjectModelDeleteDetail();
				break;

			case OrgUnitModel:
				errorMessage = I18N.CONSTANTS.deleteNotDraftOrgUnitModelError();
				confirmationMessage = I18N.CONSTANTS.deleteDraftOrgUnitModelConfirm();
				notificationTitle = I18N.CONSTANTS.adminOrgUnitModelDelete();
				notificationContent = I18N.CONSTANTS.adminOrgUnitModelDeleteDetail();
				break;

			default:
				throw new UnsupportedOperationException("Invalid model type.");
		}

		// Only models with "DRAFT" status can be deleted.
		if (model.getStatus() != ProjectModelStatus.DRAFT) {
			N10N.warn(I18N.CONSTANTS.deletionError(), errorMessage);
			return;
		}

		// Confirmation message.
		N10N.confirmation(I18N.MESSAGES.adminDeleteDraftModel(model.getName()), confirmationMessage, new ConfirmCallback() {

			/**
			 * On OK.
			 */
			@Override
			public void onAction() {

				final Delete deleteCommand = new Delete(model.getEntityName(), model.getId());
				deleteCommand.setProjectModelStatus(model.getStatus());

				dispatch.execute(deleteCommand, new CommandResultHandler<VoidResult>() {

					@Override
					public void onCommandFailure(final Throwable caught) {
						N10N.error(I18N.CONSTANTS.deletionError(), I18N.MESSAGES.entityDeleteEventError(model.getName()));
					}

					@Override
					public void onCommandSuccess(final VoidResult result) {

						// Remove the project model from the store.
						view.getStore().remove(model);
						view.getStore().commitChanges();

						// Show notification.
						N10N.infoNotif(notificationTitle, notificationContent);
					}
				}, view.getGridDeleteButton());
			}
		});
	}
}
