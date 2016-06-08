package org.sigmah.client.ui.presenter.base;

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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.Sigmah;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.presenter.base.HasSubPresenter.SubPresenter;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.view.base.HasSubView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.shared.command.result.Authentication;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import org.sigmah.client.util.profiler.Profiler;
import org.sigmah.client.util.profiler.Scenario;

/**
 * <p>
 * Abstract presenter.
 * </p>
 * <p>
 * <b>Rules to add a new presenter:</b>
 * <ol>
 * <li>Create a new class inheriting {@link AbstractPresenter} with {@link com.google.inject.Singleton} annotation or
 * not, depending on its uniqueness.</li>
 * <li>Define an inner <em>static</em> interface representing the presenter's view. This interface must have the
 * {@link com.google.inject.ImplementedBy} annotation referencing the view implementation (<u>crucial</u>).
 * See {@link AbstractView} javadoc to initialize the view implementation.</li>
 * <li>Add an accessor to the presenter into client-side {@link Injector} and call it into {@link Sigmah#onModuleLoad()}
 * entry point in order to register presenter.</li>
 * </ol>
 * </p>
 * <p>
 * <b>Utility methods provided to presenter implementations:</b>
 * <ul>
 * <li>{@link #auth()} to access the current authentication.</li>
 * <li>{@link #isAnonymous()} to check if no user is currently authenticated.</li>
 * </ul>
 * </p>
 * 
 * @param <V>
 *          Presenter's view interface extending the {@link ViewInterface} interface.
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public abstract class AbstractPresenter<V extends ViewInterface> implements Presenter<V> {

	/**
	 * Events registrations handler.
	 */
	private final List<HandlerRegistration> handlerRegistrations;

	/**
	 * Current zone presenter view.
	 */
	protected final V view;

	/**
	 * Application event bus.
	 */
	protected final EventBus eventBus;

	/**
	 * Application injector.
	 */
	protected final Injector injector;

	/**
	 * Application command dispatcher.
	 */
	protected final DispatchAsync dispatch;

	/**
	 * Flag indicating if the presenter has already been initialized.
	 * Each presenter is initialized only once.
	 */
	private boolean initialized;

	/**
	 * <p>
	 * Default abstract presenter constructor.
	 * </p>
	 * <p>
	 * Executes {@link #bind()} method.
	 * </p>
	 * 
	 * @param view
	 *          View associated to the presenter.
	 * @param injector
	 *          Injected application injector allowing access to all useful objects.
	 */
	@Inject
	protected AbstractPresenter(V view, Injector injector) {
		this.injector = injector;
		this.eventBus = injector.getEventBus();
		this.dispatch = injector.getDispatch();
		this.view = view;
		this.handlerRegistrations = new ArrayList<HandlerRegistration>();

		if (Log.isDebugEnabled()) {
			Log.debug("Binding '" + getClass() + "' presenter.");
		}

		bind();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	final public V getView() {
		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bind() {
		// Default implementation does nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		// Default implementation does nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unbind() {
		for (final HandlerRegistration reg : handlerRegistrations) {
			reg.removeHandler();
		}
		handlerRegistrations.clear();
		onUnbind();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onUnbind() {
		// Default implementation does nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void initialize() {

		if (initialized) {
			return;
		}

		if (isSubPresenter()) {
			// Sub-presenter exception.
			final HasSubPresenter<?> parentPresenter = ((SubPresenter<?>) this).getParentPresenter();
			parentPresenter.initialize();
		}

		try {

			if (Log.isDebugEnabled()) {
				Log.debug("First initialization of '" + getClass().getName() + "'.");
			}

			view.initialize();
			onBind();

		} finally {
			// First load must be set to true.
			initialized = true;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void revealView() {
		Profiler.INSTANCE.markCheckpoint(Scenario.OPEN_PROJECT, " revealView start ");
		if (Log.isTraceEnabled()) {
			Log.trace("Reveals '" + view.getClass().getName() + "' presenter's view.");
		}

		if (isSubPresenter()) {
			// Presenter's view is shown into parent presenter's placeholder.
			final HasSubPresenter<? extends HasSubView> parentPresenter = ((SubPresenter<?>) this).getParentPresenter();
			final LayoutContainer placeHolder = parentPresenter.getView().getPlaceHolder();

			placeHolder.removeAll();
			placeHolder.add(Widget.asWidgetOrNull(getView()));

			injector.getApplicationPresenter().showPresenter(parentPresenter);

			placeHolder.layout();

		} else {
			// Presenter's view is shown into application's main view.
			injector.getApplicationPresenter().showPresenter(this);
		}

		view.onViewRevealed();
		
		onViewRevealed();
		
		Profiler.INSTANCE.markCheckpoint(Scenario.OPEN_PROJECT, " revealView end ");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beforeLeaving(EventBus.LeavingCallback callback) {
		// By default, the presenter is left.
		callback.leavingOk();
	}

	// ------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ------------------------------------------------------------------------

	/**
	 * Any {@link HandlerRegistration}s added will be removed when {@link #unbind()} is called. This provides a handy way
	 * to track event handler registrations when binding and unbinding.
	 * 
	 * @param handlerRegistration
	 *          The registration (does nothing if {@code null}).
	 */
	protected final void registerHandler(final HandlerRegistration handlerRegistration) {
		if (handlerRegistration == null) {
			return;
		}
		handlerRegistrations.add(handlerRegistration);
	}

	/**
	 * Returns if this presenter has already been initialized.
	 * 
	 * @return {@code true} if this presenter has already been initialized (i.e. already loaded once), {@code false}
	 *         otherwise.
	 */
	protected final boolean isInitialized() {
		return initialized;
	}

	/**
	 * Returns if the current presenter is a <em>sub</em> presenter.
	 * 
	 * @return {@code true} if the current presenter is a <em>sub</em> presenter.
	 */
	protected final boolean isSubPresenter() {
		return this instanceof SubPresenter;
	}

	/**
	 * Checks if no user is currently authenticated.
	 * 
	 * @return {@code true} if no user is currently authenticated, {@code false} otherwise.
	 */
	protected final boolean isAnonymous() {
		return injector.getAuthenticationProvider().isAnonymous();
	}

	/**
	 * Returns the current {@link Authentication}.
	 * 
	 * @return The current {@link Authentication}, never {@code null}.
	 */
	protected final Authentication auth() {
		return injector.getAuthenticationProvider().get();
	}

	/**
	 * <p>
	 * Method executed once the view has been rendered and the DOM has been created.
	 * </p>
	 * <em>Can be overridden by child implementation.</em>.
	 */
	protected void onViewRevealed() {
		// Default implementation does nothing.
	}

}
