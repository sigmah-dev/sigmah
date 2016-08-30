package org.sigmah.offline.sync;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.shared.util.Collections;

/**
 * Adapter for a given <code>AsyncCallback</code> type to another.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @param <I> Input type.
 * @param <O> Output type.
 */
public class AsyncAdapter<I, O> implements AsyncCallback<I> {
	
	private AsyncCallback<O> other;
	private Collections.Mapper<I, O> mapper;

	public AsyncAdapter() {
	}

	public AsyncAdapter(AsyncCallback<O> other) {
		this.other = other;
	}
	
	public AsyncAdapter(AsyncCallback<O> other, Collections.Mapper<I, O> mapper) {
		this.other = other;
		this.mapper = mapper;
	}

	@Override
	public void onFailure(Throwable caught) {
		if (other != null) {
			other.onFailure(caught);
		}
	}

	@Override
	public void onSuccess(I result) {
		if (other != null) {
			final O output;
			if (mapper != null) {
				output = mapper.forEntry(result);
			} else {
				output = null;
			}
			other.onSuccess(output);
		}
	}
	
}
