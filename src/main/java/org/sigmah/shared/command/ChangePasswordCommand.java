package org.sigmah.shared.command;

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
