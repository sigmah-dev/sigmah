package org.sigmah.server.util;

import java.util.Calendar;

import java.util.Date;
import org.sigmah.server.domain.calendar.PersonalEvent;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
/**
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class PersonnalEventLoader {

	private static final Logger LOGGER = LoggerFactory.getLogger(PersonnalEventLoader.class);

	public static void main(String[] args) {
		LOGGER.info("PersonnalEventLoader started");
		Integer projectId;
		Integer count;
		final Injector injector = Guice.createInjector(
				// Configuration module.
				new ConfigurationModule(),
				// Persistence module.
				new PersistenceModule(),
				// Mapper module.
				new MapperModule(),
				// I18nServer module.
				new I18nServerModule());

		if (args != null && args.length > 1 && StringUtils.isNumeric(args[0]) && StringUtils.isNumeric(args[1])) {
			count = Integer.valueOf(args[1]);
			projectId = Integer.valueOf(args[0]);
		} else {
			LOGGER.error("Parameters are not correct");
			LOGGER.info("PersonnalEventLoader ended with errors");
			return;
		}

		injector.getInstance(PersistService.class).start();
		final ProjectDAO projectDAO = injector.getInstance(ProjectDAO.class);
		final Project project = projectDAO.findById(projectId);
		if (project != null) {
			final EntityManager em = injector.getProvider(EntityManager.class).get();
			em.getTransaction().begin();
			LOGGER.info("Creating" + count + " PersonalEvent");
			try {
				final Calendar cal = count < 365 ? initCalendar(2016) : initCalendar(2015);
				for (int i = 0; i < count; i++) {
					cal.add(Calendar.DAY_OF_MONTH, 1);
					PersonalEvent personEvent = personalEventFactory(cal.getTime(), project.getCalendarId(), i);
					em.merge(personEvent);
				}
				em.getTransaction().commit();
			} catch (Exception e) {
				LOGGER.error("An error occured while duplicating projects.", e);
				em.getTransaction().rollback();
			} finally {
				injector.getInstance(PersistService.class).stop();
			}
			LOGGER.info("PersonnalEventLoader ended with success");
		} else {
			LOGGER.error("Project  with id = " + projectId + " not found.");
			injector.getInstance(PersistService.class).stop();
			LOGGER.info("PersonnalEventLoader ended with errors");
		}
	}

	/**
	 * Instanciate and initialize PersonalEvent.
	 *
	 * @param startDate
	 * @param parentCalendarId
	 * @param i
	 * @return
	 */
	public static PersonalEvent personalEventFactory(Date startDate, Integer parentCalendarId, int i) {
		final PersonalEvent personalEvent = new PersonalEvent();
		personalEvent.setDateCreated(new Date());
		personalEvent.setDescription("Gen personal event " + i);
		personalEvent.setSummary("gen personal event summary " + i);
		personalEvent.setStartDate(startDate);
		
		final Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);
		cal.set(Calendar.HOUR_OF_DAY, Calendar.HOUR_OF_DAY + 1);
		
		personalEvent.setEndDate(cal.getTime());
		personalEvent.setCalendarId(parentCalendarId);
		return personalEvent;
	}

	/**
	 * init calendar.
	 *
	 * @param year
	 * @return
	 */
	private static Calendar initCalendar(int year) {
		final Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return cal;
	}
}
