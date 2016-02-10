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

import com.google.inject.Inject;

import java.util.Collections;
import org.sigmah.server.domain.User;
import org.sigmah.shared.command.result.Content;
import org.sigmah.shared.dto.pivot.content.ReportContent;
import org.sigmah.shared.dto.pivot.model.PivotTableElement;
import org.sigmah.shared.dto.pivot.model.Report;
import org.sigmah.shared.dto.pivot.model.ReportElement;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.DateRange;
import org.sigmah.shared.util.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReportGenerator extends BaseGenerator<Report> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportGenerator.class);

	@Inject
    private PivotTableGenerator pivotTableGenerator;
//    private PivotChartGenerator pivotChartGenerator;
//    private TableGenerator tableGenerator;
//    private MapGenerator mapGenerator;

    public Content generateElement(User user, ReportElement element, Filter inheritedFilter,
                                   DateRange dateRange) {
//        if (element instanceof PivotChartElement) {
//            pivotChartGenerator.generate(user, (PivotChartElement) element, inheritedFilter, dateRange);
//            return ((PivotChartElement) element).getContent();
//
//        } else 
		if (element instanceof PivotTableElement) {
            pivotTableGenerator.generate(user, (PivotTableElement) element, inheritedFilter, dateRange);
            return ((PivotTableElement) element).getContent();

//        } else if (element instanceof MapElement) {
//            mapGenerator.generate(user, (MapElement) element, inheritedFilter, dateRange);
//            return ((MapElement) element).getContent();
//
//        } else if (element instanceof TableElement) {
//            tableGenerator.generate(user, ((TableElement) element), inheritedFilter, dateRange);
//            return ((TableElement) element).getContent();

        } else {
			LOGGER.warn("Unsupported element: " + element);
			return null;
        }
    }

    @Override
    public void generate(User user, Report report, Filter inheritedFilter, DateRange dateRange) {

        Filter filter = resolveElementFilter(report, dateRange);
        Filter effectiveFilter = resolveEffectiveFilter(report, inheritedFilter, dateRange);

        for (ReportElement element : report.getElements()) {

            generateElement(user, element, effectiveFilter, dateRange);

        }

        ReportContent content = new ReportContent();
        content.setFileName(generateFileName(report, dateRange, user));
        content.setFilterDescriptions(generateFilterDescriptions(effectiveFilter,
                Collections.<DimensionType>emptySet(), user));

        report.setContent(content);

    }

    public String generateFileName(Report report, DateRange dateRange, User user) {

        StringBuilder name = new StringBuilder();

        if (report.getFileName() != null) {
            name.append(resolveTemplate(report.getFileName(),
                    dateRange, user));
        } else if (report.getTitle() != null) {
            name.append(report.getTitle());
        } else {
            name.append("Report");   // TODO: i18n
        }
        return name.toString();
    }


}
