package org.sigmah.server.endpoint.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

	/**
	 * Copy all bytes from the input to the output stream, using a buffer.
	 * @param input
	 * @param output
	 * @throws IOException
	 */
	public static void copy(InputStream input, OutputStream output)
			throws IOException {
		final byte[] buffer = new byte[64 * 1024];
		int len = 0;
		while ((len = input.read(buffer)) != -1) {
		    output.write(buffer, 0, len);
		}
	}

}
