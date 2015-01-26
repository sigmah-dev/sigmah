/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.server.schedule.export;

import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/*
 * Setting up quartz with guice 
 * @author sherzod
 */
public class SchedulerModule extends AbstractModule {

	@Override
	protected void configure() {
	   bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Scopes.SINGLETON);
	   bind(GuiceJobFactory.class).in(Scopes.SINGLETON);
	   bind(QuartzScheduler.class).in(Scopes.SINGLETON);
	}
}