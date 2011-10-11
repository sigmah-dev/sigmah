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
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class IndicatorListWidget extends Composite implements HasValueChangeHandlers<Void> {

	private static final String DRAG_AND_DROP_GROUP = "logframeIndicators";
	private static IndicatorListWidgetUiBinder uiBinder = GWT
			.create(IndicatorListWidgetUiBinder.class);

	interface IndicatorListWidgetUiBinder extends
			UiBinder<Widget, IndicatorListWidget> {
	}
	
	interface Style extends CssResource {
		String indicator();
		String indicatorLabel();
		String indicatorOver();
		String indicatorSelected();
		String sourceOfVerification();
	}
		
	private int databaseId;
	private LogFrameElementDTO element;
	
	private final Dispatcher dispatcher;

	@UiField
	Label newIndicatorLink;

	@UiField()
	ListView<IndicatorDTO> indicatorList;
	
	@UiField Style style;
	private ListStore<IndicatorDTO> store;
	private FormDialogImpl<IndicatorForm> dialog;

		
	public IndicatorListWidget(EventBus eventBus, Dispatcher dispatcher, int databaseId, LogFrameElementDTO element) {
		this.dispatcher = dispatcher;
		this.databaseId = databaseId;
		this.element = element;

		initWidget(uiBinder.createAndBindUi(this));
		
		store = new ListStore<IndicatorDTO>();
		store.add(element.getIndicators());
		
		indicatorList.setTemplate("<tpl for=\".\"><div class=" + style.indicator() + ">" +
				"<div><span class=" + style.indicatorLabel() + ">{name}</span></div>" + 
				"<tpl if=\"values.sourceOfVerification\">" +
				"<div class=" + style.sourceOfVerification() + ">" + 
					I18N.CONSTANTS.sourceOfVerification() + ": " + "{sourceOfVerification}</div>" +
				"</tpl></div></tpl>");
		indicatorList.setStore(store);
		indicatorList.setBorders(false);
		indicatorList.setOverStyle(style.indicatorOver());
		indicatorList.setSelectStyle(style.indicatorSelected());
		indicatorList.setItemSelector("." + style.indicator());
		
		indicatorList.addListener(Events.Select, new Listener<ListViewEvent<IndicatorDTO>>() {
			@Override
			public void handleEvent(ListViewEvent<IndicatorDTO> be) {
				onIndicatorClicked(be.getModel());
			}
		});

		new ListViewDragSource(indicatorList)
			.setGroup(DRAG_AND_DROP_GROUP);
		new ListViewDropTarget(indicatorList)
			.setGroup(DRAG_AND_DROP_GROUP);
		
		eventBus.addListener(IndicatorEvent.CHANGED, new Listener<IndicatorEvent>() {

			@Override
			public void handleEvent(IndicatorEvent event) {
				onIndicatorChangedExternally(event);			
			}
		});

	}

	@UiHandler("newIndicatorLink")
	void onClick(ClickEvent e) {
		final IndicatorDTO newIndicator = new IndicatorDTO();
		newIndicator.setCollectIntervention(true);
		newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
		newIndicator.setDatabaseId(databaseId);
		newIndicator.setCategory( (element.getFormattedCode() + " " + element.getDescription()).trim() );
		
		showDialog(newIndicator, new FormDialogCallback() {

			@Override
			public void onValidated(FormDialogTether dlg) {
				
				dispatcher.execute(new CreateEntity(newIndicator), dialog, new AsyncCallback<CreateResult>() {

					@Override
					public void onFailure(Throwable caught) {
						// handled by dialog
					}

					@Override
					public void onSuccess(CreateResult result) {
						newIndicator.setId(result.getNewId());
						dialog.hide();
						element.getIndicators().add(newIndicator);
						indicatorList.getStore().add(newIndicator);			
					
						ValueChangeEvent.fire(IndicatorListWidget.this, null);				
					}
				});
			}
		});
	}
	
	private void onIndicatorClicked(final IndicatorDTO model) {
		showDialog(model, new FormDialogCallback() {

			@Override
			public void onValidated(FormDialogTether dlg) {
				
				dispatcher.execute(new UpdateEntity(model, model.getProperties()), dialog, new AsyncCallback<VoidResult>() {

					@Override
					public void onFailure(Throwable caught) {
						// handled by monitor
					}

					@Override
					public void onSuccess(VoidResult result) {
						dialog.hide();
						indicatorList.refresh();

						ValueChangeEvent.fire(IndicatorListWidget.this, null);				
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
		dialog.setHeading(indicator.getName() == null ? 
				I18N.CONSTANTS.newIndicator() : indicator.getName());
		dialog.setWidth(form.getPreferredDialogWidth());
		dialog.setHeight(form.getPreferredDialogHeight());
		dialog.setScrollMode(Scroll.AUTOY);
		dialog.show(callback);
	}


	/**
	 * Update our view in the event that an indicator is changed in another tab open somewhere.
	 */
	private void onIndicatorChangedExternally(IndicatorEvent event) {
		IndicatorDTO indicator = store.findModel("id", event.getEntityId());
		if(indicator != null) {
			switch(event.getChangeType()) {
			case DELETED:
				store.remove(indicator);
				break;
			case UPDATED:
				if(event.getChanges() != null) {
					event.applyChanges(indicator);
					store.update(indicator);
				}
			}
		}
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Void> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

}
