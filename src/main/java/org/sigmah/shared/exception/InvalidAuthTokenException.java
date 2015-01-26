/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.exception;

public class InvalidAuthTokenException extends CommandException {

	private static final long serialVersionUID = 5908497842936393699L;

    public InvalidAuthTokenException() {
		
	}

	public InvalidAuthTokenException(String message) {
		super(message);
	}


}
