package org.sigmah.client.ui.view.pivot.table;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.UpdateMonthlyReports;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.EditorTreeGrid;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.PivotCellEvent;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.util.GWTDates;
import org.sigmah.shared.dto.pivot.model.DateDimension;
import org.sigmah.shared.dto.pivot.model.DateUnit;
import org.sigmah.shared.dto.pivot.model.Dimension;
import org.sigmah.shared.dto.pivot.content.DimensionCategory;
import org.sigmah.shared.dto.pivot.content.EntityCategory;
import org.sigmah.shared.dto.pivot.content.MonthCategory;
import org.sigmah.shared.dto.pivot.model.PivotElement;
import org.sigmah.shared.dto.pivot.content.PivotTableData;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Dates;
import org.sigmah.shared.util.Month;

/**
 * Independent component to display and edited pivoted site / indicator results.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class PivotGridPanel extends ContentPanel implements HasValue<PivotElement>, Loadable {

	private final EventBus eventBus;
	private final DispatchAsync dispatcher;

	private PivotElement element;
	private EditorTreeGrid<PivotTableRow> grid;
	private final TreeStore<PivotTableRow> store;
	private ColumnMapping columnMapping;
	
	private HeaderDecorator headerDecorator = new GridHeaderDecorator(this);

	private Map<Integer, IndicatorDTO> indicators;

	private final Dates dates = new GWTDates();

	private boolean showAxisIcons = true;
	private boolean showSwapIcon = false;
	
	private boolean loading;

	
	public PivotGridPanel(EventBus eventBus, DispatchAsync dispatcher) {
		this.eventBus = eventBus;
		this.dispatcher = dispatcher;

		setLayout(new FitLayout());

		store = new TreeStore<PivotTableRow>();

		PivotResources.INSTANCE.css().ensureInjected();
	}

	public boolean isShowAxisIcons() {
		return showAxisIcons;
	}

	public void setShowAxisIcons(boolean showAxisIcons) {
		this.showAxisIcons = showAxisIcons;
	}

	public boolean isShowSwapIcon() {
		return showSwapIcon;
	}

	public void setShowSwapIcon(boolean showSwapIcon) {
		this.showSwapIcon = showSwapIcon;
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

	public class PivotTableRow extends BaseTreeModel {

		private PivotTableData.Axis rowAxis;

		public PivotTableRow(PivotTableData.Axis axis) {
			this.rowAxis = axis;
			set("header", headerDecorator.decorateHeader(axis));

			updateFromTree();

			for (PivotTableData.Axis child : rowAxis.getChildren()) {
				add(new PivotTableRow(child));
			}
		}

		/**
		 * Updates this model from the Cell values in the PivotTableData tree.
		 */
		private void updateFromTree() {
			this.setProperties(Collections.EMPTY_MAP);
			for (Map.Entry<PivotTableData.Axis, PivotTableData.Cell> entry : rowAxis.getCells().entrySet()) {
				String property = columnMapping.propertyNameForAxis(entry.getKey());
				set(property, entry.getValue().getValue());
			}
		}

		public PivotTableData.Axis getRowAxis() {
			return rowAxis;
		}

		public PivotTableData.Axis getColAxis(String property) {
			return columnMapping.columnAxisForProperty(property);
		}

		public DimensionCategory getCategory(String property, Dimension dimension) {
			DimensionCategory category = findCategory(rowAxis, dimension);
			if (category == null) {
				return findCategory(getColAxis(property), dimension);
			}
			return category;
		}

		private DimensionCategory findCategory(PivotTableData.Axis leaf, Dimension dim) {
			while (leaf != null) {
				if (leaf.getDimension() != null && leaf.getDimension().equals(dim)) {
					return leaf.getCategory();
				}
				leaf = leaf.getParent();
			}
			return null;
		}

		public int getIndicatorId(String property) {
			Set<Integer> indicatorRestrictions = element.getFilter().getRestrictions(DimensionType.Indicator);
			if (indicatorRestrictions.size() == 1) {
				return indicatorRestrictions.iterator().next();
			}
			EntityCategory cat = (EntityCategory) getCategory(property, new Dimension(DimensionType.Indicator));
			if (cat != null) {
				return cat.getId();
			}
			return -1;
		}

		public int getSiteId(String property) {
			Set<Integer> siteRestrictions = element.getFilter().getRestrictions(DimensionType.Site);
			if (siteRestrictions.size() == 1) {
				return siteRestrictions.iterator().next();
			}
			EntityCategory cat = (EntityCategory) getCategory(property, new Dimension(DimensionType.Site));
			if (cat != null) {
				return cat.getId();
			}
			return -1;
		}

		public Month getMonth(String property) {
			MonthCategory cat = (MonthCategory) getCategory(property, new DateDimension(DateUnit.MONTH));
			if (cat != null) {
				return new Month(cat.getYear(), cat.getMonth());
			}
			if (element.getFilter().getDateRange().isClosed()) {
				// TODO(alex) : this should check to see whether the date range
				// is actually a month range
				// but dateUtil is behaving weirdly because of conflicting
				// timezones
				return new Month(dates.getYear(element.getFilter().getMinDate()), dates.getMonth(element
				                .getFilter().getMinDate()));
			}
			throw new UnsupportedOperationException("This cell at property '" + property
			                + "' is not constrained by month");
		}

	}

	public void clear() {
		if (grid != null) {
			grid.removeAllListeners();
			removeAll();
		}
		store.removeAll();
	}

	public void setData(final PivotElement element) {
		clear();

		this.element = element;
		PivotTableData data = element.getContent().getData();

		this.columnMapping = new ColumnMapping(data, new RendererProvider(), headerDecorator);

		for (PivotTableData.Axis axis : data.getRootRow().getChildren()) {
			store.add(new PivotTableRow(axis), true);
		}

		grid = new EditorTreeGrid<PivotTableRow>(store, columnMapping.getColumnModel());
		grid.setView(new PivotGridPanelView());
		grid.getStyle().setNodeCloseIcon(null);
		grid.getStyle().setNodeOpenIcon(null);
		grid.setAutoExpandColumn("header");
		grid.setAutoExpandMin(150);
		grid.addListener(Events.CellDoubleClick, new Listener<GridEvent<PivotTableRow>>() {
			public void handleEvent(GridEvent<PivotTableRow> ge) {
				if (ge.getColIndex() != 0) {
					eventBus.fireEvent(new PivotCellEvent(PivotCellEvent.Action.DRILLDOWN, element, ge.getModel().getRowAxis(),
					                columnMapping.columnAxisForIndex(ge.getColIndex())));
				}
			}
		});
		grid.addListener(Events.HeaderClick, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> event) {
				fireEvent(Events.HeaderClick,
				                new PivotGridHeaderEvent(event, columnMapping.columnAxisForIndex(event.getColIndex())));
			}
		});
		grid.addListener(Events.CellClick, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> event) {
				if (event.getColIndex() == 0) {
					fireEvent(Events.HeaderClick, new PivotGridHeaderEvent(event, event.getModel().getRowAxis()));
				} else {
					fireEvent(Events.CellClick,
					                new PivotGridCellEvent(event, columnMapping.columnAxisForIndex(event.getColIndex())));
				}
			}
		});
		grid.addListener(Events.BeforeEdit, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> be) {
				if (!be.getModel().getRowAxis().isLeaf()) {
					be.setCancelled(true);
				}
				PivotGridCellEvent pivotEvent = new PivotGridCellEvent(be, columnMapping.columnAxisForIndex(be
				                .getColIndex()));
				IndicatorDTO indicator = indicators.get(pivotEvent.getIndicatorId());
				if(indicator != null) {
					if (indicator.isDirectDataEntryEnabled()) {
						prepareEditor(pivotEvent, indicator);
					} else {
						be.setCancelled(true);
						N10N.infoNotif(I18N.CONSTANTS.dataEntry(), I18N.CONSTANTS.indicatorDirectEntry());
					}
				}
			}
		});
		grid.addListener(Events.AfterEdit, new Listener<GridEvent<PivotTableRow>>() {

			@Override
			public void handleEvent(GridEvent<PivotTableRow> event) {
				PivotGridCellEvent pivotEvent = new PivotGridCellEvent(event, columnMapping.columnAxisForIndex(event
				                .getColIndex()));
				updateTotalsAfterEdit(pivotEvent);
				fireEvent(Events.AfterEdit, pivotEvent);
			}
		});

		grid.addStyleName(PivotResources.INSTANCE.css().pivotTable());

		add(grid);

		layout();

		new DelayedTask(new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				for (PivotTableRow row : store.getRootItems()) {
					grid.setExpanded(row, true, true);
				}
			}
		}).delay(1);

	}

	private IndicatorDTO indicatorForCell(PivotGridCellEvent event) {
		int indicatorId = event.getIndicatorId();
		return indicators.get(indicatorId);
	}

	protected void prepareEditor(PivotGridCellEvent event, IndicatorDTO indicator) {
		if (indicator != null) {
			ColumnConfig config = grid.getColumnModel().getColumn(event.getColIndex());
			IndicatorValueField field = (IndicatorValueField) config.getEditor().getField();
			field.setIndicator(indicator);
		}
	}

	private void updateTotalsAfterEdit(PivotGridCellEvent event) {
		// update the PivotTableData.Cell
		Double newValue = event.getModel().get(event.getProperty());
		event.getOrCreateCell().setValue(newValue);

		// update totals
		element.getContent().getData().updateTotals();

		syncGridWithContent();

	}

	private void syncGridWithContent() {
		for (PivotTableRow row : store.getAllItems()) {
			row.updateFromTree();
		}
		grid.getView().refresh(false);
	}

	private int findIndicatorId(PivotTableData.Axis axis) {
		while (axis != null) {
			if (axis.getDimension() != null && axis.getDimension().getType() == DimensionType.Indicator) {
				return ((EntityCategory) axis.getCategory()).getId();
			}
			axis = axis.getParent();
		}
		return -1;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<PivotElement> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	@Override
	public PivotElement getValue() {
		return element;
	}

	@Override
	public void setValue(final PivotElement value) {
		int databaseId = value.getFilter().getRestrictions(DimensionType.Database).iterator().next();
		dispatcher.execute(new GetIndicators(databaseId), new AsyncCallback<IndicatorListResult>() {

			@Override
			public void onFailure(Throwable caught) {
				// handled by monitor
			}

			@Override
			public void onSuccess(IndicatorListResult result) {
				indicators = new HashMap<Integer, IndicatorDTO>();
				for (IndicatorDTO indicator : result.getData()) {
					indicators.put(indicator.getId(), indicator);
				}
				setData(value);
			}
		}, this);

	}

	@Override
	public void setValue(PivotElement value, boolean fireEvents) {
		setData(element);
		if (fireEvents) {
			ValueChangeEvent.fire(this, value);
		}
	}

	public Store<PivotTableRow> getStore() {
		return store;
	}

	public BatchCommand composeSaveCommand() {
		BatchCommand batch = new BatchCommand();
		for (Record record : store.getModifiedRecords()) {
			PivotTableRow row = (PivotTableRow) record.getModel();
			for (String property : record.getChanges().keySet()) {
				UpdateMonthlyReports.Change change = new UpdateMonthlyReports.Change();
				change.indicatorId = row.getIndicatorId(property);
				change.month = row.getMonth(property);
				change.value = row.get(property);
				batch.add(new UpdateMonthlyReports(row.getSiteId(property), change));
			}
		}
		return batch;
	}

	private class RendererProvider implements PivotCellRendererProvider {

		@Override
		public PivotCellRenderer getRendererForColumn(PivotTableData.Axis column) {
			int indicatorId = -1;
			if (element.getFilter().isRestricted(DimensionType.Indicator)) {
				indicatorId = element.getFilter().getRestrictions(DimensionType.Indicator).iterator().next();
			} else {
				indicatorId = findIndicatorId(column);
			}
			if (indicatorId == -1) {
				return new MixedCellRenderer(indicators);
			} else {
				IndicatorDTO indicator = indicators.get(indicatorId);
				if (indicator.isQualitative()) {
					return new QualitativeCellRenderer(indicator);
				} else {
					return new QuantitativeCellRenderer(indicator);
				}
			}
		}

	}

	public void setHeaderDecoratorEditable(boolean editable) {
		if(editable) {
			headerDecorator = new GridHeaderDecorator(this);
		} else {
			headerDecorator = new ReadOnlyHeaderDecorator(this);
		}
	}

	public EditorTreeGrid<PivotTableRow> getGrid() {
		return grid;
	}
	
	public boolean hasIndicatorsInStore() {
		return (indicators != null && indicators.size() != 0);
	}
}
