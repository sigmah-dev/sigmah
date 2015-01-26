/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.client.page.passwordreset;

import java.util.Arrays;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.HasTab;
import org.sigmah.client.page.PageId;
import org.sigmah.client.page.PageState;
import org.sigmah.client.page.PageStateParser;
import org.sigmah.client.page.TabPage;
import org.sigmah.client.ui.Tab;


/**
 * @author Sherzod Muratov (sherzod.muratov@gmail.com)
 */
public class PasswordResetPageState implements PageState,TabPage, HasTab{

	public static class Parser implements PageStateParser{
		private final static PasswordResetPageState instance = new PasswordResetPageState();
		@Override
		public PageState parse(String token) {
			
			return instance;
		}
		
	}
	
	private Tab tab;
	
	@Override
	public String serializeAsHistoryToken() {		
		return null;
	}

	@Override
	public PageId getPageId() {		
		return PasswordResetPresenter.PAGE_ID;
	}

	@Override
	public List<PageId> getEnclosingFrames() {		
		return Arrays.asList(PasswordResetPresenter.PAGE_ID);
	}

	@Override
	public Tab getTab() {
 		return tab;
	}

	@Override
	public void setTab(Tab tab) {
		 this.tab = tab;
		
	}

	@Override
	public String getTabTitle() {
 		return I18N.CONSTANTS.passwordReset();
	}

}
