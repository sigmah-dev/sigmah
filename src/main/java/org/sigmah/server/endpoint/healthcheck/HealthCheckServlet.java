package org.sigmah.server.endpoint.healthcheck;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * Simple servlet called by the load balancer 
 * to verify that everything is correctly operating.
 * 
 * @author alexander
 *
 */
@Singleton
public class HealthCheckServlet extends HttpServlet {

	private final Provider<EntityManager> entityManager;	
		
	@Inject	
	public HealthCheckServlet(Provider<EntityManager> entityManager) {
		super();
		this.entityManager = entityManager;	
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		try {
			entityManager.get().createQuery("select count(db) from UserDatabase db").getSingleResult();
			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().print("OK");
		} catch(Exception caught) {
			resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection problem");
		}
	}
}
