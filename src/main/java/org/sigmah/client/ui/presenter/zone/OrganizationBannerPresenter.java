package org.sigmah.client.ui.presenter.zone;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.client.ui.presenter.base.AbstractZonePresenter;
import org.sigmah.client.ui.res.ResourcesUtils;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.zone.OrganizationBannerView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.ImageProvider;
import org.sigmah.offline.dao.LogoAsyncDAO;
import org.sigmah.offline.status.ApplicationState;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasHTML;
import com.google.gwt.user.client.ui.Panel;

/**
 * Organization banner presenter displaying organization's name and logo.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */

public class OrganizationBannerPresenter extends AbstractZonePresenter<OrganizationBannerPresenter.View> {

	/**
	 * View interface.
	 */
	
	public static interface View extends ViewInterface {

		Panel getLogoPanel();

		Panel getNamePanel();

		HasHTML getNameLabel();

	}

	private LogoAsyncDAO logoAsyncDAO;
	private ImageProvider imageProvider;
	
	private ApplicationState state;
	
	/**
	 * Default organization name.
	 */
	private static final String DEFAULT_ORGANIZATION_NAME = "Sigmah";

	/**
	 * Default organization logo URL.
	 * Should be a public resource file path.
	 */
	private static final String DEFAULT_ORGANIZATION_LOGO = ResourcesUtils.buildImageURL("header/org-default-logo.png");
	
	
	public OrganizationBannerPresenter(View view, ClientFactory injector, LogoAsyncDAO logoAsyncDAO, ImageProvider imageProvider) {
		super(view, injector);

		this.logoAsyncDAO = logoAsyncDAO;
		this.imageProvider = imageProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Zone getZone() {
		return Zone.ORG_BANNER;
	}

	@Override
	public void onBind() {
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				state = event.getState();
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onZoneRequest(final ZoneRequest zoneRequest) {

		// Updates name widget.
		final String name = ClientUtils.isNotBlank(auth().getOrganizationName()) ? auth().getOrganizationName() : DEFAULT_ORGANIZATION_NAME;
		view.getNameLabel().setHTML(name);

		// Updates logo widget - requires a servlet request.
		if (isAnonymous()) {
			view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(DEFAULT_ORGANIZATION_LOGO));
			return;
		}

		if(state != ApplicationState.OFFLINE) {
			imageProvider.provideDataUrl(auth().getOrganizationLogo(), new AsyncCallback<String>() {
				@Override
				public void onFailure(Throwable caught) {
					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(DEFAULT_ORGANIZATION_LOGO));
				}

				@Override
				public void onSuccess(String dataUrl) {
					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(dataUrl));
					logoAsyncDAO.saveOrUpdate(auth().getOrganizationId(), dataUrl);
				}
			});
			
		} else {
			logoAsyncDAO.get(auth().getOrganizationId(), new AsyncCallback<String>() {

				@Override
				public void onFailure(Throwable caught) {
					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(DEFAULT_ORGANIZATION_LOGO));
				}

				@Override
				public void onSuccess(String result) {
					final String logoUrl;
					
					if(result != null) {
						logoUrl = result;
					} else {
						logoUrl = DEFAULT_ORGANIZATION_LOGO;
					}
					
					view.getLogoPanel().getElement().getStyle().setBackgroundImage(ResourcesUtils.buildCSSImageProperty(logoUrl));
				}
			});
		}
	}

}
