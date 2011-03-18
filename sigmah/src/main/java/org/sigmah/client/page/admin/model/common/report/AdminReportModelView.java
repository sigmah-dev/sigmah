package org.sigmah.client.page.admin.model.common.report;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.model.common.report.AdminReportModelPresenter.View;
import org.sigmah.shared.dto.report.ReportModelDTO;

import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;

public class AdminReportModelView extends View {	
	
	private TreeStore<ReportModelDTO> modelsStore;

	public AdminReportModelView(){		
		this.setLayout(new FitLayout());
        setHeaderVisible(false);
        setBorders(false);
        setBodyBorder(false);
        
		add(buildModelsListGrid());		
	}

	private TreeGrid<ReportModelDTO> buildModelsListGrid(){		
		
		modelsStore = new TreeStore<ReportModelDTO>();
		
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();  
		  
        ColumnConfig column = new ColumnConfig("id",I18N.CONSTANTS.adminReportOrder(), 30);   
        column.setRenderer(new TreeGridCellRenderer<ReportModelDTO>());
		configs.add(column);
		
		column = new ColumnConfig("name",I18N.CONSTANTS.adminReportName(), 400);   
		column.setRenderer(new TreeGridCellRenderer<ReportModelDTO>());
		configs.add(column);
		
		ColumnModel cm = new ColumnModel(configs);		
		
		TreeGrid<ReportModelDTO> grid = new TreeGrid<ReportModelDTO>(modelsStore, cm); 
		grid.setAutoHeight(true);
		return grid;
	}

	@Override
	public TreeStore<ReportModelDTO> getReportModelsStore() {
		return modelsStore;
	}

	@Override
	public Component getMainPanel() {
		this.setTitle("model report");
		return this;
	}
	
	
}
