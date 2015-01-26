package org.sigmah.client.ui.presenter.base;

import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.event.PageRequestEvent;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.ui.zone.event.ZoneRequestEvent;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Common presenter interface.
 * 
 * @param <V>
 *          Presenter's view interface extending the {@link ViewInterface} interface.
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface Presenter<V extends ViewInterface> {

	/**
	 * <p>
	 * Called when the presenter is initialized. This is called before any other methods. Any event handlers and other
	 * setup should be done here rather than in the constructor.
	 * </p>
	 * <p>
	 * Allows the presenter to be <em>initialized</em> without building its entire view (with all the widgets). Thus, the
	 * view will only be loaded during the first request to the presenter.
	 * </p>
	 */
	void bind();

	/**
	 * <p>
	 * This method is called when binding the presenter. Any additional bindings should be done here.
	 * </p>
	 * <p>
	 * <em>Default implementation does nothing.</em>
	 * </p>
	 */
	void onBind();

	/**
	 * Called after the presenter and view have been finished with for the moment.
	 */
	void unbind();

	/**
	 * <p>
	 * This method is called when unbinding the presenter.
	 * </p>
	 * <p>
	 * Any handler registrations recorded with {@link AbstractPresenter#registerHandler(HandlerRegistration)} will have
	 * already been removed at this point.
	 * </p>
	 * <p>
	 * <em>Default implementation does nothing.</em>
	 * </p>
	 */
	void onUnbind();

	/**
	 * <p>
	 * Method executed on the <b>first</b> access of the current presenter.
	 * </p>
	 * <p>
	 * This method allows the presenter to be initialized without building its entire view (with all the widgets). Thus,
	 * the view will only be loaded during the first request to the presenter.
	 * </p>
	 */
	void initialize();

	/**
	 * Returns the {@link ViewInterface} for the current presenter.
	 * 
	 * @return The view.
	 */
	V getView();

	/**
	 * <p>
	 * Requests the presenter to reveal the view on screen. It should automatically ask any parent views/presenters to
	 * reveal themselves also. It should <b>not</b> trigger a refresh.
	 * </p>
	 * <p>
	 * <b>If this method is overridden by a specific presenter, the {@code super.revealView()} has to be executed.</b>
	 * </p>
	 */
	void revealView();

	/**
	 * Method called before the presenter view is hidden.
	 * 
	 * @param callback
	 *          The callback used to leave or to stay on this presenter.
	 */
	void beforeLeaving(EventBus.LeavingCallback callback);

	/**
	 * <p>
	 * Specific page presenter interface.
	 * </p>
	 * <p>
	 * Page presenters manage a {@link Page} instance that represents an URL token. These presenters can be accessed
	 * through their page token.
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static interface PagePresenter<V extends ViewInterface> extends Presenter<V> {

		/**
		 * Returns the {@link Page} object associated to the page presenter.
		 * 
		 * @return the {@link Page} object associated to the page presenter.
		 */
		Page getPage();

		/**
		 * This method is called when a {@link PageRequestEvent} is fired <b>and</b> matches with the value from
		 * {@link #getPage()}.<br/>
		 * This mechanism ensures that method is executed on each page access.
		 * 
		 * @param request
		 *          The request.
		 */
		void onPageRequest(final PageRequest request);

	}

	/**
	 * <p>
	 * Specific zone presenter interface.
	 * </p>
	 * <p>
	 * Zone presenters manage a {@link Zone} instance that represents a view area that is always visible and requires
	 * update. These presenters can be updated through their zone token.
	 * </p>
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	public static interface ZonePresenter<V extends ViewInterface> extends Presenter<V> {

		/**
		 * Returns the {@link Zone} object associated to the zone presenter.
		 * 
		 * @return the {@link Zone} object associated to the zone presenter.
		 */
		Zone getZone();

		/**
		 * This method is called when a {@link ZoneRequestEvent} is fired <b>and</b> matches with the value from
		 * {@link #getZone()}.<br/>
		 * This mechanism ensures that method is executed on each zone update.
		 * 
		 * @param zoneRequest
		 *          The zone request.
		 */
		void onZoneRequest(final ZoneRequest zoneRequest);

	}

}
