package org.sigmah.server.service.util;
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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

public class ImageMinimizer {
  public void resizeImage(InputStream imageInputStream, OutputStream outputStream, Integer resizeTo) throws IOException {
    BufferedImage image = ImageIO.read(imageInputStream);
    int width = image.getWidth();
    int height = image.getHeight();

    if (width <= resizeTo && height <= resizeTo) {
      // already resized
      compressAndWriteImage(image, outputStream);
      return;
    }

    BufferedImage scaledImage;
    if (width > height){
      scaledImage = scaleDownImage(image, resizeTo, resizeTo * height / width);
    } else {
      scaledImage = scaleDownImage(image, resizeTo * width / height, resizeTo);
    }
    compressAndWriteImage(scaledImage, outputStream);
  }

  private BufferedImage scaleDownImage(BufferedImage image, int width, int height) {
    int type = BufferedImage.TYPE_INT_ARGB;
    if (image.getTransparency() == Transparency.OPAQUE) {
      type = BufferedImage.TYPE_INT_RGB;
    }

    BufferedImage scaledImage = new BufferedImage(width, height, type);
    Graphics2D graphics = scaledImage.createGraphics();
    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    graphics.drawImage(image, 0, 0, width, height, null);
    graphics.dispose();

    return scaledImage;
  }

  private void compressAndWriteImage(BufferedImage bufferedImage, OutputStream outputStream) throws IOException {
    ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();

    ImageWriteParam writeParam = imageWriter.getDefaultWriteParam();
    writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    writeParam.setCompressionQuality(0.90f);

    MemoryCacheImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
    imageWriter.setOutput(imageOutputStream);
    imageWriter.write(null, new IIOImage(bufferedImage, null, null), writeParam);

    imageOutputStream.flush();
  }
}
