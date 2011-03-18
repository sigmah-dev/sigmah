/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.table;

import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageLoader;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.PageStateSerializer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class PivotPageLoader implements PageLoader {

    private Provider<PivotPage> page;

    @Inject
    public PivotPageLoader(NavigationHandler pageManager, PageStateSerializer placeSerializer, Provider<PivotPage> page) {
    	this.page = page;
        pageManager.registerPageLoader(PivotPage.PAGE_ID, this);
        placeSerializer.registerParser(PivotPage.PAGE_ID, new PivotPageState.Parser());
    }

    @Override
    public void load(PageId pageId, PageState pageState, final AsyncCallback<Page> callback) {

        GWT.runAsync(new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess(page.get());
            }
        });

    }
}
