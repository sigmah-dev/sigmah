/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.exception;

/*
 * @author Alex Bertram
 */
public class UnexpectedCommandException extends CommandException {


    private static final long serialVersionUID = -4328653065951775285L;

    public UnexpectedCommandException() {
    }

    public UnexpectedCommandException(String message) {
        super(message);
    }

    public UnexpectedCommandException(Throwable e) {
        super(e);
    }
}
