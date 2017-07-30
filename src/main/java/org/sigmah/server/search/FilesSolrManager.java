package org.sigmah.server.search;

import java.io.IOException;

import org.sigmah.server.domain.value.FileVersion;

public interface FilesSolrManager {

	public Boolean FilesImport(SolrSearcher instance) throws IOException;

	Boolean indexFile(FileVersion fv);
	
}
