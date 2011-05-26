package org.sigmah.server.endpoint.gwtrpc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.domain.logframe.LogFrameGroupType;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.test.InjectionSupport;


@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/project-indicator.db.xml")
public class UpdateLogFrameTest extends CommandTestCase {

    @Test
    public void logFrame() throws CommandException {
    	
    	createNewLogFrame();
    	
    	// now verify that we can reload
    	ProjectDTO project = execute(new GetProject(1));
    	
    	
    }
    
    @Test
    public void deleteActivity() throws CommandException {
    	
    	createNewLogFrame();
    	
    	// now verify that we can reload
    	ProjectDTO project = execute(new GetProject(1));
    	
    	project.getLogFrameDTO()
    		.getSpecificObjectives().get(0)
    		.getExpectedResults().get(0)
    		.getActivities().remove(1);
    		 
    	
    	execute(new UpdateLogFrame(project.getLogFrameDTO(), 1));

    	// reload project
    	project = execute(new GetProject(1));
    	
    	assertThat("number of activies", project.getLogFrameDTO()
    						.getSpecificObjectives().get(0)
    						.getExpectedResults().get(0)
    						.getActivities().size(), equalTo(1));
    	    	
    }
    
    @Test
    public void saveIndicators() throws CommandException {
    	
    	createNewLogFrame();
    	
    	// now retrieve the new log frame and the list of indicators
    	ProjectDTO project = execute(new GetProject(1));
    	IndicatorListResult indicators = execute(GetIndicators.forDatabase(1));
    	
    	project.getLogFrameDTO()
    		.getSpecificObjectives().get(0)
    		.getIndicators().add( indicators.getData().get(0));
    	
    	
    	execute(new UpdateLogFrame(project.getLogFrameDTO(), project.getId()));
    	
    	// validate the indicator was properly added
    	
    	project = execute(new GetProject(1));

    	assertThat( project.getLogFrameDTO()
    					.getSpecificObjectives().get(0)
    					.getIndicators().get(0)
    						.getName(), equalTo( indicators.getData().get(0).getName()) );
    
    	
    }
    
    @Test
    public void newIndicator() throws CommandException {
    	
    	createNewLogFrame();
    	
    	// add a new indicator
    	ProjectDTO project = execute(new GetProject(1));
    	
    	IndicatorDTO newInd = new IndicatorDTO();
    	newInd.setDatabaseId(1);
    	newInd.setName("Number of children vaccinated");
    	newInd.setAggregation(IndicatorDTO.AGGREGATE_SUM);
    	newInd.setUnits("children");
    	newInd.setObjective(1000);
    	
    	project.getLogFrameDTO()
    		.getSpecificObjectives().get(0)
    		.getIndicators().add( newInd );
    	
    	
    	execute(new UpdateLogFrame(project.getLogFrameDTO(), project.getId()));
    	
    	// validate the indicator was properly added
    	
    	project = execute(new GetProject(1));

    	assertThat( project.getLogFrameDTO()
    					.getSpecificObjectives().get(0)
    					.getIndicators().get(0)
    						.getName(), equalTo( newInd.getName()) );
    	
    	// .. and validate that the indicator is linked to the database
    	IndicatorListResult indicators = execute(GetIndicators.forDatabase(1));
    	IndicatorDTO theNewIndicator = findByName(indicators, newInd.getName());
    	
    }

	private IndicatorDTO findByName(IndicatorListResult indicators, String name) {
		for(IndicatorDTO indicator : indicators.getData()) {
			if(indicator.getName().equals(name)) {
				return indicator;
			}
		}
		throw new AssertionError("indicator with name '" + name + "' not found");
	}

	private void createNewLogFrame() throws CommandException {
		
		LogFrameGroupDTO activityGroup = new LogFrameGroupDTO();
		activityGroup.setType(LogFrameGroupType.ACTIVITY);
		activityGroup.setLabel("Community-based");
		
		LogFrameActivityDTO activity = new LogFrameActivityDTO();
    	activity.setCode(1);
    	activity.setLabel("Vaccination");
    	activity.setAdvancement(0);

    	LogFrameActivityDTO activity2 = new LogFrameActivityDTO();
    	activity2.setCode(2);
    	activity2.setLabel("Awareness raising");
    	activity2.setAdvancement(0);
    	activity2.setGroup(activityGroup);
    	    	
    	ExpectedResultDTO result1 = new ExpectedResultDTO();
    	result1.setCode(1);
    	result1.setLabel("R1");
    	result1.setInterventionLogic("95% of children are vaccinated");
    	result1.setActivities(Arrays.asList(activity, activity2));
    	
    	activity.setParentExpectedResult(result1);
    	activity2.setParentExpectedResult(result1);
    	
    	
    	SpecificObjectiveDTO so1 = new SpecificObjectiveDTO();
    	so1.setCode(1);
    	so1.setAssumptions("The community is open to vaccinating their children");
    	so1.setInterventionLogic("Assure that all children are vaccinated");
    	so1.setRisks("A resumption of hostilities could disrupt the vaccination program");
    	so1.setExpectedResults(Arrays.asList(result1));
    	
    	result1.setParentSpecificObjective(so1);
    	
    	LogFrameModelDTO model = new LogFrameModelDTO();
    	model.setName("Generic Model");
    	
    	LogFrameDTO logFrame = new LogFrameDTO();
    	logFrame.setMainObjective("Reduce child mortalite");
    	logFrame.setSpecificObjectives(Arrays.asList(so1));
    	logFrame.setLogFrameModel(model);
    	logFrame.getGroups().add(activityGroup);
    	
    	so1.setParentLogFrame(logFrame);
    	activityGroup.setParentLogFrame(logFrame);
    	
    	// verify that is saved without error
    	execute(new UpdateLogFrame(logFrame, 1));
	}
    
}
