package org.sigmah.client.ui.presenter;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.HelpView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.Language;
import org.sigmah.shared.servlet.URLs;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Help presenter.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class HelpPresenter extends AbstractPagePresenter<HelpPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(HelpView.class)
	public static interface View extends ViewInterface {

		void setHelpURL(String url);

	}

	private String url;
	private String anchor;

	@Inject
	public HelpPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.HELP;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// Retrieves the anchor.
		final Page page = injector.getPageManager().getCurrentPage(false);
        //removal of all "-"/"_" because LibreOffice writer2xhtml plugin unable to 
        //      keep them in anchors of user guide html export
        anchor = page.toString().replace("-", "").replace("_", ""); 

		// Builds the help URL.
		this.url = buildHelpURL(auth().getLanguage());

	}

	/**
	 * Checks if the manual exists before revealing the help popup.
	 */
	@Override
	public final void revealView() {

		// Check if the URL exists before revealing the popup.
		URLs.checkURL(url, new AsyncCallback<Boolean>() {

			@Override
			public void onSuccess(Boolean result) {

				// The manual doesn't exist for the given language, use the English manual instead.
				if (!result) {

					url = buildHelpURL(Language.EN);
					URLs.checkURL(url, new AsyncCallback<Boolean>() {

						@Override
						public void onSuccess(Boolean result) {
							if (result) {
								showHelp();
							} else {
								showError(new IllegalStateException("The English manual is unavailable."));
							}
						}

						@Override
						public void onFailure(Throwable caught) {
							showError(caught);
						}

					});

				}
				// The manual exists.
				else {
					showHelp();
				}

			}

			@Override
			public void onFailure(Throwable caught) {
				showError(caught);
			}

		});

	}

	/**
	 * Reveals the view with the current URL.
	 */
	private void showHelp() {

		// Adds the anchor.
		if (ClientUtils.isNotBlank(anchor)) {
			url += "#" + anchor;
		}

		// Reveals the view.
		view.setHelpURL(url);
		super.revealView();

	}

	/**
	 * Displays an error message and doesn't reveal the view.
	 * 
	 * @param caught
	 */
	private void showError(Throwable caught) {

		if (Log.isErrorEnabled()) {
			Log.error("Cannot open the help manual.", caught);
		}

		view.setHelpURL(null);
		N10N.error(I18N.CONSTANTS.manualOpeningError());

	}

	/**
	 * Builds the help URL.
	 * 
	 * @param language
	 *          The language of the help manual.
	 * @return The help URL.
	 */
	private static String buildHelpURL(Language language) {
		return URLs.buildApplicationURL("manuals", language != null ? language.getLocale() : "", "manual.html");
	}

}
