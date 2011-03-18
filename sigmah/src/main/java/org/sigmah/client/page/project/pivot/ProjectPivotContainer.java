package org.sigmah.client.page.project.pivot;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.project.ProjectSubPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.client.page.table.PivotGridPanel;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotTableElement;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
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
	
	
	@Inject
	public ProjectPivotContainer(Dispatcher dispatcher, PivotGridPanel gridPanel) {
		this.dispatcher = dispatcher;
		this.gridPanel = gridPanel;
		
		setHeaderVisible(false);
		setLayout(new FitLayout());
		gridPanel.setHeaderVisible(false);
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
		
		List<PivotLayout> layouts = new ArrayList<PivotLayout>();
		layouts.add(new SiteByIndicatorLayout(dispatcher, dateFilter, gridPanel));
		layouts.add(new SitesByTimeLayout(dispatcher, dateFilter, gridPanel));
		layouts.add(new IndicatorByTimeLayout(dispatcher, siteFilter, gridPanel));
		
		historySelector = new HistorySelector();
		historySelector.setLayouts(layouts);
		historySelector.addValueChangeHandler(new ValueChangeHandler<PivotLayout>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<PivotLayout> event) {
				layoutChange(event.getValue());
			}
		});
		
		ToolBar toolBar = new ToolBar();
		toolBar.add(historySelector.getPrevButton());
		toolBar.add(historySelector.getNextButton());
		toolBar.add(new SeparatorToolItem());
		toolBar.add(new Label("Site"));
		toolBar.add(siteFilter);
		toolBar.add(new Label(I18N.CONSTANTS.indicator()));
		toolBar.add(indicatorFilter);
		toolBar.add(new Label(I18N.CONSTANTS.indicatorFilterToolBarLabel()));
		toolBar.add(dateFilter);
		toolBar.add(new FillToolItem());
		toolBar.add(new Label("Default view"));
		toolBar.add(new CheckBox());
		setTopComponent(toolBar);
	}
	
	@Override
	public void loadProject(ProjectDTO project) {
		this.currentDatabaseId = project.getId();
		historySelector.setValueByIndex(0);
		indicatorFilter.setDatabaseId(currentDatabaseId);
		siteFilter.setDatabaseId(currentDatabaseId);
		dateFilter.fillMonths(project.getStartDate());
	}

	
	protected void layoutChange(PivotLayout value) {
		value.activate(currentDatabaseId);
	}

	private void onIndicatorSelected() {
		siteFilter.clear();
		dateFilter.clear();
		
		PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		pivot.addRowDimension(new Dimension(DimensionType.Site));
		
		//pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, currentDatabaseId);
		filter.addRestriction(DimensionType.Indicator, indicatorFilter.getSelectedIndicatorId());
		pivot.setFilter(filter);
		
		loadPivot(pivot);
	}
	
	private void onSiteSelected() {
		indicatorFilter.clear();
		dateFilter.clear();
		
	    PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		pivot.addRowDimension(new Dimension(DimensionType.IndicatorCategory));
		pivot.addRowDimension(new Dimension(DimensionType.Indicator));
		
		pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		//pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, currentDatabaseId);
		filter.addRestriction(DimensionType.Site, siteFilter.getSelectedSiteId());
		pivot.setFilter(filter);
		
		loadPivot(pivot);
	}


	private void onDateSelected() {
		indicatorFilter.clear();
		siteFilter.clear();
		
		PivotTableElement pivot = new PivotTableElement();
		pivot.setShowEmptyCells(true);

		pivot.addColDimension(new Dimension(DimensionType.IndicatorCategory));
		pivot.addColDimension(new Dimension(DimensionType.Indicator));
		pivot.addRowDimension(new Dimension(DimensionType.Site));
		
		pivot.addColDimension(new DateDimension(DateUnit.YEAR));
		//pivot.addColDimension(new DateDimension(DateUnit.MONTH));
		
		Filter filter = new Filter();
		filter.addRestriction(DimensionType.Database, currentDatabaseId);
		filter.setDateRange(dateFilter.getSelectedDateRange());
		pivot.setFilter(filter);
		
		loadPivot(pivot);
	}

	private void loadPivot(final PivotTableElement pivot) {
		dispatcher.execute(new GenerateElement<PivotContent>(pivot), null, new AsyncCallback<PivotContent>() {

			@Override
			public void onFailure(Throwable caught) {
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
