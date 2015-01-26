package org.sigmah.client.ui.widget;

/**
 * An element which can be set in a loading state.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Loadable {

	/**
	 * Sets whether or not the element must be display in its loading state. While the loading state is <code>true</code>,
	 * the element is disabled and can show a custom loading icon.
	 * 
	 * @param loading
	 *          The loading state.
	 */
	void setLoading(boolean loading);

	/**
	 * Gets the current loading state.
	 * 
	 * @return The current loading state.
	 */
	boolean isLoading();

}
