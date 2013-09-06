package org.sigmah.server.endpoint.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.google.inject.AbstractModule;

public class FileManagerTestConfigModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(FileStorageProvider.class).toInstance(new StorageProviderStub());
	}
	
	
	private static class StorageProviderStub implements FileStorageProvider {

		public Map<String, ByteArrayOutputStream> files = new HashMap<String, ByteArrayOutputStream>();
		
		
		@Override
		public OutputStream create(String storageId) throws IOException {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			files.put(storageId, baos);
			return baos;
		}

		@Override
		public InputStream open(String storageId) throws IOException {
			return new ByteArrayInputStream(files.get(storageId).toByteArray());
		}

		@Override
        public Boolean delete(String storageId) {
			ByteArrayOutputStream baos = files.remove(storageId);
			Boolean deleted = false;
			if(baos != null) {
				deleted = true;
			} 
	        return deleted;
        }
		
		
		
		
	}
	
	
}