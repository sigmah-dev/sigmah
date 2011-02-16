package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.UserListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetUsersByOrganizationHandler implements CommandHandler<GetUsersByOrganization> {

    private static final Log log = LogFactory.getLog(GetUsersByOrganizationHandler.class);

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetUsersByOrganizationHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult execute(GetUsersByOrganization cmd, User user) throws CommandException {

        final List<UserDTO> userDTOList = new ArrayList<UserDTO>();

        if (log.isDebugEnabled()) {
            log.debug("[execute] Gets users for organization #" + cmd.getOrganizationId() + ".");
        }

        final Integer userId = cmd.getUserId();

        if (userId == null) {

            final Query q = em.createQuery("SELECT u FROM User u WHERE u.organization.id = :orgid");
            q.setParameter("orgid", cmd.getOrganizationId());

            final List<User> users = (List<User>) q.getResultList();

            if (users != null) {
                for (final User u : users) {
                    final UserDTO userDTO = mapper.map(u, UserDTO.class);
                    userDTO.setCompleteName(userDTO.getFirstName() != null ? userDTO.getFirstName() + " "
                            + userDTO.getName() : userDTO.getName());
                    userDTOList.add(userDTO);
                }
            }

        } else {

            final Query q = em.createQuery("SELECT u FROM User u WHERE u.id = :userid AND u.organization.id = :orgid");
            q.setParameter("userid", userId);
            q.setParameter("orgid", cmd.getOrganizationId());

            try {
                final User u = (User) q.getSingleResult();
                final UserDTO userDTO = mapper.map(u, UserDTO.class);
                userDTO.setCompleteName(userDTO.getFirstName() != null ? userDTO.getFirstName() + " "
                        + userDTO.getName() : userDTO.getName());
                userDTOList.add(userDTO);
            } catch (NoResultException e) {
                // nothing.
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("[execute] Found " + userDTOList.size() + " users.");
        }

        return new UserListResult(userDTOList);
    }
}
