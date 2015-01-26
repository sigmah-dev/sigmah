/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report.model.generator;



import org.sigmah.server.domain.User;
import org.sigmah.shared.dto.pivot.content.PivotContent;
import org.sigmah.shared.dto.pivot.model.PivotTableElement;
import org.sigmah.shared.util.DateRange;
import org.sigmah.shared.util.Filter;

public class PivotTableGenerator extends PivotGenerator<PivotTableElement> {

    @Override
    public void generate(User user, PivotTableElement element, Filter inheritedFilter,
                         DateRange dateRange) {

        Filter filter = resolveElementFilter(element, dateRange);
        Filter effectiveFilter = inheritedFilter == null ? filter : new Filter(inheritedFilter, filter);

        PivotContent content = new PivotContent();
        content.setEffectiveFilter(effectiveFilter);
        content.setFilterDescriptions(
                generateFilterDescriptions(
                        filter, element.allDimensionTypes(), user));

        content.setData(generateData(user.getId(), user.getLocaleInstance(), element, effectiveFilter,
                element.getRowDimensions(), element.getColumnDimensions()));

        element.setContent(content);
    }


}
