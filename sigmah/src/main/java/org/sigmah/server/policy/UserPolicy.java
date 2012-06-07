package org.sigmah.server.policy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.auth.impl.PasswordHelper;
import org.sigmah.server.dao.hibernate.UserDAOImpl;
import org.sigmah.server.endpoint.gwtrpc.handler.GetUsersWithProfilesHandler;
import org.sigmah.server.mail.Invitation;
import org.sigmah.server.mail.Mailer;
import org.sigmah.shared.dao.UserDAO;
import org.sigmah.shared.dao.UserUnitDAO;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.OrgUnitProfile;
import org.sigmah.shared.domain.profile.Profile;
import org.sigmah.shared.dto.UserDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.inject.Inject;

import org.sigmah.server.endpoint.gwtrpc.PasswordManagementServlet;

/**
 * Create user policy.
 * 
 * @author nrebiai
 * 
 */
public class UserPolicy implements EntityPolicy<User> {

    private final Mapper mapper;
    private final EntityManager em;
    private final UserDAO userDAO;
    private final UserUnitDAO userUnitDAO;
    private final Mailer<Invitation> inviteMailer;
    private static final Log log = LogFactory.getLog(UserPolicy.class);

    // The key for reading the host url value from
    // src/main/resources/sigmah.properties
    final public static String KEY_HOST_URL = "host.url";

    // The default value if the key above does not exist in
    // src/main/resources/sigmah.properties
    final public static String DEFAULT_HOST_URL = "http://www.sigmah.org";

    private final Properties properties;

    @Inject
    public UserPolicy(EntityManager em, UserDAO userDAO, UserUnitDAO userUnitDAO, Mailer<Invitation> inviteMailer,
            Mapper mapper, Properties properties) {
        this.em = em;
        this.userDAO = userDAO;
        this.userUnitDAO = userUnitDAO;
        this.inviteMailer = inviteMailer;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public Object create(User executingUser, PropertyMap properties) {
        User userToPersist = null;
        User userFound = null;
        OrgUnitProfile orgUnitProfileToPersist = null;
        OrgUnitProfile orgUnitProfileFound = null;

        // get User that need to be saved from properties
        int id = (Integer) properties.get("id");
        String email = properties.get("email");
        String name = properties.get("name");
        String firstName = properties.get("firstName");
        String locale = properties.get("locale");
        String password = properties.get("pwd");
        int orgUnitId = (Integer) properties.get("orgUnit");
        List<Integer> profilesIds = properties.get("profiles");

        // Save user
        if (email != null && name != null) {

            userFound = em.find(User.class, id);
            if (userFound != null) {
                if (userUnitDAO.doesOrgUnitProfileExist(userFound))
                    orgUnitProfileFound = userUnitDAO.findOrgUnitProfileByUser(userFound);
            }

            userToPersist = UserDAOImpl.createNewUser(email, name, locale);
            userToPersist.setFirstName(firstName);
            userToPersist.setOrganization(executingUser.getOrganization());

            if (password != null && !password.isEmpty()) {
                userToPersist.setHashedPassword(PasswordHelper.hashPassword(password));
                userToPersist.setChangePasswordKey(null);
                userToPersist.setDateChangePasswordKeyIssued(new Date());
            }

            if (userFound != null && userFound.getId() > 0) {
                // update user
                userToPersist.setId(userFound.getId());
                userToPersist = em.merge(userToPersist);
            } else {
                // create new user
                if (!userDAO.doesUserExist(email)) {
                    password = PasswordHelper.generatePassword();
                    userToPersist.setHashedPassword(PasswordHelper.hashPassword(password));                    
                    userDAO.persist(userToPersist);
                    try {

                    /*
                        //osarrat 2011-10-17 : commented until confirm url are managed
                        final StringBuilder sb = new StringBuilder();
                        sb.append(this.properties.getProperty(KEY_HOST_URL, DEFAULT_HOST_URL));
                        sb.append("/NewUserConfirm?");
                        sb.append(userToPersist.getChangePasswordKey());
                        final String confirmUrl = sb.toString();

                        if (log.isDebugEnabled()) {
                            log.debug("Send the email after creating the new user: " + userToPersist.getEmail());
                            log.debug("The url is : " + confirmUrl);
                        }
                      */
                        final String confirmUrl = this.properties.getProperty(KEY_HOST_URL, DEFAULT_HOST_URL); 
                      
                        // final Locale userLocale =
                        // LocaleHelper.getLocaleObject(userToPersist);
                        final Locale userLocale = new Locale(locale);
                        inviteMailer.send(new Invitation(userToPersist, executingUser, confirmUrl, password), userLocale, true);

                    } catch (Exception e) {
                        // ignore, don't abort because mail didn't work
                        log.debug("Exception happens here: \n");
                        log.debug(e.getMessage());
                        System.out.println(e.getStackTrace());
                    }
                } else {
                    return null;
                }
            }

            // update link to profile
            if (userToPersist.getId() > 0) {
                if (userFound != null && userFound.getId() > 0 && orgUnitProfileFound != null)
                    orgUnitProfileToPersist = orgUnitProfileFound;
                else
                    orgUnitProfileToPersist = new OrgUnitProfile();

                OrgUnit orgUnit = em.find(OrgUnit.class, orgUnitId);
                if (orgUnit != null) {
                    orgUnitProfileToPersist.setOrgUnit(orgUnit);

                    List<Profile> profilesToPersist = new ArrayList<Profile>();
                    for (int profileId : profilesIds) {
                        Profile profile = em.find(Profile.class, profileId);
                        profilesToPersist.add(profile);
                    }
                    orgUnitProfileToPersist.setProfiles(profilesToPersist);
                    orgUnitProfileToPersist.setUser(userToPersist);
                    if (userFound != null && userFound.getId() > 0 && orgUnitProfileFound != null) {
                        orgUnitProfileToPersist = em.merge(orgUnitProfileToPersist);
                    } else {
                        em.persist(orgUnitProfileToPersist);
                    }
                    if (orgUnitProfileToPersist.getId() != 0) {
                        userToPersist.setOrgUnitWithProfiles(orgUnitProfileToPersist);
                    }
                }
            }
        }

        UserDTO userPersisted = null;
        if (userToPersist != null) {
            userPersisted = GetUsersWithProfilesHandler.mapUserToUserDTO(userToPersist, mapper);
            userPersisted.setIdd(userToPersist.getId());
        }

        return userPersisted;
    }

    @Override
    public void update(User user, Object entityId, PropertyMap changes) {
        // TODO Auto-generated method stub

    }

    public BaseModelData createDraft(Map<String, Object> properties) {
        // TODO Auto-generated method stub
        return null;
    }

}
