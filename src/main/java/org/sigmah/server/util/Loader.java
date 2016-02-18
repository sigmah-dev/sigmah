/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sigmah.server.util;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.persist.PersistService;
import java.util.Arrays;
import java.util.HashSet;
import javax.persistence.EntityManager;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.Country;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.base.EntityId;
import org.sigmah.server.domain.logframe.LogFrameModel;
import org.sigmah.server.inject.ConfigurationModule;
import org.sigmah.server.inject.I18nServerModule;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.server.servlet.exporter.models.Realizer;

/**
 *
 * @author Mohamed KHADHRAOUI
 */
public class Loader {
	
	/*Id of project , project should be in database*/
	private static final Integer PROJECT_ID=953;
	/*Count of projecte to be inserted */
	private static final Integer COUNT=2;
	
	public static void main(String[] args) {
		final Injector injector = Guice.createInjector(
			// Configuration module.
			new ConfigurationModule(),
			// Persistence module.
			new PersistenceModule(),
			// Mapper module.
			new MapperModule(),
			// I18nServer module.
			new I18nServerModule());
		
		injector.getInstance(PersistService.class).start();
		try {
			
			ProjectDAO projectDAO = injector.getInstance(ProjectDAO.class);			
			Project project=projectDAO.findById(PROJECT_ID);
			
			for(int i=0;i<COUNT;i++){
				Project newProject=Realizer.realize(project, new HashSet<>(Arrays.asList("id")), ProjectModel.class,User.class,OrgUnit.class,Country.class,PhaseModel.class,LogFrameModel.class);
				newProject.setName("gen-" + i);
				newProject.getId();
				final EntityManager em = injector.getProvider(EntityManager.class).get();
				em.getTransaction().begin();
				//em.persist(newProject);
				em.merge(newProject);				
				em.getTransaction().commit();				
			}			
		} finally {
			injector.getInstance(PersistService.class).stop();
		}
	}	
}
