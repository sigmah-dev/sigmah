package org.sigmah.client.ui.presenter.project.indicator;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.dom.client.Element;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.view.project.indicator.ProjectIndicatorManagementView;

import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.project.AbstractProjectPresenter;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.view.project.indicator.IndicatorResources;
import org.sigmah.client.ui.widget.HasTreeGrid;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorElement;
import org.sigmah.shared.dto.IndicatorGroup;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.util.ProfileUtils;

/**
 * Project's indicators management presenter which manages the {@link ProjectIndicatorManagementView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectIndicatorManagementPresenter extends AbstractProjectPresenter<ProjectIndicatorManagementPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ProjectIndicatorManagementView.class)
	public static interface View extends AbstractProjectPresenter.View, HasTreeGrid<IndicatorElement> {
		SplitButton getSaveButton();
		MenuItem getSaveItem();
		MenuItem getDiscardChangesItem();
		Button getNewIndicatorGroupButton();
		Button getNewIndicatorButton();
		Button getDeleteButton();
		Button getRefreshButton();
		Button getExportButton();
		
		ViewPopupInterface getIndicatorGroupPopup();
		FormPanel getIndicatorGroupForm();
		Field<String> getIndicatorGroupNameField();
		Button getIndicatorGroupSaveButton();
		
		void refreshTreeGrid();
		void setEditable(boolean editable);
	}
	
	private Integer currentDatabaseId;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ProjectIndicatorManagementPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.PROJECT_INDICATORS_MANAGEMENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		view.getIndicatorGroupPopup().initialize();
		
		view.getTreeGrid().addListener(Events.CellClick, new Listener<GridEvent<IndicatorElement>>() {

			@Override
			public void handleEvent(GridEvent<IndicatorElement> gridEvent) {
				if(ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MANAGE_INDICATOR) && computeTarget(gridEvent) == Target.LABEL) {
					onEditIndicator((IndicatorDTO)gridEvent.getModel());
				}
			}
		});
		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onSave();
			}
		});
		view.getSaveItem().addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				onSave();
			}
		});
		view.getDiscardChangesItem().addSelectionListener(new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				onDiscardChanges();
			}
		});
		view.getNewIndicatorGroupButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				view.getIndicatorGroupForm().reset();
				view.getIndicatorGroupPopup().center();
			}
		});
		view.getIndicatorGroupSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onNewIndicatorGroup();
			}
		});
		
		view.getNewIndicatorButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onNewIndicator();
			}
		});
		
		view.getDeleteButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onDelete();
			}
		});
		
		view.getRefreshButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				doLoad();
			}
		});
		
		view.getExportButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				// BUGFIX #674: Calling the "export" method when the user clicks on the export button.
				onExport();
			}
		});
		
		eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(UpdateEvent event) {
				if(event.concern(UpdateEvent.INDICATOR_UPDATED)) {
					doLoad();
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		currentDatabaseId = Integer.valueOf(request.getParameter(RequestParameter.ID));
		
		final boolean canManageIndicators = ProfileUtils.isGranted(auth(), GlobalPermissionEnum.MANAGE_INDICATOR);
		view.getSaveButton().setVisible(canManageIndicators);
		view.getNewIndicatorGroupButton().setVisible(canManageIndicators);
		view.getNewIndicatorButton().setVisible(canManageIndicators);
		view.getDeleteButton().setVisible(canManageIndicators);
		view.setEditable(canManageIndicators);
		
		doLoad();
	}

	@Override
	protected boolean hasValueChanged() {
		return !view.getStore().getModifiedRecords().isEmpty();
	}

	private void doLoad() {
		dispatch.execute(new GetIndicators(currentDatabaseId), new CommandResultHandler<IndicatorListResult>() {

			@Override
			protected void onCommandSuccess(IndicatorListResult result) {
				final TreeStore<IndicatorElement> treeStore = view.getStore();
				treeStore.removeAll();

				for (IndicatorGroup group : result.getGroups()) {
					treeStore.add(group, false);
					for (IndicatorDTO indicator : group.getIndicators()) {
						treeStore.add(group, indicator, false);
					}
				}

				if (!result.getUngroupedIndicators().isEmpty()) {
					treeStore.add((List) result.getUngroupedIndicators(), false);
				}

				view.refreshTreeGrid();
			}

		}, new LoadingMask(view.getTreeGrid()));
	}
	
	// --
	// Actions
	// --
	
	private void onSave() {
		final BatchCommand updateCommands = new BatchCommand();
		
		for(final Record record : view.getStore().getModifiedRecords()) {
			if(record.getModel() instanceof EntityDTO) {
				final EntityDTO entity = (EntityDTO) record.getModel();
				final HashMap<String, Object> changes = new HashMap<String, Object>();
				for(final String property : record.getChanges().keySet()) {
					// Retrieves the new value of the property.
					// (record.getChanges() only retrieves the original value).
					changes.put(property, record.get(property));
				}
				
				updateCommands.add(new UpdateEntity(entity, changes));
			}
		}
		
		dispatch.execute(updateCommands, new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandSuccess(ListResult<Result> result) {
				view.getStore().commitChanges();
			}
			
		}, view.getSaveButton());
	}
	
	private void onDiscardChanges() {
		view.getStore().rejectChanges();
	}
	
	private void onNewIndicatorGroup() {
		final HashMap<String, Object> properties = new HashMap<String, Object>();
		properties.put(IndicatorGroup.DATABASE_ID, currentDatabaseId);
		properties.put(IndicatorGroup.NAME, view.getIndicatorGroupNameField().getValue());
		dispatch.execute(new CreateEntity(IndicatorGroup.ENTITY_NAME, properties), new CommandResultHandler<CreateResult>() {

			@Override
			protected void onCommandSuccess(CreateResult result) {
				view.getIndicatorGroupPopup().hide();
				doLoad();
			}
			
		}, view.getNewIndicatorGroupButton(), view.getIndicatorGroupSaveButton());
	}
	
	private void onNewIndicator() {
		eventBus.navigateRequest(Page.INDICATOR_EDIT
			.requestWith(RequestParameter.ID, currentDatabaseId));
	}
	
	private void onEditIndicator(IndicatorDTO indicatorDTO) {
		eventBus.navigateRequest(Page.INDICATOR_EDIT.request()
			.addParameter(RequestParameter.ID, currentDatabaseId)
			.addData(RequestParameter.MODEL, indicatorDTO));
	}
	
	private void onDelete() {
		final IndicatorElement selected = getSelectedItem();
		if (selected instanceof IndicatorDTO) {
			N10N.confirmation(I18N.CONSTANTS.delete(), I18N.CONSTANTS.confirmDeleteIndicator(), new ConfirmCallback() {

				@Override
				public void onAction() {
					deleteIndicator((IndicatorDTO) selected);
				}
			});
		} else if (selected instanceof IndicatorGroup) {
			deleteIndicatorGroup((IndicatorGroup) selected);
		}
	}
	
	private void onExport() {
		final ServletUrlBuilder urlBuilder =
				new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), ServletConstants.Servlet.EXPORT, ServletConstants.ServletMethod.EXPORT_PROJECT_INDICATORS);
		
		urlBuilder.addParameter(RequestParameter.ID, getProject().getId());

		ClientUtils.launchDownload(urlBuilder.toString());
	}
	
	private void deleteIndicatorGroup(IndicatorGroup selected) {
		final TreeStore<IndicatorElement> treeStore = view.getStore();
		
		List<IndicatorElement> children = treeStore.getChildren(selected);
		treeStore.remove(selected);
		treeStore.getRecord(selected).set("isDeleted", true);
		// we don't delete the indicators, just move them out of the group
		for (IndicatorElement child : children) {
			treeStore.add(child, false);
			treeStore.getRecord(child).set("groupId", null);
		}
	}
	
	private void deleteIndicator(final IndicatorDTO selected) {
		dispatch.execute(new Delete(selected), new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				view.getStore().remove(selected);
				
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.INDICATOR_REMOVED, selected.getId()));
			}
			
		}, new LoadingMask(view.getTreeGrid(), I18N.CONSTANTS.deleting()));
	}
	
	// --
	// Utility methods
	//--
	
	/**
	 * Retrieve the selected item.
	 * 
	 * @return The selected item.
	 */
	private IndicatorElement getSelectedItem() {
		return view.getTreeGrid().getSelectionModel().getSelectedItem();
	}
	
	/**
	 * Possible click targets for the "name" column.
	 */
	public enum Target {
		MAP_ICON, STAR_ICON, LABEL, NONE
	}
	
	/**
	 * Find the target of a click on the "name" column.
	 * 
	 * @param gridEvent click event on the tree grid.
	 * @return The target of the given event.
	 */
	private Target computeTarget(GridEvent gridEvent) {
		final IndicatorResources.Style css = IndicatorResources.INSTANCE.css();
		
		final Element targetElement = gridEvent.getEvent().getEventTarget().cast();
		final String targetClass = targetElement.getClassName();
		
		if (css.indicatorLabel().equals(targetClass)) {
			return Target.LABEL;
		} else if (css.mapIcon().equals(targetClass) || css.emptyMapIcon().equals(targetClass)) {
			return Target.MAP_ICON;
		} else if (css.emptyStarIcon().equals(targetClass) || css.starIcon().equals(targetClass)) {
			return Target.STAR_ICON;
		} else {
			return Target.NONE;
		}
	}
}
