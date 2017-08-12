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

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.sigmah.server.conf.Properties;
import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.shared.conf.PropertyKey;

import com.google.inject.Inject;

/**
 * Implementation of {@link FilesSolrManager} job to execute Solr Full Data Import and
 * import of files.Necessary for updating search index.
 * 
 * @author 
 */
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

	@Override
	public Boolean filesImport(SolrSearcher instance) throws IOException {

		System.out.println("Starting files indexing!");
		List<FileVersion> listFileVersions = fileDAO.findAllVersions();
		Boolean allFilesIndexed = true;
		for (FileVersion fv : listFileVersions) {
			if (fileStorageProvider.exists(fv.getPath())) {
				allFilesIndexed = allFilesIndexed && indexFile(fv);
			}
		}
		return allFilesIndexed;

	}

	@Override
	public Boolean indexFile(FileVersion fv) {
		ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");
		try {
			Path path = Paths.get(getStorageRootPath(), fv.getPath());
			System.out.println("Path for the file to be indexed: " + path.toString());
			req.addFile(new File(path.toString()), "pdforword");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		req.setParam("commit", "true");
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
		req.setParam("literal.file_version_id", fv.getId().toString());
		req.setParam("literal.file_author_id", fv.getAuthor().getId().toString());
		
		try {
			SolrSearcher.solrServer.request(req);
			return true;
		} catch (SolrServerException | IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	private String getStorageRootPath() {
		return properties.getProperty(PropertyKey.FILE_REPOSITORY_NAME);
	}

}
