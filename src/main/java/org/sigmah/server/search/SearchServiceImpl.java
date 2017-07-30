package org.sigmah.server.search;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import org.sigmah.client.search.SearchService;
import org.sigmah.server.search.SolrSearcher;
import org.sigmah.shared.dto.search.SearchResultsDTO;

@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService{
	
	//implementation of the Search Methods
	@Override
	public ArrayList<SearchResultsDTO> search(String searchStr, String filter){
		return SolrSearcher.getInstance().search(searchStr, filter);
	}
	
	@Override
	public Boolean index() {
		return SolrSearcher.getInstance().FullDataImport();
	}

	@Override
	public Boolean autoIndex() {
		if( SolrIndexJobActivator.getSolrIndexJobActivator() == null )
			return false;
		return true;
	}

	@Override
	public Boolean updateCore(String solrCoreUrl) {
		if( SolrSearcher.getNewInstance(solrCoreUrl) != null ) return true;
		return false;
	}
	
}
