/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.report.generator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.sigmah.server.dao.PivotDAO;
import org.sigmah.server.dao.PivotDAO.Bucket;
import org.sigmah.server.util.DateUtilCalendarImpl;
import org.sigmah.shared.command.Month;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.date.DateUtil;
import org.sigmah.shared.report.content.DimensionCategory;
import org.sigmah.shared.report.content.LabeledDimensionCategory;
import org.sigmah.shared.report.content.MonthCategory;
import org.sigmah.shared.report.content.PivotTableData;
import org.sigmah.shared.report.content.PivotTableData.Axis;
import org.sigmah.shared.report.content.QuarterCategory;
import org.sigmah.shared.report.content.SimpleCategory;
import org.sigmah.shared.report.content.YearCategory;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateRange;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.PivotElement;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public abstract class PivotGenerator<T extends PivotElement> extends BaseGenerator<T> {

	private DateUtil dateUtil = new DateUtilCalendarImpl();

	public PivotGenerator(PivotDAO pivotDAO) {
		super(pivotDAO);
	}

	protected PivotTableData generateData(int userId, Locale locale,
			T element,
			Filter filter,
			List<Dimension> rowDims, List<Dimension> colDims) {


		Populator populator = new Populator(element, rowDims, colDims, locale);


		if(element.isShowEmptyCells()) {
			Set<Dimension> dateDimensions = new HashSet<Dimension>();
			Set<Dimension> otherDimensions = new HashSet<Dimension>();

			for(Dimension dim : (Set<Dimension>)element.allDimensions()) {
				if(dim instanceof DateDimension) {
					dateDimensions.add(dim);
				} else {
					otherDimensions.add(dim);
				}
			}
			List<Bucket> buckets = Collections.emptyList();
			if(!otherDimensions.isEmpty()) {
				buckets = pivotDAO.queryDimensionCategories(userId, filter, otherDimensions) ;
			}
			if(!filter.getDateRange().isClosed() && !dateDimensions.isEmpty()) {
				throw new RuntimeException("If a date dimension is specified in rows/cols and showEmptyCells is set, " +
						"than a closed DateRange filter must be provided");
			}

			populator.addHeaders(buckets,filter.getDateRange());
		}

		populator.addValues(pivotDAO.aggregate(
				userId, filter,
				element.allDimensions()));


		return populator.getTable();
	}




	private class Populator {

		private Map<Dimension, Comparator<PivotTableData.Axis>> comparators;
		private List<Dimension> rowDims;
		private List<Dimension> colDims;
		private PivotTableData table;
		private Locale locale;

		public Populator(T element, List<Dimension> rowDims, List<Dimension> colDims, Locale locale) {
			this.rowDims = rowDims;
			this.colDims = colDims;
			this.locale = locale;
			comparators = createComparators(element.allDimensions());
			table = new PivotTableData(rowDims, colDims);


		}
		
		private void addValues(List<Bucket> buckets) {
			for(Bucket bucket : buckets) {
				PivotTableData.Axis row = findRowNode(bucket);
				row.setValue(findColumnNode(bucket), bucket.doubleValue(), bucket.count());
			}
		}
		 
		public PivotTableData getTable() {
			return table;
		}
		
		private PivotTableData.Axis findColumnNode(Bucket bucket) {

			return colDims.isEmpty() ? table.getRootColumn() :
				find(table.getRootColumn(), colDims.iterator(), bucket);
		}
		
		private PivotTableData.Axis findRowNode(Bucket bucket) {
			return rowDims.isEmpty() ? table.getRootRow() :
				find(table.getRootRow(), rowDims.iterator(), bucket);
		}
		
		/**
		 * Recursively descends the pivot table axis to find or add the leaf node
		 * for this bucket.
		 * 
		 * @param axis
		 * @param dimensionIterator
		 * @param bucket
		 * @return
		 */
		private PivotTableData.Axis find(PivotTableData.Axis axis,
				Iterator<Dimension> dimensionIterator,
				PivotDAO.Bucket bucket) {

			Dimension childDimension = dimensionIterator.next();
			DimensionCategory category = bucket.getCategory(childDimension);
			PivotTableData.Axis child = null;

			child = axis.getChild(category);
			if (child == null) {

				String categoryLabel = childDimension.getLabel(category);

				if (categoryLabel == null) {
					categoryLabel = renderLabel(childDimension, category);
				}

				child = axis.addChild(childDimension,
						bucket.getCategory(childDimension),
						categoryLabel,
						comparators.get(childDimension));

			}

			if (dimensionIterator.hasNext()) {
				return find(child, dimensionIterator, bucket);
			} else {
				return child;
			}
		}
		

		private String renderLabel(Dimension childDimension, DimensionCategory category) {
			if (category instanceof LabeledDimensionCategory) {
				return ((LabeledDimensionCategory) category).getLabel();
			
			} else if (category instanceof YearCategory) {
				return Integer.toString(((YearCategory) category).getYear());

			} else if (category instanceof QuarterCategory) {
				// TODO: i18n
				QuarterCategory quarter = (QuarterCategory) category;
				return Integer.toString(quarter.getYear()) + "T" + quarter.getQuarter();

			} else if (category instanceof MonthCategory) {
				return renderMonthLabel(category);
				
			} else if (category instanceof SimpleCategory) {
				return ((SimpleCategory) category).getLabel();
			}
			return "(Totale)"; // TODO
		}

		private String renderMonthLabel(DimensionCategory category) {
			SimpleDateFormat format = (SimpleDateFormat) SimpleDateFormat.getDateInstance(SimpleDateFormat.SHORT, locale);
			String[] months = format.getDateFormatSymbols().getShortMonths();

			return months[((MonthCategory) category).getMonth() - 1];
		}
		
		private Dimension next(List<Dimension> list, Dimension parent) {
			int index = list.indexOf(parent);
			return index+1 < list.size() ? list.get(index+1) : null;
		}
		
		private boolean hasNext(List<Dimension> list, Dimension parent) {
			int index = list.indexOf(parent);
			return index+1 < list.size();
		}
		
		private void addHeaders(List<Bucket> databaseRows, DateRange dateRange) {
			if(!rowDims.isEmpty()) {
				addHeaders(table.getRootRow(), rowDims, databaseRows, dateRange);
			}
			if(!colDims.isEmpty()) {
				addHeaders(table.getRootColumn(), colDims, databaseRows, dateRange);
			}
		}

		private void addHeaders(PivotTableData.Axis parent, List<Dimension> dims, List<Bucket> buckets, DateRange range) {
			Dimension childDimension = next(dims, parent.getDimension());
			if(childDimension instanceof DateDimension) {
				if(((DateDimension) childDimension).getUnit() == DateUnit.YEAR) {
					addYears(parent, dims, buckets, range);
				} else if(((DateDimension) childDimension).getUnit() == DateUnit.MONTH) { 
					addMonths(parent, dims, buckets, range);
				}
			} else if(childDimension != null) {
				for(DimensionCategory category : categories(buckets, childDimension)) {
					PivotTableData.Axis child = parent.addChild(childDimension, category, renderLabel(childDimension, category), comparators.get(childDimension));
					addHeaders(child, dims, filter(buckets, childDimension, category), range);
				}
			}
		}
		
		private Set<DimensionCategory> categories(List<Bucket> buckets, Dimension dim) {
			Set<DimensionCategory> set = new HashSet<DimensionCategory>();
			for(Bucket bucket : buckets) {
				DimensionCategory category = bucket.getCategory(dim);
				if(category != null) {
					set.add(category);
				}
			}
			return set;
		}
		
		private List<Bucket> filter(List<Bucket> buckets, Dimension dim, DimensionCategory cat) {
			List<Bucket> filtered = new ArrayList<Bucket>();
			for(Bucket bucket : buckets) {
				if(cat.equals(bucket.getCategory(dim))) {
					filtered.add(bucket);
				}
			}
			return filtered;
		}

		private void addYears(PivotTableData.Axis parent, List<Dimension> dims, List<Bucket> buckets, DateRange dateRange) {
			DateDimension yearDim = new DateDimension(DateUnit.YEAR);
			
			int startYear = dateUtil.getYear(dateRange.getMinDate());
			int endYear = dateUtil.getYear(dateRange.getMaxDate());
			for(int year = startYear; year<=endYear;++year) {
				PivotTableData.Axis child = parent.addChild(yearDim, new YearCategory(year), Integer.toString(year), DEFAULT_COMPARATOR);
				addHeaders(child, dims, buckets, DateRange.intersection(dateRange, dateUtil.yearRange(year)));
			}
		}

		private void addMonths(PivotTableData.Axis parent, List<Dimension> dims, List<Bucket> buckets, DateRange dateRange) {
			DateDimension monthDim = new DateDimension(DateUnit.MONTH);

			Month startMonth = new Month(dateUtil.getYear(dateRange.getMinDate()), dateUtil.getMonth(dateRange.getMinDate()));
			Month endMonth =  new Month(dateUtil.getYear(dateRange.getMaxDate()), dateUtil.getMonth(dateRange.getMaxDate()));
			for(Month m=startMonth;m.compareTo(endMonth)<=0;m=m.next()) {
				MonthCategory monthCategory = new MonthCategory(m.getYear(), m.getMonth());
				Axis child = parent.addChild(monthDim, monthCategory, renderMonthLabel(monthCategory),  DEFAULT_COMPARATOR );
				addHeaders(child, dims, buckets, DateRange.intersection(dateRange, dateUtil.monthRange(m)));
			}
		}
	}

	protected Map<Dimension, Comparator<PivotTableData.Axis>> createComparators(Set<Dimension> dimensions) {
		Map<Dimension, Comparator<PivotTableData.Axis>> map =
			new HashMap<Dimension, Comparator<PivotTableData.Axis>>();

		for (Dimension dimension : dimensions) {
			if (dimension.isOrderDefined()) {
				map.put(dimension, new DefinedCategoryComparator(dimension.getOrdering()));

			} else {
				map.put(dimension, new CategoryComparator());
			}
		}
		return map;
	}

	private static final CategoryComparator DEFAULT_COMPARATOR = new CategoryComparator();

	private static class CategoryComparator implements Comparator<PivotTableData.Axis> {

		@Override
		public int compare(PivotTableData.Axis a1, PivotTableData.Axis a2) {
			Comparable c1 = a1.getCategory().getSortKey();
			Comparable c2 = a2.getCategory().getSortKey();

			if (c1 == null && c2 == null) {
				return 0;
			}
			if (c1 == null) {
				return -1;
			}
			if (c2 == null) {
				return 1;
			}


			return c1.compareTo(c2);
		}
	}

	private static class DefinedCategoryComparator implements Comparator<PivotTableData.Axis> {
		private final Map<DimensionCategory, Integer> orderMap;

		public DefinedCategoryComparator(List<DimensionCategory> order) {
			orderMap = new HashMap<DimensionCategory, Integer>();
			for (int i = 0; i != order.size(); ++i) {
				orderMap.put(order.get(i), i);
			}
		}

		@Override
		public int compare(PivotTableData.Axis a1, PivotTableData.Axis a2) {
			Integer o1 = orderMap.get(a1.getCategory());
			Integer o2 = orderMap.get(a2.getCategory());

			if (o1 == null) {
				o1 = Integer.MAX_VALUE;
			}
			if (o2 == null) {
				o2 = Integer.MAX_VALUE;
			}

			return o1.compareTo(o2);
		}
	}
}
