package org.sigmah.server.search;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.*;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.impl.*;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.util.NamedList;
import org.sigmah.server.conf.BasicProperties;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.shared.conf.PropertyKey;
import org.sigmah.shared.dto.search.SearchResultsDTO;
import org.sigmah.shared.servlet.ServletUrlBuilder;
import org.sigmah.shared.servlet.ServletConstants;

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
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("SOLR CONNECTION FAILED");
			instance = null;
			e.printStackTrace();
			return null;
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

	public ArrayList<SearchResultsDTO> search(String searchStr, String filter) {

		ArrayList<SearchResultsDTO> searchList = new ArrayList<SearchResultsDTO>();

		SolrQuery query = new SolrQuery();
		query.setQuery(searchStr);
		//query.addSort("doc_id", ORDER.desc);
		
		if (filter.equals("Projects")){
			query.set("fq", "PROJECT");
			//query.set("qt", "/searchproject");
		}
		else if (filter.equals("Contacts")){
			query.set("fq", "CONTACT");
		}
		else if (filter.equals("OrgUnits")){
			query.set("fq", "ORG_UNIT");
		// query.addSortField("weight", ORDER.desc);
		}else{
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rsp != null) {
			Gson gson = new Gson();
			Iterator iter = rsp.getResults().iterator();
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