/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.passwordreset;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.event.NavigationEvent.NavigationError;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.Frame;
import org.sigmah.client.page.NavigationCallback;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.TabPage;

import com.google.gwt.user.client.ui.HTML;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;


/**
 * Presenter for reset password view
 * 
 * @author Sherzod Muratov (sherzod.muratov@gmail.com)
 */


public class PasswordResetPresenter implements TabPage, Frame{
	
	public static final PageId PAGE_ID = new PageId("passwordReset");

	@ImplementedBy(PasswordResetView.class)	
	public interface View{		
		
	}
	
	private final Authentication authentication;
	private Page activePage;
	private final View view;
	
 	
	@Inject
	public PasswordResetPresenter(final View view,final Authentication authentication){
		this.view  = view;
		this.authentication=authentication;
	}
	
		

	@Override
	public PageId getPageId() {
		return PAGE_ID;
	}

	@Override
	public Object getWidget() {
		if (authentication != null) {
			final HTML infoHTML = new HTML(I18N.CONSTANTS.authorizedUserPasswordResetInfo());
			infoHTML.addStyleName("important-label-white");
 			return infoHTML;
		}
		return view;
	}

	@Override
	public void requestToNavigateAway(PageState place,
			NavigationCallback callback) {	
	    callback.onDecided(NavigationError.NONE);
	}

	@Override
	public String beforeWindowCloses() {
		return null;
	}

	@Override
	public boolean navigate(PageState place) {
		return true;
	}
	
	@Override
	public void shutdown() {		
	}



	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.passwordReset();
	}
 


	@Override
	public AsyncMonitor showLoadingPlaceHolder(PageId pageId,
			PageState loadingPlace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActivePage(Page page) {
		this.activePage = page;

	}

	@Override
	public Page getActivePage() {
		return this.activePage;
	}

}