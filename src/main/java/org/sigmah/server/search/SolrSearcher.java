package org.sigmah.server.search;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.sigmah.shared.dto.search.SearchResultsDTO;

import com.google.gson.Gson;

public class SolrSearcher {

	private String urlString;
	public static SolrClient solrServer;
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
			} catch (SolrServerException e) {
				// TODO Auto-generated catch block
				System.out.println("SOLR CONNECTION FAILED");
				instance = null;
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				System.out.println("SOLR CONNECTION FAILED");
				instance = null;
				e.printStackTrace();
			} catch (IOException e){
				System.out.println("SOLR CONNECTION FAILED");
				instance = null;
				e.printStackTrace();
			}
		}
		return instance;
	}

	public static SolrSearcher getNewInstance(String solrCoreUrl) { //only for use when solr core url has been updated

		instance = new SolrSearcher();
		instance.urlString = solrCoreUrl;
		try {
			instance.loadServer();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			System.out.println("SOLR CONNECTION FAILED");
			instance = null;
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("SOLR CONNECTION FAILED");
			instance = null;
			e.printStackTrace();
		} catch (IOException e){
			System.out.println("SOLR CONNECTION FAILED");
			instance = null;
			e.printStackTrace();
		}
		return instance;
	}

	private void loadServer() throws MalformedURLException, SolrServerException, IOException {
		System.out.println("SOLR CONNECTING TO: " + urlString);
		solrServer = new HttpSolrClient.Builder(urlString).build();
		System.out.println("SOLR CONNECTION CONNECTED");
	}

	public ArrayList<SearchResultsDTO> search(String searchStr, String filter) {

		ArrayList<SearchResultsDTO> searchList = new ArrayList<SearchResultsDTO>();

		SolrQuery query = new SolrQuery();
		query.setQuery(searchStr);
		//query.addSort("doc_id", ORDER.desc);
		
		if (filter.equals("Projects")){
			query.set("fq", "doc_type:PROJECT");
			//query.set("qt", "/searchproject");
		}
		else if (filter.equals("Contacts")){
			query.set("fq", "doc_type:CONTACT");
		}
		else if (filter.equals("OrgUnits")){
			query.set("fq", "doc_type:ORG_UNIT");
		// query.addSortField("weight", ORDER.desc);
		}else if (filter.equals("Your Files")){
			query.set("fq", "doc_type:FILE");
		// query.addSortField("weight", ORDER.desc);
		}
		else{
			//query.set("qt", "/search");
		}

		QueryResponse rsp = null;

		try {
			if (solrServer != null) {
				rsp = solrServer.query(query);
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		if (rsp != null) {
			Gson gson = new Gson();
			Iterator<?> iter = rsp.getResults().iterator();
			//rsp.get
			//rsp.getHighlighting().get("ORG_UNIT_1637").get("org_unit_model_name").get(0); //returns highlighted string
			//how do i send it from back-end to front end, very hard
			while (iter.hasNext()) {
				SearchResultsDTO descriptor = new SearchResultsDTO();
				SolrDocument resultDoc = (SolrDocument) iter.next();
				descriptor.setResult(gson.toJson(resultDoc).toString());
				searchList.add(descriptor);

			}

		}

		return searchList;
	}

	public static Boolean fullDataImport() {
		ModifiableSolrParams params = new ModifiableSolrParams();
		params.set("qt", "/dataimport");
		params.set("command", "full-import");
		try {
			QueryResponse response = solrServer.query(params);
			if (response != null) {
				System.out.println("Successful FULL DATA IMPORT! " + System.currentTimeMillis());
				return true;
			} else {
				System.out.println("Failure in FULL  DATA IMPORT! " + System.currentTimeMillis());
				return false;
			}
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

}