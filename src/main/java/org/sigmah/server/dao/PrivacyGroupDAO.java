package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.profile.Profile;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.profile.PrivacyGroup} domain class.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public interface PrivacyGroupDAO extends DAO<PrivacyGroup, Integer> {

	/**
	 * Counts the number of <b>distinct</b> {@link Profile}(s) related to the given {@code privacyGroupId}.
	 * 
	 * @param privacyGroupId
	 *          The {@link PrivacyGroup} id.
	 * @return The number of {@link Profile}(s) related to the given {@code privacyGroupId}.
	 */
	int countRelatedProfiles(Integer privacyGroupId);

	/**
	 * Retrieves the <b>distinct</b> {@link Profile}(s) related to the given {@code privacyGroupId}.
	 * 
	 * @param privacyGroupId
	 *          The {@link PrivacyGroup} id.
	 * @return The number of {@link Profile}(s) related to the given {@code privacyGroupId}.
	 */
	List<Profile> findRelatedProfiles(Integer privacyGroupId);

	/**
	 * Counts the number of {@link FlexibleElement}(s) related to the given {@code privacyGroupId}.
	 * 
	 * @param privacyGroupId
	 *          The {@link PrivacyGroup} id.
	 * @return The number of {@link FlexibleElement}(s) related to the given {@code privacyGroupId}.
	 */
	int countRelatedFlexibleElements(Integer privacyGroupId);

	/**
	 * Retrieves the {@link FlexibleElement}(s) related to the given {@code privacyGroupId}.
	 * 
	 * @param privacyGroupId
	 *          The {@link PrivacyGroup} id.
	 * @return The number of {@link FlexibleElement}(s) related to the given {@code privacyGroupId}.
	 */
	List<FlexibleElement> findRelatedFlexibleElements(Integer privacyGroupId);

}
