package org.sigmah.client.ui.view.project.logframe.grid;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.logframe.LogFrameElementDTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import java.util.Map;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.RequestParameter;

public class IndicatorListWidget extends Composite implements HasValueChangeHandlers<Void> {

	private static final String CLICKABLE_STYLE = "logframe-grid-code-label-active";
	
	private final EventBus eventBus;
	private final int databaseId;
	private final LogFrameElementDTO element;
	
	private final FlexTable table;

	public IndicatorListWidget(EventBus eventBus, final int databaseId, final LogFrameElementDTO element) {
		this.eventBus = eventBus;
		this.databaseId = databaseId;
		this.element = element;

		table = new FlexTable();
		table.setWidth("100%");
		table.setStyleName("log-frame-indicators-table");
		updateTable();

		final Label newIndicatorLink = new Label(I18N.CONSTANTS.newIndicator());
		newIndicatorLink.addStyleName(CLICKABLE_STYLE);
		newIndicatorLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showNewIndicatorForm();
			}
		});

		final Grid grid = new Grid(2, 1);
		grid.setCellSpacing(0);
		grid.setWidget(0, 0, table);
		grid.setWidget(1, 0, newIndicatorLink);

		initWidget(grid);

		eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(UpdateEvent event) {
				if(event.concern(UpdateEvent.INDICATOR_UPDATED)) {
					onIndicatorUpdatedExternally((Integer)event.getParam(0), (Map<String, Object>)event.getParam(1));
					
				} else if(event.concern(UpdateEvent.INDICATOR_REMOVED)) {
					onIndicatorDeletedExternally((Integer)event.getParam(0));
				}
			}
		});
	}

	private void showNewIndicatorForm() {
		String category = (element.getFormattedCode() + " " + element.getDescription()).trim();
		if(category.length() > 1024) {
			category = category.substring(0, 1024);
		}
		
		eventBus.navigateRequest(Page.INDICATOR_EDIT.request()
			.addParameter(RequestParameter.ID, databaseId)
			.addParameter(RequestParameter.CATEGORY, category)
			.addData(RequestParameter.REQUEST, new CommandResultHandler<IndicatorDTO>() {
				@Override
				protected void onCommandSuccess(IndicatorDTO result) {
					element.getIndicators().add(result);
					updateTable();
					ValueChangeEvent.fire(IndicatorListWidget.this, null);
				}
			}
		));
	}

	private void updateTable() {
		int rowIndex = 0;
		for (final IndicatorDTO indicator : element.getIndicators()) {
			updateRow(rowIndex, indicator);
			rowIndex++;
		}
	}

	private void updateRow(final int rowIndex, final IndicatorDTO indicator) {

		final Label label = new Label(indicator.getName());

		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onIndicatorClicked(rowIndex, indicator);
			}
		});
		label.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				label.addStyleName(CLICKABLE_STYLE);
			}
		});
		label.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				label.removeStyleName(CLICKABLE_STYLE);
			}
		});
		table.setWidget(rowIndex, 0, label);
		table.getFlexCellFormatter().setStyleName(rowIndex, 0, "log-frame-indicators-table-cell");

		table.setHTML(rowIndex, 1, indicator.getSourceOfVerification());
		table.getFlexCellFormatter().setStyleName(rowIndex, 1, "log-frame-indicators-table-cell");
	}

	private void onIndicatorClicked(final int rowIndex, final IndicatorDTO indicator) {
		eventBus.navigateRequest(Page.INDICATOR_EDIT.request()
			.addParameter(RequestParameter.ID, databaseId)
			.addData(RequestParameter.MODEL, indicator)
			.addData(RequestParameter.REQUEST, new CommandResultHandler<IndicatorDTO>() {
				@Override
				protected void onCommandSuccess(IndicatorDTO result) {
					updateRow(rowIndex, indicator);
					ValueChangeEvent.fire(IndicatorListWidget.this, null);
				}
			}
		));
	}

	/**
	 * Update our view in the event that an indicator is changed in another tab
	 * open somewhere.
	 */
	private void onIndicatorDeletedExternally(int entityId) {
		final IndicatorDTO indicator = findIndicator(entityId);
		if (indicator != null) {
			element.getIndicators().remove(indicator);
			updateTable();
		}
	}
	
	private void onIndicatorUpdatedExternally(int entityId, Map<String, Object> changes) {
		final IndicatorDTO indicator = findIndicator(entityId);
		if (indicator != null) {
			indicator.setProperties(changes);
			updateTable();
		}
	}
	
	private IndicatorDTO findIndicator(int entityId) {
		for (IndicatorDTO dto : element.getIndicators()) {
			if (dto.getId().equals(entityId)) {
				return dto;
			}
		}
		return null;
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Void> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
