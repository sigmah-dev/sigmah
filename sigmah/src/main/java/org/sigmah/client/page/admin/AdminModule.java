package org.sigmah.client.page.admin;

import org.sigmah.client.page.admin.model.common.AdminOneModelPresenter;
import org.sigmah.client.page.admin.model.common.AdminOneModelView;

import com.google.gwt.inject.client.AbstractGinModule;

public class AdminModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(AdminPresenter.View.class).to(AdminView.class);
		bind(AdminOneModelPresenter.View.class).to(AdminOneModelView.class);
	}

}
