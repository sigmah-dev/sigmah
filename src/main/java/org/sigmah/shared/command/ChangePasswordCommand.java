package org.sigmah.shared.command;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Changes the password of the current user.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ChangePasswordCommand implements Command<VoidResult> {
	/**
	 * Current password.
	 */
	private String currentPassword;
	/**
	 * New password.
	 */
	private String newPassword;
	/**
	 * Confirmation of the new password (must be equals).
	 */
	private String confirmNewPassword;

	protected ChangePasswordCommand() {
	}

	public ChangePasswordCommand(String currentPassword, String newPassword, String confirmNewPassword) {
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
		this.confirmNewPassword = confirmNewPassword;
	}

	/**
	 * Retrieves the current password.
	 * @return The current password.
	 */
	public String getCurrentPassword() {
		return currentPassword;
	}

	/**
	 * Retrieves the new password.
	 * @return The new password.
	 */
	public String getNewPassword() {
		return newPassword;
	}

	/**
	 * Retrieves the password confirmation.
	 * @return The password confirmation.
	 */
	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
}
