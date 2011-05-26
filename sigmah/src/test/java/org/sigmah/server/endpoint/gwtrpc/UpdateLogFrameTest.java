package org.sigmah.server.endpoint.gwtrpc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.test.InjectionSupport;


@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/projects.db.xml")
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
    		.getSpecificObjectivesDTO().get(0)
    		.getExpectedResultsDTO().get(0)
    		.getActivitiesDTO().remove(1);
    		 
    	
    	execute(new UpdateLogFrame(project.getLogFrameDTO(), 1));

    	// reload project
    	project = execute(new GetProject(1));
    	
    	assertThat("number of activies", project.getLogFrameDTO()
    						.getSpecificObjectivesDTO().get(0)
    						.getExpectedResultsDTO().get(0)
    						.getActivitiesDTO().size(), equalTo(1));
    	
    	    	
    }
    
    

	private void createNewLogFrame() throws CommandException {
		LogFrameActivityDTO activity = new LogFrameActivityDTO();
    	activity.setCode(1);
    	activity.setLabel("Vaccination");
    	activity.setAdvancement(0);

    	LogFrameActivityDTO activity2 = new LogFrameActivityDTO();
    	activity2.setCode(2);
    	activity2.setLabel("Awareness raising");
    	activity2.setAdvancement(0);
    	
    	ExpectedResultDTO result1 = new ExpectedResultDTO();
    	result1.setCode(1);
    	result1.setLabel("R1");
    	result1.setInterventionLogic("95% of children are vaccinated");
    	result1.setActivitiesDTO(Arrays.asList(activity, activity2));
    	
    	activity.setParentExpectedResultDTO(result1);
    	activity2.setParentExpectedResultDTO(result1);
    	
    	SpecificObjectiveDTO so1 = new SpecificObjectiveDTO();
    	so1.setCode(1);
    	so1.setAssumptions("The community is open to vaccinating their children");
    	so1.setInterventionLogic("Assure that all children are vaccinated");
    	so1.setRisks("A resumption of hostilities could disrupt the vaccination program");
    	so1.setExpectedResultsDTO(Arrays.asList(result1));
    	
    	result1.setParentSpecificObjectiveDTO(so1);
    	
    	LogFrameModelDTO model = new LogFrameModelDTO();
    	model.setName("Generic Model");
    	
    	LogFrameDTO logFrame = new LogFrameDTO();
    	logFrame.setMainObjective("Reduce child mortalite");
    	logFrame.setSpecificObjectivesDTO(Arrays.asList(so1));
    	logFrame.setLogFrameModelDTO(model);
    	
    	so1.setParentLogFrameDTO(logFrame);
    	
    	// verify that is saved without error
    	execute(new UpdateLogFrame(logFrame, 1));
	}
    
}
