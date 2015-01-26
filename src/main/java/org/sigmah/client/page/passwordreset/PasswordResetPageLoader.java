/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.passwordreset;

import org.sigmah.client.SigmahInjector;
import org.sigmah.client.page.NavigationHandler;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageLoader;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.PageStateSerializer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * This Page loader is only registered for unauthorized users
 * 
 * @author Sherzod Muratov (sherzod.muratov@gmail.com)
 */
public class PasswordResetPageLoader implements PageLoader{

	private final SigmahInjector injector;
	private PasswordResetPresenter presenter;
	
	@Inject
	public PasswordResetPageLoader(final SigmahInjector injector,
			final NavigationHandler navigationHandler,
			final PageStateSerializer placeSerializer){
		this.injector = injector;
		navigationHandler.registerPageLoader(PasswordResetPresenter.PAGE_ID, this);
		placeSerializer.registerParser(PasswordResetPresenter.PAGE_ID, new PasswordResetPageState.Parser());		
	}
	
	
	@Override
	public void load(PageId pageId, PageState pageState,AsyncCallback<Page> callback) {
		if(pageId.equals(PasswordResetPresenter.PAGE_ID)){
			
			if(presenter == null)
				presenter=injector.getPasswordResetPresenter();
			
			presenter.navigate(pageState);
			callback.onSuccess(presenter);
		}
	}
	
	

}
