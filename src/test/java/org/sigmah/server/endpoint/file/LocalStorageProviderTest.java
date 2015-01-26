package org.sigmah.server.endpoint.file;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

public class LocalStorageProviderTest {

    private File tempFolder;
    private Properties config;

    @Before
    public void setUp() {
        tempFolder = new File(System.getProperty("java.io.tmpdir", "FileManagerTest" + new Date().getTime()));
        tempFolder.mkdirs();

        config = new Properties();
        config.setProperty(FileModule.REPOSITORY_NAME, tempFolder.getAbsolutePath());
    }

    @Test
    public void saveAndReopen() throws IOException {

        String testString = "testing 1...2...3...";

        LocalStorageProvider provider = new LocalStorageProvider(config);

        OutputStream out = provider.create("XYZ123");
        out.write(testString.getBytes());
        out.close();

        InputStream in = provider.open("XYZ123");
        byte[] bytesRead = new byte[testString.getBytes().length];
        in.read(bytesRead);
        in.close();

        assertThat(bytesRead, equalTo(testString.getBytes()));
    }

}
