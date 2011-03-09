/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah;

import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;

/**
 * Utility class allowing the creation of a Google Gears query before its execution.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Query {

    private String statement;
    private String[] arguments;

    public Query() {
    }

    public Query(String statement) {
        setStatement(statement);
    }

    public final void setStatement(String statement) {
        this.statement = statement;

        int argumentCount = 0;

        char[] chars = statement.toCharArray();
        for(char letter : chars)
            if(letter == '?')
                argumentCount++;

        arguments = new String[argumentCount];
    }

    public String getStatement() {
        return statement;
    }

    public void setArgument(int index, String argument) {
        this.arguments[index] = argument;
    }

    public void setArgument(int index, Object argument) {
        this.arguments[index] = argument.toString();
    }

    public void setArgument(int index, int argument) {
        this.arguments[index] = Integer.toString(argument);
    }

    public void setArgument(int index, long argument) {
        this.arguments[index] = Long.toString(argument);
    }

    public void setArgument(int index, float argument) {
        this.arguments[index] = Float.toString(argument);
    }

    public void setArgument(int index, double argument) {
        this.arguments[index] = Double.toString(argument);
    }

    public void setArgument(int index, boolean argument) {
        this.arguments[index] = Boolean.toString(argument);
    }

    public void setArgument(int index, char argument) {
        this.arguments[index] = Character.toString(argument);
    }

    public ResultSet execute() throws DatabaseException {
        final Factory factory = Factory.getInstance();
        final Database database = factory.createDatabase();

        database.open(OnlineMode.LOCAL_DATABASE_NAME);

        final ResultSet resultSet = database.execute(statement, arguments);

        database.close();

        return resultSet;
    }

    public ResultSet execute(final Database database) throws DatabaseException {
        return database.execute(statement, arguments);
    }
}
