package org.sigmah.client.ui.view.pivot;

import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetSites;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.command.result.SiteResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.pivot.model.PivotTableElement;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.SiteHandler;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.view.pivot.table.PivotGridCellEvent;
import org.sigmah.client.ui.view.pivot.table.PivotGridHeaderEvent;
import org.sigmah.client.ui.view.pivot.table.PivotGridHeaderEvent.IconTarget;
import org.sigmah.client.ui.view.pivot.table.PivotGridPanel;
import org.sigmah.client.ui.view.pivot.table.PivotGridPanel.PivotTableRow;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.SplitButton;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.GWTDates;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.dto.pivot.content.EntityCategory;
import org.sigmah.shared.dto.pivot.content.IStateManager;
import org.sigmah.shared.dto.pivot.content.MonthCategory;
import org.sigmah.shared.dto.pivot.content.PivotContent;
import org.sigmah.shared.dto.pivot.content.PivotTableData;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.util.Filter;
import org.sigmah.shared.util.ProfileUtils;

public class ProjectPivotContainer extends ContentPanel implements Loadable {

	private final EventBus eventBus;
	private final DispatchAsync dispatcher;
	
	private final List<HandlerRegistration> registrations;

	private Authentication authentication;
	
	private final PivotGridPanel gridPanel;

	private final IStateManager stateManager;

	private int currentDatabaseId;
	private final HistorySelector historySelector;

	private LayoutComposer composer;

	private ToolBar toolBar;
	private SplitButton saveButton;
	private SeparatorToolItem saveButtonSeparator;
	private ComboBox<SiteDTO> siteFilter;
	private ComboBox<IndicatorDTO> indicatorFilter;
	private ComboBox<DateRangeModel> dateFilter;
	private CheckBox defaultViewCheckBox;
	
	private boolean updated;
	
	private boolean loading;

	/**
	 * Keeps track of the user's last choice regarding axis swapping, to be used
	 * when a new date is filtered
	 */
	private boolean lastAxesSwapped = false;

	private PivotLayout currentLayout;
	private PivotTableElement currentPivot;
	
	@Inject
	public ProjectPivotContainer(EventBus eventBus, DispatchAsync dispatcher, PivotGridPanel gridPanel, IStateManager stateManager) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.stateManager = stateManager;
		this.gridPanel = gridPanel;
		this.updated = false;
		this.registrations = new ArrayList<HandlerRegistration>();

		ProjectPivotResources.INSTANCE.style().ensureInjected();

		setLayout(new FitLayout());
		setHeadingText(I18N.CONSTANTS.indicators());
		
		gridPanel.setHeaderVisible(false);

		gridPanel.addListener(Events.HeaderClick, new Listener<PivotGridHeaderEvent>() {

			@Override
			public void handleEvent(PivotGridHeaderEvent be) {
				onHeaderClicked(be);
			}
		});

		gridPanel.addListener(Events.AfterEdit, new Listener<PivotGridCellEvent>() {

			@Override
			public void handleEvent(PivotGridCellEvent event) {
				onCellEdited(event);
			}
		});

		gridPanel.getStore().addListener(Events.Update, new Listener<StoreEvent<PivotGridPanel.PivotTableRow>>() {

			@Override
			public void handleEvent(StoreEvent<PivotTableRow> be) {
				setDirty(true);
			}
		});

		add(gridPanel);
		
		historySelector = new HistorySelector();

		createToolBar();
		createFilterListeners();
		
		gridPanel.addListener(Events.CellClick, new Listener<PivotGridCellEvent>() {

			@Override
			public void handleEvent(PivotGridCellEvent event) {
				if(!ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_INDICATOR)) {
					event.stopEvent();

					event.getGrid().addListener(Events.BeforeEdit, new Listener<BaseEvent>() {

						@Override
						public void handleEvent(BaseEvent be) {
							be.setCancelled(true);
						}
					});
				}
			}
		});

		setTopComponent(toolBar);
	}
	
	// --
	// Loadable implementation.
	// --
	
	@Override
	public void setLoading(boolean loading) {
		this.loading = loading;
		
		if(loading) {
			mask(I18N.CONSTANTS.loading());
		} else {
			unmask();
		}
	}

	@Override
	public boolean isLoading() {
		return loading;
	}
	
	// --
	// Public API.
	// --
	
	public void onPageRequest(Authentication authentication, ProjectDTO project) {
		this.authentication = authentication;
		setDirty(false);
		
		loadProject(project);
		
		gridPanel.setHeaderDecoratorEditable(ProfileUtils.isGranted(authentication, GlobalPermissionEnum.MANAGE_INDICATOR) ||
			ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_INDICATOR));
		
		saveButton.setVisible(ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_INDICATOR));
		
		registerChangeListeners();
	}
	
	public void onPageChange() {
		unregisterChangeListeners();
	}

	public SplitButton getSaveButton() {
		return saveButton;
	}

	public PivotGridPanel getGridPanel() {
		return gridPanel;
	}
	
	public void setDirty(boolean dirty) {
		saveButton.setEnabled(dirty);
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
	
	// ---
	// Private methods.
	// ---
	
	private void createToolBar() {
		// Save button.
		saveButton = Forms.saveSplitButton();
		saveButtonSeparator = new SeparatorToolItem();

		// Site filter combo box.
		siteFilter = new SiteFilterCombo(dispatcher);
		
		// Indicator filter combo box.
		indicatorFilter = new IndicatorFilterCombo(dispatcher);
		
		// Date filter combo box.
		dateFilter = new DateFilterCombo();
		
		// Set as default view checkbox.
		defaultViewCheckBox = new CheckBox();
		defaultViewCheckBox.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent event) {
				onDefaultCheck(event);
			}
		});

		final Label defaultViewLabel = new Label(I18N.CONSTANTS.defaultView());
		defaultViewLabel.setLabelFor(defaultViewCheckBox.getId());
		
		// Creating the tool bar.
		toolBar = new ToolBar();
		toolBar.addStyleName(ProjectPivotResources.INSTANCE.style().toolbar());
		toolBar.add(saveButton);
		toolBar.add(saveButtonSeparator);
		toolBar.add(new Label(I18N.CONSTANTS.site()));
		toolBar.add(siteFilter);
		toolBar.add(new Label(I18N.CONSTANTS.indicator()));
		toolBar.add(indicatorFilter);
		toolBar.add(new Label(I18N.CONSTANTS.indicatorFilterToolBarLabel()));
		toolBar.add(dateFilter);
		toolBar.add(new FillToolItem());
		toolBar.add(historySelector.getPrevButton());
		toolBar.add(historySelector.getNextButton());
		toolBar.add(defaultViewLabel);
		toolBar.add(defaultViewCheckBox);
	}
	
	private void createFilterListeners() {
		indicatorFilter.addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent event) {
				onIndicatorSelected();
			}
		});
		siteFilter.addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {
				onSiteSelected();
			}
		});

		dateFilter.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				onDateSelected();
			}
		});

		historySelector.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				historyValueChange(historySelector.getLayouts().get(event.getValue()));
			}
		});

		defaultViewCheckBox.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent event) {
				onDefaultCheck(event);
			}
		});
	}
	
	private void registerChangeListeners() {
		registrations.add(eventBus.addHandler(SiteEvent.getType(), new SiteHandler() {

			@Override
			public void handleEvent(SiteEvent siteEvent) {
				refresh();
			}
		}));
		registrations.add(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(UpdateEvent event) {
				if(event.concern(UpdateEvent.INDICATOR_UPDATED)) {
					refresh();
				}
			}
		}));
	}
	
	private void unregisterChangeListeners() {
		for(final HandlerRegistration registration : registrations) {
			registration.removeHandler();
		}
		registrations.clear();
	}

	public void loadProject(ProjectDTO project) {
		this.currentDatabaseId = project.getId();
		((IndicatorFilterCombo)indicatorFilter).setDatabaseId(currentDatabaseId);
		((SiteFilterCombo)siteFilter).setDatabaseId(currentDatabaseId);
		((DateFilterCombo)dateFilter).fillMonths(project.getStartDate());
		composer = new LayoutComposer(new GWTDates(), project);

		pivotToDefault();
	}

	private void pivotToDefault() {
		String defaultLayoutId = (String) stateManager.get(defaultPivotStateKey());
		if (defaultLayoutId == null) {
			pivotToImplictDefault();
		} else {
			PivotLayout.deserialize(dispatcher, currentDatabaseId, defaultLayoutId, new AsyncCallback<PivotLayout>() {

				@Override
				public void onSuccess(PivotLayout result) {
					historySelector.onNewLayout(result);
					pivotTo(result);
				}

				@Override
				public void onFailure(Throwable caught) {
					pivotToImplictDefault();
				}
			});
		}
	}

	private void pivotToImplictDefault() {
		dateFilter.setValue(dateFilter.getStore().getAt(0));
		onDateSelected();
	}

	/**
	 * Handle when the user selects an indicator from the filter
	 */
	private void onIndicatorSelected() {

		final IndicatorDTO indicator = indicatorFilter.getValue();
		IndicatorLayout layout = new IndicatorLayout(indicator);
		historySelector.onNewLayout(layout);
		pivotTo(layout);
	}

	/**
	 * Handle user selection of the site filter combo
	 */
	private void onSiteSelected() {

		final SiteDTO site = (SiteDTO) siteFilter.getValue();
		SiteLayout layout = new SiteLayout(site);
		historySelector.onNewLayout(layout);
		pivotTo(layout);
	}

	/**
	 * Handle user selection of the date combo
	 */
	private void onDateSelected() {

		final DateRangeModel dateRangeModel = dateFilter.getValue();
		DateLayout dateLayout = new DateLayout(dateRangeModel, lastAxesSwapped);
		historySelector.onNewLayout(dateLayout);

		pivotTo(dateLayout);

	}

	private void pivotTo(IndicatorLayout layout) {
		siteFilter.clear();
		dateFilter.clear();

		setHeadingText(I18N.MESSAGES.projectPivotByIndicator(layout.getIndicator().getName()));
		gridPanel.setShowSwapIcon(false);

		PivotTableElement pivot = composer.fixIndicator(layout.getIndicator().getId());
		loadPivot(pivot);

		onLayoutChanged(layout);
	}

	private void pivotTo(SiteLayout layout) {
		indicatorFilter.clear();
		dateFilter.clear();

		PivotTableElement pivot = composer.fixSite(layout.getSite().getId());
		setHeadingText(I18N.MESSAGES.projectPivotBySite(layout.getSite().getLocationName()));
		gridPanel.setShowSwapIcon(false);

		loadPivot(pivot);

		onLayoutChanged(layout);
	}

	private void pivotTo(DateLayout layout) {
		indicatorFilter.clear();
		siteFilter.clear();
		setHeadingText(I18N.MESSAGES.projectPivotByMonth(layout.getModel().getLabel()));
		gridPanel.setShowSwapIcon(true);

		PivotTableElement pivot = composer.fixDateRange(layout.getDateRange(), layout.getAxesSwapped());

		loadPivot(pivot);

		lastAxesSwapped = layout.getAxesSwapped();

		onLayoutChanged(layout);
	}

	private void onLayoutChanged(PivotLayout layout) {
		currentLayout = layout;
		defaultViewCheckBox.setValue(
			layout.serialize().equals(stateManager.get(defaultPivotStateKey()))
		);
	}

	private void onHeaderClicked(PivotGridHeaderEvent event) {
		if (event.getIconTarget() == IconTarget.ZOOM) {
			onZoom(event);
		} else if (event.getIconTarget() == IconTarget.EDIT) {
			PivotTableData.Axis axis = event.getAxis();
			if (axis.getDimension().getType() == DimensionType.Indicator) {
				editIndicator(((EntityCategory) axis.getCategory()).getId());
			}
		} else if (event.getIconTarget() == IconTarget.SWAP) {
			swapAxes();
		}
	}

	private void swapAxes() {
		if (currentLayout instanceof DateLayout) {
			DateLayout newLayout = ((DateLayout) currentLayout).swapAxes();
			historySelector.onNewLayout(newLayout);
			pivotTo(newLayout);
		}
	}

	private void onZoom(PivotGridHeaderEvent event) {
		PivotTableData.Axis axis = event.getAxis();
		if (axis.getDimension().getType() == DimensionType.Site) {
			SiteDTO site = new SiteDTO();
			site.setId(((EntityCategory) axis.getCategory()).getId());
			site.setLocationName(axis.getLabel());

			siteFilter.setValue(site);
			onSiteSelected();

		} else if (axis.getDimension().getType() == DimensionType.Indicator) {
			IndicatorDTO indicator = new IndicatorDTO();
			indicator.setId(((EntityCategory) axis.getCategory()).getId());
			indicator.setName(axis.getLabel());

			indicatorFilter.setValue(indicator);
			onIndicatorSelected();
		} else if (axis.getDimension().getType() == DimensionType.Date) {
			if (axis.getCategory() instanceof MonthCategory) {
				DateRangeModel model = DateFilterCombo.monthModel((MonthCategory) axis.getCategory());
				dateFilter.setValue(model);
				onDateSelected();
			}
		}
	}

	private void editIndicator(final int id) {
		if (!ProfileUtils.isGranted(authentication, GlobalPermissionEnum.MANAGE_INDICATOR)
		                && !ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_INDICATOR))
			return;
		dispatcher.execute(new GetIndicators(this.currentDatabaseId), new CommandResultHandler<IndicatorListResult>() {

			@Override
			protected void onCommandSuccess(IndicatorListResult result) {
				for (IndicatorDTO indicator : result.getData()) {
					if (indicator.getId() == id) {
						editIndicator(indicator);
					}
				}
			}

		}, this);
	}

	protected void editIndicator(IndicatorDTO indicator) {
		eventBus.navigateRequest(Page.INDICATOR_EDIT.request()
			.addParameter(RequestParameter.ID, currentDatabaseId)
			.addData(RequestParameter.MODEL, indicator), this);
	}

	private void onCellEdited(final PivotGridCellEvent event) {
		if (ProfileUtils.isGranted(authentication, GlobalPermissionEnum.EDIT_INDICATOR)) {
			this.updated = true;
			setDirty(true);
			if (event.getCell() != null) {
				if (event.getCell().getCount() > 1) {
					N10N.confirmation(I18N.CONSTANTS.confirmUpdate(), I18N.CONSTANTS.confirmUpdateOfAggregatedCell(), new ConfirmCallback() {

						@Override
						public void onAction() {
							event.getRecord().set(event.getProperty(), event.getCell().getValue());
						}
					});
				}
			}
		}
	}

	private void historyValueChange(PivotLayout layout) {
		pivotTo(layout);
	}

	private void pivotTo(PivotLayout layout) {
		if (layout instanceof SiteLayout) {
			siteFilter.setValue(((SiteLayout) layout).getSite());
			pivotTo((SiteLayout) layout);

		} else if (layout instanceof IndicatorLayout) {
			indicatorFilter.setValue(((IndicatorLayout) layout).getIndicator());
			pivotTo((IndicatorLayout) layout);

		} else if (layout instanceof DateLayout) {
			dateFilter.setValue(((DateLayout) layout).getModel());
			pivotTo((DateLayout) layout);
		}
	}

	private void refresh() {
		loadPivot(currentPivot);
	}

	private void onDefaultCheck(FieldEvent event) {

		final boolean newValue = defaultViewCheckBox.getValue();

		N10N.confirmation(I18N.CONSTANTS.defaultView(), I18N.CONSTANTS.confirmDefaultViewChange(), new ConfirmCallback() {

			@Override
			public void onAction() {
				persistDefaultView(newValue);
			}
		}, new ConfirmCallback() {

			@Override
			public void onAction() {
				defaultViewCheckBox.setValue(!newValue);
			}
		});
	}

	private void persistDefaultView(boolean newValue) {
		if (newValue) {
			stateManager.set(defaultPivotStateKey(), currentLayout.serialize());
			Info.display(I18N.CONSTANTS.saved(), I18N.CONSTANTS.defaultViewChanged());
		} else {
			// don't allow unchecking: there is not really a logical action to
			// take because there always needs to be a default view
			defaultViewCheckBox.setValue(true);
		}
	}

	private String defaultPivotStateKey() {
		return "ProjectPivotDefault" + currentDatabaseId;
	}

	private void loadPivot(final PivotTableElement pivot) {
		currentPivot = pivot;
		dispatcher.execute(new GenerateElement<PivotContent>(pivot), new AsyncCallback<PivotContent>() {

			@Override
			public void onFailure(Throwable caught) {
				gridPanel.clear();
				Log.debug("Pivot failed", caught);
			}

			@Override
			public void onSuccess(PivotContent content) {
				if(content != null) {
					pivot.setContent(content);
					gridPanel.setValue(pivot);
				}
			}
		}, this);
	}

	public void save() {
		dispatcher.execute(gridPanel.composeSaveCommand(), new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandSuccess(ListResult<Result> result) {
					gridPanel.getStore().commitChanges();
					setDirty(false);
					eventBus.fireEvent(new UpdateEvent(UpdateEvent.INDICATOR_UPDATED, ProjectPivotContainer.this));
				}
		}, this);
	}

	public void discard() {
		gridPanel.getStore().rejectChanges();
		setDirty(false);
	}
	
	public void viewDidAppear() {
		if (gridPanel.hasIndicatorsInStore()) {
			Filter filter = new Filter();
			filter.addRestriction(DimensionType.Database, currentDatabaseId);

			GetSites request = new GetSites();
			request.setFilter(filter);

			dispatcher.execute(request, new CommandResultHandler<SiteResult>() {

				@Override
				protected void onCommandSuccess(SiteResult result) {
					if (result.getData().isEmpty()) {
						N10N.info(I18N.CONSTANTS.projectWithNoSitesWarning());
					}
				}
			}, this);
		}
	}

	public boolean hasValueChanged() {
		return updated;
	}

	public void forgetAllChangedValues() {
		updated = false;
		discard();
	}

}
