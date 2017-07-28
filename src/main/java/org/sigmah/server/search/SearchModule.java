package org.sigmah.server.search;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sigmah.server.dao.FileDAO;
import org.sigmah.server.dao.impl.FileHibernateDAO;
import org.sigmah.server.dao.util.SQLDialect;
import org.sigmah.server.dao.util.SQLDialectProvider;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.persist.jpa.JpaPersistModule;

public class SearchModule extends AbstractModule {

	@Override
	protected void configure() {
		// TODO Auto-generated method stub

		// Binds providers.
		bind(SQLDialect.class).toProvider(SQLDialectProvider.class).in(Singleton.class);

		// Installs the JPA module.
		install(new JpaPersistModule("sigmah-dev"));

		// JSR-303 : bean validation.
		final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		final Validator validator = factory.getValidator();
		bind(Validator.class).toInstance(validator);

		bind(FileDAO.class).to(FileHibernateDAO.class).in(Singleton.class);
	}

}
