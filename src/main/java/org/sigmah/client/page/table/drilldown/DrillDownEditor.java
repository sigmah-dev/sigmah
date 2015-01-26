/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.table.drilldown;

import org.sigmah.client.AppEvents;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.event.PivotCellEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.entry.SiteGridPanel;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.PivotTableData;
import org.sigmah.shared.report.model.DimensionType;

import com.extjs.gxt.ui.client.event.Listener;

/**
 * 
 * Subclass of SiteGridPanel that filters based on pivot cell.
 * 
 * @author Alex Bertram
 */
public class DrillDownEditor extends SiteGridPanel {

    private final DateUtil dateUtil;
    private Listener<PivotCellEvent> eventListener;

    public DrillDownEditor(EventBus eventBus, Dispatcher service, IStateManager stateMgr, DateUtil dateUtil) {
    	super(eventBus, service, stateMgr);    	
    	this.dateUtil = dateUtil;

    	setHeading(I18N.CONSTANTS.drilldown());
       

        eventListener = new Listener<PivotCellEvent>() {
            public void handleEvent(PivotCellEvent be) {
                onDrillDown(be);
            }
        };
        eventBus.addListener(AppEvents.Drilldown, eventListener);
    }

    @Override
    public void shutdown() {
        super.shutdown();
        eventBus.removeListener(AppEvents.Drilldown, eventListener);
    }

    public void onDrillDown(PivotCellEvent event) {

        // construct our filter from the intersection of rows and columns
        Filter filter = new Filter(filterFromAxis(event.getRow()), filterFromAxis(event.getColumn()));

        // apply the effective filter
        final Filter effectiveFilter = new Filter(filter, event.getElement().getContent().getEffectiveFilter());

        // determine the indicator
        final int indicatorId = effectiveFilter.getRestrictions(DimensionType.Indicator).iterator().next();
        effectiveFilter.clearRestrictions(DimensionType.Indicator);

        // TODO : add configuration option to SiteGridPanel that allows choosing indicators
        
        
        load(effectiveFilter);
        
    }


    private Filter filterFromAxis(PivotTableData.Axis axis) {

        Filter filter = new Filter();
        while (axis != null) {
            if (axis.getDimension() != null) {
                if (axis.getDimension().getType() == DimensionType.Date) {
                    filter.setDateRange(dateUtil.rangeFromCategory(axis.getCategory()));
                } else if (axis.getCategory() instanceof EntityCategory) {
                    filter.addRestriction(axis.getDimension().getType(), ((EntityCategory) axis.getCategory()).getId());
                }
            }
            axis = axis.getParent();
        }
        return filter;
    }

}


