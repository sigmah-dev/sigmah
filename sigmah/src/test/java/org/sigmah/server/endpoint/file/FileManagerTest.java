package org.sigmah.server.endpoint.file;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.server.endpoint.file.FileManager.DownloadableFile;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.test.InjectionSupport;
import org.sigmah.test.MockHibernateModule;
import org.sigmah.test.Modules;

import com.google.inject.Inject;


@RunWith(InjectionSupport.class)
@Modules({
        MockHibernateModule.class,
        FileManagerTestConfigModule.class
})
@OnDataSet("/dbunit/projects.db.xml")
public class FileManagerTest {
	
	
	private static final int AUTHOR_ID = 1;
	

	@Inject
	private FileManagerImpl fileManager;
	
	@Inject
	private FileStorageProvider storageProvider;
		

	
	@Test
	public void testLocalFileSystem() throws IOException {
		
		Map<String, String> fileProperties = new HashMap<String, String>();
		fileProperties.put(FileUploadUtils.DOCUMENT_NAME, "/home/alex/test.doc");
		fileProperties.put(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT, "1");
				
		String fileId = fileManager.saveNewFile(fileProperties, "Hello World".getBytes() , AUTHOR_ID);
		
		assertThat( contents( fileManager.getFile(fileId, null) ), equalTo("Hello World"));		
	}
	
	
	
	private String contents(DownloadableFile item) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader( storageProvider.open(item.getStorageId())));
		return reader.readLine();
	
	}
	
}

