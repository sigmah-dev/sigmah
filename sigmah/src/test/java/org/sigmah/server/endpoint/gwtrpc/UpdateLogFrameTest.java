package org.sigmah.server.endpoint.gwtrpc;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.CopyLogFrame;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.domain.logframe.LogFrameGroupType;
import org.sigmah.shared.domain.logframe.SpecificObjective;
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
    	
    	// retrieve the new logframe
    	ProjectDTO project = execute(new GetProject(1));
    	
    	IndicatorDTO newInd = addNewIndicatorToLogFrame(project);
    	
    	
    	
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


    @Test
    public void copyLogFrame() throws CommandException {
    	
    	createNewLogFrame();    	
    	
    	// retrieve the new logframe
    	ProjectDTO project = execute(new GetProject(1));

    	IndicatorDTO newInd = addNewIndicatorToLogFrame(project);

    	LogFrameDTO copiedLogFrame = execute(new CopyLogFrame(project.getLogFrameDTO().getId(), 2));

    	SpecificObjectiveDTO soCopy = copiedLogFrame.getSpecificObjectives().get(0);
    	SpecificObjectiveDTO soOriginal = project.getLogFrameDTO().getSpecificObjectives().get(0);
    	
		assertThat(soCopy.getCode(), equalTo(soOriginal.getCode()));
		assertThat(soCopy.getId(), not(equalTo(soOriginal.getId())));
		assertThat(soCopy.getAssumptions(), equalTo(soOriginal.getAssumptions()));
		assertThat(soCopy.getExpectedResults().size(), equalTo(soOriginal.getExpectedResults().size()));
		
		ExpectedResultDTO erCopy = soCopy.getExpectedResults().get(0);
		ExpectedResultDTO erOriginal = soOriginal.getExpectedResults().get(0);
    	
		assertThat(erCopy.getCode(), equalTo(erOriginal.getCode()));
		assertThat(erCopy.getId(), not(equalTo(erOriginal.getId())));
		assertThat(erCopy.getActivities().size(), equalTo(erOriginal.getActivities().size()));
		
		LogFrameActivityDTO aCopy = erCopy.getActivities().get(0);
		LogFrameActivityDTO aOriginal = erOriginal.getActivities().get(0);
		
		assertThat(aCopy.getId(), not(equalTo(aOriginal.getId())));
		assertThat(aCopy.getCode(), equalTo(aOriginal.getCode()));
		assertThat(aCopy.getTitle(), equalTo(aOriginal.getTitle()));
		    	
//    	assertThat( copiedLogFrame
//    		.getSpecificObjectives().get(0)
//    		.getIndicators().size(), equalTo(1));
    	
    	
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
		
    	LogFrameModelDTO model = new LogFrameModelDTO();
    	model.setName("Generic Model");
		
    	LogFrameDTO logFrame = new LogFrameDTO();
    	logFrame.setMainObjective("Reduce child mortalite");
    	logFrame.setLogFrameModel(model);
		
    	LogFrameGroupDTO soGroup = logFrame.addGroup("S.O. 1", LogFrameGroupType.SPECIFIC_OBJECTIVE);
    	
    	SpecificObjectiveDTO so1 = logFrame.addSpecificObjective();
    	so1.setCode(1);
    	so1.setAssumptions("The community is open to vaccinating their children");
    	so1.setInterventionLogic("Assure that all children are vaccinated");
    	so1.setRisks("A resumption of hostilities could disrupt the vaccination program");
    	so1.setParentLogFrame(logFrame);
    	so1.setGroup(soGroup);
    	
    	LogFrameGroupDTO resultGroup = logFrame.addGroup("R. 1", LogFrameGroupType.EXPECTED_RESULT);
    		
    	ExpectedResultDTO result1 = so1.addExpectedResult();
    	result1.setCode(1);
    	result1.setLabel("R1");
    	result1.setInterventionLogic("95% of children are vaccinated");
    	result1.setGroup(resultGroup);
    	
    	
		LogFrameGroupDTO activityGroup = logFrame.addGroup("Community-based", LogFrameGroupType.ACTIVITY);
		
		LogFrameActivityDTO activity = result1.addActivity();
    	activity.setCode(1);
    	activity.setLabel("Vaccination");
    	activity.setAdvancement(0);
    	activity.setGroup(activityGroup);

    	LogFrameActivityDTO activity2 = result1.addActivity();
    	activity2.setCode(2);
    	activity2.setLabel("Awareness raising");
    	activity2.setAdvancement(0);
    	activity2.setGroup(activityGroup);
    	    	
    	
    	// verify that is saved without error
    	execute(new UpdateLogFrame(logFrame, 1));
	}

	private IndicatorDTO addNewIndicatorToLogFrame(ProjectDTO project) throws CommandException {
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
    	
		return newInd;
	}
    
	
}
