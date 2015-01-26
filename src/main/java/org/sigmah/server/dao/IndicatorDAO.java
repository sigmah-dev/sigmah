package org.sigmah.server.dao;

import java.sql.Connection;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Indicator;
import org.sigmah.shared.command.result.IndicatorListResult;

/**
 * Data Access Object for {@link org.sigmah.server.domain.Indicator} domain objects.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface IndicatorDAO extends DAO<Indicator, Integer> {

	public void queryIndicatorGroups(Connection connection, final int databaseId);

	public void queryIndicatorsByDatabaseWithCurrentValues(Connection connection, final int databaseId);

	public IndicatorListResult getResult();

}
