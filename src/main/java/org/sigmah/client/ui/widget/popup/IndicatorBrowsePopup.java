package org.sigmah.client.ui.widget.popup;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckNodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * Dialog that enables a user to select Indicators.
 * 
 * @author Alexander Bertram (akbertram@gmail.com) v1.3
 * @author Raphaël Calabro (rcalabro@gmail.com) v2.0
 */
public class IndicatorBrowsePopup extends AbstractPopupView<PopupWidget> {

	private final DispatchAsync dispatcher;
	private final TreeLoader<ModelData> loader;
	private TreePanel<ModelData> treePanel;
	
	private AsyncCallback<VoidResult> callback;

	@Inject
	public IndicatorBrowsePopup(DispatchAsync dispatcher) {
		super(new PopupWidget(true));
		
		this.dispatcher = dispatcher;
		
		this.loader = new BaseTreeLoader<ModelData>(new TreeProxy()) {

			@Override
			public boolean hasChildren(ModelData parent) {
				return parent instanceof ProjectDTO;
			}
		};
	}

	@Override
	public void initialize() {
		setPopupTitle(I18N.CONSTANTS.selectIndicators());
		
		final TreeStore<ModelData> treeStore = new TreeStore<ModelData>(loader);
		treePanel = new TreePanel<ModelData>(treeStore);
		treePanel.setCheckable(true);
		treePanel.setCheckNodes(CheckNodes.LEAF);
		treePanel.setIconProvider(new ModelIconProvider<ModelData>() {
			
			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				if(model instanceof ProjectDTO) {
					return IconImageBundle.ICONS.database();
				} else {
					return null;
				}
			}
		});
		treePanel.setLabelProvider(new ModelStringProvider<ModelData>() {
			
			@Override
			public String getStringValue(ModelData model, String property) {
				if(model instanceof ProjectDTO) {
					return ((ProjectDTO) model).getFullName();
				} else if (model instanceof IndicatorDTO) { 
					return ((IndicatorDTO) model).getName();
				} else {
					throw new IllegalArgumentException("ModelData class should either be ProjectDTO or IndicatorDTO. Found: " + model.getClass());
				}
			}
		});
		
		
		final Button okButton = Forms.button(I18N.CONSTANTS.ok());
		okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				callback.onSuccess(null);
			}
		});
		
		final Button cancelButton = Forms.button(I18N.CONSTANTS.cancel());
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				hide();
			}
		});
		
		getPopup().addButton(okButton);
		getPopup().addButton(cancelButton);
		
		initPopup(treePanel);
	}
	
	/**
	 * Shows the form and calls back if the user clicks OK. 
	 * 
	 * @param callback
	 */
	public void show(AsyncCallback<VoidResult> callback) {
		this.callback = callback;
		center();
		loader.load();
	}
	
	
	/**
	 * 
	 * @return the list of checked indicators.
	 */
	public List<IndicatorDTO> getSelection() {	
		return (List)treePanel.getCheckedSelection();
	}
	
	/**
	 * 
	 * @return the selected indicators as {@link IndicatorDataSourceDTO} dtos.
	 */
	public List<IndicatorDataSourceDTO> getSelectionAsDataSources() {
		List<IndicatorDataSourceDTO> list = new ArrayList<IndicatorDataSourceDTO>();
		for(ModelData model : treePanel.getCheckedSelection()) {
			IndicatorDTO indicator = (IndicatorDTO) model;
			ProjectDTO project = (ProjectDTO) treePanel.getStore().getParent(model);
			
			IndicatorDataSourceDTO datasource = new IndicatorDataSourceDTO();
			datasource.setDatabaseId(project.getId());
			datasource.setDatabaseName(project.getName());
			datasource.setIndicatorCode(indicator.getCode());
			datasource.setIndicatorName(indicator.getName());
			datasource.setIndicatorId(indicator.getId());

			list.add(datasource);
			
		}
		return list;
	}
	
	
	private class TreeProxy extends RpcProxy<List<?>> {

		@Override
		protected void load(Object parent, final AsyncCallback<List<?>> callback) {
			if(parent == null) {
				final GetProjects command = new GetProjects((ProjectModelType) null, ProjectDTO.Mode.BASE);

				dispatcher.execute(command, new CommandResultHandler<ListResult<ProjectDTO>>() {

					@Override
					protected void onCommandSuccess(ListResult<ProjectDTO> result) {
						callback.onSuccess(result.getList());
					}
				});
				
			} else if(parent instanceof ProjectDTO) {
				dispatcher.execute(new GetIndicators((ProjectDTO)parent), new CommandResultHandler<IndicatorListResult>() {

					@Override
					protected void onCommandSuccess(IndicatorListResult result) {
						callback.onSuccess(result.getData());
					}
				});
			}
		}

	}	
	
}
