/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.dao.hibernate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.sigmah.server.dao.PivotDAO;
import org.sigmah.server.domain.AggregationMethod;
import org.sigmah.shared.dao.Filter;
import org.sigmah.shared.dao.SQLDialect;
import org.sigmah.shared.dao.SqlQueryBuilder;
import org.sigmah.shared.dao.SqlQueryBuilder.ResultHandler;
import org.sigmah.shared.report.content.DimensionCategory;
import org.sigmah.shared.report.content.EntityCategory;
import org.sigmah.shared.report.content.MonthCategory;
import org.sigmah.shared.report.content.QuarterCategory;
import org.sigmah.shared.report.content.SimpleCategory;
import org.sigmah.shared.report.content.YearCategory;
import org.sigmah.shared.report.model.AdminDimension;
import org.sigmah.shared.report.model.AttributeGroupDimension;
import org.sigmah.shared.report.model.DateDimension;
import org.sigmah.shared.report.model.DateUnit;
import org.sigmah.shared.report.model.Dimension;
import org.sigmah.shared.report.model.DimensionType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.inject.Inject;


/**
 * PivotDAO implementation for hibernate using native SQL.
 *
 */

public class PivotHibernateDAO implements PivotDAO {

	private final EntityManager em;
	private final SQLDialect dialect;


	@Inject
	public PivotHibernateDAO(EntityManager em, SQLDialect dialect) {
		this.em = em;
		this.dialect = dialect;
	}

	/**
	 * Internal interface to a group of objects that are responsible for
	 * bundling results from the SQL ResultSet object into a Bucket
	 */
	private interface Bundler {
		public void bundle(ResultSet rs, Bucket bucket) throws SQLException;
	}

	private static class SumAndAverageBundler implements Bundler {
		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			int aggMethod = rs.getInt(1);

			double value;
			if (aggMethod == AggregationMethod.Sum.code()) {
				value = rs.getDouble(2);
			} else if (aggMethod == AggregationMethod.Average.code()) {
				value = rs.getDouble(3);
			} else if( aggMethod == AggregationMethod.Multinomial.code()) {
				// right now we do not expect any aggregated values at this level
				// this will need to be expanded to a second query to go further than this
				value = rs.getDouble(2);
			} else {
				assert false : "Database has a weird value for aggregation method = " + aggMethod;
			value = rs.getDouble(2);
			}

			bucket.setDoubleValue(value);
			bucket.setCount(rs.getInt(4));
			bucket.setAggregation(aggMethod);
		}
	}

	private static class SiteCountBundler implements Bundler {
		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			bucket.setDoubleValue((double) rs.getInt(1));
		}
	}

	private static class SimpleBundler implements Bundler {
		private final Dimension dimension;
		private final int labelColumnIndex;

		private SimpleBundler(Dimension dimension, int labelColumnIndex) {
			this.labelColumnIndex = labelColumnIndex;
			this.dimension = dimension;
		}

		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			String label = rs.getString(labelColumnIndex);
			if(label != null && !label.trim().isEmpty()) {
				bucket.setCategory(dimension, new SimpleCategory(label));
			}
		}
	}


	private static class AttributeBundler implements Bundler {
		private final Dimension dimension;
		private final int labelColumnIndex;
		private final int attributeCount;

		private AttributeBundler(Dimension dimension, int labelColumnIndex, int attributeCount) {
			this.labelColumnIndex = labelColumnIndex;
			this.dimension = dimension;
			this.attributeCount = attributeCount;
		}

		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {

			StringBuilder buff = new StringBuilder();
			for (int i = labelColumnIndex; i < labelColumnIndex + attributeCount; i++) {

				if (rs.getString(i) != null && !"null".equals(rs.getString(i))) {
					if (buff.length() > 0) {
						buff.append(", ");
					}
					buff.append(rs.getString(i));
				}
			}
			bucket.setCategory(dimension, new SimpleCategory(buff.toString()));
		}
	}

	private static class EntityBundler implements Bundler {
		private final int idColumnIndex;
		private final Dimension dimension;

		private EntityBundler(Dimension key, int idColumnIndex) {
			this.idColumnIndex = idColumnIndex;
			this.dimension = key;
		}

		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			int entityId = rs.getInt(idColumnIndex);
			if(!rs.wasNull()) {	
				bucket.setCategory(dimension, new EntityCategory(
						entityId,
						rs.getString(idColumnIndex + 1)));
			}
		}
	}


	private static class OrderedEntityBundler implements Bundler {
		private final int idColumnIndex;
		private final Dimension dimension;

		private OrderedEntityBundler(Dimension dimension, int idColumnIndex) {
			this.idColumnIndex = idColumnIndex;
			this.dimension = dimension;
		}

		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			int entityId = rs.getInt(idColumnIndex);
			if(!rs.wasNull()) {
				bucket.setCategory(dimension, new EntityCategory(
						entityId,
						rs.getString(idColumnIndex + 1),
						rs.getInt(idColumnIndex + 2)));
			}
		}
	}

	private static class YearBundler implements Bundler {
		private final Dimension dimension;
		private final int yearColumnIndex;

		private YearBundler(Dimension dimension, int yearColumnIndex) {
			this.dimension = dimension;
			this.yearColumnIndex = yearColumnIndex;
		}

		@Override
		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			bucket.setCategory(dimension, new YearCategory(
					rs.getInt(yearColumnIndex)));
		}
	}

	private static class MonthBundler implements Bundler {
		private final Dimension dimension;
		private final int yearColumnIndex;

		private MonthBundler(Dimension dimension, int yearColumnIndex) {
			this.dimension = dimension;
			this.yearColumnIndex = yearColumnIndex;
		}

		@Override
		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			int year = rs.getInt(yearColumnIndex);
			if(!rs.wasNull()) {
				int month = rs.getInt(yearColumnIndex + 1);
				bucket.setCategory(dimension, new MonthCategory(year, month));
			}	
		}
	}

	private static class QuarterBundler implements Bundler {
		private final Dimension dimension;
		private final int yearColumnIndex;

		private QuarterBundler(int yearColumnIndex, Dimension dimension) {
			this.dimension = dimension;
			this.yearColumnIndex = yearColumnIndex;
		}

		@Override
		public void bundle(ResultSet rs, Bucket bucket) throws SQLException {
			int year = rs.getInt(yearColumnIndex);
			int quarter = rs.getInt(yearColumnIndex + 1);

			bucket.setCategory(dimension, new QuarterCategory(year, quarter));
		}
	}

	public List<Bucket> aggregate(int userId, Filter filter, Set<Dimension> dimensions) {
		return aggregate(userId, filter, dimensions, false);
	}

	public List<Bucket> aggregate(int userId, Filter filter, Set<Dimension> dimensions, boolean showEmptyCells) {
		final List<Bucket> buckets = new ArrayList<Bucket>();
		
		
		if(dimensions.contains(new Dimension(DimensionType.Site))) {
			
		}

		Query query1 = new Query(userId, filter, dimensions, buckets);
		query1.queryForSumAndAverages();
		
		Query query2 = new Query(userId, filter, dimensions, buckets);
		query2.queryForSiteCounts();
		
		return buckets;
	}
	
	
	
	@Override
	public List<Bucket> queryDimensionCategories(int userId, Filter filter,
			Set<Dimension> dimensions) {
		
		final List<Bucket> buckets = new ArrayList<Bucket>();

		Filter noDates = new Filter(filter);
		noDates.setMaxDate(null);
		noDates.setMinDate(null);
		
		Query query = new Query(userId, noDates, dimensions, buckets);
		query.queryForDimensionCategories();
		
		return buckets;
	}

	@Override
	public List<String> getFilterLabels(DimensionType type, Collection<Integer> ids) {
		// TODO
		return new ArrayList<String>();
	}

	private class Query {

		private int userId;
		private Filter filter;
		private Set<Dimension> dimensions;
		private List<Bucket> buckets;
		
		public Query(int userId, Filter filter,
				Set<Dimension> dimensions, List<Bucket> buckets) {
			super();
			this.userId = userId;
			this.filter = filter;
			this.dimensions = dimensions;
			this.buckets = buckets;
		}

		private int nextColumnIndex;
		private final StringBuilder from = new StringBuilder();
		private final StringBuilder where = new StringBuilder();    	
		private final StringBuilder columns = new StringBuilder();
		private final StringBuilder groupBy = new StringBuilder();
		private final List<Object> parameters = new ArrayList<Object>();
		private final List<Bundler> bundlers = new ArrayList<Bundler>();

			
		/**
		 * Starts building a query for indicators of type SUM and AVERAGE.
		 * 
		 * <pre>
		 *                                        --> Activity                                      
		 *               [     Indicator      ]  /        
		 * IV --> RP --> |       union        |  --> UserDatabase
		 *         \     [  IDS -> Indicator  ]			     
		 *          \
		 *           --> Site --> Location
		 *           --> Partner (OrgUnit)
		 * 			
		 * </pre> 
		 * 
		 * <p>This query also performs the linking of indicator data sources by using a
		 * derived union to fan an individual result out to both its own proper indicator
		 * and any indicators for which it serves as a source.
		 * 
		 * <p>The Site, Partner (OrgUnit), and Location entities are linked to the source
		 * results, so an indicator with results coming from another database will retain its 
		 * its location and agent (OrgUnit).
		 * 
		 * <p>Source data will, however, be mapped into the destination user database and
		 * activity, so permissions will be applied to the later 
		 * 
		 * 
		 */
		public void queryForSumAndAverages() {
			from.append(" IndicatorValue V " +
					 "LEFT JOIN ReportingPeriod Period ON (Period.ReportingPeriodId=V.ReportingPeriodId) " +
					 "LEFT JOIN (" + 
					 		"(SELECT IndicatorId as SourceId, IndicatorId, Aggregation, Name, Category, SortOrder, DateDeleted, DatabaseId, ActivityId FROM Indicator) " +
					 			" UNION ALL " +
					 		"(SELECT DS.IndicatorSourceId as SourceId, I.IndicatorId, I.Aggregation, I.Name, I.Category, I.SortOrder, I.DateDeleted, I.DatabaseId, I.ActivityId " +
					 			" FROM Indicator_Datasource DS " +
					 			" LEFT JOIN Indicator I ON (DS.IndicatorId = I.IndicatorId) ) " + 
					 		") AS Indicator ON (Indicator.SourceId = V.IndicatorId) " +
					 "LEFT JOIN Site ON (Period.SiteId = Site.SiteId) " +
					 "LEFT JOIN Partner ON (Site.PartnerId = Partner.PartnerId) " +
					 "LEFT JOIN Location ON (Location.LocationId = Site.LocationId) " +
					 "LEFT JOIN UserDatabase ON (Indicator.DatabaseId = UserDatabase.DatabaseId) " +
					 "LEFT JOIN Activity ON (Activity.ActivityId = Site.ActivityId) ");

			 // Retrieve only AVERAGES (of any value), SUMs (non zero), And Multinomial (value labels)
			 where.append("( (V.value <> 0 and Indicator.Aggregation=0) or Indicator.Aggregation=1 or Indicator.Aggregation=3) ");
			 /*
			  * First add the indicator to the query: we can't aggregate values from different
			  * indicators so this is a must
			  */

			 columns.append("Indicator.Aggregation, SUM(V.Value), AVG(V.Value), COUNT(V.Value)");
			 groupBy.append("Indicator.IndicatorId, Indicator.Aggregation");

			 bundlers.add(new SumAndAverageBundler());

			 nextColumnIndex = 5;
			 
			 buildAndExecuteRestOfQuery();
		}

	

		public void queryForSiteCounts() {

			/* We're just going to go ahead and add all the tables we need to the SQL statement;
			 * this saves us some work and hopefully the SQL server will optimze out any unused
			 * tables
			 */

			from.append(" Site " +
					"LEFT JOIN Partner ON (Site.PartnerId = Partner.PartnerId) " +
					"LEFT JOIN Location ON (Location.LocationId = Site.LocationId) " +
					"LEFT JOIN Activity ON (Activity.ActivityId = Site.ActivityId) " +
					"LEFT JOIN Indicator ON (Indicator.ActivityId = Activity.ActivityId) " +
					"LEFT JOIN UserDatabase ON (Activity.DatabaseId = UserDatabase.DatabaseId) " +
			"LEFT JOIN ReportingPeriod Period ON (Period.SiteId = Site.SiteId) ");

			/* First add the indicator to the query: we can't aggregate values from different
			 * indicators so this is a must
			 *
			 */


			columns.append("COUNT(DISTINCT Site.SiteId)");

			groupBy.append("Indicator.IndicatorId");

			where.append("Indicator.Aggregation=2 ");
			
			bundlers.add(new SiteCountBundler());

			nextColumnIndex = 2;

			buildAndExecuteRestOfQuery();
			
		}
		
		public void queryForDimensionCategories() {
			// TODO: partners 
			 from.append(" UserDatabase " +
					"LEFT JOIN Site ON (Site.DatabaseId = UserDatabase.DatabaseId) " +
					"LEFT JOIN Activity ON (Activity.ActivityId = Site.ActivityId) " +
			 		"LEFT JOIN Location ON (Location.LocationId = Site.LocationId) " +
			 		"LEFT JOIN Indicator ON (Indicator.DatabaseId = UserDatabase.DatabaseId) ");
			
			 where.append(" 1=1 ");
			 nextColumnIndex = 1;
			 buildAndExecuteRestOfQuery();
		}
		

		protected void buildAndExecuteRestOfQuery() {


			StringBuilder dimColumns = new StringBuilder();

			/* Now add any other dimensions  */

			for (Dimension dimension : dimensions) {

				if (dimension.getType() == DimensionType.Activity) {
					dimColumns.append(", Site.ActivityId, Activity.Name, Activity.SortOrder");
					bundlers.add(new OrderedEntityBundler(dimension, nextColumnIndex));
					nextColumnIndex += 3;

				} else if (dimension.getType() == DimensionType.ActivityCategory) {
					dimColumns.append(", Activity.Category");
					bundlers.add(new SimpleBundler(dimension, nextColumnIndex));
					nextColumnIndex += 1;

				} else if (dimension.getType() == DimensionType.Site) {
					dimColumns.append(", Site.SiteId, Location.Name" );
					bundlers.add(new EntityBundler(dimension, nextColumnIndex));
					nextColumnIndex += 2;

				} else if (dimension.getType() == DimensionType.Database) {
					dimColumns.append(", Database.DatabaseId, UserDatabase.Name");
					bundlers.add(new EntityBundler(dimension, nextColumnIndex));
					nextColumnIndex += 2;

				} else if (dimension.getType() == DimensionType.Partner) {
					dimColumns.append(", Site.PartnerId, Partner.Name");
					bundlers.add(new EntityBundler(dimension, nextColumnIndex));
					nextColumnIndex += 2;

				} else if (dimension.getType() == DimensionType.Indicator) {
					dimColumns.append(", Indicator.IndicatorId, Indicator.Name, Indicator.SortOrder");
					bundlers.add(new OrderedEntityBundler(dimension, nextColumnIndex));
					nextColumnIndex += 3;

				} else if (dimension.getType() == DimensionType.IndicatorCategory) {
					dimColumns.append(", Indicator.Category");
					bundlers.add(new SimpleBundler(dimension, nextColumnIndex));
					nextColumnIndex += 1;

				} else if (dimension instanceof DateDimension) {
					DateDimension dateDim = (DateDimension) dimension;

					if (dateDim.getUnit() == DateUnit.YEAR) {
						dimColumns.append(", ")
						.append(dialect.yearFunction("Period.Date2"));

						bundlers.add(new YearBundler(dimension, nextColumnIndex));
						nextColumnIndex += 1;

					} else if (dateDim.getUnit() == DateUnit.MONTH) {
						dimColumns.append(", ")
						.append(dialect.yearFunction("Period.Date2"))
						.append(", ")
						.append(dialect.monthFunction("Period.Date2"));

						bundlers.add(new MonthBundler(dimension, nextColumnIndex));
						nextColumnIndex += 2;

					} else if (dateDim.getUnit() == DateUnit.QUARTER) {
						dimColumns.append(", ")
						.append(dialect.yearFunction("Period.Date2"))
						.append(", ")
						.append(dialect.quarterFunction("Period.Date2"));
						bundlers.add(new QuarterBundler(nextColumnIndex, dimension));
						nextColumnIndex += 2;
					}

				} else if (dimension instanceof AdminDimension) {
					AdminDimension adminDim = (AdminDimension) dimension;

					String tableAlias = "AdminLevel" + adminDim.getLevelId();

					from.append(" LEFT JOIN " +
							"(SELECT L.LocationId, E.AdminEntityId, E.Name " +
							"FROM LocationAdminLink L " +
							"LEFT JOIN AdminEntity E ON (L.AdminEntityId=E.AdminEntityID) " +
					"WHERE E.AdminLevelId=").append(adminDim.getLevelId())
					.append(") AS ").append(tableAlias)
					.append(" ON (Location.LocationId=").append(tableAlias).append(".LocationId)");

					dimColumns.append(", ").append(tableAlias).append(".AdminEntityId")
					.append(", ").append(tableAlias).append(".Name");

					bundlers.add(new EntityBundler(adminDim, nextColumnIndex));
					nextColumnIndex += 2;
				} else if (dimension instanceof AttributeGroupDimension) {
					AttributeGroupDimension attrGroupDim = (AttributeGroupDimension) dimension;
					List < Integer > attributeIds = queryAttributeIds(attrGroupDim);
					int count = 0;
					for (Integer attributeId: attributeIds) {
						String tableAlias = "Attribute" + attributeId;

						from.append("LEFT JOIN " +
								"(SELECT AttributeValue.SiteId, Attribute.Name as " + tableAlias + "val " +
								"FROM AttributeValue " +
								"LEFT JOIN  Attribute ON (Attribute.AttributeId = AttributeValue.AttributeId) " +
						"WHERE AttributeValue.value AND Attribute.AttributeId = ")
						.append(attributeId).append(") AS ").append(tableAlias).append(" ON (")
						.append(tableAlias).append(".SiteId = Site.SiteId)");

						dimColumns.append(", ").append(tableAlias).append(".").append(tableAlias).append("val ");
						count++;
					}
					Log.debug("Total attribute column count = " + count);

					bundlers.add(new AttributeBundler(dimension, nextColumnIndex, count));
					nextColumnIndex += count;

				}
			}

			columns.append(dimColumns);
			/* add the dimensions to our column and group by list */
			groupBy.append(dimColumns);

			/* And start on our where clause... */

			// don't include entities that have been deleted
			where.append(" and Site.dateDeleted is null and " +
					"Activity.dateDeleted is null and " +
					"Indicator.dateDeleted is null and " +
			"UserDatabase.dateDeleted is null ");

			// and only allow results that are visible to this user.
			appendVisibilityFilter(where, userId);



			if (filter.getMinDate() != null) {
				where.append(" AND Period.date2 >= ?");
				parameters.add(new java.sql.Date(filter.getMinDate().getTime()));
			}
			if (filter.getMaxDate() != null) {
				where.append(" AND Period.date2 <= ?");
				parameters.add(new java.sql.Date(filter.getMaxDate().getTime()));
			}


			appendDimensionRestrictions(where, filter, parameters);

			if(columns.charAt(0) == ',') {
				columns.setCharAt(0, ' ');
			}
			if(groupBy.charAt(0) == ',') {
				groupBy.setCharAt(0, ' ');
			}
			
			final StringBuilder sql = new StringBuilder();
			sql.append("SELECT ").append(columns).append(" FROM ").append(from)
			.append(" WHERE ").append(where).append(" GROUP BY ").append(groupBy);

			Session session = ((HibernateEntityManager) em).getSession();
			System.out.println(sql.toString());

			session.doWork(new Work() {
				public void execute(Connection connection) throws SQLException {
					PreparedStatement stmt = connection.prepareStatement(sql.toString());

					for (int i = 0; i != parameters.size(); ++i) {
						stmt.setObject(i + 1, parameters.get(i));
					}

					ResultSet rs = stmt.executeQuery();
					while (rs.next()) {
						Bucket bucket = new Bucket();

						for (Bundler bundler : bundlers) {
							bundler.bundle(rs, bucket);
						}

						buckets.add(bucket);
					}
				}
			});
		}


		private List<Integer> queryAttributeIds(AttributeGroupDimension attrGroupDim) {
			return em.createQuery("select a.id from Attribute a where a.group.id=?1")
			.setParameter(1, attrGroupDim.getAttributeGroupId())
			.getResultList();
		}

		
	}
	

	public void appendVisibilityFilter(StringBuilder where, int userId) {
		where.append(" AND ");
		where.append("(UserDatabase.OwnerUserId = ").append(userId).append(" OR ")
		.append(userId).append(" in (select p.UserId from UserPermission p " +
				"where p.AllowView and " +
		"p.UserId=").append(userId).append(" AND p.DatabaseId = UserDatabase.DatabaseId))");
	}

	public static void appendDimensionRestrictions(StringBuilder where, Filter filter, List<Object> parameters) {
		for (DimensionType type : filter.getRestrictedDimensions()) {
			if (type == DimensionType.Indicator) {
				appendIdCriteria(where, "Indicator.IndicatorId", filter.getRestrictions(type), parameters);

			} else if (type == DimensionType.Activity) {
				appendIdCriteria(where, "Site.ActivityId", filter.getRestrictions(type), parameters);

			} else if (type == DimensionType.Site) {
				appendIdCriteria(where, "Site.SiteId", filter.getRestrictions(type), parameters);

			} else if (type == DimensionType.Database) {
				appendIdCriteria(where, "Site.DatabaseId", filter.getRestrictions(type), parameters);

			} else if (type == DimensionType.Partner) {
				appendIdCriteria(where, "Site.PartnerId", filter.getRestrictions(type), parameters);

			} else if (type == DimensionType.AdminLevel) {
				where.append(" AND Site.LocationId IN " +
				"(SELECT Link.LocationId FROM LocationAdminLink Link WHERE 1=1 ");

				appendIdCriteria(where, "Link.AdminEntityId", filter.getRestrictions(type), parameters);
				where.append(") ");
			}
		}
	}

	public static void appendIdCriteria(StringBuilder sb, String fieldName, Collection<Integer> ids, List<Object> parameters) {
		sb.append(" AND ").append(fieldName);


		if (ids.size() == 1) {
			sb.append(" = ?");
		} else {
			sb.append(" IN (? ");
			for (int i = 1; i != ids.size(); ++i) {
				sb.append(", ?");
			}
			sb.append(")");
		}

		for (Integer id : ids) {
			parameters.add(id);
		}
	}
}
