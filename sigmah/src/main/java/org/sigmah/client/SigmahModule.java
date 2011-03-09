/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchEventSource;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.RemoteServiceProvider;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.dispatch.remote.RemoteDispatcher;
import org.sigmah.client.inject.Root;
import org.sigmah.client.inject.SigmahAuthProvider;
import org.sigmah.client.offline.command.LocalDispatcher;
import org.sigmah.client.offline.sigmah.DispatchOperator;
import org.sigmah.client.offline.sigmah.handler.GetCountriesHandler;
import org.sigmah.client.offline.sigmah.handler.GetMonitoredPointsHandler;
import org.sigmah.client.offline.sigmah.handler.GetOrganizationHandler;
import org.sigmah.client.offline.sigmah.handler.GetRemindersHandler;
import org.sigmah.client.offline.sigmah.handler.GetUsersByOrganizationHandler;
import org.sigmah.client.page.Frame;
import org.sigmah.client.page.PageStateSerializer;
import org.sigmah.client.page.charts.ChartPage;
import org.sigmah.client.page.charts.ChartPagePresenter;
import org.sigmah.client.page.common.GalleryPage;
import org.sigmah.client.page.common.GalleryView;
import org.sigmah.client.util.state.GXTStateManager;
import org.sigmah.client.util.state.IStateManager;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.RemoteCommandServiceAsync;

/**
 * Gin configuration module for Sigmah.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SigmahModule extends AbstractGinModule {

    /**
     * Configures the Gin injector.
     */
    @Override
    protected void configure() {

//        bind(NavigationHandler.class).to(SigmahNavigationHandler.class);
        
        bind(Authentication.class).toProvider(SigmahAuthProvider.class).in(Singleton.class);
        bind(RemoteCommandServiceAsync.class).toProvider(RemoteServiceProvider.class).in(Singleton.class);
        bind(Dispatcher.class).to(DispatchOperator.class);
        bind(DispatchEventSource.class).to(RemoteDispatcher.class);
        bind(PageStateSerializer.class).in(Singleton.class);
        bind(EventBus.class).to(LoggingEventBus.class).in(Singleton.class);
        bind(IStateManager.class).to(GXTStateManager.class);
        
        bind(Frame.class).annotatedWith(Root.class).to(SigmahAppFrame.class);
        
        // Required by the 'Setup' page
        bind(GalleryView.class).to(GalleryPage.class);
        
        // Required by the 'Charts' page
        bind(ChartPagePresenter.View.class).to(ChartPage.class);


        //DAOs for off-line
//        bind(SQLDialect.class).to(SqliteDialect.class).in(Singleton.class);
//        bind(SiteTableDAO.class).to(SqlSiteTableDAO.class).in(Singleton.class);
    }

//    @Provides
//    @Singleton
//    protected Connection provideConnection(Authentication auth) {
//        try {
//            return GearsConnectionFactory.getConnection(DispatchOperator.LOCAL_DATABASE_NAME);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    @Provides
    protected LocalDispatcher provideLocalDispatcher(Authentication auth) {

        LocalDispatcher dispatcher = new LocalDispatcher(auth);
//        dispatcher.registerHandler(GetSchema.class, schemaHandler);
//        dispatcher.registerHandler(GetSites.class, sitesHandler);
//        dispatcher.registerHandler(GetAdminEntities.class, adminHandler);
        dispatcher.registerHandler(GetMonitoredPoints.class, new GetMonitoredPointsHandler());
        dispatcher.registerHandler(GetReminders.class, new GetRemindersHandler());
        dispatcher.registerHandler(GetOrganization.class, new GetOrganizationHandler());
        dispatcher.registerHandler(GetCountries.class, new GetCountriesHandler());
        dispatcher.registerHandler(GetUsersByOrganization.class, new GetUsersByOrganizationHandler());

        return dispatcher;
    }
}
