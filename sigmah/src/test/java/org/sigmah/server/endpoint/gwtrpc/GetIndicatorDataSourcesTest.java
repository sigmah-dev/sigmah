/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.GetIndicatorDataSources;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.IndicatorDataSourceList;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.test.InjectionSupport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
public class GetIndicatorDataSourcesTest extends CommandTestCase {


    @Test
    public void indicatorWithNoDataSources() throws CommandException {

        setUser(1);

        IndicatorDataSourceList dsList = execute(new GetIndicatorDataSources(1));
        assertThat(dsList.getData().size(), equalTo(0));
    }

    @Test
    public void indicatorWithAddedDataSources() throws CommandException {
        setUser(1);

        Set<Integer> dataSources = new HashSet<Integer>();
        dataSources.add(2);

        Map<String,Object> changes = new HashMap<String,Object>();
        changes.put("dataSourceIds", dataSources);


        execute(new UpdateEntity("Indicator", 1, changes));

        IndicatorDataSourceList dsList = execute(new GetIndicatorDataSources(1));
        assertThat(dsList.getData().size(), equalTo(1));
        assertThat(dsList.getData().get(0).getIndicatorName(), equalTo("baches"));
        assertThat(dsList.getData().get(0).getIndicatorId(), equalTo(2));
        assertThat(dsList.getData().get(0).getDatabaseId(), equalTo(1));
        assertThat(dsList.getData().get(0).getDatabaseName(), equalTo("PEAR"));
    }

    @Test
    public void createIndicatorWithSources() throws CommandException {

        Set<Integer> dataSources = new HashSet<Integer>();
        dataSources.add(1);
        dataSources.add(2);

        Map<String,Object> properties = new HashMap<String,Object>();
        properties.put("databaseId", 1);
        properties.put("name", "total beneficiaries");
        properties.put("dataSourceIds", dataSources);

        CreateResult created = execute(new CreateEntity("Indicator", properties));

        IndicatorDataSourceList dsList = execute(new GetIndicatorDataSources(created.getNewId()));

        assertThat(dsList.getData().size(), equalTo(2));


    }
}
