package org.sigmah.server.search;

import java.io.IOException;

public interface FilesSolrManager {

	public Boolean FilesImport(SolrSearcher instance) throws IOException;
	
}
