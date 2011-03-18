package org.sigmah.client.page.config.design;

import java.util.Collections;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.entry.SiteGridPanel;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.inject.Inject;

public class IndicatorDialog extends Window {
	
	private IndicatorForm indicatorForm;
	private SiteGridPanel siteGridPanel;
	@Inject
	public IndicatorDialog(SiteGridPanel siteGridPanel) {
		
		setWidth(475);
		setHeight(300);
		setHeading("New Indicator");
		setClosable(true);
		
		indicatorForm = new IndicatorForm();
		indicatorForm.setHeaderVisible(false);
		indicatorForm.setScrollMode(Scroll.AUTOY);
		indicatorForm.setStyleAttribute("backgroundColor", "white");
		
		TabItem defTab = new TabItem("Definition");
		defTab.setLayout(new FitLayout());
		defTab.add(indicatorForm);
		
		this.siteGridPanel = siteGridPanel;
		
		TabItem valuesTab = new TabItem(I18N.CONSTANTS.value());
		valuesTab.setLayout(new FitLayout());
		valuesTab.add(siteGridPanel);
		
		TabItem linkTab = new TabItem("Links");
		
		
		TabPanel tabPanel = new TabPanel();
		tabPanel.add(defTab);
		tabPanel.add(valuesTab);
		tabPanel.add(linkTab);
		
		setLayout(new FitLayout());
		add(tabPanel);
	
	}
	
	public void bindIndicator(int databaseId, IndicatorDTO indicator, Store store) {
		
		indicatorForm.getBinding().setStore(store);
		indicatorForm.getBinding().bind(indicator);
		
		Filter siteFilter = new Filter();
		siteFilter.addRestriction(DimensionType.Database, databaseId);
		siteGridPanel.load(siteFilter, Collections.singleton(indicator));
	}
	

}
