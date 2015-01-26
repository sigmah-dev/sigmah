package org.sigmah.client.page.admin;

import org.sigmah.client.page.admin.management.AdminBackupManagementPresenter;
import org.sigmah.client.page.admin.management.AdminBackupManagementView;
import org.sigmah.client.page.admin.management.AdminCoreManagementPresenter;
import org.sigmah.client.page.admin.management.AdminCoreManagementView;
import org.sigmah.client.page.admin.management.AdminExportManagementPresenter;
import org.sigmah.client.page.admin.management.AdminExportManagementView;
import org.sigmah.client.page.admin.management.AdminManagementPresenter;
import org.sigmah.client.page.admin.management.AdminManagementView;
import org.sigmah.client.page.admin.model.common.AdminOneModelPresenter;
import org.sigmah.client.page.admin.model.common.AdminOneModelView;

import com.google.gwt.inject.client.AbstractGinModule;

public class AdminModule extends AbstractGinModule {

	@Override
	protected void configure() {
		bind(AdminPresenter.View.class).to(AdminView.class);
		bind(AdminOneModelPresenter.View.class).to(AdminOneModelView.class);
		bind(AdminManagementPresenter.View.class).to(AdminManagementView.class);
		bind(AdminCoreManagementPresenter.View.class).to(AdminCoreManagementView.class);
		bind(AdminBackupManagementPresenter.View.class).to(AdminBackupManagementView.class);
		bind(AdminExportManagementPresenter.View.class).to(AdminExportManagementView.class);
	}

}
