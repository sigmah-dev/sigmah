package org.sigmah.server.endpoint.export.sigmah.spreadsheet.data;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.sigmah.client.page.project.logframe.CodePolicy;
import org.sigmah.server.endpoint.export.sigmah.Exporter;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.logframe.ExpectedResult;
import org.sigmah.shared.domain.logframe.LogFrame;
import org.sigmah.shared.domain.logframe.LogFrameActivity;
import org.sigmah.shared.domain.logframe.LogFrameGroup;
import org.sigmah.shared.domain.logframe.LogFrameModel;
import org.sigmah.shared.domain.logframe.Prerequisite;
import org.sigmah.shared.domain.logframe.SpecificObjective;
import org.sigmah.shared.dto.IndicatorDTO;

/*
 * Log frame specific
 * data store to be passed to templates
 * 
 * @author sherzod
 * 
 */
public class LogFrameExportData {
	
	
	public final static int NUMBER_OF_COLS = 7;
	
	private final NumberFormat AGGR_SUM_FORMATTER = new DecimalFormat("#,###");
	private final NumberFormat AGGR_AVG_FORMATTER = new DecimalFormat("0.00");
	
	private final Project project;
	private final Exporter exporter;
	private final LogFrame logFrame; 
	private final LogFrameModel logFrameModel; 
	
	private final Map<LogFrameGroup, ArrayList<SpecificObjective>> soMap;
	private final Map<LogFrameGroup, ArrayList<ExpectedResult>> erMap;
	private final Map<LogFrameGroup, ArrayList<LogFrameActivity>> acMap;
	private final Map<LogFrameGroup, ArrayList<Prerequisite>> prMap;
	private final Map<Integer, IndicatorDTO> indMap;
	
	private final ArrayList<SpecificObjective> soMainList;
	private final ArrayList<ExpectedResult> erMainList;
	private final ArrayList<LogFrameActivity> acMainList;
	private final ArrayList<Prerequisite> prMainList;

	private class LogFrameGroupComparator implements Comparator<LogFrameGroup>{

		@Override
		public int compare(LogFrameGroup o1, LogFrameGroup o2) {
			return o1.getLabel().compareTo(o2.getLabel());
		}
		
	}
	
	public LogFrameExportData(final Project project,final Exporter exporter) {
		
		this.project=project;
		this.exporter=exporter;
		this.logFrame=project.getLogFrame();
		this.logFrameModel = logFrame.getLogFrameModel();

		soMap = new TreeMap<LogFrameGroup, 
				ArrayList<SpecificObjective>>(new LogFrameGroupComparator());
		erMap = new TreeMap<LogFrameGroup,
				ArrayList<ExpectedResult>>(new LogFrameGroupComparator());
		acMap = new TreeMap<LogFrameGroup, 
				ArrayList<LogFrameActivity>>(new LogFrameGroupComparator());
		prMap = new TreeMap<LogFrameGroup, 
				ArrayList<Prerequisite>>(new LogFrameGroupComparator());
		indMap = new TreeMap<Integer, IndicatorDTO>();
		
		soMainList=new ArrayList<SpecificObjective>();
		erMainList=new ArrayList<ExpectedResult>();
		acMainList=new ArrayList<LogFrameActivity>();
		prMainList=new ArrayList<Prerequisite>();
		
		fillCollection();
 		sortAll();
	}
	
	private void fillCollection(){
		// SO
		for (final SpecificObjective so : logFrame.getSpecificObjectives()) {
			final LogFrameGroup soGroup = so.getGroup();
			
			ArrayList<SpecificObjective> soList = soMap.get(soGroup);
			if (soList == null) {
				soList = new ArrayList<SpecificObjective>();
				soMap.put(soGroup, soList);
			}
			soList.add(so);
			soMainList.add(so);

			// ER
			for (final ExpectedResult er : so.getExpectedResults()) {
				final LogFrameGroup erGroup = er.getGroup();
				ArrayList<ExpectedResult> erList = erMap.get(erGroup);
				if (erList == null) {
					erList = new ArrayList<ExpectedResult>();
					erMap.put(erGroup, erList);
				}
				erList.add(er);
				erMainList.add(er);

				// Activity
				for (final LogFrameActivity ac : er.getActivities()) {
					final LogFrameGroup acGroup = ac.getGroup();
					ArrayList<LogFrameActivity> acList = acMap
							.get(acGroup);
					if (acList == null) {
						acList = new ArrayList<LogFrameActivity>();
						acMap.put(acGroup, acList);
					}
					acList.add(ac);
					acMainList.add(ac);
				}
			}
		}

		// Prerequisite
		for (final Prerequisite pr : logFrame.getPrerequisites()) {
			final LogFrameGroup prGroup = pr.getGroup();
			ArrayList<Prerequisite> prList = prMap.get(prGroup);
			if (prList == null) {
				prList = new ArrayList<Prerequisite>();
				prMap.put(prGroup, prList);
			}
			prList.add(pr);
			prMainList.add(pr);
		}
	}
	
 
	/*
	 * Sorts items by their positions
	 */
	private void sortAll() {
		// so
		if(getEnableSpecificObjectivesGroups()){		
			for (final LogFrameGroup soGroup : soMap.keySet()) {
				Collections.sort(soMap.get(soGroup),
						new Comparator<SpecificObjective>() {
							@Override
							public int compare(SpecificObjective o1,
									SpecificObjective o2) {
								return o1.getPosition().compareTo(o2.getPosition());
							}
						});
			}
		}else{
			Collections.sort(soMainList,
					new Comparator<SpecificObjective>() {
						@Override
						public int compare(SpecificObjective o1,
								SpecificObjective o2) {
							return o1.getPosition().compareTo(o2.getPosition());
						}
					});
		}

		// er
		if(getEnableExpectedResultsGroups()){
			for (final LogFrameGroup erGroup : erMap.keySet()) {
				Collections.sort(erMap.get(erGroup),
						new Comparator<ExpectedResult>() {
	
							@Override
							public int compare(ExpectedResult o1, ExpectedResult o2) {
								return o1.getPosition().compareTo(o2.getPosition());
							}
						});
			}
		}else{
			Collections.sort(erMainList,
					new Comparator<ExpectedResult>() {

						@Override
						public int compare(ExpectedResult o1, ExpectedResult o2) {
							return o1.getPosition().compareTo(o2.getPosition());
						}
					});
		}
		// ac
		if(getEnableActivitiesGroups()){
			for (final LogFrameGroup group : acMap.keySet()) {
				Collections.sort(acMap.get(group),
						new Comparator<LogFrameActivity>() {
	
							@Override
							public int compare(LogFrameActivity o1, LogFrameActivity o2) {
								return o1.getPosition().compareTo(o2.getPosition());
							}
						});
			}
		}else{
			Collections.sort(acMainList,
					new Comparator<LogFrameActivity>() {

						@Override
						public int compare(LogFrameActivity o1, LogFrameActivity o2) {
							return o1.getPosition().compareTo(o2.getPosition());
						}
					});
		}
		
		//pr
		if(getEnablePrerequisitesGroups()){
			for (final LogFrameGroup group : prMap.keySet()) {
				Collections.sort(prMap.get(group),
						new Comparator<Prerequisite>() {
	
							@Override
							public int compare(Prerequisite o1, Prerequisite o2) {
								return o1.getPosition().compareTo(o2.getPosition());
							}
						});
			}
		}else{
			Collections.sort(prMainList,
					new Comparator<Prerequisite>() {
						@Override
						public int compare(Prerequisite o1, Prerequisite o2) {
							return o1.getPosition().compareTo(o2.getPosition());
						}
					});
		}

	}
	
	public String getFormattedCode(int code) {
		final StringBuilder sb = new StringBuilder();
		sb.append(CodePolicy.getLetter(code, true, 1));
		sb.append(".");
		return sb.toString();
	}
		
	public String getDetailedIndicatorName(Integer indicatorId) {

		IndicatorDTO dto = indMap.get(indicatorId);		
		StringBuilder builder = new StringBuilder(dto.getName());
		builder.append(" (");
		if (dto.getLabelCounts() != null) {
			builder.append(dto.formatMode());
		} else {

			if (dto.getCurrentValue() == null)
				dto.setCurrentValue(0.0);
			if (dto.getAggregation() == IndicatorDTO.AGGREGATE_AVG) {
				builder.append(AGGR_AVG_FORMATTER
						.format(dto.getCurrentValue()));
			} else {
				builder.append(AGGR_SUM_FORMATTER
						.format(dto.getCurrentValue()));
			}
			builder.append("/");
			builder.append(dto.getObjective() != null ? dto.getObjective(): "0.0");
		}
		builder.append(")");

		return builder.toString();
	}

	public String getLocalizedVersion(String key) {
		return exporter.localize(key);
	}

	public String getTitleOfAction() {
		return project.getFullName();
	}

	public String getMainObjective() {
		return logFrame.getMainObjective();
	}

	public Map<LogFrameGroup, ArrayList<SpecificObjective>> getSoMap() {
		return soMap;
	}

	public Map<LogFrameGroup, ArrayList<ExpectedResult>> getErMap() {
		return erMap;
	}

	public Map<LogFrameGroup, ArrayList<LogFrameActivity>> getAcMap() {
		return acMap;
	}

	public Map<LogFrameGroup, ArrayList<Prerequisite>> getPrMap() {
		return prMap;
	} 			

	public Map<Integer, IndicatorDTO> getIndMap() {
		return indMap;
	}
	
	

	public ArrayList<SpecificObjective> getSoMainList() {
		return soMainList;
	}

	public ArrayList<ExpectedResult> getErMainList() {
		return erMainList;
	}

	public ArrayList<LogFrameActivity> getAcMainList() {
		return acMainList;
	}

	public ArrayList<Prerequisite> getPrMainList() {
		return prMainList;
	}

	public boolean getEnableSpecificObjectivesGroups() {
		return logFrameModel.getEnableSpecificObjectivesGroups();
	}

	public boolean getEnableExpectedResultsGroups() {
		return logFrameModel.getEnableExpectedResultsGroups();
	}

	public boolean getEnableActivitiesGroups() {
		return logFrameModel.getEnableActivitiesGroups();
	}

	public boolean getEnablePrerequisitesGroups() {
		return logFrameModel.getEnablePrerequisitesGroups();
	}

}
