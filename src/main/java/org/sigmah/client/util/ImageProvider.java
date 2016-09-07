package org.sigmah.client.util;
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

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.shared.servlet.ServletConstants;
import org.sigmah.shared.servlet.ServletRequestBuilder;

public class ImageProvider {
  private final Injector injector;

  @Inject
  public ImageProvider(Injector injector) {
    this.injector = injector;
  }

  // TODO: Create a servlet which return the image stream, not a Byte64 string which is very heavy

  public void provideDataUrl(final String imageId, final AsyncCallback<String> callback) {
    ServletRequestBuilder builder = new ServletRequestBuilder(injector, RequestBuilder.GET, ServletConstants.Servlet.FILE, ServletConstants.ServletMethod.DOWNLOAD_LOGO);
    builder.addParameter(RequestParameter.ID, imageId);
    builder.send(new ServletRequestBuilder.RequestCallbackAdapter() {
      @Override
      public void onResponseReceived(final Request request, final Response response) {
        if (response.getStatusCode() != Response.SC_OK) {
          callback.onFailure(new RequestException(response.getStatusText()));
          return;
        }
        callback.onSuccess(response.getText());
      }

      @Override
      public void onError(Request request, Throwable exception) {
        super.onError(request, exception);

        callback.onFailure(exception);
      }
    });
  }
}
