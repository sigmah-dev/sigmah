package org.sigmah.client.ui.presenter.admin.models.importer;

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.admin.models.importer.ImportModelView;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.shared.servlet.ServletConstants.Servlet;
import org.sigmah.shared.servlet.ServletConstants.ServletMethod;
import org.sigmah.shared.servlet.ServletUrlBuilder;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)v2.0
 */
@Singleton
public class ImportModelPresenter extends AbstractPagePresenter<ImportModelPresenter.View> {

	private ServletMethod method;
	private String message;
	private String type;

	@ImplementedBy(ImportModelView.class)
	public static interface View extends ViewPopupInterface {

		Button getImportButton();

		Button getCancelButton();

		FormPanel getForm();

		void setProjectPerspective();

		FileUploadField getUploadField();

		void removeProjectPerspective();

	}

	@Inject
	protected ImportModelPresenter(View view, Injector injector) {
		super(view, injector);
	}

	@Override
	public void onBind() {

		// import
		importButtonListener();

		// cancel
		cancelButtonListener();

	}

	@Override
	public Page getPage() {
		return Page.IMPORT_MODEL;
	}

	@Override
	public void onPageRequest(PageRequest request) {

		// TODO ?? view.getUploadField().clear();

		type = request.getParameter(RequestParameter.TYPE);

		if (AdminUtil.ADMIN_CATEGORY_MODEL.equals(type)) {

			method = ServletMethod.IMPORT_MODEL_CATEGORY;

			setPageTitle(I18N.CONSTANTS.adminCategoryImport());

			message = I18N.CONSTANTS.adminCategoryTypeStandard();

			view.removeProjectPerspective();

		} else if (AdminUtil.ADMIN_REPORT_MODEL.equals(type)) {

			method = ServletMethod.IMPORT_MODEL_REPORT;

			setPageTitle(I18N.CONSTANTS.adminReportModelImport());

			message = I18N.CONSTANTS.adminReportModelStandard();

			view.removeProjectPerspective();

		} else if (AdminUtil.ADMIN_PROJECT_MODEL.equals(type)) {

			method = ServletMethod.IMPORT_MODEL_PROJECT;

			setPageTitle(I18N.CONSTANTS.adminProjectModelImport());

			message = I18N.CONSTANTS.adminProjectModelStandard();

			view.setProjectPerspective();

		} else if (AdminUtil.ADMIN_ORG_UNIT_MODEL.equals(type)) {

			method = ServletMethod.IMPORT_MODEL_ORGUNIT;

			setPageTitle(I18N.CONSTANTS.adminOrgUnitsModelImport());

			message = I18N.CONSTANTS.adminOrgUnitsModelStandard();

			view.removeProjectPerspective();

		}

	}

	private void importButtonListener() {

		view.getImportButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				onImportRun();
			}

		});

		view.getForm().addListener(Events.Submit, new Listener<FormEvent>() {

			@Override
			public void handleEvent(FormEvent be) {
				final String result = be.getResultHtml();

				view.hide();
				// Import failed.
				if (result.equals("#500")) {

					if (AdminUtil.ADMIN_CATEGORY_MODEL.equals(type)) {

						N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminCategoryImportError());

					} else if (AdminUtil.ADMIN_REPORT_MODEL.equals(type)) {

						N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminReportModelImport());

					} else if (AdminUtil.ADMIN_PROJECT_MODEL.equals(type)) {

						N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminProjectModelImportError());

					} else if (AdminUtil.ADMIN_ORG_UNIT_MODEL.equals(type)) {

						N10N.warn(I18N.CONSTANTS.error(), I18N.CONSTANTS.adminOrgUnitsModelImportError());

					}

				} else {

					if (AdminUtil.ADMIN_CATEGORY_MODEL.equals(type)) {

						N10N.infoNotif(I18N.CONSTANTS.adminCategoryImport(), I18N.CONSTANTS.adminCategoryImportDetailt());

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.CATEGORY_MODEL_IMPORT));

					} else if (AdminUtil.ADMIN_REPORT_MODEL.equals(type)) {

						N10N.infoNotif(I18N.CONSTANTS.adminReportModelImport(), I18N.CONSTANTS.adminReportModelImportDetail());

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.REPORT_MODEL_IMPORT));

					} else if (AdminUtil.ADMIN_PROJECT_MODEL.equals(type)) {

						N10N.infoNotif(I18N.CONSTANTS.adminProjectModelImport(), I18N.CONSTANTS.adminProjectModelImportDetail());

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.PROJECT_MODEL_IMPORT));

					} else if (AdminUtil.ADMIN_ORG_UNIT_MODEL.equals(type)) {

						N10N.infoNotif(I18N.CONSTANTS.adminOrgUnitsModelImport(), I18N.CONSTANTS.adminOrgUnitsModelImportDetail());

						eventBus.fireEvent(new UpdateEvent(UpdateEvent.ORG_UNIT_MODEL_IMPORT));
					}

				}

			}
		});

	}

	private void cancelButtonListener() {

		view.getCancelButton().addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				view.hide();
			}
		});
	}

	private void onImportRun() {

		if (view.getForm().isValid()) {

			ServletUrlBuilder url = new ServletUrlBuilder(injector.getAuthenticationProvider(), injector.getPageManager(), Servlet.IMPORT, method);
			view.getForm().setAction(url.toString());
			view.getForm().submit();

		} else {

			N10N.warn(I18N.CONSTANTS.createFormIncomplete(), I18N.MESSAGES.importFormIncompleteDetails(message));

		}

	};
}
