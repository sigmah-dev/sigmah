package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetUserDatabase;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.UserDatabaseListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.dto.UserDatabaseDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetUserDatabaseHandler implements CommandHandler<GetUserDatabase> {
	
	private static final Log log = LogFactory.getLog(GetUserDatabaseHandler.class);

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetUserDatabaseHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }
	
	@Override
    public CommandResult execute(GetUserDatabase cmd, User user) throws CommandException {

        final List<UserDatabaseDTO> userDatabaseDTOList = new ArrayList<UserDatabaseDTO>();

        // Creates selection query.
        int userId = user.getId();
        final Query query = em.createQuery("SELECT u FROM UserDatabase u " +
        		"WHERE u.owner = " + userId + " ORDER BY u.id");

        // Gets all users entities.
        @SuppressWarnings("unchecked")
        final List<UserDatabase> dbs = (List<UserDatabase>) query.getResultList();

        // Mapping (entity -> dto).
        if (dbs != null) {
            for (final UserDatabase oneDB : dbs) {
                userDatabaseDTOList.add(mapper.map(oneDB, UserDatabaseDTO.class));

            }
        }

        if (log.isDebugEnabled()) {
            log.debug("[execute] Found " + userDatabaseDTOList.size() + " databases.");
        }

        return new UserDatabaseListResult(userDatabaseDTOList);
    }
}
