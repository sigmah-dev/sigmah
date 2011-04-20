/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.orgunit.reports;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.orgunit.OrgUnitPresenter;
import org.sigmah.client.page.project.SubPresenter;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.GetProjectReport;
import org.sigmah.shared.command.GetProjectReports;
import org.sigmah.shared.command.result.ProjectReportListResult;
import org.sigmah.shared.dto.OrgUnitDTO;
import org.sigmah.shared.dto.OrgUnitDTO.LocalizedElement;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ReportReference;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Sub presenter that manage the "reports" view from the organizational unit
 * page.
 * 
 * @author Kristela Macaj(kmacaj@ideia.fr)
 */
public class OrgUnitReportsPresenter implements SubPresenter {
    private Dispatcher dispatcher;
    private EventBus eventBus;
    private Authentication authentication;

    private OrgUnitPresenter orgUnitPresenter;
    private OrgUnitDTO currentOrgUnitDTO;

    private OrgUnitReportsView view;
    private ListStore<ReportReference> reportStore;

    int currentReportId = -1;

    public OrgUnitReportsPresenter(Authentication authentication, Dispatcher dispatcher, EventBus eventBus,
            OrgUnitPresenter orgUnitPresenter) {
        this.authentication = authentication;
        this.dispatcher = dispatcher;
        this.eventBus = eventBus;
        this.orgUnitPresenter = orgUnitPresenter;
    }

    @Override
    public Component getView() {
        // Creates the view
        if (view == null) {
            reportStore = new ListStore<ReportReference>();
            reportStore.setMonitorChanges(true);
            view = new OrgUnitReportsView(authentication, eventBus, dispatcher, reportStore);
        }

        // Calculating the report id
        int reportId = currentReportId;
        final String arg = orgUnitPresenter.getCurrentState().getArgument();
        if (arg != null)
            reportId = Integer.parseInt(arg);

        if (!orgUnitPresenter.getCurrentOrgUnitDTO().equals(currentOrgUnitDTO)) {
            // If the current project has changed, clear the view
            currentOrgUnitDTO = orgUnitPresenter.getCurrentOrgUnitDTO();
            reportStore.removeAll();

            if (arg == null)
                reportId = -1;
        }

        // If the report id has changed
        if (currentReportId != reportId) {
            currentReportId = reportId;

            if(reportId != -1) {
                // Configuring the view to display the given report
                Log.debug("Loading report #" + reportId);
                final GetProjectReport getProjectReport = new GetProjectReport(reportId);
                dispatcher.execute(getProjectReport, null, new AsyncCallback<ProjectReportDTO>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }

                    @Override
                    public void onSuccess(ProjectReportDTO result) {
                        view.setReport(result);
                    }
                });

            } else {
                view.setReport(null);
            }
        }

        return view;
    }

    @Override
    public void discardView() {
        this.view = null;
    }
    
    @Override
    public void viewDidAppear() {
        // Updating the current state
        view.setCurrentState(orgUnitPresenter.getCurrentState());
        //organizational unit dont have phase element
        view.setPhaseName(null);

        // Reset the attach documents menu.
        AttachMenuBuilder.createMenu(currentOrgUnitDTO, FilesListElementDTO.class,
                view.getAttachButton(), reportStore, authentication, dispatcher, eventBus);

        // TODO: Do something to add the report list elements too
        final List<LocalizedElement> reportElements = currentOrgUnitDTO.getLocalizedElements(ReportElementDTO.class);
        reportElements.addAll(currentOrgUnitDTO.getLocalizedElements(ReportListElementDTO.class));

        AttachMenuBuilder.createMenu(currentOrgUnitDTO, reportElements,
                view.getCreateReportButton(), reportStore, authentication, dispatcher, eventBus);

        
        // Updates the report & document list
        
        // Retrieves reports.
        GetProjectReports getProjectReports = new GetProjectReports(null, currentOrgUnitDTO.getId());
        dispatcher.execute(getProjectReports, null, new AsyncCallback<ProjectReportListResult>() {
            @Override
            public void onSuccess(ProjectReportListResult result) {
                if (reportStore.getCount() > 0) {
                    reportStore.removeAll();
                }

                reportStore.add(result.getData());
            }

            @Override
            public void onFailure(Throwable caught) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        // Retrieves all the files lists elements in the current project.
        final List<GetProjectDocuments.FilesListElement> filesLists = new ArrayList<GetProjectDocuments.FilesListElement>();
        final List<LocalizedElement> filesLists2 = currentOrgUnitDTO.getLocalizedElements(FilesListElementDTO.class);
        for (LocalizedElement e : filesLists2) {
            filesLists.add(new GetProjectDocuments.FilesListElement((long) e.getElement().getId(), I18N.CONSTANTS.projectDetails(), e
                    .getElement().getLabel()));
        }

        // Retrieves documents.
        dispatcher.execute(new GetProjectDocuments(currentOrgUnitDTO.getId(), filesLists), null,
                new AsyncCallback<ProjectReportListResult>() {
                    @Override
                    public void onSuccess(ProjectReportListResult result) {
                        reportStore.add(result.getData());
                        reportStore.sort("name", SortDir.ASC);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                });
    }
}
