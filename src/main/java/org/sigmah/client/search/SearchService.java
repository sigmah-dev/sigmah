package org.sigmah.client.search;

import java.util.ArrayList;

import org.sigmah.shared.dto.search.SearchResultsDTO;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("search")
public interface SearchService extends RemoteService {
	//TODO Add method stubs here
	ArrayList<SearchResultsDTO> search(String searchStr, String filter);
	Boolean index();
	Boolean autoIndex();
	Boolean updateCore(String solrCoreUrl);
}
