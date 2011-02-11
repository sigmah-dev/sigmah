/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.command.result.RemindersResultList;

/**
 * Request to retrieve the reminder of every project available to the current user.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetReminders implements Command<RemindersResultList> {
    private static final long serialVersionUID = 1L;

}
