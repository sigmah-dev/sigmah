/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.auth;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.client.inject.SigmahAuthProvider;
import org.sigmah.server.dao.AuthenticationDAO;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.schedule.export.GlobalExportJobActivator;
import org.sigmah.shared.Cookies;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.GlobalPermission;
import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.domain.profile.PrivacyGroupPermission;
import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.dto.UserInfoDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.profile.ProfileUtils;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * Creates and returns the current user informations.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class SigmahAuthDictionaryServlet extends HttpServlet {

    private static final Log log = LogFactory.getLog(SigmahAuthDictionaryServlet.class);

    private static final long serialVersionUID = -1298849337754771926L;

    @Inject
    private Injector injector;

    private String getAuthToken(Cookie[] cookies) {
        if (cookies != null) {
            for (final Cookie cookie : cookies) {
                if (Cookies.AUTH_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	
    	/*
    	 * Initialize quartz scheduler here, because it needs link{EntityManager}
    	 * which can only accessed in request scope. 
    	 */    	    
    	injector.getInstance(GlobalExportJobActivator.class);
    	
        if (req.getParameter("remove") != null) {
            final Cookie cookie = new Cookie("authToken", "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            resp.addCookie(cookie);

        } else {
            final HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put(SigmahAuthProvider.SHOW_MENUS, String.valueOf(false));

            final String authToken = getAuthToken(req.getCookies());
            if (authToken != null) {
                final AuthenticationDAO authDAO = injector.getInstance(AuthenticationDAO.class);
                final Authentication auth = authDAO.findById(authToken);

                final User user = auth.getUser();
                
                if(user.getOrganization() == null) {
                	log.error(String.format("User with id=%d, email=%s has no organization set, cannot log into the Sigmah interface.", 
                			user.getId(), user.getEmail()));
                	resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your account is not configured for use with Sigmah");
                	return;
                }
                

                parameters.put(SigmahAuthProvider.USER_ID, Integer.toString(user.getId()));
                parameters.put(SigmahAuthProvider.USER_TOKEN, '"' + authToken + '"');
                parameters.put(SigmahAuthProvider.USER_EMAIL, '"' + user.getEmail() + '"');
                parameters.put(SigmahAuthProvider.USER_NAME, '"' + user.getName() + '"');
                parameters.put(SigmahAuthProvider.USER_FIRST_NAME, '"' + user.getFirstName() + '"');
                parameters.put(SigmahAuthProvider.USER_ORG_ID, Integer.toString(user.getOrganization().getId()));
                parameters.put(SigmahAuthProvider.USER_ORG_UNIT_ID,
                        Integer.toString(user.getOrgUnitWithProfiles().getOrgUnit().getId()));

                // Custom serialization of the profile.
                final ProfileDTO aggregatedProfile = aggregateProfiles(user, null, injector);
                final String aggregatedProfileAsString = ProfileUtils.writeProfile(aggregatedProfile);
                parameters.put(SigmahAuthProvider.USER_AG_PROFILE, '"' + aggregatedProfileAsString + '"');
                if (log.isDebugEnabled()) {
                    log.debug("[doGet] Writes aggregated profile: " + aggregatedProfile);
                    log.debug("[doGet] String representation of the profile: " + aggregatedProfileAsString);
                }
            }

            final Properties properties = injector.getInstance(Properties.class);
            parameters.put(SigmahAuthProvider.VERSION_NUMBER, '"' + properties.getProperty("version.number") + '"');

            final Charset utf8 = Charset.forName("UTF-8");
            resp.setCharacterEncoding("UTF-8");

            final ServletOutputStream output = resp.getOutputStream();
            output.println("var " + SigmahAuthProvider.DICTIONARY_NAME + " = {");

            boolean first = true;
            for (Map.Entry<String, String> entry : parameters.entrySet()) {
                if (first)
                    first = false;
                else
                    output.println(",");

                output.print(entry.getKey() + ": ");
                output.write(entry.getValue().getBytes(utf8));
            }

            output.println("};");
        }
    }

    /**
     * Aggregates the list of profiles of a user.
     *
     * @param user
     *            The user.
     * @param infos
     *            The optional user info to fill.
     * @return The aggregated profile DTO.
     */
    public static ProfileDTO aggregateProfiles(User user, UserInfoDTO infos, Injector injector) {

        // Profiles.
        if (infos != null) {
            infos.setProfiles(new ArrayList<ProfileDTO>());
        }

        // The user may have several profiles which link it to its org unit.
        // This handler merges also all the profiles in one 'aggregated
        // profile'.
        final ProfileDTO aggretatedProfileDTO = new ProfileDTO();
        aggretatedProfileDTO.setName("AGGREGATED_PROFILE");
        aggretatedProfileDTO.setGlobalPermissions(EnumSet.noneOf(GlobalPermissionEnum.class));
        aggretatedProfileDTO.setPrivacyGroups(new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>());

        if (user != null && user.getOrgUnitWithProfiles() != null
                && user.getOrgUnitWithProfiles().getProfiles() != null) {

            // For each profile.
            for (final Profile profile : user.getOrgUnitWithProfiles().getProfiles()) {

                // Creates the corresponding profile DTO.
                final ProfileDTO profileDTO = new ProfileDTO();
                profileDTO.setName(profile.getName());

                // Global permissions.
                profileDTO.setGlobalPermissions(EnumSet.noneOf(GlobalPermissionEnum.class));
                if (profile.getGlobalPermissions() != null) {
                    for (final GlobalPermission p : profile.getGlobalPermissions()) {
                        profileDTO.getGlobalPermissions().add(p.getPermission());

                        // Aggregates global permissions among profiles.
                        aggretatedProfileDTO.getGlobalPermissions().add(p.getPermission());
                    }
                }

                final Mapper mapper = injector.getInstance(Mapper.class);

                // Privacy groups.
                profileDTO.setPrivacyGroups(new HashMap<PrivacyGroupDTO, PrivacyGroupPermissionEnum>());
                if (profile.getPrivacyGroupPermissions() != null) {
                    for (final PrivacyGroupPermission p : profile.getPrivacyGroupPermissions()) {

                        final PrivacyGroupDTO groupDTO = mapper.map(p.getPrivacyGroup(), PrivacyGroupDTO.class);
                        profileDTO.getPrivacyGroups().put(groupDTO, p.getPermission());

                        // Aggregates privacy groups among profiles.
                        if (aggretatedProfileDTO.getPrivacyGroups().get(groupDTO) != PrivacyGroupPermissionEnum.WRITE) {
                            aggretatedProfileDTO.getPrivacyGroups().put(groupDTO, p.getPermission());
                        }
                    }
                }

                if (infos != null) {
                    infos.getProfiles().add(profileDTO);
                }
            }
        }

        if (infos != null) {
            infos.setAggregatedProfile(aggretatedProfileDTO);
        }

        return aggretatedProfileDTO;
    }
}
