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

/**
 * Main class where a single instance of the Solr client is initialized.
 * Contains essential methods like search and dataimport which are called by
 * {@link SearchServiceImpl}
 * 
 * @author Aditya Adhikary (aditya15007@iiitd.ac.in)
 */
public class SolrSearcher {

	private String urlString;
	public static SolrClient solrServer;
	private static SolrSearcher instance;

	public String getUrlString() {
		return urlString;
	}

	private SolrSearcher() {
	}

	public static SolrSearcher getInstance() { 

		if (instance == null) {
			instance = new SolrSearcher();
			try {
				instance.loadServer();
			} catch (SolrServerException e) {
				System.out.println("SOLR CONNECTION FAILED");
				instance = null;
				e.printStackTrace();
			} catch (MalformedURLException e) {
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

	public static SolrSearcher getNewInstance(String solrCoreUrl) { 

		//only for use when solr core url has been updated
		
		instance = new SolrSearcher();
		instance.urlString = solrCoreUrl;
		try {
			instance.loadServer();
		} catch (SolrServerException e) {
			System.out.println("SOLR CONNECTION FAILED");
			instance = null;
			e.printStackTrace();
		} catch (MalformedURLException e) {
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
		
		if ("Projects".equals(filter)){
			query.set("fq", "doc_type:PROJECT");
			//query.set("qt", "/searchproject");
		}
		else if ("Contacts".equals(filter)){
			query.set("fq", "doc_type:CONTACT");
		}
		else if ("OrgUnits".equals(filter)){
			query.set("fq", "doc_type:ORG_UNIT");
		// query.addSortField("weight", ORDER.desc);
		}else if ("Your Files".equals(filter)){
			query.set("fq", "doc_type:FILE");
		// query.addSortField("weight", ORDER.desc);
		}
		//query.set("qt", "/search");

		QueryResponse rsp = null;

		try {
			if (solrServer != null) {
				rsp = solrServer.query(query);
			}
		} catch (SolrServerException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		if (rsp != null) {
			Gson gson = new Gson();
			Iterator<?> iter = rsp.getResults().iterator();
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
			e.printStackTrace();
			return false;
		}
	}

}