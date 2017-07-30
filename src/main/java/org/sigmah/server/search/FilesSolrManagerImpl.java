package org.sigmah.server.search;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.common.util.NamedList;
import org.hibernate.metamodel.source.binder.Binder;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.OrgUnitDAO;
import org.sigmah.server.dao.impl.FileHibernateDAO;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.shared.conf.PropertyKey;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

public class FilesSolrManagerImpl implements FilesSolrManager {

	/**
	 * Injected {@link FileDAO}.
	 */
	public FileDAO fileDAO;

	/**
	 * Injected {@link FileDAO}.
	 */
	private FileStorageProvider fileStorageProvider;
	
	/**
	 * Injected application properties.
	 */
	private final Properties properties;

	@Inject
	public FilesSolrManagerImpl(FileDAO fileDAO, FileStorageProvider fileStorageProvider, Properties properties) {
		this.fileDAO = fileDAO;
		this.fileStorageProvider = fileStorageProvider;
		this.properties = properties;
	}

	public FileDAO getFileDAO() {
		return fileDAO;
	}

	@Override
	public Boolean FilesImport(SolrSearcher instance) throws IOException {

		// 2. I get all the files(How?) as java file objects and for each I
		// request the solr server with an update query

		System.out.println("I AM HERE!");
		List<FileVersion> listFileVersions = fileDAO.findAllVersions();
		Boolean allFilesIndexed = true;
		for (FileVersion fv : listFileVersions) {
			if (fileStorageProvider.exists(fv.getPath())) {
					allFilesIndexed = allFilesIndexed && indexFile(fv);
			}
		}
		return allFilesIndexed;
		
	}

	public Boolean indexFile(FileVersion fv){
		ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
		try {
			Path path = Paths.get(getStorageRootPath(), fv.getPath());
			System.out.println("Path for the file: " + path.toString());
//			InputStream inputStream = fileStorageProvider.open(filename);
//			FileUtils.copyInputStreamToFile(inputStream, file);
			req.addFile(new File(path.toString()), "pdforword");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		req.setParam("commit", "true");
		// req.setParam(ExtractingParams.EXTRACT_ONLY, "true");
		req.setParam("literal.doc_id", "FILE_" + fv.getId().toString());
		req.setParam("literal.doc_type", "FILE");
		req.setParam("literal.file_id", fv.getPath());
		req.setParam("literal.file_name", fv.getName());
		req.setParam("literal.file_ext", fv.getExtension());
		req.setParam("literal.file_comments", fv.getComments());
		req.setParam("literal.file_added_date", fv.getAddedDate().toString());
		req.setParam("literal.file_version_num", fv.getVersionNumber().toString());
		req.setParam("literal.file_size", fv.getSize().toString());
		req.setParam("literal.file_author", fv.getAuthor().getFullName());
		req.setParam("literal.file_author_organization", fv.getAuthor().getOrganization().getName());
		req.setParam("literal.file_author_email", fv.getAuthor().getEmail());
		//req.setParam("literal.file_author_email", fv.getAuthor().);
		
		NamedList<Object> result = null;
		try {
			result = SolrSearcher.solrServer.request(req);
			return true;
		} catch (SolrServerException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private String getStorageRootPath() {
		return properties.getProperty(PropertyKey.FILE_REPOSITORY_NAME);
	}
	
}
