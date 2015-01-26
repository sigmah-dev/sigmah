/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import com.google.gwt.gears.client.database.ResultSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.sigmah.client.offline.sigmah.Query;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.OrganizationDTO;

/**
 * Write and read OrganizationDTO locally.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OrganizationDAO {

    private OrganizationDAO() {
    }

    /**
     * Creates the required database to persist {@link OrganizationDTO}.
     * @param database Google Gears database, must have already been opened.
     * @throws DatabaseException
     */
    public static void createTablesIfNotExists(final Database database) throws DatabaseException {
        createOrganizationTable(database);
        createOrgUnitTable(database);
    }

    /**
     * Removes everything related to organizations and organizational units from the local database.
     * @param database Google Gears database, must have already been opened.
     * @throws DatabaseException
     */
    public static void truncateTables(final Database database) throws DatabaseException {
        database.execute("DELETE FROM organization");
        database.execute("DELETE FROM orgUnit");
    }

    /**
     * Insert the given organization into the local database. If an entry with
     * the same ID already exists, it will be overridden.
     * @param organization Organization to write.
     * @param database Google Gears database, must have already been opened.
     */
    public static void insertOrReplaceOrganization(OrganizationDTO organization, final Database database) throws DatabaseException {
        asQuery(organization).execute(database);
        iterateInsertOnOrgUnit(organization.getRoot(), database);
    }

    /**
     * Read an organization from the local database.
     * @param organizationId Identifier of the organization to read.
     * @param database Google Gears database, must have already been opened.
     * @return An OrganizationDTO or <code>null</code> if the given identifier isn't present in the local database.
     * @throws DatabaseException
     */
    public static OrganizationDTO selectOrganization(int organizationId, final Database database) throws DatabaseException {
        final ResultSet resultSet = database.execute("SELECT * FROM organization "
                + "WHERE id = ?",
                Integer.toString(organizationId));

        final OrganizationDTO organizationDTO = asOrganizationDTO(resultSet);
        if(organizationDTO != null) {
            final OrgUnitDTO rootOrgUnit = iterateSelectOnOrgUnit(organizationDTO.getRoot().getId(), database);
            organizationDTO.setRoot(rootOrgUnit);
        }

        return organizationDTO;
    }

    private static void iterateInsertOnOrgUnit(OrgUnitDTO orgUnitDTO, Database database) throws DatabaseException {
        asQuery(orgUnitDTO).execute(database);

        for(OrgUnitDTO child : orgUnitDTO.getChildren())
            iterateInsertOnOrgUnit(child, database);
    }

    private static OrgUnitDTO iterateSelectOnOrgUnit(int orgUnitId, Database database) throws DatabaseException {
        final ResultSet resultSet = database.execute("SELECT * FROM orgUnit "
                + "WHERE id = ?",
                Integer.toString(orgUnitId));

        final OrgUnitDTO orgUnitDTO = asOrgUnitDTO(resultSet);
        if(orgUnitDTO != null)
            orgUnitDTO.setChildren(findSubOrgUnits(orgUnitDTO.getId(), database));

        return orgUnitDTO;
    }

    private static Set<OrgUnitDTO> findSubOrgUnits(int parentOrgUnitId, Database database) throws DatabaseException {
        final ResultSet resultSet = database.execute("SELECT * FROM orgUnit "
                + "WHERE parent = ?",
                Integer.toString(parentOrgUnitId));

        final LinkedHashSet<OrgUnitDTO> children = new LinkedHashSet<OrgUnitDTO>();

        while(resultSet.isValidRow()) {
        	 
            final OrgUnitDTO orgUnitDTO = asOrgUnitDTO(resultSet);
            Log.debug("OrgUnit, parent : "+parentOrgUnitId+", child : "+orgUnitDTO.getId());
            
            if(orgUnitDTO != null)
                orgUnitDTO.setChildren(findSubOrgUnits(orgUnitDTO.getId(), database));

            children.add(orgUnitDTO);
            resultSet.next();
        }

        return children;
    }

    private static void createOrganizationTable(final Database database) throws DatabaseException {
        database.execute("CREATE TABLE IF NOT EXISTS organization ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT,"
                + "logo TEXT,"
                + "root INTEGER"
                + ")");
    }

    private static void createOrgUnitTable(final Database database) throws DatabaseException {
        database.execute("CREATE TABLE IF NOT EXISTS orgUnit ("
                + "id INTEGER PRIMARY KEY,"
                + "name TEXT,"
                + "fullname TEXT,"
                + "orgUnitModel INTEGER,"
                + "organization INTEGER,"
                + "parent INTEGER,"
                + "calendarId INTEGER,"
                + "country INTEGER"
                + ")");
    }

    

    private static Query asQuery(OrganizationDTO organizationDTO) {
        final Query query = new Query("INSERT OR REPLACE INTO organization (id, name, logo, root) VALUES (?, ?, ?, ?)");
        query.setArgument(0, organizationDTO.getId());
        query.setArgument(1, organizationDTO.getName());
        query.setArgument(2, organizationDTO.getLogo());
        query.setArgument(3, organizationDTO.getRoot().getId());

        return query;
    }

    private static Query asQuery(OrgUnitDTO orgUnitDTO) {
        final Query query = new Query("INSERT OR REPLACE INTO orgUnit (id, name, fullname, orgUnitModel, organization, parent, calendarId, country) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        query.setArgument(0, orgUnitDTO.getId());
        query.setArgument(1, orgUnitDTO.getName());
        query.setArgument(2, orgUnitDTO.getFullName());

        final OrgUnitModelDTO model = orgUnitDTO.getOrgUnitModel();
        if(model != null)
            query.setArgument(3, model.getId());


        final OrganizationDTO organization = orgUnitDTO.getOrganization();
        if(organization != null)
            query.setArgument(7, organization.getId());

        final OrgUnitDTO parent = orgUnitDTO.getParent();
        if(parent != null)
            query.setArgument(8, orgUnitDTO.getParent().getId());

        query.setArgument(9, orgUnitDTO.getCalendarId());

        CountryDTO countryDTO = orgUnitDTO.getOfficeLocationCountry();
        if(countryDTO != null)
            query.setArgument(10, countryDTO.getId());

        return query;
    }

    private static OrganizationDTO asOrganizationDTO(ResultSet resultSet) throws DatabaseException {
        if(resultSet == null || !resultSet.isValidRow())
            return null;

        final OrganizationDTO organizationDTO = new OrganizationDTO();
        organizationDTO.setId(resultSet.getFieldAsInt(0));
        organizationDTO.setName(resultSet.getFieldAsString(1));
        organizationDTO.setLogo(resultSet.getFieldAsString(2));

        // Temporary value
        final OrgUnitDTO orgUnitDTO = new OrgUnitDTO();
        orgUnitDTO.setId(resultSet.getFieldAsInt(3));

        organizationDTO.setRoot(orgUnitDTO);

        return organizationDTO;
    }

    private static OrgUnitDTO asOrgUnitDTO(ResultSet resultSet) throws DatabaseException {
        if(resultSet == null || !resultSet.isValidRow())
            return null;

        final OrgUnitDTO orgUnitDTO = new OrgUnitDTO();
        orgUnitDTO.setId(resultSet.getFieldAsInt(0));
        orgUnitDTO.setName(resultSet.getFieldAsString(1));
        orgUnitDTO.setFullName(resultSet.getFieldAsString(2));
        orgUnitDTO.setOrgUnitModel(getDummyOrgUnitModel(resultSet.getFieldAsInt(3)));
        orgUnitDTO.setOrganization(null);
        orgUnitDTO.setParent(null);
        orgUnitDTO.setCalendarId(resultSet.getFieldAsInt(9));
        orgUnitDTO.setOfficeLocationCountry(null);

        return orgUnitDTO;
    }

    private static OrgUnitModelDTO getDummyOrgUnitModel(int orgUnitModelId) {
        final OrgUnitModelDTO orgUnitModelDTO = new OrgUnitModelDTO();
        orgUnitModelDTO.setId(orgUnitModelId);
        orgUnitModelDTO.setName("Dummy");
        orgUnitModelDTO.setTitle("Title");
        orgUnitModelDTO.setCanContainProjects(Boolean.FALSE);
        orgUnitModelDTO.setHasBudget(Boolean.FALSE);

        return orgUnitModelDTO;
    }
}
