package org.sigmah.server.endpoint.file;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

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
		
		
		
		
	}
	
	
}