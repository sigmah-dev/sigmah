package org.sigmah.server.search;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.sigmah.client.search.SearchService;
import org.sigmah.server.search.SolrSearcher;
import org.sigmah.shared.dto.search.SearchResultsDTO;

@SuppressWarnings("serial")
public class SearchServiceImpl extends RemoteServiceServlet implements SearchService{
	
	//implementation of the Search Methods
	@Override
	public ArrayList<SearchResultsDTO> search(String searchStr, String filter){
//		HttpServletRequest request = this.getThreadLocalRequest();
//		HttpSession session = request.getSession();
//		ServletContext context = session.getServletContext();
		return SolrSearcher.getInstance().search(searchStr, filter);
	}
	
	@Override
	public Boolean index() {
		// TODO Auto-generated method stub
		return SolrSearcher.getInstance().FullDataImport();
	}

	@Override
	public Boolean autoIndex() {
		// TODO Auto-generated method stub
		if( SolrIndexJobActivator.getSolrIndexJobActivator() == null )
			return false;
		return true;
	}

	@Override
	public Boolean updateCore(String solrCoreUrl) {
		// TODO Auto-generated method stub
		if( SolrSearcher.getNewInstance(solrCoreUrl) != null ) return true;
		return false;
	}
	
	@Override
	public Boolean filesIndex() throws IOException {
		// TODO Auto-generated method stub
//		System.out.println("Tryna file index, wth is happening here?");
//		FilesSolrHandler filesSolrHandler = null;
//		try {
//			filesSolrHandler = new FilesSolrHandler();
//			//injector.injectMembers(filesSolrHandler);
//			System.out.println("Is this null: " + filesSolrHandler);
//		} catch (RuntimeException e) {
//			// TODO Auto-generated catch block
//			System.out.println("HELL: " + filesSolrHandler);
//			e.printStackTrace();
//		}
//		System.out.println("This is not supposed to be null: " + filesSolrHandler.getFileDAO());
//		return filesSolrHandler.FilesImport(SolrSearcher.getInstance());
		return true;
	}
}
