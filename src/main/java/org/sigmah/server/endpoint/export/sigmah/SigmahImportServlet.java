package org.sigmah.server.endpoint.export.sigmah;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.endpoint.file.FileManager;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
public class SigmahImportServlet extends HttpServlet {

	private static final long serialVersionUID = 8161472823847155801L;

	/**
	 * Logger.
	 */
	private static final Log log = LogFactory.getLog(SigmahExportServlet.class);

	/**
	 * The entity manager.
	 */
	private final Injector injector;

	@Inject
	public SigmahImportServlet(Injector injector) {
		this.injector = injector;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
	                IOException {
		try {

			final EntityManager em = injector.getInstance(EntityManager.class);

			FileItemFactory factory = new DiskFileItemFactory();
			final ServletFileUpload fileUpload = new ServletFileUpload(factory);

			for (Object v : request.getParameterMap().values()) {
				log.debug("field '" + v.toString() + "' (FILE)");
			}
			try {
				final List<FileItem> items = fileUpload.parseRequest(request);

				final Iterator<FileItem> iterator = items.iterator();

				byte[] data = null;

				while (iterator.hasNext()) {

					final FileItem item = iterator.next();
					final String name = item.getFieldName();

					final InputStream stream = item.getInputStream();

					// Only download the uploaded file content.
					if (!item.isFormField()) {
						if (log.isDebugEnabled()) {
							log.debug("[doPost] Reads file content from the field ; name: " + name + ".");
						}

						// Stream to read the file content.
						final OutputStream outputStream;
						final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
						outputStream = byteArrayOutputStream;

						// Stores the file content in a bytes array.
						int c = stream.read();
						while (c != -1) {
							outputStream.write(c);
							c = stream.read();
						}

						outputStream.close();

						data = byteArrayOutputStream.toByteArray();
						stream.close();

					}

					if (data != null) {
						// Files manager.
						final FileManager manager = injector.getInstance(FileManager.class);
						String uniqueFileId = manager.writeContent(data);
						// Avoids the pre tags around the result
						response.setContentType("text/html");
						response.getWriter().write(uniqueFileId);
					}

				}

			} catch (Exception e) {

			}

		} catch (Throwable e) {
			response.setStatus(500);
			log.error("[doGet] An error occurred during the import", e);
			throw new ServletException("An error occurred during the import.", e);
		}
	}

}
