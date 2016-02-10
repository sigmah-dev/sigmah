package org.sigmah.server.service;

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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.Indicator;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.security.UnauthorizedAccessException;

import com.google.inject.Singleton;
import java.util.ArrayList;

/**
 * {@link Indicator} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class IndicatorService extends AbstractEntityService<Indicator, Integer, IndicatorDTO> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Indicator create(final PropertyMap properties, final UserExecutionContext context) throws UnauthorizedAccessException {

		final User user = context.getUser();
		final Indicator indicator = new Indicator();

		if (properties.containsKey("activityId")) {
			Object o = properties.get("activityId");
			indicator.setActivity(em().getReference(Activity.class, o));
			assertDesignPriviledges(user, indicator.getActivity().getDatabase());

		} else if (properties.containsKey("databaseId")) {
			Object o = properties.get("databaseId");
			indicator.setDatabase(em().getReference(UserDatabase.class, o));
			assertDesignPriviledges(user, indicator.getDatabase());
		}

		updateIndicatorProperties(indicator, properties);

		if (indicator.getName().length() > 1024) {
			indicator.setName(indicator.getName().substring(0, 1024));
		}
		em().persist(indicator);

		return indicator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Indicator update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) throws UnauthorizedAccessException {

		Indicator indicator = em().find(Indicator.class, entityId);

		// todo: make UserDatabase non-nullable
		UserDatabase db = indicator.getDatabase();
		if (db == null) {
			db = indicator.getActivity().getDatabase();
		}

		assertDesignPriviledges(context.getUser(), db);

		if (indicator.getName().length() > 1024) {
			indicator.setName(indicator.getName().substring(0, 1024));
		}

		updateIndicatorProperties(indicator, changes);

		return indicator;
	}

	@SuppressWarnings("unchecked")
	private void updateIndicatorProperties(Indicator indicator, PropertyMap changes) {

		if (changes.containsKey(IndicatorDTO.NAME)) {
			indicator.setName((String) changes.get(IndicatorDTO.NAME));
		}

		if (changes.containsKey(IndicatorDTO.AGGREGATION)) {
			indicator.setAggregation((Integer) changes.get(IndicatorDTO.AGGREGATION));
		}

		if (changes.containsKey(IndicatorDTO.CATEGORY)) {
			indicator.setCategory((String) changes.get(IndicatorDTO.CATEGORY));
		}

		if (changes.containsKey(IndicatorDTO.COLLECT_INTERVENTION)) {
			indicator.setCollectIntervention((Boolean) changes.get(IndicatorDTO.COLLECT_INTERVENTION));
		}

		if (changes.containsKey(IndicatorDTO.COLLECT_MONITORING)) {
			indicator.setCollectMonitoring((Boolean) changes.get(IndicatorDTO.COLLECT_MONITORING));
		}

		if (changes.containsKey(IndicatorDTO.CODE)) {
			indicator.setCode((String) changes.get(IndicatorDTO.CODE));
		}

		if (changes.containsKey(IndicatorDTO.DESCRIPTION)) {
			indicator.setDescription((String) changes.get(IndicatorDTO.DESCRIPTION));
		}

		if (changes.containsKey(IndicatorDTO.UNITS)) {
			indicator.setUnits((String) changes.get(IndicatorDTO.UNITS));
		}

		if (changes.containsKey(IndicatorDTO.OBJECTIVE)) {
			indicator.setObjective((Double) changes.get(IndicatorDTO.OBJECTIVE));
		}

		if (changes.containsKey(IndicatorDTO.SORT_ORDER)) {
			indicator.setSortOrder((Integer) changes.get(IndicatorDTO.SORT_ORDER));
		}

		final List<String> labels = changes.get(IndicatorDTO.LABELS);
		if (labels != null) {
			if(indicator.getLabels() == null) {				
				indicator.setLabels(new ArrayList<String>());
			} else {
				indicator.getLabels().clear();
			}
			indicator.getLabels().addAll(labels);
		}

		if (changes.containsKey(IndicatorDTO.SOURCE_OF_VERIFICATION)) {
			indicator.setSourceOfVerification((String) changes.get(IndicatorDTO.SOURCE_OF_VERIFICATION));
		}

		if (changes.containsKey(IndicatorDTO.GROUP_ID)) {
			if (changes.get(IndicatorDTO.GROUP_ID) != null) {
				indicator.setActivity(em().getReference(Activity.class, changes.get(IndicatorDTO.GROUP_ID)));
			} else {
				indicator.setActivity(null);
			}
		}

		if (changes.containsKey(IndicatorDTO.DIRECT_DATA_ENTRY_ENABLED)) {
			indicator.setDirectDataEntryEnabled((Boolean) changes.get(IndicatorDTO.DIRECT_DATA_ENTRY_ENABLED));
		}

		if (changes.containsKey(IndicatorDTO.DATA_SOURCE_IDS)) {
			Set<Integer> ids = (Set<Integer>) changes.get(IndicatorDTO.DATA_SOURCE_IDS);
			if (ids.isEmpty()) {
				indicator.setDataSources(Collections.<Indicator> emptySet());
			} else {
				List<Indicator> dataSources = em().createQuery("select i from Indicator i where i.id in (:ids)").setParameter("ids", ids).getResultList();
				indicator.setDataSources(new HashSet<Indicator>(dataSources));
			}
		}
		
		if (indicator.getActivity() != null) {
			indicator.getActivity().getDatabase().setLastSchemaUpdate(new Date());
		} else {
			indicator.getDatabase().setLastSchemaUpdate(new Date());
		}
	}

	/**
	 * Asserts that the user has permission to modify the structure of the given database.
	 * 
	 * @param user
	 *          THe user for whom to check permissions
	 * @param database
	 *          The database the user is trying to modify
	 * @throws RuntimeException
	 *           If the user does not have permission.
	 */
	private void assertDesignPriviledges(final User user, final UserDatabase database) throws UnauthorizedAccessException {

		if (!database.isAllowedDesign(user)) {
			throw new UnauthorizedAccessException("Illegal access.");
		}

	}

}
