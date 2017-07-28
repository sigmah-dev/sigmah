package org.sigmah.server.search;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;
import org.hibernate.metamodel.source.binder.Binder;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.impl.FileHibernateDAO;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.inject.PersistenceModule;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

public class FilesSolrHandler {

	@Inject
	public FileDAO fileDAO;
	
//	public FilesSolrHandler(){
//		PersistenceModule pm = new PersistenceModule();
//		Injector injector = Guice.createInjector(pm);
//		fileDAO = injector.getInstance(FileHibernateDAO.class);
//		
//	}
//	
	public FileDAO getFileDAO() {
		return fileDAO;
	}
	
	public Boolean FilesImport(SolrSearcher instance) throws IOException{
		//1. I get all the files(How?) and their metadata, add it to a list of solr docs and then index them
//		SolrDocument solrdoc = new SolrDocument();
//		Collection<SolrInputDocument> docs;
		//solrServer.
		//solrdoc.addField(arg0, arg1);
		
		
		//2. I get all the files(How?) as java file objects and for each I request the solr server with an update query
		
		//instance.fileStorageProvider.open(storageId)
		System.out.println("I AM HERE!");
		System.out.println(fileDAO);
//		Collection<Integer> filesIds = new ArrayList<Integer>();
//		filesIds.add(3887);
//		filesIds.add(3902);
//		filesIds.add(3905);
//		filesIds.add(3907);
//		filesIds.add(3910);
		//List<FileVersion> listFileVersions = fileDAO.findVersions(filesIds, null);
		List<FileVersion> listFileVersions = fileDAO.findAllVersions();
		System.out.println("GUBI" + listFileVersions.toString());
		//will this method even work?
		List<String> filePaths = new ArrayList<String>();
		for( FileVersion fv : listFileVersions){
			System.out.println("IAMHERE Name: " + fv.getName() + " Path: " + fv.getPath());
			filePaths.add(fv.getPath());
//			//fv.getAuthor().getOrganization();
//			ServletUrlBuilder urlBuilder =
//					new ServletUrlBuilder(authenticationProvider, pageManager, ServletConstants.Servlet.FILE, ServletConstants.ServletMethod.DOWNLOAD_FILE);
//			urlBuilder.addParameter(RequestParameter.ID, fv.getParentFile().getId());
		}
		
		ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
		String filename = "/home/aditya/test-tika.pdf";
		req.addFile(new File(filename), "pdf");
		req.setParam("commit", "true");
		//req.setParam(ExtractingParams.EXTRACT_ONLY, "true");
		//req.setParam("literal.doc_type", "FILE");
		
		NamedList<Object> result = null;
		try {
			result = SolrSearcher.solrServer.request(req);
			return true;
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		//System.out.println("Result: " + result.toString());
	}
}

