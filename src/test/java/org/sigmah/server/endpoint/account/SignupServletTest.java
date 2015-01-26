package org.sigmah.server.endpoint.account;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.OnDataSet;
import org.sigmah.shared.domain.Organization;
import org.sigmah.test.InjectionSupport;
import org.sigmah.test.MockHibernateModule;
import org.sigmah.test.Modules;

import com.google.inject.Inject;
import com.google.inject.Provider;

@RunWith(InjectionSupport.class)
@OnDataSet("/dbunit/sites-simple1.db.xml")
@Modules({MockHibernateModule.class})
public class SignupServletTest {
	
	
	@Inject
	Provider<EntityManager> emProvider;
	
	@Inject
	EntityManagerFactory emf;
	
	@Test
	public void newOrg() throws ServletException, IOException {
		
		Properties config = new Properties();
		config.setProperty("multi.tenant.signup", "enabled");
		
		SignupServlet servlet = new SignupServlet(emProvider, config);
		
		HttpServletRequest req = createMock(HttpServletRequest.class);
		expect(req.getParameter("userEmail")).andReturn("joe@black.com").anyTimes();
		expect(req.getParameter("userName")).andReturn("joe@black.com").anyTimes();
		expect(req.getParameter("userPass")).andReturn("jimbob").anyTimes();
		expect(req.getParameter("organizationName")).andReturn("My NGO").anyTimes();
		replay(req);
		
		HttpServletResponse resp = createMock(HttpServletResponse.class);
		replay(resp);
		
		emProvider.get().getTransaction().begin();
		servlet.doPost(req, resp);
		emProvider.get().getTransaction().commit();
		
		// verify that everything was committed
		EntityManager em = emf.createEntityManager();
		Organization org = (Organization) em.createQuery("Select o from Organization o where o.name=:name")
			.setParameter("name", "My NGO")
			.getSingleResult();
		
		assertThat( org.getOrgUnit().size(), equalTo(1) );
				
	}

}
