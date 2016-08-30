package org.sigmah.server.util;

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
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import java.util.Arrays;
import java.util.HashSet;
import javax.persistence.EntityManager;
import org.apache.commons.lang3.StringUtils;

import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Organization;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.logframe.LogFrameModel;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.server.servlet.exporter.models.Realizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mohamed KHADHRAOUI (mohamed.khadhraoui@netapsys.fr)
 */
public class Loader {

	/**
	 * Id of project , project should be in database.
	 */
	private static Integer projectId = 15472 /*953*/;
	/**
	 * Count of project to be inserted.
	 */
	private static  Integer count = 2;
	
	private static final Logger LOGGER=LoggerFactory.getLogger(Loader.class);

	/**
	 * Load one project from data base and duplicate its multiples times for
	 * test.
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		LOGGER.info("Loader project started.");
		final Injector injector = Guice.createInjector(
				// Configuration module.
				new ConfigurationModule(),
				// Persistence module.
				new PersistenceModule(),
				// Mapper module.
				new MapperModule(),
				// I18nServer module.
				new I18nServerModule());
		if(args!=null){
			if( args.length>0 && StringUtils.isNumeric(args[0])){
				projectId=Integer.valueOf(args[0]);
			}
			if(args.length>1 &&  StringUtils.isNumeric(args[1])){
				count=Integer.valueOf(args[1]);
			}
		}
		
		injector.getInstance(PersistService.class).start();
		final EntityManager em = injector.getProvider(EntityManager.class).get();
		final ProjectDAO projectDAO = injector.getInstance(ProjectDAO.class);
		final Project project = projectDAO.findById(projectId);
		if(project==null){
			LOGGER.info("Project not found .");
			return ;
		}
		LOGGER.info("Duplicating project with id: "+project + " for "+count+" times ");
		em.getTransaction().begin();
		try {			
			final Class[] classes = {Organization.class, 
									 ProjectModel.class, 
									 User.class, OrgUnit.class, Country.class, PhaseModel.class, LogFrameModel.class};
			
			for (int i = 0; i < count; i++) {
				final Project newProject = Realizer.realize(project, new HashSet<>(Arrays.asList("id")), classes);
				newProject.setName("gen-" + i);
				newProject.getId();
				em.merge(newProject);
			}
			em.getTransaction().commit();
		} catch (Exception e) {
			LOGGER.error("An error occured while duplicating projects.", e);
			em.getTransaction().rollback();
		} finally {
			injector.getInstance(PersistService.class).stop();
		}
		LOGGER.info("Loader project ended.");
	}
	
	
	
}
