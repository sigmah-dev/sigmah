package org.sigmah.client.page.config.design;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.GetProjects.ProjectResultType;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorDataSourceDTO;
import org.sigmah.shared.dto.ProjectDTOLight;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelStringProvider;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel.CheckNodes;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.inject.Inject;

/**
 * Dialog that enables a user to select Indicators.
 * 
 * @author alexander
 *
 */
public class IndicatorBrowseDialog extends Dialog {


	private final Dispatcher dispatcher;
	private final TreeStore<ModelData> treeStore;
	private final TreePanel<ModelData> treePanel;
	private TreeLoader<ModelData> loader;
	
	private FormDialogCallback callback;

	
	@Inject
	public IndicatorBrowseDialog(Dispatcher dispatcher) {
		super();
		this.dispatcher = dispatcher;
		
		loader = new BaseTreeLoader<ModelData>(new TreeProxy()) {

			@Override
			public boolean hasChildren(ModelData parent) {
				return parent instanceof ProjectDTOLight;
			}
			
		};
		treeStore = new TreeStore<ModelData>(loader);
		treePanel = new TreePanel<ModelData>(treeStore);
		treePanel.setCheckable(true);
		treePanel.setCheckNodes(CheckNodes.LEAF);
		treePanel.setIconProvider(new ModelIconProvider<ModelData>() {
			
			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				if(model instanceof ProjectDTOLight) {
					return IconImageBundle.ICONS.database();
				} else {
					return null;
				}
			}
		});
		treePanel.setLabelProvider(new ModelStringProvider<ModelData>() {
			
			@Override
			public String getStringValue(ModelData model, String property) {
				if(model instanceof ProjectDTOLight) {
					return ((ProjectDTOLight) model).getFullName();
				} else if (model instanceof IndicatorDTO) { 
					return ((IndicatorDTO) model).getName();
				} else {
					return "huh?";
				}
			}
		});
		setWidth(450);
		setHeight(350);
		setButtons(OKCANCEL);
		setHeading("Select indicators");
		setLayout(new FitLayout());
		setBodyStyle("background: white");
		add(treePanel);
		
	}
	
	/**
	 * Shows the form and calls back if the user clicks OK. 
	 * 
	 * @param callback
	 */
	public void show(FormDialogCallback callback) {
		this.callback = callback;
		super.show();
		loader.load();
	}
	
	
	@Override
	protected void onButtonPressed(Button button) {
		if(button.getItemId().equals(CANCEL)) {
			hide();
		} else {	
			callback.onValidated();
		}
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
			ProjectDTOLight project = (ProjectDTOLight) treePanel.getStore().getParent(model);
			
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
	
	
	private class TreeProxy extends RpcProxy {

		@Override
		protected void load(Object parent, final AsyncCallback callback) {
			if(parent == null) {
				GetProjects command = new GetProjects();
				command.setReturnType(ProjectResultType.PROJECT_LIGHT);

				dispatcher.execute(command, null, new AsyncCallback<ProjectListResult>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(ProjectListResult result) {
						callback.onSuccess(result.getListProjectsLightDTO());	
					}
				});
			} else if(parent instanceof ProjectDTOLight) {
				
				dispatcher.execute(GetIndicators.forDatabase(((ProjectDTOLight) parent).getId()), null, new AsyncCallback<IndicatorListResult>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(IndicatorListResult result) {
						callback.onSuccess(result.getData());
					}
				});
				
			}
		}

	}	
	
}
