package org.sigmah.server.endpoint.gwtrpc.handler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dao.IndicatorDAO;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.IndicatorDTO;
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
	
		final List<IndicatorDTO> list = new ArrayList<IndicatorDTO>();
		
		entityManager.getSession().doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				IndicatorDAO indicatorDAO = new IndicatorDAO(connection);
				list.addAll( indicatorDAO.queryIndicatorsByDatabaseWithCurrentValues(cmd.getUserDatabaseId()));
			}
		});
		
		return new IndicatorListResult(list);
	}
}
