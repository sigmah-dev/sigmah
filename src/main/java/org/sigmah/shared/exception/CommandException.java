/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.exception;

import org.sigmah.shared.command.result.CommandResult;

public class CommandException extends Exception implements CommandResult {


    private static final long serialVersionUID = -8176739197006545511L;

    public CommandException(String message) {
        super(message);
    }

    public CommandException() {
	}

    public CommandException(Throwable e) {
        super(e);
    }
}
