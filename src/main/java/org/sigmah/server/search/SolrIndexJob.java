package org.sigmah.server.search;

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

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.google.inject.Injector;

/**
 * Scheduled (in {@link SolrIndexJobActivator}) job to execute Solr Full Data Import and
 * import of files.Necessary for updating search index.
 * 
 * @author 
 */
public class SolrIndexJob implements Job{

	@Override
	public void execute(JobExecutionContext executionContext) throws JobExecutionException {
		
		final JobDataMap dataMap = executionContext.getJobDetail().getJobDataMap();
		final EntityManager em = (EntityManager) dataMap.get("em");
		final Injector injector = (Injector) dataMap.get("injector");
		System.out.println("Starting Solr Full Data Import!");
		if( !SolrSearcher.getInstance().fullDataImport()){
			System.out.println("Could not finish Full Data Import!");
			return;
		}
		System.out.println("Finished Solr Full Data Import!");
		EntityTransaction tx = null;
		
		try {
			tx = em.getTransaction();
			tx.begin();
			FilesSolrManager filesSolrManager = injector.getInstance(FilesSolrManager.class);
			System.out.println("Starting Files Solr Indexing!");
			filesSolrManager.filesImport(SolrSearcher.getInstance());
			System.out.println("Finished Files Solr Indexing!");
			tx.commit();
			System.out.println("Scheduled indexing of solr server over");
			
		}catch(RuntimeException | IOException e){
			if (tx != null && tx.isActive())
				tx.rollback();
			System.out.println("Could not complete Files Solr Indexing!");
			System.out.println("Ran into unexpected Runtime Exception while indexing files!");
			e.printStackTrace();
		}
		
	}

}
