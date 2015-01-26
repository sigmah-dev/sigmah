package org.sigmah.server.endpoint.gwtrpc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.dozer.Mapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.CopyLogFrame;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.command.result.LogFrameResult;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.logframe.IndicatorCopyStrategy;
import org.sigmah.shared.domain.logframe.LogFrame;
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

import com.google.inject.Inject;


@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/project-indicator.db.xml")
public class LogFrameIntegrationTest extends CommandTestCase {

    @Test
    public void logFrame() throws CommandException {
    	
    	createNewLogFrameForProject(1);
    	
    	// now verify that we can reload
    	ProjectDTO project = execute(new GetProject(1));
    	
    	
    }
    
    @Test
    public void deleteActivity() throws CommandException {
    	
    	createNewLogFrameForProject(1);
    	
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
    	
    	createNewLogFrameForProject(1);
    	
    	// now retrieve the new log frame and the list of indicators
    	ProjectDTO project = execute(new GetProject(1));
    	IndicatorListResult indicators = execute(GetIndicators.forDatabase(1));
    	
    	project.getLogFrameDTO()
    		.getSpecificObjectives().get(0)
    		.getIndicators().add( indicators.getData().get(0));
    	
    	
    	LogFrameResult saved = execute(new UpdateLogFrame(project.getLogFrameDTO(), project.getId()));
    	
    	// validate the indicator was properly added
    	
    	project = execute(new GetProject(1));

    	assertThat( project.getLogFrameDTO()
    					.getSpecificObjectives().get(0)
    					.getIndicators().get(0)
    						.getName(), equalTo( indicators.getData().get(0).getName()) );
    
    	
   
    	
    }
    
    @Test
    public void newIndicator() throws CommandException {
    	createNewLogFrameForProject(1);
    	
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
        	
    	// now try resaving to make sure that the indicator preserves its relationship with UserDatabase
    	LogFrameResult afterReSave = execute(new UpdateLogFrame(project.getLogFrameDTO(), project.getId()));
    	
    	IndicatorDTO theNewIndicatorAfterReSave = afterReSave.getLogFrame()
    		.getSpecificObjectives().get(0).getIndicators().get(0);
    	
    	assertThat(theNewIndicatorAfterReSave.getId(), equalTo(theNewIndicator.getId()));
    	
    	
    }


    @Test
    public void copyLogFrame() throws CommandException {
    	
    	createNewLogFrameForProject(1);    	
    	
    	// retrieve the new logframe
    	ProjectDTO project = execute(new GetProject(1));

    	IndicatorDTO newInd = addNewIndicatorToLogFrame(project);

    	LogFrameDTO copiedLogFrame = execute(new CopyLogFrame(project.getLogFrameDTO().getId(), 2));

    	SpecificObjectiveDTO soCopy = copiedLogFrame.getSpecificObjectives().get(0);
    	SpecificObjectiveDTO soOriginal = project.getLogFrameDTO().getSpecificObjectives().get(0);
    	
		assertThat(soCopy.getCode(), equalTo(soOriginal.getCode()));
		assertThat(soCopy.getId(), not(equalTo(soOriginal.getId())));
		assertThat(soCopy.getRisksAndAssumptions(), equalTo(soOriginal.getRisksAndAssumptions()));
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
		    	
    	List<IndicatorDTO> indicators = copiedLogFrame
    		.getSpecificObjectives().get(0)
    		.getIndicators();
    	
		assertThat( indicators.size(), equalTo(1));
    	assertThat( indicators.get(0).getDatabaseId(), equalTo(2));    	
    	
    	// assure that the original is unmolested!
    	IndicatorListResult reloadedIndicators = execute(GetIndicators.forDatabase(1));
    	
    	assertThat( findByName(reloadedIndicators, newInd.getName()).getDatabaseId(), equalTo(1));
    }
    
    @Test
    public void replaceLogFrame() throws CommandException {
    	
    	LogFrameResult result1 = createNewLogFrameForProject(1);
    	LogFrameResult result2 = createNewLogFrameForProject(2);
    	
    	execute(new CopyLogFrame(result1.getLogFrame().getId(), 2));
    	    	
    }
    
    @Test
    @OnDataSet("/dbunit/project-indicator.db.xml")
    public void replaceLogFrameWithIndicatorLinking() throws CommandException {
    	
    	LogFrameResult result1 = createNewLogFrameForProject(1);
    	addNewIndicatorToLogFrame(execute(new GetProject(1)));
    	
    	
    	LogFrameResult result2 = createNewLogFrameForProject(2);
    	
    	execute(CopyLogFrame.from(result1.getLogFrame().getId())
    			.to(2)
    			.with(IndicatorCopyStrategy.DUPLICATE));
    	
    	
    	   	    	    	
    }
    
    

	private IndicatorDTO findByName(IndicatorListResult indicators, String name) {
		for(IndicatorDTO indicator : indicators.getData()) {
			if(indicator.getName().equals(name)) {
				return indicator;
			}
		}
		throw new AssertionError("indicator with name '" + name + "' not found");
	}

	private LogFrameResult createNewLogFrameForProject(int projectId) throws CommandException {
		
    	LogFrameModelDTO model = new LogFrameModelDTO();
    	model.setName("Generic Model");
		
    	LogFrameDTO logFrame = new LogFrameDTO();
    	logFrame.setMainObjective("Reduce child mortalite");
    	logFrame.setLogFrameModel(model);
		
    	LogFrameGroupDTO soGroup = logFrame.addGroup("S.O. 1", LogFrameGroupType.SPECIFIC_OBJECTIVE);
    	
    	SpecificObjectiveDTO so1 = logFrame.addSpecificObjective();
    	so1.setCode(1);
    	so1.setRisksAndAssumptions("The community is open to vaccinating their children");
    	so1.setInterventionLogic("Assure that all children are vaccinated");
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
    	return execute(new UpdateLogFrame(logFrame, projectId));
	}

	private IndicatorDTO addNewIndicatorToLogFrame(ProjectDTO project) throws CommandException {
		IndicatorDTO newInd = new IndicatorDTO();
    	newInd.setDatabaseId(1);
    	newInd.setName("Number of children vaccinated");
    	newInd.setAggregation(IndicatorDTO.AGGREGATE_SUM);
    	newInd.setUnits("children");
    	newInd.setObjective(1000.0d);
    	
    	project.getLogFrameDTO()
    		.getSpecificObjectives().get(0)
    		.getIndicators().add( newInd );
    	
    	execute(new UpdateLogFrame(project.getLogFrameDTO(), project.getId()));
    	
		return newInd;
	}
	
	@Inject
	private Mapper mapper;
	
	@Test
	public void indicatorIdIsMapped() throws CommandException {
		
		LogFrameModelDTO model = new LogFrameModelDTO();
    	model.setName("Generic Model");
		
    	LogFrameDTO logFrame = new LogFrameDTO();
    	logFrame.setMainObjective("Reduce child mortalite");
    	logFrame.setLogFrameModel(model);
		
    	LogFrameGroupDTO soGroup = logFrame.addGroup("S.O. 1", LogFrameGroupType.SPECIFIC_OBJECTIVE);
    	
    	SpecificObjectiveDTO so1 = logFrame.addSpecificObjective();
    	so1.setCode(1);
    	so1.setRisksAndAssumptions("The community is open to vaccinating their children");
    	so1.setInterventionLogic("Assure that all children are vaccinated");
    	so1.setParentLogFrame(logFrame);
    	so1.setGroup(soGroup);
    	
		IndicatorListResult indicators = execute(GetIndicators.forDatabase(1));
		IndicatorDTO anIndicator = indicators.getData().get(0);
		
		so1.getIndicators().add(anIndicator);
		
		LogFrame entity = mapper.map(logFrame, LogFrame.class);
		Indicator anIndicatorMapped = entity.getSpecificObjectives().get(0).getIndicators().iterator().next();
		
		assertThat( anIndicatorMapped.getId(), equalTo( anIndicator.getId() ));
		
	}
    
	
}
