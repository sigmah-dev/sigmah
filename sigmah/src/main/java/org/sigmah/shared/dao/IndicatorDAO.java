
package org.sigmah.shared.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dao.SqlQueryBuilder.ResultHandler;
import org.sigmah.shared.dto.IndicatorDTO;

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
	public List<IndicatorDTO> queryIndicatorsByDatabaseWithCurrentValues(int databaseId) {
    	final List<IndicatorDTO> list = new ArrayList<IndicatorDTO>();
    	
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
		.from("Indicator i")
			.leftJoin("Site s").on("s.databaseId=i.databaseId")
			.leftJoin("ReportingPeriod p").on("s.siteId = p.SiteId")
			.leftJoin("IndicatorValue v").on("p.ReportingPeriodId = v.ReportingPeriodId and v.IndicatorId=i.indicatorId")
		.whereTrue("i.databaseId=" + databaseId)
		.groupBy("i.indicatorId, i.name, i.aggregation, i.units, i.category, i.description, i.listheader,i.objective")
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
				dto.setObjective(rs.getDouble(8));

				if(dto.getAggregation() == IndicatorDTO.AGGREGATE_SUM) {
					dto.setCurrentValue(rs.getDouble(9));
				} else if(dto.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
					dto.setCurrentValue(rs.getDouble(10));
				}
				
				list.add(dto);
			}
		});		
    	
    	return list;
    }
	
}
