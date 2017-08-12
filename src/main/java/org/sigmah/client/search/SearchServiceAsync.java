package org.sigmah.client.search;

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

import java.util.ArrayList;

import org.sigmah.server.search.SearchServiceImpl;
import org.sigmah.shared.dto.search.SearchResultsDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Async interface on the client side which is implemented by 
 * {@link SearchServiceImpl} on the server side.
 * 
 */
public interface SearchServiceAsync {

	public void search(String searchStr, String filter, AsyncCallback<ArrayList<SearchResultsDTO>> callback);
	public void index(AsyncCallback<Boolean> callback);
	public void updateCore(String solrCoreUrl, AsyncCallback<Boolean> callback);
}
