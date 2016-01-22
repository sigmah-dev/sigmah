package org.sigmah.offline.handler;

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

import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.HashMap;
import java.util.HashSet;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.AuthenticationAsyncDAO;
import org.sigmah.offline.dao.PageAccessAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.js.PageAccessJS;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.SecureNavigationResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.referential.PrivacyGroupPermissionEnum;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.SecureNavigationCommandHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class SecureNavigationAsyncHandler implements AsyncCommandHandler<SecureNavigationCommand, SecureNavigationResult>, DispatchListener<SecureNavigationCommand, SecureNavigationResult> {

	@Inject
	private AuthenticationAsyncDAO authenticationAsyncDAO;
	
	@Inject
	private PageAccessAsyncDAO pageAccessAsyncDAO;

	@Override
	public void execute(SecureNavigationCommand command, OfflineExecutionContext executionContext, AsyncCallback<SecureNavigationResult> callback) {
		final SecureNavigationResult result = new SecureNavigationResult();
		final RequestManager<SecureNavigationResult> requestManager = new RequestManager<SecureNavigationResult>(result, callback);
		
		authenticationAsyncDAO.get(new RequestManagerCallback<SecureNavigationResult, Authentication>(requestManager) {
			@Override
			public void onRequestSuccess(Authentication authentication) {
				result.setAuthentication(authentication);
				
				if(authentication == null) {
					// User is anonymous
					Cookies.removeCookie(org.sigmah.shared.Cookies.AUTH_TOKEN_COOKIE);
					
					// TODO: Create a shared class to avoid code duplicate for server and client.
					final ProfileDTO aggretatedProfileDTO = new ProfileDTO();
					aggretatedProfileDTO.setName("AGGREGATED_PROFILE");
					aggretatedProfileDTO.setGlobalPermissions(new HashSet<GlobalPermissionEnum>());
					aggretatedProfileDTO.setPrivacyGroups(new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>());
		
					Language language = Language.fromString(LocaleInfo.getCurrentLocale().getLocaleName());
					if(language == null) {
						language = Language.EN;
					}
					
					result.setAuthentication(new Authentication(null, "anonymous@nowhere.com", "anonymous", null, language, null, null, null, null, aggretatedProfileDTO));
				}
			}
		});
		
		pageAccessAsyncDAO.get(command.getPage(), new RequestManagerCallback<SecureNavigationResult, PageAccessJS>(requestManager) {
			@Override
			public void onRequestSuccess(PageAccessJS pageAccessJS) {
				result.setGranted(pageAccessJS.isGranted());
			}
		});
		
		requestManager.ready();
	}

	@Override
	public void onSuccess(SecureNavigationCommand command, SecureNavigationResult result, Authentication authentication) {
        if(result != null && result.getAuthentication() != null) {
            authenticationAsyncDAO.saveOrUpdate(result.getAuthentication());
            pageAccessAsyncDAO.saveOrUpdate(PageAccessJS.createPageAccessJS(command.getPage(), result.isGranted()));
            
			final Storage storage = Storage.getLocalStorageIfSupported();
			storage.setItem(LocalDispatchServiceAsync.LAST_USER_ITEM, result.getAuthentication().getUserEmail());
        }
	}
}
