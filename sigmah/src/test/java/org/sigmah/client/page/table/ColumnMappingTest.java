package org.sigmah.client.page.table;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.sigmah.shared.domain.AdminEntity;
import org.sigmah.shared.report.content.DimensionCategory;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.PivotTableData;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.model.AdminDimension;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;
import org.sigmah.shared.report.model.PivotTableElement;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.HeaderGroupConfig;
import com.google.gwt.junit.GWTMockUtilities;

public class ColumnMappingTest {

	@Test
	public void unbalancedColumnTree() {
		
		
		AdminDimension province = new AdminDimension(1);
		AdminDimension territory = new AdminDimension(2);
		AdminDimension groupement = new AdminDimension(3);
		Dimension site = new Dimension(DimensionType.Site);
		Dimension indicator = new Dimension(DimensionType.Indicator);
				
		
		PivotTableData data = new PivotTableData(Arrays.asList(indicator), Arrays.asList(province, territory, groupement, site));
		
		PivotTableData.Axis kinshasa = data.getRootColumn().addChild(province, entity(1, "Kinshasa"));
		PivotTableData.Axis citeDeJoix = kinshasa.addChild(site, entity(1, "Cite de Joix"));
		
		PivotTableData.Axis nordKivu = data.getRootColumn().addChild(province, entity(2, "Nord Kivu"));
		PivotTableData.Axis goma = nordKivu.addChild(site, entity(2, "Goma"));
		PivotTableData.Axis lubero = nordKivu.addChild(territory, entity(3, "Lubero"));
		PivotTableData.Axis baswali = lubero.addChild(groupement, entity(4, "Baswali"));
		PivotTableData.Axis beni = baswali.addChild(site, entity(3, "Beni"));
		
		PivotTableElement element = new PivotTableElement();
		
		ColumnMapping mapping = new ColumnMapping(data, new HeaderDecoratorStub());
		
										 /* row, 	col, 	rowSpan, 	colSpan */
		assertGroupPos(mapping, kinshasa, 	0, 		1, 		3, 			1);
		assertGroupPos(mapping, nordKivu, 	0, 		2, 		1, 			2);
		assertGroupPos(mapping, lubero, 	1,		3, 		1, 			1);
		assertGroupPos(mapping, baswali, 	2, 		3, 		1, 			1);

		
	}

	private void assertGroupPos(ColumnMapping mapping, Axis axis, int row,
			int col, int rowSpan, int colSpan) {

		for(HeaderGroupConfig group : mapping.getColumnModel().getHeaderGroups()) {
			if(group.getHtml().equals(axis.getLabel())) {
				if(group.getRow() != row || group.getColumn() != col || group.getRowspan() != rowSpan || group.getColspan() != colSpan) {
					throw new AssertionError(String.format("Expected header group '%s' at (%d, %d) with rowspan %d, colspan %d, but found at (%d, %d), rowspan %d, colspan %d",
							axis.getLabel(),
							row,
							col,
							rowSpan,
							colSpan,
							group.getRow(),
							group.getColumn(),
							group.getColspan(),
							group.getRowspan()));
				}
				return;
			}
		}
		throw new AssertionError(String.format("Did not find header group '%s'", axis.getLabel()));
	}

	private EntityCategory entity(int id, String label) {
		return new EntityCategory(id, label);
	}
	
	private static class HeaderDecoratorStub implements HeaderDecorator {

		@Override
		public String decorateHeader(Axis axis) {
			return axis.getLabel();
		}

		@Override
		public String cornerCellHtml() {
			return ""; 
		}
		
	}
}
