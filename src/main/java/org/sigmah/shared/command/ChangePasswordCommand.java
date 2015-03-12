package org.sigmah.shared.command;

import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.VoidResult;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ChangePasswordCommand implements Command<VoidResult> {
	
	private String currentPassword;
	private String newPassword;
	private String confirmNewPassword;

	protected ChangePasswordCommand() {
	}

	public ChangePasswordCommand(String currentPassword, String newPassword, String confirmNewPassword) {
		this.currentPassword = currentPassword;
		this.newPassword = newPassword;
		this.confirmNewPassword = confirmNewPassword;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getConfirmNewPassword() {
		return confirmNewPassword;
	}
	
}
