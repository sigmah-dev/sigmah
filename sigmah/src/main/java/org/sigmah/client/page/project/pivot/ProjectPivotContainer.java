package org.sigmah.client.page.project.pivot;

import java.util.Date;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.project.ProjectSubPresenter;
import org.sigmah.client.page.table.PivotGridCellEvent;
import org.sigmah.client.page.table.PivotGridHeaderEvent;
import org.sigmah.client.page.table.PivotGridHeaderEvent.IconTarget;
import org.sigmah.client.page.table.PivotGridPanel;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.MonthCategory;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.model.DateRange;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotTableElement;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class ProjectPivotContainer extends ContentPanel implements ProjectSubPresenter {

	private final Dispatcher dispatcher;
	
	private final IndicatorFilterCombo indicatorFilter;
	private final SiteFilterCombo siteFilter;
	private final DateFilterCombo dateFilter;
	private final PivotGridPanel gridPanel;
	
	private int currentDatabaseId;
	private HistorySelector historySelector;
	
	private LayoutComposer composer;
		
	@Inject
	public ProjectPivotContainer(Dispatcher dispatcher, PivotGridPanel gridPanel) {
		this.dispatcher = dispatcher;
		this.gridPanel = gridPanel;
		
		setHeaderVisible(false);
		setLayout(new FitLayout());
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
		historySelector.addValueChangeHandler(new ValueChangeHandler<PivotLayout>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<PivotLayout> event) {
				historyValueChange(event.getValue());
			}
		});
		
		ToolBar toolBar = new ToolBar();
		toolBar.add(historySelector.getPrevButton());
		toolBar.add(historySelector.getNextButton());
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new Label(I18N.CONSTANTS.site()));
		toolBar.add(siteFilter);
		toolBar.add(new Label(I18N.CONSTANTS.indicator()));
		toolBar.add(indicatorFilter);
		toolBar.add(new Label(I18N.CONSTANTS.indicatorFilterToolBarLabel()));
		toolBar.add(dateFilter);
		toolBar.add(new FillToolItem());
		toolBar.add(new Label(I18N.CONSTANTS.defaultView()));
		toolBar.add(new CheckBox());
		setTopComponent(toolBar);
	}
	

	@Override
	public void loadProject(ProjectDTO project) {
		this.currentDatabaseId = project.getId();
		indicatorFilter.setDatabaseId(currentDatabaseId);
		siteFilter.setDatabaseId(currentDatabaseId);
		dateFilter.fillMonths(project.getStartDate());
		composer = new LayoutComposer(project.getId(), new DateRange(project.getStartDate(), new Date()));
		
		dateFilter.setValue(dateFilter.getStore().getAt(0));
		onDateSelected();
	}


	private void onIndicatorSelected() {
		siteFilter.clear();
		dateFilter.clear();
		
		PivotTableElement pivot = composer.fixIndicator(indicatorFilter.getSelectedIndicatorId());
		
		loadPivot(new PivotLayout(indicatorFilter.getValue()), pivot);
	}

	private void onSiteSelected() {
		indicatorFilter.clear();
		dateFilter.clear();
		
	    PivotTableElement pivot = composer.fixSite(siteFilter.getSelectedSiteId());
		
		loadPivot(new PivotLayout(siteFilter.getValue()), pivot);
	}

	private void onDateSelected() {
		indicatorFilter.clear();
		siteFilter.clear();
		
		PivotTableElement pivot = composer.fixDateRange(dateFilter.getValue().getDateRange());
		
		loadPivot(new PivotLayout(dateFilter.getValue()), pivot);
	}

	private void onHeaderClicked(PivotGridHeaderEvent event) {
		Log.debug("Header clicked : " + event.getAxis());
		
		if(event.getIconTarget() == IconTarget.ZOOM) {
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
					DateRangeModel model = DateRangeModel.monthModel((MonthCategory)axis.getCategory());
					dateFilter.setValue(model);
					onDateSelected();
				}
			}
		}
	}
	
	private void onCellEdited(final PivotGridCellEvent event) {
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

	private void historyValueChange(PivotLayout value) {
		if(value.getFilter() instanceof SiteDTO) {
			siteFilter.setValue(value.getFilter());
			onSiteSelected();
		} else if(value.getFilter() instanceof IndicatorDTO) {
			indicatorFilter.setValue((IndicatorDTO) value.getFilter());
		} else if(value.getFilter() instanceof DateRangeModel) {
			dateFilter.setValue((DateRangeModel) value.getFilter());
		}
	}

	private void loadPivot(final PivotLayout layout, final PivotTableElement pivot) {
		dispatcher.execute(new GenerateElement<PivotContent>(pivot), null, new AsyncCallback<PivotContent>() {

			@Override
			public void onFailure(Throwable caught) {
				MessageBox.alert("Pivot", "Pivot failed: " + caught.getMessage(), null);
			}

			@Override
			public void onSuccess(PivotContent content) {
				pivot.setContent(content);
				gridPanel.setValue(pivot);
				historySelector.onNewLayout(layout);
			}
		});
	}
	

	@Override
	public Component getView() {
		return this;
	}

	@Override
	public void discardView() {
		
	}

	@Override
	public void viewDidAppear() {
		// TODO Auto-generated method stub
		
	}

}
