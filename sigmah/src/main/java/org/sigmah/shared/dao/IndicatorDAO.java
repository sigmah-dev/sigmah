
package org.sigmah.shared.dao;

import org.sigmah.shared.dao.SqlQueryBuilder.ResultHandler;
import org.sigmah.shared.dto.IndicatorDTO;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object for {@link org.sigmah.shared.domain.Indicator} domain objects. 
 * @author Alex Bertram
 */
public class IndicatorDAO  {
	
	private final Connection connection;
	

    public IndicatorDAO(Connection connection) {
		super();
		this.connection = connection;
	}

    /**
     * Returns a sorted list of IndicatorDTOs for the given database,
     * as well as their current aggregate value.
     * 
     * @param databaseId
     * @return
     */
	public List<IndicatorDTO> queryIndicatorsByDatabaseWithCurrentValues(final int databaseId) {
    	final List<IndicatorDTO> list = new ArrayList<IndicatorDTO>();
    	final Map<Integer, IndicatorDTO> map = new HashMap<Integer, IndicatorDTO>();
    	SqlQueryBuilder
    		.select("i.indicatorId")
    			.appendField("i.name")
	    		.appendField("i.aggregation")
	    		.appendField("i.units")
	    		.appendField("i.category")
	    		.appendField("i.description")
	    		.appendField("i.listHeader")
	    		.appendField("i.objective")
	    		.appendField("SUM(v.Value)")
	    		.appendField("COUNT(v.Value)")
	    		.appendField("i.SourceOfVerification")
		.from("Indicator i")
			.leftJoin("Site s").on("s.databaseId=i.databaseId")
			.leftJoin("ReportingPeriod p").on("s.siteId = p.SiteId")
			.leftJoin("IndicatorValue v").on("p.ReportingPeriodId = v.ReportingPeriodId and v.IndicatorId=i.indicatorId")
		.whereTrue("i.databaseId=" + databaseId)
		.whereTrue("i.dateDeleted is null")
		.groupBy("i.indicatorId, i.name, i.aggregation, i.units, i.category, i.description, i.listheader,i.objective," +
				"i.sourceOfVerification,i.sortOrder")
		.orderBy("i.sortOrder")
		.forEachResult(connection, new ResultHandler() {
			
			@Override
			public void handle(ResultSet rs) throws SQLException {
				IndicatorDTO dto = new IndicatorDTO();
				dto.setId(rs.getInt(1));
				dto.setName(rs.getString(2));
				dto.setAggregation(rs.getInt(3));
				dto.setUnits(rs.getString(4));
				dto.setCategory(rs.getString(5));
				dto.setDescription(rs.getString(6));
				dto.setCode(rs.getString(7));
				dto.setDatabaseId(databaseId);
				dto.setSourceOfVerification(rs.getString(11));
								
				double objective = rs.getDouble(8);
				if(!rs.wasNull()) {
					dto.setObjective(objective);
				}

				Double currentValue = null;
				if(dto.getAggregation() == IndicatorDTO.AGGREGATE_SUM) {
					currentValue = rs.getDouble(9);
					if(!rs.wasNull()) {
						dto.setCurrentValue(currentValue);
					}
				} else if(dto.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
					currentValue = rs.getDouble(10);
					if(!rs.wasNull()) {
						dto.setCurrentValue(currentValue);
					}
				} 
				
				list.add(dto);
				map.put(dto.getId(), dto);
			}
		});		
    	
    	SqlQueryBuilder
		.select("i.indicatorId")
    		.appendField("l.code")
    		.appendField("l.element")
    		.appendField("COUNT(v.Value)")
			.from("Indicator i")
				.leftJoin("Site s").on("s.databaseId=i.databaseId")
				.leftJoin("Indicator_labels l").on("i.IndicatorId = l.Indicator_IndicatorId")
				.leftJoin("ReportingPeriod p").on("s.siteId = p.SiteId")
				.leftJoin("IndicatorValue v").on("p.ReportingPeriodId = v.ReportingPeriodId and v.IndicatorId=i.indicatorId and v.Value=l.code")
			.whereTrue("i.databaseId=" + databaseId)
			.where("i.aggregation").equalTo(IndicatorDTO.AGGREGATE_MULTINOMIAL)
			.groupBy("i.indicatorId, l.element, l.code")
			.orderBy("l.code")
			.forEachResult(connection, new ResultHandler() {
				
				@Override
				public void handle(ResultSet rs) throws SQLException {
					int id = rs.getInt(1);
					int code = rs.getInt(2);
					String label = rs.getString(3);
					int count = rs.getInt(4);
					
					IndicatorDTO dto = map.get(id);
					if(dto.getLabels() == null) {
						dto.setLabels(new ArrayList<String>());
					}
					dto.getLabels().add(label);

                    if(dto.getLabelCounts() == null) {
                        dto.setLabelCounts(new ArrayList<Integer>());
                    }
                    dto.getLabelCounts().add(count);
				}
			});		
    	return list;
    }
	
}
