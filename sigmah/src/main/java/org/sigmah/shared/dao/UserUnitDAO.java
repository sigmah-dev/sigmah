/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dao;

import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.OrgUnitProfile;


/**
 * Data Access Object for the {@link org.sigmah.shared.domain.User} domain class.
 *
 * @author nrebiai
 */
public interface UserUnitDAO extends DAO<OrgUnitProfile, Integer> {

	OrgUnitProfile findOrgUnitProfileByUser(User user);

	boolean doesOrgUnitProfileExist(User user);
   
}
