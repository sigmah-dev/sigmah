package org.sigmah.server.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.*;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.sigmah.shared.dto.search.SearchResultsDTO;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;

public class SolrSearcher {

	private String urlString;
	private SolrClient solrServer;
	private static SolrSearcher instance;

	public String getUrlString() {
		return urlString;
	}

	private SolrSearcher() {
	}

	public static SolrSearcher getInstance() { // Singleton
		
		if (instance == null) {
			instance = new SolrSearcher();
			try {
				instance.loadServer();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return instance;
	}

	private void loadServer() throws MalformedURLException {
		urlString = "http://localhost:8983/solr/Test_Sigmah";
		solrServer = new HttpSolrClient.Builder(urlString).build();
		Window.alert("Successful solr connection!");
	}

	public SolrDocumentList SolrTestQuery() {
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		QueryResponse response;
		try {
			response = solrServer.query(query);
			SolrDocumentList list = response.getResults();
			for (SolrDocument doc : response.getResults()) {
				Window.alert(doc.toString());
			}
			return list;
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	public ArrayList<SearchResultsDTO> search(String searchStr) {

		ArrayList<SearchResultsDTO> searchList = new ArrayList<SearchResultsDTO>();

		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");

		//query.addSortField("weight", ORDER.desc);
		
		QueryResponse rsp = null;

		try {
			if (solrServer != null) {
				rsp = solrServer.query(query);
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rsp != null) {
			Iterator iter = rsp.getResults().iterator();
			while (iter.hasNext()) {
				SearchResultsDTO descriptor = new SearchResultsDTO();
				SolrDocument resultDoc = (SolrDocument) iter.next();
				
				//Will make this more specific later
				Map<String, Object> results = resultDoc.getFieldValueMap();
				for (Map.Entry<String, Object> entry : results.entrySet()){
				    descriptor.getResult().add(entry.getKey().toString() + " ::: " + entry.getValue().toString());
				}

//				descriptor.setUrlOrName((String) resultDoc.getFieldValue("id"));
//				descriptor.setSubText((String) resultDoc.getFieldValue("links"));
//				descriptor.setRelevance((String) resultDoc.getFieldValue("cat"));

				searchList.add(descriptor);

			}
			
//			SolrQuery q = new SolrQuery();
//			QueryRequest req = new QueryRequest(q);
//
//			NoOpResponseParser rawJsonResponseParser = new NoOpResponseParser();
//			rawJsonResponseParser.setWriterType("json");
//			req.setResponseParser(rawJsonResponseParser);
//
//			NamedList<Object> resp = null;
//			try {
//				resp = solrServer.request(req);
//			} catch (SolrServerException | IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			String jsonResponse = (String) resp.get("response");
//
//			System.out.println(jsonResponse );
		}

		return searchList;
	}
	
}