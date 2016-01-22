package org.sigmah.server.file.impl;

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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;

import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.file.LogoManager;
import org.sigmah.shared.util.FileType;

import com.google.inject.Inject;

/**
 * Implementation of the {@link LogoManager}.
 * 
 * @author Aurélien Ponçon
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class LogoManagerImpl implements LogoManager {

	/**
	 * Logo height.
	 */
	private static final int LOGO_HEIGHT = 56;

	/**
	 * Logo width.
	 */
	private static final int LOGO_WIDTH = 126;

	/**
	 * Injected {@link FileStorageProvider}.
	 */
	private final FileStorageProvider fileStorageProvider;

	@Inject
	public LogoManagerImpl(final FileStorageProvider storageProvider) {
		this.fileStorageProvider = storageProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long updateLogo(final InputStream logoInputStream, final String path) throws IOException {

		final InputStream inputStream = validateLogo(logoInputStream);

		final long size = fileStorageProvider.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);

		logoInputStream.close();
		
		return size;
	}

	/**
	 * Resizes the logo to the correct height and width.
	 * 
	 * @param logoInputStream
	 *          The input stream of the image
	 * @return an {@link InputStream} of the new image.
	 */
	private static InputStream validateLogo(final InputStream logoInputStream) throws IOException {

		final BufferedImage originalImage = ImageIO.read(logoInputStream);
		int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		final BufferedImage resizedImage = new BufferedImage(LOGO_WIDTH, LOGO_HEIGHT, type);
		Graphics2D g = null;

		try (final ByteArrayOutputStream os = new ByteArrayOutputStream()) {

			g = resizedImage.createGraphics();
			g.drawImage(originalImage, 0, 0, LOGO_WIDTH, LOGO_HEIGHT, null);
			g.dispose();

			g.setComposite(AlphaComposite.Src);

			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			ImageIO.write(resizedImage, FileType.PNG.getExtension(false), os);

			final byte[] data = os.toByteArray();

			return new BufferedInputStream(new ByteArrayInputStream(data));

		} finally {
			if (g != null) {
				g.dispose();
			}

			logoInputStream.close();
		}
	}

}
