package org.sigmah.server.endpoint.gwtrpc;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.test.InjectionSupport;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetIndicatorsHandlerTest extends CommandTestCase {

    private static final int DATABASE_OWNER = 1;


    @Test
    public void testList() throws CommandException {


        setUser(DATABASE_OWNER);

        GetIndicators cmd = new GetIndicators();
        cmd.setUserDatabaseId(1);
        
        IndicatorListResult result = execute(cmd);    
        
        assertThat(result.getData().size(), equalTo(3));
        
        IndicatorDTO i0 = result.getData().get(0);
        IndicatorDTO i1 = result.getData().get(1);
        IndicatorDTO i2 = result.getData().get(2);
        
        assertThat(i0.getName(), equalTo("baches"));
        
        assertThat(i1.getName(), equalTo("beneficiaries"));
        assertThat(i1.getId(), equalTo(1));
        assertThat(i1.getCurrentValue(), equalTo(15100d));
        assertThat(i1.getObjective(), equalTo(10000d));

        assertThat(i2.getName(), equalTo("Nb. of distributions"));
    }
    
    
    @Test
    @OnDataSet("/dbunit/project-indicator.db.xml")
    public void testListWithCategorical() throws CommandException {


        setUser(DATABASE_OWNER);

        GetIndicators cmd = new GetIndicators();
        cmd.setUserDatabaseId(2);
        
        IndicatorListResult result = execute(cmd);    
        
        assertThat(result.getData().size(), equalTo(2));
        
        IndicatorDTO construction = result.getData().get(0);
        IndicatorDTO service = result.getData().get(1);
        
        assertThat(construction.getLabels().size(), equalTo(3));
        assertThat(construction.getLabels().get(0), equalTo("Good"));
        assertThat(construction.getLabels().get(1), equalTo("Bad"));
        assertThat(construction.getLabels().get(2), equalTo("Ugly"));
        
        assertThat(service.getLabels().size(), equalTo(3));
        assertThat(service.getLabels().get(0), equalTo("Mediocre"));
        assertThat(service.getLabels().get(1), equalTo("Pretty bad"));
        assertThat(service.getLabels().get(2), equalTo("Terrible"));

    }

}
