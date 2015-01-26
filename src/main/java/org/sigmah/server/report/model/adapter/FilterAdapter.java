package org.sigmah.server.report.model.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.sigmah.shared.dto.pivot.content.Filter;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.DateRange;

/**
 * FilterAdapter.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FilterAdapter extends XmlAdapter<FilterAdapter.FilterElement, Filter> {

	/**
	 * Restriction class.
	 */
	public static class Restriction {

		@XmlAttribute
		public String dimension;

		@XmlElement(name = "category")
		public List<String> categories = new ArrayList<String>(0);

	}

	/**
	 * FilterElement class.
	 */
	public static class FilterElement {

		@XmlElement(name = "restriction")
		public List<Restriction> restrictions = new ArrayList<Restriction>(0);

		@XmlElement
		public DateRange dateRange;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Filter unmarshal(final FilterElement element) throws Exception {

		final Filter filter = new Filter();
		filter.setDateRange(element.dateRange);

		for (final Restriction restriction : element.restrictions) {
			for (final String category : restriction.categories) {
				filter.addRestriction(DimensionType.fromString(restriction.dimension), Integer.parseInt(category));
			}
		}

		return filter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FilterElement marshal(final Filter filter) throws Exception {

		final FilterElement element = new FilterElement();
		element.dateRange = filter.getDateRange();

		for (final DimensionType dimensionType : filter.getRestrictedDimensions()) {

			final Restriction restriction = new Restriction();
			restriction.dimension = dimensionType.toString().toLowerCase();

			for (final Integer id : filter.getRestrictions(dimensionType)) {
				restriction.categories.add(id.toString());
			}

			element.restrictions.add(restriction);
		}

		return element;
	}

}
