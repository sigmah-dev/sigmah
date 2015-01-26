package org.sigmah.client.ui.presenter;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.HelpView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.SigmahOldAnchors;
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
		anchor = SigmahOldAnchors.map(page);

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
