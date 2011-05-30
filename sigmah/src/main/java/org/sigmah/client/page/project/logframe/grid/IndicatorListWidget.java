package org.sigmah.client.page.project.logframe.grid;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.config.design.IndicatorForm;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.logframe.LogFrameElementDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class IndicatorListWidget extends Composite  {

	private static IndicatorListWidgetUiBinder uiBinder = GWT
			.create(IndicatorListWidgetUiBinder.class);

	interface IndicatorListWidgetUiBinder extends
			UiBinder<Widget, IndicatorListWidget> {
	}
		
	private int databaseId;
	private LogFrameElementDTO element;
	
	private final Dispatcher dispatcher;

	@UiField
	Label newIndicatorLink;

	@UiField(provided=true)
	ListView<IndicatorDTO> indicatorList;
		
	public IndicatorListWidget(Dispatcher dispatcher, int databaseId, LogFrameElementDTO element) {
		this.dispatcher = dispatcher;
		this.databaseId = databaseId;
		this.element = element;

			
		ListStore<IndicatorDTO> store = new ListStore<IndicatorDTO>();
		store.add(element.getIndicators());

		indicatorList = new ListView<IndicatorDTO>();
		indicatorList.setStore(store);
		indicatorList.setDisplayProperty("name");
		indicatorList.setBorders(false);
		
		indicatorList.addListener(Events.Select, new Listener<ListViewEvent<IndicatorDTO>>() {
			@Override
			public void handleEvent(ListViewEvent<IndicatorDTO> be) {
				
			}
		});

		
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiHandler("newIndicatorLink")
	void onClick(ClickEvent e) {
		final IndicatorDTO newIndicator = new IndicatorDTO();
		newIndicator.setCollectIntervention(true);
		newIndicator.setAggregation(IndicatorDTO.AGGREGATE_SUM);
		newIndicator.setDatabaseId(databaseId);
		
		final IndicatorForm form = new IndicatorForm(dispatcher);
		form.getBinding().bind(newIndicator);
		form.setIdVisible(false);
		form.setCategoryVisible(false);
		
		final FormDialogImpl<IndicatorForm> dialog = new FormDialogImpl<IndicatorForm>(form);
		dialog.setHeading(I18N.CONSTANTS.newIndicatorGroup());
		dialog.setWidth(form.getPreferredDialogWidth());
		dialog.setHeight(form.getPreferredDialogHeight());
		dialog.setScrollMode(Style.Scroll.AUTOY);
		dialog.show(new FormDialogCallback() {

			@Override
			public void onValidated() {
				dialog.hide();
				element.getIndicators().add(newIndicator);
				indicatorList.getStore().add(newIndicator);
			}
		});

	}

}
