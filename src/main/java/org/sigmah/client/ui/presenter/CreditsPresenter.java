package org.sigmah.client.ui.presenter;

import org.sigmah.client.ClientFactory;

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
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.CreditsView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.GetProperties;
import org.sigmah.shared.command.result.MapResult;
import org.sigmah.shared.conf.PropertyKey;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasText;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Credits presenter.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */

public class CreditsPresenter extends AbstractPagePresenter<CreditsPresenter.View> {

	/**
	 * Manages partner roles.
	 * 
	 * @author Tom Miette (tmiette@ideia.fr)
	 */
	private static enum PartnerRole {

		DEVELOPER("DEV", I18N.CONSTANTS.sigmah_partners_role_development()),
		ERGONOMIST("DES", I18N.CONSTANTS.sigmah_partners_role_graphic()),
		DESIGNER("GRA", I18N.CONSTANTS.sigmah_partners_role_design());

		private final String code;
		private final String i18n;

		private PartnerRole(String code, String i18n) {
			this.code = code;
			this.i18n = i18n;
		}

		private static String i18n(String code) {

			if (ClientUtils.isNotBlank(code)) {
				for (final PartnerRole role : PartnerRole.values()) {
					if (code.equalsIgnoreCase(role.code)) {
						return role.i18n;
					}
				}
			}

			return null;

		}

	}

	/**
	 * View interface.
	 */
	public static interface View extends ViewInterface {

		HasText getVersionNameLabel();

		HasText getVersionNumberLabel();

		HasText getVersionRefLabel();

		void clearManagers();

		void addManager(final String name, final String url);

		void clearPartners();

		void addPartner(final String name, final String url, final String role);

		void clearDevelopers();

		void addDeveloper(final String name, final String email);

		void clearContributors();

		void addContributor(final String name, final String email);

	}

	
	public CreditsPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.CREDITS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		dispatch.execute(new GetProperties(PropertyKey.APP_NAME, PropertyKey.VERSION_NAME, PropertyKey.VERSION_NUMBER, PropertyKey.VERSION_REFERENCE,
			PropertyKey.VERSION_MANAGERS, PropertyKey.VERSION_PARTNERS, PropertyKey.VERSION_DEVELOPERS, PropertyKey.VERSION_CONTRIBUTORS),
			new AsyncCallback<MapResult<PropertyKey, String>>() {

				@Override
				public void onFailure(Throwable caught) {

					// Cannot retrieves some properties, show the credits view anyway.
					if (Log.isErrorEnabled()) {
						Log.error("Cannot retrieves some application properties.", caught);
					}

					onSuccess(null);

				}

				@Override
				public void onSuccess(MapResult<PropertyKey, String> result) {

					// Version.

					final String appName = result != null ? result.get(PropertyKey.APP_NAME) : null;
					final String versionName = result != null ? result.get(PropertyKey.VERSION_NAME) : null;
					String appVersionLabel = ClientUtils.trimToEmpty(appName);
					appVersionLabel += (ClientUtils.isNotBlank(versionName) ? " - " : "") + ClientUtils.trimToEmpty(versionName);
					final String versionNumber = result != null ? result.get(PropertyKey.VERSION_NUMBER) : null;
					final String versionRef = result != null ? result.get(PropertyKey.VERSION_REFERENCE) : null;

					view.getVersionNameLabel().setText(appVersionLabel);
					view.getVersionNumberLabel().setText(versionNumber);
					view.getVersionRefLabel().setText(versionRef);

					// Managers.

					view.clearManagers();
					final String managersString = result.get(PropertyKey.VERSION_MANAGERS);
					if (ClientUtils.isNotBlank(managersString)) {
						final String[] managers = managersString.split(";");
						if (managers != null && managers.length > 0) {
							for (final String manager : managers) {
								if (ClientUtils.isNotBlank(manager)) {
									final String[] attributes = manager.split(",");
									if (attributes != null && attributes.length > 0) {
										final String name = ClientUtils.trimToEmpty(attributes[0]);
										final String url = attributes.length > 1 ? ClientUtils.trimToEmpty(attributes[1]) : "";
										view.addManager(name, url);
									}
								}
							}
						}
					}

					// Partners.

					view.clearPartners();
					final String partnersString = result.get(PropertyKey.VERSION_PARTNERS);
					if (ClientUtils.isNotBlank(partnersString)) {
						final String[] partners = partnersString.split(";");
						if (partners != null && partners.length > 0) {
							for (final String partner : partners) {
								if (ClientUtils.isNotBlank(partner)) {
									final String[] attributes = partner.split(",");
									if (attributes != null && attributes.length > 0) {
										final String name = ClientUtils.trimToEmpty(attributes[0]);
										final String role = attributes.length > 1 ? ClientUtils.trimToEmpty(attributes[1]) : "";
										final String url = attributes.length > 2 ? ClientUtils.trimToEmpty(attributes[2]) : "";
										view.addPartner(name, url, PartnerRole.i18n(role));
									}
								}
							}
						}
					}

					// Developers.

					view.clearDevelopers();
					final String developersString = result.get(PropertyKey.VERSION_DEVELOPERS);
					if (ClientUtils.isNotBlank(developersString)) {
						final String[] developers = developersString.split(";");
						if (developers != null && developers.length > 0) {
							for (final String developer : developers) {
								if (ClientUtils.isNotBlank(developer)) {
									final String[] attributes = developer.split(",");
									if (attributes != null && attributes.length > 0) {
										final String name = ClientUtils.trimToEmpty(attributes[0]);
										final String email = attributes.length > 1 ? ClientUtils.trimToEmpty(attributes[1]) : "";
										view.addDeveloper(name, email);
									}
								}
							}
						}
					}

					// Contributors.

					view.clearContributors();
					final String contributorsString = result.get(PropertyKey.VERSION_CONTRIBUTORS);
					if (ClientUtils.isNotBlank(contributorsString)) {
						final String[] contributors = contributorsString.split(";");
						if (contributors != null && contributors.length > 0) {
							for (final String contributor : contributors) {
								if (ClientUtils.isNotBlank(contributor)) {
									final String[] attributes = contributor.split(",");
									if (attributes != null && attributes.length > 0) {
										final String name = ClientUtils.trimToEmpty(attributes[0]);
										final String email = attributes.length > 1 ? ClientUtils.trimToEmpty(attributes[1]) : "";
										view.addContributor(name, email);
									}
								}
							}
						}
					}

				}

			});

	}
}
