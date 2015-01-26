/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;

/**
 *
 * Executes a batch of commands atomically.
 * <p/>
 * Unlike sending multiple commands to the server using
 * {@link org.sigmah.shared.command.RemoteCommandService#execute(String, java.util.List)},
 * the commands in the given list are guaranted to be executed in sequence and within
 * a single transaction. If one command fails, all commands will be rolled back and the
 * BatchCommand will fail.
 * <p/>
 * Returns {@link org.sigmah.shared.command.result.ListResult&lt;Result&gt;}
 *
 * @author Alexander Bertram (akbertram@gmail.com)
 */
public class BatchCommand implements Command<ListResult<Result>> {

    private List<Command> commands = new ArrayList<Command>();

    public BatchCommand() {
    }

    public BatchCommand(Command... commands) {
        this.commands = new ArrayList<Command>(commands.length);
        for(Command cmd : commands) {
            this.commands.add(cmd);
        }
    }

    public BatchCommand(List<Command> commands) {
        this.commands = commands;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public void setCommands(List<Command> commands) {
        this.commands = commands;
    }

    public void add(Command command) {
        commands.add(command);
    }

	public void addAll(BatchCommand batchCommand) {
		commands.addAll(batchCommand.getCommands());
	}
}
