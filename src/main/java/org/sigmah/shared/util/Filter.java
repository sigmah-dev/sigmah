package org.sigmah.shared.util;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.referential.DimensionType;

/**
 * <p>
 * Defines a filter of activity data as a date range and a set of restrictions on {@code Dimensions}.
 * </p>
 * <p>
 * A server-side version of this class exists for XML manipulations, see {@link org.sigmah.server.report.model.Filter}.
 * </p>
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Denis Colliot (dcolliot@ideia.fr) v2.0
 */
public class Filter implements Serializable {

	// TODO: should be restrictions on DIMENSIONS and not DimensionTypes!!

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1723797257472204799L;

	/**
	 * The filter restrictions.
	 */
	// Should not be 'final' due to GWT RPC serialization policy.
	private Map<DimensionType, Set<Integer>> restrictions = new HashMap<DimensionType, Set<Integer>>();

	/**
	 * The filter dates range.
	 */
	private DateRange dateRange = new DateRange();

	/**
	 * Constructs a <code>Filter</code> with no restrictions. All data visible to the user will be included.
	 */
	public Filter() {
		// Serialization.
	}

	/**
	 * Constructs a copy of the given <code>filter</code>
	 * 
	 * @param filter
	 *          The filter which to copy.
	 */
	public Filter(final Filter filter) {
		for (final Map.Entry<DimensionType, Set<Integer>> entry : filter.restrictions.entrySet()) {
			this.restrictions.put(entry.getKey(), new HashSet<Integer>(entry.getValue()));
		}
		this.dateRange = new DateRange(filter.dateRange);
	}

	/**
	 * Constructs a <code>Filter</code> as the intersection between two <code>Filter</code>s.
	 * 
	 * @param a
	 *          The first filter
	 * @param b
	 *          The second filter
	 */
	public Filter(final Filter a, final Filter b) {

		final Set<DimensionType> types = new HashSet<DimensionType>();
		types.addAll(a.restrictions.keySet());
		types.addAll(b.restrictions.keySet());

		for (final DimensionType type : types) {
			this.restrictions.put(type, ClientUtils.intersect(a.getRestrictionSet(type, false), b.getRestrictionSet(type, false)));
		}

		this.dateRange = DateRange.intersection(a.getDateRange(), b.getDateRange());
	}

	public DateRange getDateRange() {
		if (dateRange == null) {
			dateRange = new DateRange();
		}
		return dateRange;
	}
	
	public void setDateRange(DateRange range) {
		this.dateRange = range;
	}

	public Map<DimensionType, Set<Integer>> getRestrictions() {
		return restrictions;
	}

	public void setRestrictions(Map<DimensionType, Set<Integer>> restrictions) {
		this.restrictions = restrictions;
	}
	
	public Set<Integer> getRestrictions(DimensionType type) {
		return getRestrictionSet(type, false);
	}

	private Set<Integer> getRestrictionSet(DimensionType type, boolean create) {
		Set<Integer> set = restrictions.get(type);

		if (set == null) {
			if (!create) {
				return Collections.emptySet();
			}
			set = new HashSet<Integer>();
			restrictions.put(type, set);
		}

		return set;
	}

	public void addRestriction(DimensionType type, int categoryId) {
		Set<Integer> set = getRestrictionSet(type, true);
		set.add(categoryId);
	}

	public void addRestriction(DimensionType type, Collection<Integer> categoryIds) {
		Set<Integer> set = getRestrictionSet(type, true);
		set.addAll(categoryIds);
	}

	public void clearRestrictions(DimensionType type) {
		restrictions.remove(type);
	}

	public boolean isRestricted(DimensionType type) {
		return restrictions.containsKey(type);
	}

	public boolean isDateRestricted() {
		return dateRange.getMinDate() != null || dateRange.getMaxDate() != null;
	}

	public Set<DimensionType> getRestrictedDimensions() {
		return new HashSet<DimensionType>(restrictions.keySet());
	}

	public Date getMinDate() {
		return getDateRange().getMinDate();
	}

	public void setMinDate(Date minDate) {
		getDateRange().setMinDate(minDate);
	}

	public Date getMaxDate() {
		return getDateRange().getMaxDate();
	}

	public void setMaxDate(Date maxDate) {
		getDateRange().setMaxDate(maxDate);
	}

	public Filter onActivity(int activityId) {
		addRestriction(DimensionType.Activity, activityId);
		return this;
	}

	public Filter onSite(int siteId) {
		addRestriction(DimensionType.Site, siteId);
		return this;
	}

	public static Filter filter() {
		return new Filter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		final ToStringBuilder builder = new ToStringBuilder(this);

		builder.append("types", getRestrictedDimensions());
		builder.append("dateRange", dateRange);

		return builder.toString();
	}

}
