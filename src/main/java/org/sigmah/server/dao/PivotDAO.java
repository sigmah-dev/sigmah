/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.dao;

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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.sigmah.shared.dto.pivot.model.Dimension;
import org.sigmah.shared.dto.pivot.content.DimensionCategory;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;

/**
 * PivotDAO is a reporting data access object that provides aggregation ("or pivoting")
 *  {@link org.sigmah.server.domain.Site}s by a given set of dimensions.
 *
 * @author Alex Bertram
 */
public interface PivotDAO {

    /**
     *
     * @param userId the id of the User for whom the data is restricted
     * @param filter a {@link Filter} restricting the sites
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
        private int aggregation;
        
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
        
		public int aggregation() {
			return aggregation;
		}
		

		public void setAggregation(int aggMethod) {
			this.aggregation = aggMethod;
			
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
