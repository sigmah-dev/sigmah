package org.sigmah.shared.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link FileType} related tests.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class FileTypeTest {

	@Test
	public void fromExtensionTest() {

		Assert.assertNull(FileType.fromExtension(null));
		Assert.assertNull(FileType.fromExtension(""));
		Assert.assertNull(FileType.fromExtension("   "));
		Assert.assertNull(FileType.fromExtension("unknown"));
		Assert.assertNull(FileType.fromExtension(" un.txt.known "));
		Assert.assertEquals(FileType.PDF, FileType.fromExtension(".pdf"));
		Assert.assertEquals(FileType.PDF, FileType.fromExtension("pdf"));
		Assert.assertEquals(FileType.PDF, FileType.fromExtension("  pdf  "));
		Assert.assertEquals(FileType.CSV, FileType.fromExtension(".csv"));
		Assert.assertEquals(FileType.CSV, FileType.fromExtension("csv"));
		Assert.assertEquals(FileType.CSV, FileType.fromExtension("   .csv    "));
		Assert.assertEquals(FileType.CSV, FileType.fromExtension("   csv    "));

		// With default value.
		Assert.assertEquals(FileType.HTML, FileType.fromExtension(null, FileType.HTML));
		Assert.assertEquals(FileType.PDF, FileType.fromExtension("", FileType.PDF));
		Assert.assertEquals(FileType.JPEG, FileType.fromExtension("   ", FileType.JPEG));
		Assert.assertEquals(FileType.CSV, FileType.fromExtension("unknown", FileType.CSV));
		Assert.assertEquals(FileType.PDF, FileType.fromExtension("pdf", FileType._DEFAULT));
		Assert.assertEquals(FileType.PDF, FileType.fromExtension(".pdf", FileType._DEFAULT));
		Assert.assertEquals(FileType.XML, FileType.fromExtension("xml", FileType._DEFAULT));
		Assert.assertEquals(FileType.XML, FileType.fromExtension(".xml", FileType._DEFAULT));

	}

	@Test
	public void fromContentTypeTest() {

		Assert.assertNull(FileType.fromContentType(null));
		Assert.assertNull(FileType.fromContentType(""));
		Assert.assertNull(FileType.fromContentType("   "));
		Assert.assertNull(FileType.fromContentType("unknown"));
		Assert.assertEquals(FileType.PDF, FileType.fromContentType(FileType.PDF.getContentType()));
		Assert.assertEquals(FileType.CSS, FileType.fromContentType(FileType.CSS.getContentType()));

		// With default value.
		Assert.assertEquals(FileType.HTML, FileType.fromContentType(null, FileType.HTML));
		Assert.assertEquals(FileType.PDF, FileType.fromContentType("", FileType.PDF));
		Assert.assertEquals(FileType.JPEG, FileType.fromContentType("   ", FileType.JPEG));
		Assert.assertEquals(FileType.JPEG, FileType.fromContentType("unknown", FileType.JPEG));
		Assert.assertEquals(FileType._DEFAULT, FileType.fromContentType("unknown", FileType._DEFAULT));
		Assert.assertEquals(FileType.PDF, FileType.fromContentType("application/pdf", FileType._DEFAULT));
		Assert.assertEquals(FileType.CSV, FileType.fromContentType("text/csv", FileType._DEFAULT));

	}

}
