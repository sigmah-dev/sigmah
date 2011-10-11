package org.sigmah.client.page.config.design;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetIndicatorDataSources;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.result.BatchResult;
import org.sigmah.shared.command.result.IndicatorDataSourceList;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;
import org.sigmah.shared.dto.ProjectDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
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

	private CheckBox directBox;
	private CheckBox otherIndicatorsBox;
	private ContentPanel panel;
	private LayoutContainer container;
	
	// Collections.emptySet is not serializable :-(
	private static final Set<Integer> EMPTY_SET = new HashSet<Integer>();
	
	private Set<Integer> oldValue = EMPTY_SET;
	
	public DatasourceField(Dispatcher dispatcher) {
		super(new LayoutContainer());
		this.dispatcher = dispatcher;

		container = (LayoutContainer)getWidget();

		directBox = new CheckBox();
		directBox.setBoxLabel(I18N.MESSAGES.indicatorDatasourceDirect(I18N.CONSTANTS.loading()));
		container.add(directBox);
		
		otherIndicatorsBox = new CheckBox();
		otherIndicatorsBox.setBoxLabel(I18N.CONSTANTS.indicatorDataSourceOther());
		otherIndicatorsBox.addListener(Events.Change, new Listener<FieldEvent>() {
			@Override
			public void handleEvent(FieldEvent event) {
				boolean checked = event.getValue() == Boolean.TRUE;
				showGrid(checked);
				
				onChange();
			}
		});
		container.add(otherIndicatorsBox);
		
		panel = new ContentPanel();
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
		panel.setVisible(false);
		container.add(panel);
		
	}
	
	public CheckBox getDirectBox() {
		return directBox;
	}
	
	private void addSource() {
		final IndicatorBrowseDialog dialog = new IndicatorBrowseDialog(dispatcher);
		dialog.show(new FormDialogCallback() {

			@Override
			public void onValidated() {
				for(IndicatorDataSourceDTO datasource : dialog.getSelectionAsDataSources()) {
					if(!alreadyIncluded(datasource)) {
						listView.getStore().add(datasource);
					}
				}
				dialog.hide();
				onChange();
			}
		});
	}

	private void onChange() {
		fireChangeEvent(oldValue, getValue());
		oldValue = getValue();
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

	public void load(final IndicatorDTO indicator) {
		listView.getStore().removeAll();

		BatchCommand batch = new BatchCommand();
		batch.add(new GetProject(indicator.getDatabaseId()));
		if(indicator.get("id")!=null) {
			batch.add(new GetIndicatorDataSources(indicator.getId()));
		}
		
		dispatcher.execute(batch, new MaskingAsyncMonitor(container, I18N.CONSTANTS.loading()), 
				new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(BatchResult result) {
				ProjectDTO project = ((ProjectDTO)result.getResults().get(0));

				List<IndicatorDataSourceDTO> indicators;
				if(result.getResults().size() >= 2) {
					indicators = ((IndicatorDataSourceList)result.getResults().get(1)).getData();
				} else {
					indicators = Collections.emptyList();
				}
				
				listView.getStore().add(indicators);
				otherIndicatorsBox.disableEvents(true);
				otherIndicatorsBox.setValue(!indicators.isEmpty());
				showGrid(!indicators.isEmpty());
				otherIndicatorsBox.disableEvents(false);
				directBox.setValue(indicator.isDirectDataEntryEnabled());
				directBox.setBoxLabel(I18N.MESSAGES.indicatorDatasourceDirect(project.getName()));
				
				oldValue = getValue();
			}
		});
	}
	
	
	@Override
	public Set<Integer> getValue() {
		if(otherIndicatorsBox.getValue()) {
			return getSelectedIds();
		} else {
			return EMPTY_SET;
		}
	}

	private Set<Integer> getSelectedIds() {
		Set<Integer> ids = new HashSet<Integer>();
		for(IndicatorDataSourceDTO datasource : listView.getStore().getModels()) {
			ids.add(datasource.getIndicatorId());
		}
		return ids;
	}

	private void showGrid(boolean visible) {
		panel.setVisible(visible);
		container.layout(true);
	}
}
