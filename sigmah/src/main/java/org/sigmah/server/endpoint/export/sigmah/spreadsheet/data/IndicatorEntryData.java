package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.server.endpoint.export.sigmah.spreadsheet.ExportConstants.MultiItemText;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.report.content.PivotTableData;

public class IndicatorEntryData extends ExportData{
	 
	private final String projectName;
	private final Map<Integer,String> groupMap=new HashMap<Integer,String>();
	private final IndicatorListResult indicators;
	private final Map<Integer,PivotTableData> entryMap=new HashMap<Integer, PivotTableData>();  
	
 	public IndicatorEntryData(final Exporter exporter,
 			final IndicatorListResult indicators,
 			final String projectName){ 		
		super(exporter,4);
		this.indicators=indicators;
		this.projectName=projectName;
	}
 	
 	public Object getFormattedValue(IndicatorDTO dto) {
 		Object formatted=null;
 		if (dto.getLabelCounts() != null) {
 			formatted=dto.formatMode();
		} else {
 			if (dto.getCurrentValue() == null)
				dto.setCurrentValue(0.0);
			if (dto.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
				formatted=dto.getCurrentValue();
			} else {
				formatted=new Long(dto.getCurrentValue().longValue());
			}		
		}
		return formatted;
	}
 	
 	
 	public MultiItemText formatPossibleValues(List<String> list){
 		final StringBuffer builder=new StringBuffer();
        int lines=1;
        for(String text:list){
        	 builder.append(" - ");
			 builder.append(text);
			 builder.append("\n");
			 lines++;
        }
        String value=null;
        if(lines>1){
        	value= builder.substring(0, builder.length()-2);
        	lines--;       
        }
        
 		return new MultiItemText(value, lines);
 	}
 

 	public String getLabelByIndex(List<String> labels,Double doubleIndex){
		String label="";
		int index=0;
	 
		if(doubleIndex!=null){
			index=(int)doubleIndex.doubleValue()-1;
		}
		if(labels.size()>index){
			label=labels.get(index);
		}
		
		return label;		
	}
	 
 
	public Map<Integer, String> getGroupMap() {
		return groupMap;
	}

	public Map<Integer, PivotTableData> getEntryMap() {
		return entryMap;
	}

	public String getProjectName() {
		return projectName;
	}

	public IndicatorListResult getIndicators() {
		return indicators;
	}
	
	
	
	
}
