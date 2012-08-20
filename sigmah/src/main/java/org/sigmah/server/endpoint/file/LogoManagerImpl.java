package org.sigmah.server.endpoint.file;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Documented;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.shared.domain.Organization;
import org.sigmah.shared.dto.value.FileUploadUtils;

import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * 
 * Implementation of the logo manager
 * 
 * @author Aurélien Ponçon
 *
 */
public class LogoManagerImpl implements LogoManager{
    
    private final static int LOGO_HEIGHT = 56;
    private final static int LOGO_WIDTH = 126;
    
    /**
     * Logger.
     */
    private static final Log log = LogFactory.getLog(FileManagerImpl.class);

    private final FileStorageProvider fileStorageProvider;

    @Inject
    public LogoManagerImpl(FileStorageProvider storageProvider) {
        this.fileStorageProvider = storageProvider;
    }

    @Override
    public void updateLogo(InputStream logoInputStream, String path) throws IOException {
        final OutputStream outputStream = new BufferedOutputStream(fileStorageProvider.create(path));
        
        InputStream inputStream = validateLogo(logoInputStream);
        
        IOUtil.copy(inputStream, outputStream);
        
        logoInputStream.close();
        outputStream.close();
    }

    /*
     * Resize the logo to the correct height and width 
     * 
     * @param logoInputStream
     *              The inputstream of the image
     * @return an inputstream of the new image
     */
    private InputStream validateLogo(InputStream logoInputStream) throws IOException {
        
        BufferedImage originalImage = ImageIO.read(logoInputStream);
        int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
        
        
        BufferedImage resizedImage = new BufferedImage(LOGO_WIDTH, LOGO_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, LOGO_WIDTH, LOGO_HEIGHT, null);
        g.dispose();
        
        g.setComposite(AlphaComposite.Src);
        
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "png" , os);
        
        byte[] data = os.toByteArray();
        os.close();
        logoInputStream.close();
        
        return new BufferedInputStream(new ByteArrayInputStream(data));
    }

}
