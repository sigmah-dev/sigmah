package org.sigmah.offline.sync;

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
