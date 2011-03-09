/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import com.google.inject.Singleton;
import org.sigmah.client.i18n.I18N;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OnlineMode {
    public final static String LOCAL_DATABASE_NAME = "sigmah";
    
    private Database localDatabase;
    private boolean databaseCheckIsDone;

    /**
     * Verify if the usage of Google Gears has been approved by the user.<br>
     * If the permission hasn't been granted yet, this method will ask for
     * permission if <code>askForPermission</code> is true.<br>
     * <br>
     * Also, this method creates the database
     * @param askForPermission <code>true</code> to ask for permission to use
     * Google Gears (only if needed), <code>false</code> otherwise.
     */
    private void checkPermission(boolean askForPermission) {
        final Factory factory = Factory.getInstance();

        if(factory != null && !databaseCheckIsDone) {

            if(!factory.hasPermission() && askForPermission)
                factory.getPermission("Sigmah", "desktopicons/48x48.png", I18N.CONSTANTS.sigmahOfflineDescription());

            if(factory.hasPermission()) {
                localDatabase = factory.createDatabase();

                // Verifying the existence of the "status" table
                localDatabase.open(LOCAL_DATABASE_NAME);
                try {
                    localDatabase.execute("SELECT online FROM status WHERE id = 1");

                } catch (DatabaseException ex) {
                    // Most likely, the database doesn't exists.

                    // Creating the database
                    createStatusTable();

                } finally {
                    try {
                        localDatabase.close();
                    } catch (DatabaseException ex) {
                        Log.debug("Database closing error.", ex);
                    }
                }

                databaseCheckIsDone = true;
            }
        }
    }

    /**
     * Read the current mode in the local database.
     * @return <code>true</code> if the user is in online mode, <code>false</code> otherwise.
     */
    public boolean isOnline() {
        checkPermission(false);

        if(localDatabase != null) {
            localDatabase.open(LOCAL_DATABASE_NAME);
            try {
                final ResultSet result = localDatabase.execute("SELECT online FROM status WHERE id = 1");
                return asBoolean(result.getFieldAsInt(0));

            } catch (DatabaseException ex) {
                // Most likely, the database doesn't exists.
                Log.debug("Error while trying to determine the current mode.", ex);

            } finally {
                try {
                    localDatabase.close();
                } catch (DatabaseException ex) {
                    Log.debug("Database closing error.", ex);
                }
            }
        }

        return true;
    }

    /**
     * Changes the current mode of the application.
     * @param online <code>true</code> to use the online mode, <code>false</code> to use the offline mode.
     */
    public void setOnline(boolean online) {
        checkPermission(true);

        if(localDatabase != null) {
            Log.debug("Setting online state to "+online);

            localDatabase.open(LOCAL_DATABASE_NAME);
            try {
                localDatabase.execute("UPDATE status SET online = ? WHERE id = 1", asString(online));

            } catch (DatabaseException ex) {
                // Most likely, the database doesn't exists.
                Log.debug("Error while writing the new online/offline mode in the database.", ex);

            } finally {
                try {
                    localDatabase.close();
                } catch (DatabaseException ex) {
                    Log.debug("Database closing error.", ex);
                }
            }
        }
    }

    private void createStatusTable() {
        if(localDatabase != null) {
            Log.debug("Creating the table 'status'...");

            try {
                localDatabase.execute(
                          "CREATE TABLE IF NOT EXISTS status ("
                        + "id INTEGER PRIMARY KEY,"
                        + "online INTEGER"
                        + ")");

                localDatabase.execute("INSERT INTO status (id, online) VALUES (1, 1)");

            } catch (DatabaseException ex) {
                Log.debug("Error while creating the 'status' table.", ex);

            }
        }
    }

    private String asString(boolean value) {
        return value ? "1" : "0";
    }

    private boolean asBoolean(int value) {
        return value != 0;
    }
}
