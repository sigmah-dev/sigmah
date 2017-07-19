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
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.sigmah.server.conf.BasicProperties;
import org.sigmah.server.conf.Properties;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.dto.search.SearchResultsDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;

import java.net.*;

public class SolrSearcher {

	private Properties properties = new BasicProperties();

	private String urlString;
	private static SolrClient solrServer;
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
				System.out.println("SOLR CONNECTION FAILED");
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
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			System.out.println("SOLR CONNECTION FAILED");
			e.printStackTrace();
		}
		return instance;
	}

	private void loadServer() throws MalformedURLException {
		// urlString = "http://localhost:8983/solr/Test_Sigmah";
		// urlString = properties.getProperty(PropertyKey.SOLR_CORE_URL);
		//urlString = 
		System.out.println("SOLR CONNECTING TO: " + urlString);
		solrServer = new HttpSolrClient.Builder(urlString).build();
		// Window.alert("Successful solr connection!");
		System.out.println("SOLR CONNECTION CONNECTED");
	}

	public SolrDocumentList SolrTestQuery() {
		SolrQuery query = new SolrQuery();
		query.setQuery("*:*");
		QueryResponse response;
		try {
			response = solrServer.query(query);
			System.out.println("SOLR QUERY HAPPENED");
			SolrDocumentList list = response.getResults();
			for (SolrDocument doc : response.getResults()) {
				// Window.alert(doc.toString());
			}
			return list;
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("SOLR SERVER EXCEPTION Failed");
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("SOLR SERVER EXCEPTION Failed");
			e.printStackTrace();
			return null;
		}

	}

	public ArrayList<SearchResultsDTO> search(String searchStr, String filter) {

		ArrayList<SearchResultsDTO> searchList = new ArrayList<SearchResultsDTO>();

		SolrQuery query = new SolrQuery();
		query.setQuery(searchStr);
		if (filter.equals("Projects"))
			query.set("fq", "PROJECT");
		else if (filter.equals("Contacts"))
			query.set("fq", "CONTACT");
		else if (filter.equals("OrgUnits"))
			query.set("fq", "ORG_UNIT");
		// query.addSortField("weight", ORDER.desc);

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
			Gson gson = new Gson();
			Iterator iter = rsp.getResults().iterator();
			while (iter.hasNext()) {
				SearchResultsDTO descriptor = new SearchResultsDTO();
				SolrDocument resultDoc = (SolrDocument) iter.next();
				descriptor.setResult(gson.toJson(resultDoc).toString());
				searchList.add(descriptor);

			}

		}

		return searchList;
	}

	public static Boolean FullDataImport() {
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