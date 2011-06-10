package org.sigmah.client.page.project.pivot;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.event.IndicatorEvent;
import org.sigmah.client.event.ProjectEvent;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.toolbar.ActionListener;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.client.page.config.design.IndicatorDialog;
import org.sigmah.client.page.project.ProjectSubPresenter;
import org.sigmah.client.page.table.PivotGridCellEvent;
import org.sigmah.client.page.table.PivotGridHeaderEvent;
import org.sigmah.client.page.table.PivotGridHeaderEvent.IconTarget;
import org.sigmah.client.page.table.PivotGridPanel;
import org.sigmah.client.page.table.PivotGridPanel.PivotTableRow;
import org.sigmah.client.util.DateUtilGWTImpl;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.MonthCategory;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotTableElement;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class ProjectPivotContainer extends ContentPanel implements ProjectSubPresenter, ActionListener {

	private final EventBus eventBus;
	private final Dispatcher dispatcher;

	private final IndicatorFilterCombo indicatorFilter;
	private final SiteFilterCombo siteFilter;
	private final DateFilterCombo dateFilter;
	private final PivotGridPanel gridPanel;
	private final Provider<IndicatorDialog> indicatorDialogProvider;

	private final IStateManager stateManager;
	
	private int currentDatabaseId;
	private HistorySelector historySelector;

	private LayoutComposer composer;

	private ActionToolBar toolBar;
	
	/**
	 * Keeps track of the user's last choice regarding axis swapping,
	 * to be used when a new date is filtered
	 */
	private boolean lastAxesSwapped = false;
	
	private PivotLayout currentLayout;
	private PivotTableElement currentPivot;
	private CheckBox defaultViewCheckBox;
	
	private Listener<BaseEvent> changeListener;
	private EventType[] changeEventTypes = new EventType[] { 
			IndicatorEvent.CHANGED,
			SiteEvent.CREATED,
			SiteEvent.UPDATED, 
			ProjectEvent.CHANGED };
	
	@Inject
	public ProjectPivotContainer(EventBus eventBus, Dispatcher dispatcher, PivotGridPanel gridPanel, 
			Provider<IndicatorDialog> indicatorDialog, IStateManager stateManager) {
		this.dispatcher = dispatcher;
		this.eventBus = eventBus;
		this.stateManager = stateManager;
		this.gridPanel = gridPanel;
		this.indicatorDialogProvider = indicatorDialog;
		
		ProjectPivotResources.INSTANCE.style().ensureInjected();

		setHeaderVisible(false);
		setLayout(new FitLayout());
		gridPanel.setHeading(I18N.CONSTANTS.indicators());
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
				toolBar.setDirty(true);
			}
		});
		add(gridPanel);

		indicatorFilter = new IndicatorFilterCombo(dispatcher);
		indicatorFilter.addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent event) {
				onIndicatorSelected();
			}
		});
		siteFilter = new SiteFilterCombo(dispatcher);
		siteFilter.addListener(Events.Select, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent be) {		
				onSiteSelected();
			}
		});

		dateFilter = new DateFilterCombo();
		dateFilter.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				onDateSelected();
			}
		});

		historySelector = new HistorySelector();
		historySelector.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				historyValueChange(historySelector.getLayouts().get(event.getValue()));
			}
		});
		
		defaultViewCheckBox = new CheckBox();
		defaultViewCheckBox.addListener(Events.OnClick, new Listener<FieldEvent>() {

			@Override
			public void handleEvent(FieldEvent event) {
				onDefaultCheck(event);
			}
			
		});
		
		Label defaultViewLabel = new Label(I18N.CONSTANTS.defaultView());
		defaultViewLabel.setLabelFor(defaultViewCheckBox.getId());

		toolBar = new ActionToolBar();
		toolBar.addStyleName(ProjectPivotResources.INSTANCE.style().toolbar());
		toolBar.addSaveSplitButton();
		toolBar.setDirty(false);
		
		toolBar.add(new SeparatorToolItem());
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
		toolBar.setListener(this);
		toolBar.setDirty(false);
		setTopComponent(toolBar);
		
		registerChangeListeners();
	}

	private void registerChangeListeners() {
		changeListener = new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				if(be.getSource() != ProjectPivotContainer.this) {
					refresh();
				}
			}
		};	
		for(EventType eventType : changeEventTypes) {
			eventBus.addListener(eventType, changeListener);
		}
	}

	private void unregisterChangeListeners() {
		for(EventType eventType : changeEventTypes) {
			eventBus.removeListener(eventType, changeListener);
		}
	}
	
	@Override
	public void loadProject(ProjectDTO project) {
		this.currentDatabaseId = project.getId();
		indicatorFilter.setDatabaseId(currentDatabaseId);
		siteFilter.setDatabaseId(currentDatabaseId);
		dateFilter.fillMonths(project.getStartDate());
		composer = new LayoutComposer(new DateUtilGWTImpl(), project);
		
		pivotToDefault();
	}
	
	private void pivotToDefault() {
		String defaultLayoutId = (String) stateManager.get(defaultPivotStateKey());
		if(defaultLayoutId == null) {
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
	
	private void onProjectChanged() {
		dispatcher.execute(new GetProject(currentDatabaseId), new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()),
				new AsyncCallback<ProjectDTO>() {

			@Override
			public void onFailure(Throwable caught) {
			}
	
			@Override
			public void onSuccess(ProjectDTO project) {
				dateFilter.fillMonths(project.getStartDate());			
			}
		});
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

		final SiteDTO site = (SiteDTO)siteFilter.getValue();
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

		gridPanel.setHeading(I18N.MESSAGES.projectPivotByIndicator(layout.getIndicator().getName()));
		gridPanel.setShowSwapIcon(false);

		PivotTableElement pivot = composer.fixIndicator(layout.getIndicator().getId());
		loadPivot(pivot);

		onLayoutChanged(layout);
	}

	
	private void pivotTo(SiteLayout layout) {
		indicatorFilter.clear();
		dateFilter.clear();
		
		PivotTableElement pivot = composer.fixSite(layout.getSite().getId());
		gridPanel.setHeading(I18N.MESSAGES.projectPivotBySite(layout.getSite().getLocationName()));
		gridPanel.setShowSwapIcon(false);
		
		loadPivot(pivot);
		
		onLayoutChanged(layout);
	}

	private void pivotTo(DateLayout layout) {
		indicatorFilter.clear();
		siteFilter.clear();
		gridPanel.setHeading(I18N.MESSAGES.projectPivotByMonth(layout.getModel().getLabel()));
		gridPanel.setShowSwapIcon(true);
		
		PivotTableElement pivot = composer.fixDateRange(layout.getDateRange(), layout.getAxesSwapped());

		loadPivot(pivot);
		
		lastAxesSwapped = layout.getAxesSwapped();
		
		onLayoutChanged(layout);
	}
	

	private void onLayoutChanged(PivotLayout layout) {
		currentLayout = layout;
		if(layout.serialize().equals(stateManager.get(defaultPivotStateKey()))) {
			defaultViewCheckBox.setValue(true);
		} else {
			defaultViewCheckBox.setValue(false);
		}
	}
	
	private void onHeaderClicked(PivotGridHeaderEvent event) {
		Log.debug("Header clicked : " + event.getAxis());

		if(event.getIconTarget() == IconTarget.ZOOM) {
			onZoom(event);
		} else if(event.getIconTarget() == IconTarget.EDIT) {
			Axis axis = event.getAxis();
			if(axis.getDimension().getType() == DimensionType.Indicator) {
				editIndicator( ((EntityCategory)axis.getCategory()).getId() );
			}
		} else if(event.getIconTarget() == IconTarget.SWAP) {
			swapAxes();
		}
	}

	private void swapAxes() {
		if(currentLayout instanceof DateLayout) {
			DateLayout newLayout = ((DateLayout) currentLayout).swapAxes();
			historySelector.onNewLayout(newLayout);
			pivotTo(newLayout);
		}
	}


	private void onZoom(PivotGridHeaderEvent event) {
		Axis axis = event.getAxis();
		if(axis.getDimension().getType() == DimensionType.Site) {
			SiteDTO site = new SiteDTO();
			site.setId( ((EntityCategory)axis.getCategory()).getId() );
			site.setLocationName( axis.getLabel() );

			siteFilter.setValue(site);
			onSiteSelected( );

		} else if(axis.getDimension().getType() == DimensionType.Indicator) {
			IndicatorDTO indicator = new IndicatorDTO();
			indicator.setId( ((EntityCategory)axis.getCategory()).getId() );
			indicator.setName( axis.getLabel() );

			indicatorFilter.setValue(indicator);
			onIndicatorSelected( );
		} else if(axis.getDimension().getType() == DimensionType.Date) {
			if(axis.getCategory() instanceof MonthCategory) {
				DateRangeModel model = DateFilterCombo.monthModel((MonthCategory)axis.getCategory());
				dateFilter.setValue(model);
				onDateSelected();
			}
		}	

	}
	
	private void editIndicator(final int id) {
		dispatcher.execute(GetIndicators.forDatabase(this.currentDatabaseId), new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()), new AsyncCallback<IndicatorListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// monitor
			}

			@Override
			public void onSuccess(IndicatorListResult result) {
				for(IndicatorDTO indicator : result.getData()) {
					if(indicator.getId() == id) {
						editIndicator(indicator);
					}
				}
			}
			
		});
	}

	protected void editIndicator(IndicatorDTO indicator) {
		IndicatorDialog dialog = indicatorDialogProvider.get();
		dialog.show(currentDatabaseId, indicator);
	}

	private void onCellEdited(final PivotGridCellEvent event) {
		toolBar.setDirty(true);
		if(event.getCell() != null) {
			if(event.getCell().getCount() > 1) {
				MessageBox.confirm(I18N.CONSTANTS.confirmUpdate(), I18N.CONSTANTS.confirmUpdateOfAggregatedCell(), new Listener<MessageBoxEvent>() {

					@Override
					public void handleEvent(MessageBoxEvent mbEvent) {
						if(!mbEvent.getButtonClicked().getItemId().equals(MessageBox.OK)) {
							event.getRecord().set(event.getProperty(), event.getCell().getValue());
						}
					}
				});
			}
		}
	}

	private void historyValueChange(PivotLayout layout) {
		pivotTo(layout);
	}


	private void pivotTo(PivotLayout layout) {
		if(layout instanceof SiteLayout) {
			siteFilter.setValue(((SiteLayout) layout).getSite());
			pivotTo((SiteLayout)layout);
		
		} else if(layout instanceof IndicatorLayout) {
			indicatorFilter.setValue(((IndicatorLayout) layout).getIndicator());
			pivotTo((IndicatorLayout)layout);
		
		} else if(layout instanceof DateLayout) {
			dateFilter.setValue(((DateLayout) layout).getModel());
			pivotTo((DateLayout)layout);
		}
	}
	
	private void refresh() {
		loadPivot(currentPivot);
	}


	private void onDefaultCheck(FieldEvent event) {
		if(defaultViewCheckBox.getValue()) {
			stateManager.set(defaultPivotStateKey(), currentLayout.serialize());
			Info.display(I18N.CONSTANTS.saved(), I18N.CONSTANTS.defaultViewChanged());
		} else {
			// don't allow unchecking: there is not really a logical action to take because
			// there always needs to be a default view
			defaultViewCheckBox.setValue(true);
		}
	}
	
	private String defaultPivotStateKey() {
		return "ProjectPivotDefault" + currentDatabaseId;
	}
	
	private void loadPivot(final PivotTableElement pivot) {
		currentPivot = pivot;
		dispatcher.execute(new GenerateElement<PivotContent>(pivot), new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()), 
				new AsyncCallback<PivotContent>() {

			@Override
			public void onFailure(Throwable caught) {
				gridPanel.clear();
				MessageBox.alert("Pivot", "Pivot failed: " + caught.getMessage(), null);
			}

			@Override
			public void onSuccess(PivotContent content) {
				pivot.setContent(content);
				gridPanel.setValue(pivot);
			}
		});
	}

	@Override
	public void onUIAction(String actionId) {
		if(UIActions.save.equals(actionId)) {
			save();
		} else if(UIActions.discardChanges.equals(actionId)) {
			discard();
		}
	}

	private void save() {

		dispatcher.execute(gridPanel.composeSaveCommand(),
				new MaskingAsyncMonitor(this, I18N.CONSTANTS.saving()), new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// handled by monitor
			}

			@Override
			public void onSuccess(BatchResult result) {
				gridPanel.getStore().commitChanges();
				toolBar.setDirty(false);
				eventBus.fireEvent(new IndicatorEvent(
						IndicatorEvent.CHANGED, 
						ProjectPivotContainer.this));
			}
		});
	}
	
	private void discard() {
		gridPanel.getStore().rejectChanges();
		toolBar.setDirty(false);
	}

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public void discardView() {
		unregisterChangeListeners();
	}

	@Override
	public void viewDidAppear() {

	}
}
