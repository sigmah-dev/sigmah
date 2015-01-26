/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UnavailableCommandException extends Exception {

    public UnavailableCommandException() {
    }

    public UnavailableCommandException(String message) {
        super(message);
    }
}
