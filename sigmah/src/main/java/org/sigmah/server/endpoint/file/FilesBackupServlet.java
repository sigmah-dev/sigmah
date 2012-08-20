package org.sigmah.server.endpoint.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.Cookies;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.dao.PartnerDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.endpoint.file.FileManager.FileElement;
import org.sigmah.server.endpoint.file.FileManager.FolderElement;
import org.sigmah.server.endpoint.file.FileManager.RepositoryElement;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Servlet that provide an access to the backup system
 * 
 * @author Aurélien Ponçon
 */
@Singleton
public class FilesBackupServlet extends HttpServlet {

    private static final long serialVersionUID = 5004316483095799685L;

    private static final Log log = LogFactory.getLog(FilesBackupServlet.class);

    private Injector injector;
    private final FileManager fileManager;
    private final FileStorageProvider fileStorageProvider;
    private final int BUFFER = 2048;

    @Inject
    public FilesBackupServlet(Injector injector) {
        super();
        this.injector = injector;
        fileManager = injector.getInstance(FileManager.class);
        fileStorageProvider = injector.getInstance(FileStorageProvider.class);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String authToken = Cookies.getCookieValue(Cookies.AUTH_TOKEN_COOKIE, request);
        if (authToken == null) {
            log.error("[doGet] You need to be authenticated");
            throw new ServletException("You need to be authenticated");
        }
        final AuthenticationDAO authenticationDAO = injector.getInstance(AuthenticationDAO.class);
        final Authentication authentication = authenticationDAO.findById(authToken);
        User user = authentication.getUser();
        
        String downloadVersionsParam = request.getParameter("downloadVersions");
        if (downloadVersionsParam == null) {
            log.error("[doGet] downloadVersions cannot be found.");
            throw new ServletException("downloadVersions cannot be found.");
        }
        Boolean downloadVersions = Boolean.valueOf(downloadVersionsParam);

        String orgunitParam = request.getParameter("orgUnit");
        if (orgunitParam == null) {
            log.error("[doGet] orgUnit cannot be found.");
            throw new ServletException("orgUnit cannot be found.");
        }
        final PartnerDAO partnerDAO = injector.getInstance(PartnerDAO.class);
        final OrgUnit orgUnit = partnerDAO.findById(Integer.valueOf(orgunitParam));

        String zipName = user.getOrganization().getName() + ".zip";

        try {
            final ZipOutputStream zos =
                    new ZipOutputStream(new BufferedOutputStream(fileStorageProvider.create(zipName)));
            zos.setMethod(ZipOutputStream.DEFLATED);
            zos.setLevel(9);

            final RepositoryElement repository = fileManager.getRepository(orgUnit, user, downloadVersions);
            repository.setName("");
            zipRepository(repository, zos, "");

            zos.close();

            final InputStream zis = new BufferedInputStream(fileStorageProvider.open(zipName));

            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");

            // Writes file content to the HTTP response.
            final ServletOutputStream outputStream = response.getOutputStream();
            IOUtil.copy(zis, outputStream);

            zis.close();
            outputStream.close();

        } catch (ZipException ze) {
            log.error("[doGet] ZipException not found" + ze.getMessage());
            // Send an empty zip file (java.util.zip API throw an exception when creating an empty zip file)
            
            // Header of a zip file
            byte[] octets = {0x50, 0x4B ,0x05 ,0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 
                             0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
            response.setContentType("application/octet-stream");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");

            final ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(octets);
            outputStream.close();
        } catch (FileNotFoundException fnfe) {
            log.error("[doGet] File not found");
            throw new ServletException("File not found", fnfe);
        } catch (IOException ioe) {
            log.error("[doGet] HTTP response I/O error." + ioe.getMessage());
            throw new ServletException("HTTP response I/O error.", ioe);
        }
    }

    private void zipRepository(RepositoryElement root, ZipOutputStream zipOutputStream, String actualPath)
            throws IOException {
        String path = (actualPath.equals("") ? root.getName() : actualPath + "/" + root.getName());
        if (root instanceof FileElement) {
            FileElement file = (FileElement) root;
            final InputStream is = new BufferedInputStream(fileStorageProvider.open(file.getStorageId()), BUFFER);

            ZipEntry zipEntry = new ZipEntry(path);
            zipOutputStream.putNextEntry(zipEntry);

            byte data[] = new byte[BUFFER];

            int count;
            while ((count = is.read(data)) != -1) {
                zipOutputStream.write(data);
            }

            zipOutputStream.closeEntry();
            is.close();
        } else if (root instanceof FolderElement) {
            FolderElement folder = (FolderElement) root;
            for (RepositoryElement element : folder.getChildren()) {
                zipRepository(element, zipOutputStream, path);
            }            
        }
    }
}
