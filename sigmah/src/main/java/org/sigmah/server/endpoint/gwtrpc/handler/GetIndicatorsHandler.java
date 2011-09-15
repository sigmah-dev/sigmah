package org.sigmah.server.endpoint.gwtrpc.handler;

import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dao.IndicatorDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetIndicatorsHandler implements CommandHandler<GetIndicators> {

	private final HibernateEntityManager entityManager;
	
	@Inject
	public GetIndicatorsHandler(HibernateEntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}


	@Override
	public CommandResult execute(final GetIndicators cmd, User user)
			throws CommandException {
		
		final IndicatorDAO indicatorDAO = new IndicatorDAO();
		
		entityManager.getSession().doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				indicatorDAO.queryIndicatorGroups(connection, cmd.getUserDatabaseId());
				indicatorDAO.queryIndicatorsByDatabaseWithCurrentValues(connection, cmd.getUserDatabaseId());
			}
		});
		
		return indicatorDAO.getResult();
	}
}
