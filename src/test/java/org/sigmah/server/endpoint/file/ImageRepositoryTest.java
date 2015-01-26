package org.sigmah.server.endpoint.file;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;

public class ImageRepositoryTest {

	
	@Test
	public void acceptFile() throws MalformedURLException, IOException {
		Properties config = new Properties();
		config.setProperty(ImageRepository.IMAGES_REPOSITORY_NAME, "/var/www/images");
		
		ImageRepository repo = new ImageRepository(config);
		assertThat(repo.getRootUri().toString(), equalTo("file:/var/www/images"));
				
	}

	@Test @Ignore("won't pass  on *nix systems")
	public void acceptWindowsFile() throws MalformedURLException, IOException {
		Properties config = new Properties();
		config.setProperty(ImageRepository.IMAGES_REPOSITORY_NAME, "C:\\www_pub\\images\\");
		
		ImageRepository repo = new ImageRepository(config);
		assertThat(repo.getRootUri().toString(), equalTo("file:/c:/www_pub/images"));
				
	}
	
	@Test
	public void acceptURL() throws MalformedURLException, IOException {
		Properties config = new Properties();
		config.setProperty(ImageRepository.IMAGES_REPOSITORY_NAME, "https://s3.amazonaws.com/sigmah_logos/");
		
		ImageRepository repo = new ImageRepository(config);
		
		repo.getImage("sif.png").toURL().openStream();

	}
	
}
