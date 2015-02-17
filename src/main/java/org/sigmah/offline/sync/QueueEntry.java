package org.sigmah.offline.sync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.ui.widget.Loadable;

/**
 * Executable entry from a {@link CommandQueue}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface QueueEntry<R> {
	void run(AsyncCallback<R> callback, Loadable... loadables);
}
