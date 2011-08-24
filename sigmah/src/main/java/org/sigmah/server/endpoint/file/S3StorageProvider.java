package org.sigmah.server.endpoint.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.google.inject.Inject;

/**
 * Implements a {@code FileStorageProvider} using Amazon AWS's S3 storage web
 * service.
 * 
 * This provider can be configured from sigmah.properties with the following
 * properties:
 * 
 * <pre>
 * repository.file_storage_provider_class=org.sigmah.server.endpoint.file.S3StorageProvider
 * repository.s3_bucket=sigmah_repository
 * aws.access_key=XYZXYZXYXZYXZYXZXYZX
 * aws.secret_key=XTYY/DFUdfkerjekrjewrSDSTF
 * </pre>
 * 
 * @author alexander
 * 
 */
public class S3StorageProvider implements FileStorageProvider {

    private final AWSCredentials credentials;
    private final String bucketName;

    @Inject
    public S3StorageProvider(Properties config) {
        this.credentials = new BasicAWSCredentials(config.getProperty("aws.access_key"),
                config.getProperty("aws.secret_key"));
        this.bucketName = config.getProperty(FileModule.REPOSITORY_NAME);
    }

    @Override
    public OutputStream create(final String storageId) throws IOException {
        final File tempFile = File.createTempFile("s3temp", storageId);
        return new FileOutputStream(tempFile) {

            @Override
            public void close() throws IOException {
                super.close();

                putFile(storageId, tempFile);

            }
        };
    }

    @Override
    public InputStream open(String storageId) throws IOException {
        AmazonS3Client client = new AmazonS3Client(credentials);
        S3Object object = client.getObject(bucketName, storageId);
        return object.getObjectContent();
    }

    protected void putFile(String storageId, File tempFile) {
        AmazonS3Client client = new AmazonS3Client(credentials);
        try {
            client.putObject(bucketName, storageId, tempFile);
        } finally {
            tempFile.delete();
        }
    }

}
