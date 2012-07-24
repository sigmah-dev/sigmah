package org.sigmah.client.page.project.logframe.grid;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.event.IndicatorEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.dialog.FormDialogTether;
import org.sigmah.client.page.config.design.IndicatorForm;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.logframe.LogFrameElementDTO;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.Listener;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;

public class IndicatorListWidget extends Composite implements
		HasValueChangeHandlers<Void> {

	private final Dispatcher dispatcher;
	private final LogFrameElementDTO element;
	private final int databaseId;
	
	private final String clickAbleStyle = "logframe-grid-code-label-active";
	private FormDialogImpl<IndicatorForm> dialog;
	private final FlexTable table;
	
	public IndicatorListWidget(EventBus eventBus, final Dispatcher dispatcher,
			final int databaseId, final LogFrameElementDTO element) {
		this.dispatcher = dispatcher;
		this.element=element;	
		this.databaseId=databaseId;
		
		table = new FlexTable();
		table.setWidth("100%");
		table.setStyleName("log-frame-indicators-table");
		updateTable();
		
		final Label newIndicatorLink = new Label(I18N.CONSTANTS.newIndicator());
		newIndicatorLink.addStyleName(clickAbleStyle);
		newIndicatorLink.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				showNewIndicatorForm();
			}
		});
		
		final Grid grid=new Grid(2, 1); 
		grid.setCellSpacing(0);
 		grid.setWidget(0, 0, table);
		grid.setWidget(1, 0, newIndicatorLink);
		
		initWidget(grid);

		eventBus.addListener(IndicatorEvent.CHANGED,
				new Listener<IndicatorEvent>() {

					@Override
					public void handleEvent(IndicatorEvent event) {
						onIndicatorChangedExternally(event);
					}
				});

	}
	
	private void showNewIndicatorForm(){
		final IndicatorDTO newIndicator = new IndicatorDTO();
		newIndicator.setCollectIntervention(true);
		newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
		newIndicator.setDatabaseId(databaseId);

		String category = (element.getFormattedCode() + " " + element.getDescription()).trim();
		if (category.length() > 1024)
			category = category.substring(0, 1024);
		newIndicator.setCategory(category);

		showDialog(newIndicator, new FormDialogCallback() {

			@Override
			public void onValidated(FormDialogTether dlg) {

				dispatcher.execute(new CreateEntity(newIndicator),
						dialog, new AsyncCallback<CreateResult>() {

							@Override
							public void onFailure(Throwable caught) {
								// handled by dialog
							}

							@Override
							public void onSuccess(CreateResult result) {
								newIndicator.setId(result.getNewId());
								dialog.hide();
								element.getIndicators().add(newIndicator);
								updateTable();
								ValueChangeEvent.fire(IndicatorListWidget.this, null);
							}
						});
			}
		});
	}
	
	private void updateTable(){
		int rowIndex = 0;
		for (final IndicatorDTO indicator : element.getIndicators()) {
			updateRow(rowIndex, indicator);
			rowIndex++;
		}
	}
	
	private void updateRow(final int rowIndex,final IndicatorDTO indicator){

		final Label label = new Label(indicator.getName());
		
		label.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				onIndicatorClicked(rowIndex,indicator);
			}
		});
		label.addMouseOverHandler(new MouseOverHandler() {

			@Override
			public void onMouseOver(MouseOverEvent event) {
				label.addStyleName(clickAbleStyle);
			}
		});
		label.addMouseOutHandler(new MouseOutHandler() {

			@Override
			public void onMouseOut(MouseOutEvent event) {
				label.removeStyleName(clickAbleStyle);
			}
		});
		table.setWidget(rowIndex, 0, label);
		table.getFlexCellFormatter().setStyleName(rowIndex, 0,
		"log-frame-indicators-table-cell");
		
		table.setHTML(rowIndex, 1, indicator.getSourceOfVerification());		
		table.getFlexCellFormatter().setStyleName(rowIndex, 1,
				"log-frame-indicators-table-cell");
	}

	private void onIndicatorClicked(final int rowIndex,final IndicatorDTO indicator) {
		showDialog(indicator, new FormDialogCallback() {

			@Override
			public void onValidated(FormDialogTether dlg) {

				dispatcher.execute(
						new UpdateEntity(indicator, indicator.getProperties()), dialog,
						new AsyncCallback<VoidResult>() {

							@Override
							public void onFailure(Throwable caught) {
								// handled by monitor
							}

							@Override
							public void onSuccess(VoidResult result) {
								dialog.hide();
								updateRow(rowIndex, indicator);
								ValueChangeEvent.fire(IndicatorListWidget.this,null);
							}
						});
			}
		});
	}

	private void showDialog(IndicatorDTO indicator, FormDialogCallback callback) {
		final IndicatorForm form = new IndicatorForm(dispatcher);
		form.getBinding().bind(indicator);
		form.setIdVisible(false);
		form.setGroupVisible(false);

		dialog = new FormDialogImpl<IndicatorForm>(form);
		dialog.setHeading(indicator.getName() == null ? I18N.CONSTANTS
				.newIndicator() : indicator.getName());
		dialog.setWidth(form.getPreferredDialogWidth());
		dialog.setHeight(form.getPreferredDialogHeight());
		dialog.setScrollMode(Scroll.AUTOY);
		dialog.show(callback);
	}

	/**
	 * Update our view in the event that an indicator is changed in another tab
	 * open somewhere.
	 */
	private void onIndicatorChangedExternally(IndicatorEvent event) {
		IndicatorDTO indicator = null;
		for(IndicatorDTO dto:element.getIndicators()){
			if(dto.getId()==event.getEntityId())
				indicator=dto;
		} 
		if (indicator != null) {
			switch (event.getChangeType()) {
			case DELETED:
				element.getIndicators().remove(indicator);
 				break;
			case UPDATED:
				if (event.getChanges() != null) {
					event.applyChanges(indicator);
				}
			}
			updateTable();
		}

	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Void> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
