/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report.model.generator;

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
