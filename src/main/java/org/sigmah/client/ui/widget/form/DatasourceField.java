package org.sigmah.client.ui.widget.form;

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


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.GetIndicatorDataSources;
import org.sigmah.shared.command.GetProject;
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
import java.util.ArrayList;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.dispatch.monitor.LoadingMask;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.widget.popup.IndicatorBrowsePopup;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;

/**
 * 
 * Field widget for the Indicator's {@code dataSourceIds} property. 
 * The {@code value} of this field is a set of the ids of indicators which
 * have been selected as dataSources.
 * 
 * @author Alexander Bertram (akbertram@gmail.com) 
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2
 */
public class DatasourceField extends AdapterField {
	
	private final DispatchAsync dispatcher;
	private final ListView<IndicatorDataSourceDTO> listView;
	private Button removeButton;

	private final CheckBox directBox;
	private final CheckBox otherIndicatorsBox;
	private final ContentPanel panel;
	private final LayoutContainer container;
	
	// Collections.emptySet is not serializable :-(
	private static final Set<Integer> EMPTY_SET = new HashSet<Integer>();
	
	private Set<Integer> oldValue = EMPTY_SET;
	
	public DatasourceField(DispatchAsync dispatcher) {
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
		final IndicatorBrowsePopup dialog = new IndicatorBrowsePopup(dispatcher);
		dialog.initialize();
		dialog.show(new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
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

	public void load(final Integer projectId, final IndicatorDTO indicator) {
		listView.getStore().removeAll();
		
		// Reference on the project name
		final String[] projectName = {I18N.CONSTANTS.loading()};
		
		// List of datasources
		final ArrayList<IndicatorDataSourceDTO> datasources = new ArrayList<IndicatorDataSourceDTO>();

		final RequestManager<VoidResult> requestManager = new RequestManager<VoidResult>(null, new CommandResultHandler<VoidResult>() {

			@Override
			protected void onCommandSuccess(VoidResult result) {
				listView.getStore().add(datasources);
				otherIndicatorsBox.disableEvents(true);
				otherIndicatorsBox.setValue(!datasources.isEmpty());
				showGrid(!datasources.isEmpty());
				otherIndicatorsBox.disableEvents(false);
				directBox.setValue(indicator == null || indicator.isDirectDataEntryEnabled());
				directBox.setBoxLabel(I18N.MESSAGES.indicatorDatasourceDirect(projectName[0]));
				
				oldValue = getValue();
			}
		});
		
		final LoadingMask loadingMask = new LoadingMask(container);
		
		// Retrieve every visible project
		dispatcher.execute(new GetProject(projectId, ProjectDTO.Mode.BASE), new RequestManagerCallback<VoidResult, ProjectDTO>(requestManager) {
			
			@Override
			public void onRequestSuccess(ProjectDTO result) {
				projectName[0] = result.getName();
			}
		}, loadingMask);
		
		// Retrieve the associated datasources
		if(indicator != null && indicator.getId() != null) {
			dispatcher.execute(new GetIndicatorDataSources(indicator.getId()), new RequestManagerCallback<VoidResult, ListResult<IndicatorDataSourceDTO>>(requestManager) {

				@Override
				public void onRequestSuccess(ListResult<IndicatorDataSourceDTO> result) {
					datasources.addAll(result.getData());
				}
			}, loadingMask);
		}
		
		requestManager.ready();
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
