package org.sigmah.shared.servlet;

import org.sigmah.client.util.ClientUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * URL utility methods.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class URLs {

	private URLs() {
		// Provides only static methods.
	}

	/**
	 * Checks if the given URL exist (using the HTTP GET method).
	 * 
	 * @param url
	 *          The URL to test.
	 * @param callback
	 *          The callback.<br/>
	 *          Available results (depending on the response status code):
	 *          <ul>
	 *          <li><strong>200</strong> : {@code AsyncCallback#onSuccess(true)};</li>
	 *          <li><strong>404</strong> : {@code AsyncCallback#onSuccess(false)};</li>
	 *          <li><strong>other</strong> : {@link AsyncCallback#onFailure(Throwable)};</li>
	 *          </ul>
	 */
	public static void checkURL(final String url, final AsyncCallback<Boolean> callback) {

		// Builds the request.
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, url);
		requestBuilder.setCallback(new RequestCallback() {

			@Override
			public void onResponseReceived(Request request, Response response) {

				// The URL exists.
				if (response.getStatusCode() == Response.SC_OK) {
					callback.onSuccess(true);
				}
				// The URL doesn't exists.
				else if (response.getStatusCode() == Response.SC_NOT_FOUND) {
					callback.onSuccess(false);
				}
				// Other errors.
				else {
					callback.onFailure(null);
				}

			}

			@Override
			public void onError(Request request, Throwable exception) {
				callback.onFailure(exception);
			}

		});

		// Sends the request.
		try {
			requestBuilder.send();
		} catch (RequestException e) {
			callback.onFailure(e);
		}

	}

	/**
	 * Builds and returns an URL for the current host page and the curren GWT module.
	 * 
	 * @param pathTokens
	 *          The optional additional path tokens.
	 * @return The URL.
	 */
	public static String buildApplicationURL(String... pathTokens) {

		final StringBuilder sb = new StringBuilder();

		sb.append(GWT.getHostPageBaseURL());
		sb.append(GWT.getModuleName());
		sb.append("/");

		if (pathTokens != null && pathTokens.length > 0) {
			for (final String pathToken : pathTokens) {
				if (ClientUtils.isNotBlank(pathToken)) {
					sb.append(pathToken);
					sb.append("/");
				}
			}
		}

		// Removes the last slash.
		if (sb.charAt(sb.length() - 1) == '/') {
			sb.deleteCharAt(sb.length() - 1);
		}

		return sb.toString();

	}

}
