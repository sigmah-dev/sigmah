package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.inject.persist.Transactional;
import org.sigmah.server.dao.IndicatorDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dispatch.CommandException;

import java.sql.Connection;
import java.sql.SQLException;
import javax.persistence.EntityManager;
import org.hibernate.HibernateException;
import org.hibernate.ejb.HibernateEntityManager;
import org.hibernate.jdbc.Work;
import org.sigmah.server.dao.impl.IndicatorHibernateDAO;

/**
 * Handler for {@link GetIndicators} command
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetIndicatorsHandler extends AbstractCommandHandler<GetIndicators, IndicatorListResult> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IndicatorListResult execute(final GetIndicators cmd, final UserExecutionContext context) throws CommandException {
		final EntityManager entityManager = em();
		
		// Creates a new instance for every call.
		// Be careful, this must NOT be a singleton because the DAO is stateful.
		final IndicatorDAO indicatorDAO = new IndicatorHibernateDAO();
		
		if(entityManager instanceof HibernateEntityManager) {
			findIndicators((HibernateEntityManager)entityManager, indicatorDAO, cmd.getUserDatabaseId());
		} else {
			throw new UnsupportedOperationException("Entity manager type not supported. Required org.hibernate.ejb.HibernateEntityManager, found: " + entityManager.getClass().getName());
		}
		
		return indicatorDAO.getResult();
	}

	/**
	 * Find the requested indicators in a transaction.
	 *
	 * The transaction is required by IndicatorDAO.
	 * 
	 * @param entityManager An Hibernate entity manager.
	 * @param indicatorDAO A new indicator DAO, dedicated for this use.
	 * @param userDatabaseId Identifier of the database to query.
	 * @throws HibernateException 
	 */
	@Transactional
	protected void findIndicators(final HibernateEntityManager entityManager, final IndicatorDAO indicatorDAO, final int userDatabaseId) throws HibernateException {
		entityManager.getSession().doWork(new Work() {
			
			@Override
			public void execute(Connection connection) throws SQLException {
				indicatorDAO.queryIndicatorGroups(connection, userDatabaseId);
				indicatorDAO.queryIndicatorsByDatabaseWithCurrentValues(connection, userDatabaseId);
			}
		});
	}
}
