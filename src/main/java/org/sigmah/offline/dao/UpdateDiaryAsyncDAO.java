package org.sigmah.offline.dao;

import java.util.LinkedHashMap;
import java.util.Map;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.CommandJS;
import org.sigmah.shared.command.base.Command;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateDiaryAsyncDAO extends AbstractAsyncDAO<Command> {

	@Override
	public void saveOrUpdate(Command t, AsyncCallback<Command> callback, Transaction transaction) {
		final ObjectStore commandObjectStore = transaction.getObjectStore(Store.COMMAND);
		
		final CommandJS commandJS = CommandJS.toJavaScript(t);
		commandObjectStore.add(commandJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving command " + commandJS.getCommandType() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Command " + commandJS.getCommandType() + " has been successfully saved.");
            }
        });
	}

	@Override
	public void get(int id, AsyncCallback<Command> callback, Transaction transaction) {
		throw new UnsupportedOperationException("Not supported.");
	}
	
	public void getAll(final AsyncCallback<Map<Integer, Command>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final LinkedHashMap<Integer, Command> commands = new LinkedHashMap<Integer, Command>();
				
				final ObjectStore commandObjectStore = transaction.getObjectStore(Store.COMMAND);
				final OpenCursorRequest cursorRequest = commandObjectStore.openCursor();
                
                cursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final CommandJS commandJS = (CommandJS) cursor.getValue();
							commands.put(commandJS.getId(), commandJS.toCommand());
							cursor.next();
							
						} else {
							callback.onSuccess(commands);
						}
                    }
                    
                });
            }
        });
	}
	
	@Override
	public Store getRequiredStore() {
		return Store.COMMAND;
	}
	
}
