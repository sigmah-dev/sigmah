/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.ui.view.pivot.table.drilldown;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.ui.view.project.indicator.SiteGridPanel;
import org.sigmah.shared.dto.pivot.content.EntityCategory;
import org.sigmah.shared.dto.pivot.content.IStateManager;
import org.sigmah.shared.dto.pivot.content.PivotTableData;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Dates;
import org.sigmah.shared.util.Filter;

/**
 * Subclass of SiteGridPanel that filters based on pivot cell.
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class DrillDownEditor extends SiteGridPanel {

    private final Dates dates;

    public DrillDownEditor(EventBus eventBus, DispatchAsync service, IStateManager stateMgr, Dates dates) {
    	this.dates = dates;

    	setHeadingText(I18N.CONSTANTS.drilldown());

//        Listener<PivotCellEvent> eventListener = new Listener<PivotCellEvent>() {
//            public void handleEvent(PivotCellEvent be) {
//                onDrillDown(be);
//            }
//        };
//        eventBus.addListener(AppEvents.Drilldown, eventListener);
    }

//    public void onDrillDown(PivotCellEvent event) {
//
//        // construct our filter from the intersection of rows and columns
//        Filter filter = new Filter(filterFromAxis(event.getRow()), filterFromAxis(event.getColumn()));
//
//        // apply the effective filter
//        final Filter effectiveFilter = new Filter(filter, event.getElement().getContent().getEffectiveFilter());
//
//        // determine the indicator
//        final int indicatorId = effectiveFilter.getRestrictions(DimensionType.Indicator).iterator().next();
//        effectiveFilter.clearRestrictions(DimensionType.Indicator);
//
//        // TODO : add configuration option to SiteGridPanel that allows choosing indicators
//        
//        load(effectiveFilter);
//    }


    private Filter filterFromAxis(PivotTableData.Axis axis) {

        Filter filter = new Filter();
        while (axis != null) {
            if (axis.getDimension() != null) {
                if (axis.getDimension().getType() == DimensionType.Date) {
                    filter.setDateRange(dates.rangeFromCategory(axis.getCategory()));
                } else if (axis.getCategory() instanceof EntityCategory) {
                    filter.addRestriction(axis.getDimension().getType(), ((EntityCategory) axis.getCategory()).getId());
                }
            }
            axis = axis.getParent();
        }
        return filter;
    }

}


