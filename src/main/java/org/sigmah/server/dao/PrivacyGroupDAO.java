package org.sigmah.server.dao;

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
