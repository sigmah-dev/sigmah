package org.sigmah.client.search;

import java.util.ArrayList;

import org.sigmah.shared.dto.search.SearchResultsDTO;
import com.google.gwt.user.client.rpc.AsyncCallback;


public interface SearchServiceAsync {

	public void search(String searchStr, String filter, Integer UserID, AsyncCallback<ArrayList<SearchResultsDTO>> callback);
	public void index(AsyncCallback<Boolean> callback);
}
