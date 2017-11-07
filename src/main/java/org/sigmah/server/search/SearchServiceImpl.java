package org.sigmah.server.search;

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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;
import org.sigmah.client.search.SearchService;
import org.sigmah.server.search.SolrSearcher;
import org.sigmah.shared.dto.search.SearchResultsDTO;

/**
 * Implementation of {@link SearchService} Contains implementations of async methods
 * required for search
 * 
 * @author Aditya Adhikary (aditya15007@iiitd.ac.in)
 */
@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService{

	@Override
	public ArrayList<SearchResultsDTO> search(String searchStr, String filter){
		if( SolrSearcher.getInstance() != null ){
			return SolrSearcher.getInstance().search(searchStr, filter);
		}
		return null;
	}
	
	@Override
	public Boolean index() {
		return SolrSearcher.getInstance().fullDataImport();
	}

	@Override
	public Boolean updateCore(String solrCoreUrl) {
		if( SolrSearcher.getNewInstance(solrCoreUrl) == null ) 
			return false;
		if(SolrSearcher.getInstance().search("test search", "All") == null){
			return false;
		}
		return true;
	}
	
}
