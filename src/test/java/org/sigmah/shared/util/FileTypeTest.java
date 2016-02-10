package org.sigmah.shared.util;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
