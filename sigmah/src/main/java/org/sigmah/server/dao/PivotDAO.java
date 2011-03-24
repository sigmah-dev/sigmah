/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.server.dao.hibernate.PivotHibernateDAO;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.report.content.DimensionCategory;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;

import com.google.inject.ImplementedBy;

/**
 * PivotDAO is a reporting data access object that provides aggregation ("or pivoting")
 *  {@link org.sigmah.shared.domain.Site}s by a given set of dimensions.
 *
 * @author Alex Bertram
 */
@ImplementedBy(PivotHibernateDAO.class)
public interface PivotDAO {

	

    /**
     *
     * @param userId the id of the User for whom the data is restricted
     * @param filter a {@link org.sigmah.shared.dao.Filter filter} restricting the sites
     * @param dimensions
     * @return
     */
    List<Bucket> aggregate(int userId, Filter filter, Set<Dimension> dimensions);
	
	

    List<String> getFilterLabels(DimensionType type, Collection<Integer> ids);

    /**
     * Returns the complete set of dimension categories for a user / filter / dimensions combination.
     * This can be used to build a pivot table with empty cells.
     * 
     * @param userId
     * @param filter
     * @param dimensions
     * @return
     */
    List<Bucket> queryDimensionCategories(int userId, Filter filter, Set<Dimension> dimensions);
    
    /**
     * Contains the aggregate value for an intersection of dimension categories.
     */
    public static class Bucket {
        private double value;
        private int count;
        private Map<Dimension, DimensionCategory> categories = new HashMap<Dimension, DimensionCategory>();

        public Bucket() {
        }

        public Bucket(double doubleValue) {
            this.value = doubleValue;
        }

        public Collection<Dimension> dimensions() {
            return categories.keySet();
        }

        public void setCategory(Dimension dimension, DimensionCategory category) {
            this.categories.put(dimension, category);
        }

        public DimensionCategory getCategory(Dimension dimension) {
            return categories.get(dimension);
        }

        public double doubleValue() {
            return value;
        }

        public void setDoubleValue(double value) {
            this.value = value;
        }
        
        public int count() {
        	return count;
        }
        public void setCount(int count) {
        	this.count = count;
        }
        
        @Override
        public String toString() {
        	StringBuilder sb = new StringBuilder();
        	sb.append("[Value=").append(doubleValue()).append(",Count=").append(count);
        	for(java.util.Map.Entry<Dimension, DimensionCategory> entry : categories.entrySet()) {
        		sb.append(",").append(entry.getKey()).append("=").append(entry.getValue());        		
        	}
        	sb.append("]");
        	return sb.toString();
        }
        
        public static String toString(Iterable<Bucket> buckets) {
        	StringBuilder sb = new StringBuilder();
        	for(Bucket bucket : buckets) {
        		sb.append(bucket).append("\n");
        	}
        	return sb.toString();
        }
    }
}
