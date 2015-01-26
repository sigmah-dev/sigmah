package org.sigmah.server.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.sigmah.server.auth.SecureTokenGenerator;
import org.sigmah.server.dao.PartnerDAO;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dao.UserPermissionDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.domain.UserPermission;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.mail.MailService;
import org.sigmah.shared.command.UpdateUserPermissions;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.UserPermissionDTO;
import org.sigmah.shared.dto.referential.EmailKey;
import org.sigmah.shared.dto.referential.EmailKeyEnum;
import org.sigmah.shared.dto.referential.EmailType;

import com.google.inject.Inject;

/**
 * Handler for {@link UpdateUserPermissions} command
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class UpdateUserPermissionsHandler extends AbstractCommandHandler<UpdateUserPermissions, VoidResult> {

	private final UserDAO userDAO;
	private final UserDatabaseDAO databaseDAO;
	private final PartnerDAO partnerDAO;
	private final UserPermissionDAO permDAO;
	private final MailService mailService;

	@Inject
	public UpdateUserPermissionsHandler(UserDatabaseDAO databaseDAO, PartnerDAO partnerDAO, UserDAO userDAO, UserPermissionDAO permDAO, MailService mailService) {
		this.userDAO = userDAO;
		this.partnerDAO = partnerDAO;
		this.permDAO = permDAO;
		this.mailService = mailService;
		this.databaseDAO = databaseDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoidResult execute(final UpdateUserPermissions cmd, final UserExecutionContext context) throws CommandException {

		UserDatabase database = databaseDAO.findById(cmd.getDatabaseId());
		UserPermissionDTO dto = cmd.getModel();
		User executingUser = context.getUser();

		// --
		// First check that the current user has permission to add users to to the queries.
		// --

		final boolean isOwner = executingUser.getId().equals(database.getOwner().getId());
		if (!isOwner) {
			verifyAuthority(cmd, database.getPermissionByUser(executingUser));
		}

		User user = null;
		if (userDAO.doesUserExist(dto.getEmail())) {
			user = userDAO.findUserByEmail(dto.getEmail());
		}

		if (user == null) {
			user = createNewUser(context, dto);
		}

		// --
		// Does the permission record exist ?
		// --

		UserPermission perm = database.getPermissionByUser(user);
		if (perm == null) {
			perm = new UserPermission(database, user);
			doUpdate(perm, dto, isOwner, database.getPermissionByUser(executingUser));
			permDAO.persist(perm, context.getUser());

		} else {
			doUpdate(perm, dto, isOwner, database.getPermissionByUser(executingUser));
		}

		return null;
	}

	private User createNewUser(final UserExecutionContext context, final UserPermissionDTO dto) {

		final User executingUser = context.getUser();

		// Creating the new user.
		final User user = new User();
		user.setEmail(dto.getEmail());
		user.setName(dto.getName());
		user.setNewUser(true);
		user.setLocale(executingUser.getLocale());
		user.setChangePasswordKey(SecureTokenGenerator.generate());

		userDAO.persist(user, context.getUser());
		try {

			// Preparing the parameters for the email to be sent.
			Map<EmailKey, String> parameters = new HashMap<EmailKey, String>();
			parameters.put(EmailKeyEnum.INVITING_USERNAME, User.getUserCompleteName(executingUser));
			parameters.put(EmailKeyEnum.INVITING_EMAIL, executingUser.getEmail());
			parameters.put(EmailKeyEnum.USER_USERNAME, executingUser.getName());
			parameters.put(EmailKeyEnum.CHANGE_PASS_KEY, user.getChangePasswordKey());

			mailService.send(EmailType.INVITATION, parameters, context.getLanguage(), dto.getEmail());

		} catch (Exception e) {
			// ignore, don't abort because mail didn't work
		}
		return user;
	}

	/**
	 * Verifies that the user executing the command has the permission to do assign these permissions.
	 * <p/>
	 * Static and visible for testing
	 * 
	 * @param cmd
	 * @param executingUserPermissions
	 * @throws IllegalAccessCommandException
	 */
	public static void verifyAuthority(UpdateUserPermissions cmd, UserPermission executingUserPermissions) throws CommandException {
		if (!executingUserPermissions.isAllowManageUsers()) {
			throw new CommandException("Current user does not have the right to manage other users");
		}

		if (!executingUserPermissions.isAllowManageAllUsers() && !executingUserPermissions.getPartner().getId().equals(cmd.getModel().getPartner().getId())) {
			throw new CommandException("Current user does not have the right to manage users from other partners");
		}

		if (!executingUserPermissions.isAllowDesign() && cmd.getModel().getAllowDesign()) {
			throw new CommandException("Current user does not have the right to grant design privileges");
		}

		if (!executingUserPermissions.isAllowManageAllUsers()
			&& (cmd.getModel().getAllowViewAll() || cmd.getModel().getAllowEditAll() || cmd.getModel().getAllowManageAllUsers())) {
			throw new CommandException("Current user does not have the right to grant viewAll, editAll, or manageAllUsers privileges");
		}
	}

	protected void doUpdate(UserPermission perm, UserPermissionDTO dto, boolean isOwner, UserPermission executingUserPermissions) {

		perm.setPartner(partnerDAO.findById(dto.getPartner().getId()));
		perm.setAllowView(dto.getAllowView());
		perm.setAllowEdit(dto.getAllowEdit());
		perm.setAllowManageUsers(dto.getAllowManageUsers());

		// If currentUser does not have the manageAllUsers permission, then
		// careful not to overwrite permissions that may have been granted by
		// other users with greater permissions

		// The exception is when a user with partner-level user management rights
		// (manageUsers but not manageAllUsers) removes view or edit permissions from
		// an existing user who had been previously granted viewAll or editAll rights
		// by a user with greater permissions.
		//
		// In this case, the only logical outcome (I think) is that

		if (isOwner || executingUserPermissions.isAllowManageAllUsers() || !dto.getAllowView()) {
			perm.setAllowViewAll(dto.getAllowViewAll());
		}

		if (isOwner || executingUserPermissions.isAllowManageAllUsers() || !dto.getAllowEdit()) {
			perm.setAllowEditAll(dto.getAllowEditAll());
		}

		if (isOwner || executingUserPermissions.isAllowManageAllUsers()) {
			perm.setAllowManageAllUsers(dto.getAllowManageAllUsers());
		}

		if (isOwner || executingUserPermissions.isAllowDesign()) {
			perm.setAllowDesign(dto.getAllowDesign());
		}

		perm.setLastSchemaUpdate(new Date());
	}
}
