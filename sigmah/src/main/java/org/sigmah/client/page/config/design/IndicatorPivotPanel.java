package org.sigmah.client.page.config.design;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.toolbar.ActionToolBar;
import org.sigmah.client.page.project.pivot.LayoutComposer;
import org.sigmah.client.page.table.PivotGridPanel;
import org.sigmah.client.util.DateUtilGWTImpl;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GenerateElement;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.report.content.PivotContent;
import org.sigmah.shared.report.model.PivotTableElement;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class IndicatorPivotPanel extends ContentPanel {
	
	private final Dispatcher dispatcher;
	private final PivotGridPanel gridPanel;
	
	@Inject
	public IndicatorPivotPanel( Dispatcher dispatcher, PivotGridPanel gridPanel ) {
		this.dispatcher = dispatcher;
		this.gridPanel = gridPanel;
		this.gridPanel.setHeaderVisible(false);
		this.gridPanel.setShowAxisIcons(false);
		
		ActionToolBar toolBar = new ActionToolBar();
		
		
		setLayout(new FitLayout());
		setHeaderVisible(false);
		
		add(gridPanel);
		
	}

	public void load(int databaseId, final IndicatorDTO indicator) {
		
		dispatcher.execute(new GetProject(databaseId), new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()), new AsyncCallback<ProjectDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				// handled by monitor
			}

			@Override
			public void onSuccess(ProjectDTO result) {
				loadPivot(result, indicator);
				
			}
		});
	}

	private void loadPivot(ProjectDTO project, IndicatorDTO indicator) {
		
		LayoutComposer composer = new LayoutComposer(new DateUtilGWTImpl(), project);
		final PivotTableElement pivot = composer.fixIndicator(indicator.getId());
		
		dispatcher.execute(new GenerateElement<PivotContent>(pivot), new MaskingAsyncMonitor(this, I18N.CONSTANTS.loading()), 
				new AsyncCallback<PivotContent>() {

			@Override
			public void onFailure(Throwable caught) {
				// handled by monitor
			}

			@Override
			public void onSuccess(PivotContent content) {
				pivot.setContent(content);
				gridPanel.setValue(pivot);
			}
		});				
	}
	
	public BatchCommand composeSaveCommand() {
		return gridPanel.composeSaveCommand();
	}
}
