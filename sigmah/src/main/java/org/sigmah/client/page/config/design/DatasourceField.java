package org.sigmah.client.page.config.design;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.shared.command.GetIndicatorDataSources;
import org.sigmah.shared.command.result.IndicatorDataSourceList;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * Field widget for the Indicator's {@code dataSourceIds} property. 
 * The {@code value} of this field is a set of the ids of indicators which
 * have been selected as dataSources.
 * 
 * @author alex 
 *
 */
public class DatasourceField extends AdapterField {
	
	private final Dispatcher dispatcher;
	private ListView<IndicatorDataSourceDTO> listView;
	private Button removeButton;

	public DatasourceField(Dispatcher dispatcher) {
		super(new ContentPanel());
		this.dispatcher = dispatcher;
		ContentPanel panel = (ContentPanel) getWidget();
		panel.setHeaderVisible(false);
	
		ToolBar toolBar = new ToolBar();
		toolBar.add(new Button(I18N.CONSTANTS.addIndicator(), IconImageBundle.ICONS.add(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				addSource();
			}
		}));
		removeButton = new Button(I18N.CONSTANTS.remove(), IconImageBundle.ICONS.delete(), new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				removeSelected();
			}
		});
		removeButton.disable();			
		toolBar.add(removeButton);

		panel.setTopComponent(toolBar);
		
		listView = new ListView<IndicatorDataSourceDTO>();
		listView.setStore(new ListStore<IndicatorDataSourceDTO>());
		listView.setSimpleTemplate("<strong>{indicatorName}</strong><br>{databaseName}");
		listView.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<IndicatorDataSourceDTO>() {
			
			@Override
			public void selectionChanged(
					SelectionChangedEvent<IndicatorDataSourceDTO> se) {
				removeButton.setEnabled(!se.getSelection().isEmpty());
				
			}
		});
		
		panel.setLayout(new FitLayout());
		panel.add(listView);
		panel.setHeight(150);
		
	}
	
	private void addSource() {
		final IndicatorBrowseDialog dialog = new IndicatorBrowseDialog(dispatcher);
		dialog.show(new FormDialogCallback() {

			@Override
			public void onValidated() {
				Set<Integer> oldValue = getValue();
				for(IndicatorDataSourceDTO datasource : dialog.getSelectionAsDataSources()) {
					if(!alreadyIncluded(datasource)) {
						listView.getStore().add(datasource);
					}
				}
				dialog.hide();
				fireChangeEvent(oldValue, getValue());
			}
		});
	}
	
	private void removeSelected() {
		List<IndicatorDataSourceDTO> selection = listView.getSelectionModel().getSelection();
		Set<Integer> oldValue = getValue();
		
		for(IndicatorDataSourceDTO datasource : selection) {
			listView.getStore().remove(datasource);
		}
		if(!selection.isEmpty()) {
			fireChangeEvent(oldValue, getValue());
		}
	}	
	
	private boolean alreadyIncluded(IndicatorDataSourceDTO newDatasource) {
		for(IndicatorDataSourceDTO datasource : listView.getStore().getModels()) {
			if(datasource.getIndicatorId() == newDatasource.getIndicatorId()) {
				return true;
			}
		}
		return false;
	}

	public void load(int indicatorId) {
		listView.getStore().removeAll();
		dispatcher.execute(new GetIndicatorDataSources(indicatorId), new MaskingAsyncMonitor(listView, I18N.CONSTANTS.loading()), 
				new AsyncCallback<IndicatorDataSourceList>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(IndicatorDataSourceList result) {
				listView.getStore().add(result.getData());
			}
		});
	}
	
	
	@Override
	public Set<Integer> getValue() {
		Set<Integer> ids = new HashSet<Integer>();
		for(IndicatorDataSourceDTO datasource : listView.getStore().getModels()) {
			ids.add(datasource.getIndicatorId());
		}
		return ids;
	}
}
