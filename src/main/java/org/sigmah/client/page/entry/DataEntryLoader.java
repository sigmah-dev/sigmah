/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.callback.Got;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.inject.AppInjector;
import org.sigmah.client.page.Frames;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageLoader;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.PageStateSerializer;
import org.sigmah.client.page.common.filter.AdminFilterPanel;
import org.sigmah.client.page.common.filter.DateRangePanel;
import org.sigmah.client.page.common.filter.PartnerFilterPanel;
import org.sigmah.client.page.common.nav.NavigationPanel;
import org.sigmah.client.page.common.widget.VSplitFilteredFrameSet;
import org.sigmah.shared.command.GetSchema;
import org.sigmah.shared.dto.SchemaDTO;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * Loader for the {@link DataEntryPage }
 * 
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class DataEntryLoader implements PageLoader {

	private Provider<DataEntryPage> dataEntryPageProvider;
	private Dispatcher dispatcher;
	

    @Inject
    public DataEntryLoader(NavigationHandler pageManager, PageStateSerializer placeSerializer, Dispatcher dispatcher, 
    		Provider<DataEntryPage> dataEntryPageProvider) {

        pageManager.registerPageLoader(DataEntryPage.ID, this);
        placeSerializer.registerParser(DataEntryPage.ID, new SiteGridPageState.Parser());
      
        this.dispatcher = dispatcher;
        this.dataEntryPageProvider = dataEntryPageProvider;
    }

    @Override
    public void load(final PageId pageId, final PageState pageState, final AsyncCallback<Page> callback) {

        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                if (DataEntryPage.ID.equals(pageId)) {
                    callback.onSuccess(dataEntryPageProvider.get());
                }
            }

            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });

    }
}
