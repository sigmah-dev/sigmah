/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;

import java.util.Map;

import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.event.SiteEvent;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.common.dialog.FormDialogCallback;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.widget.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteFormDialog extends FormDialogImpl<SiteForm>  {

    private final EventBus eventBus;
    private final Dispatcher dispatcher;
    
    @Inject
    public SiteFormDialog(EventBus eventBus, Dispatcher dispatcher, SiteForm form) {
        super(form);
        this.eventBus = eventBus;
        this.dispatcher = dispatcher;
        
        int clientHeight = com.google.gwt.user.client.Window.getClientHeight();

        this.setHeight((int) (clientHeight * 0.95));
        this.setWidth(450);
    }

    /**
     * Shows the dialog and handles OK  by creating a new site.
     * EntityEvent.CREATED is fired by the event bus upon successful completion.
     * 
     * @param site default values for the new site
     */
	public void create(final SiteDTO site) {
		form.setSite(site);
		show(new FormDialogCallback() {

			@Override
			public void onValidated() {
				final Map<String, Object> props = form.getPropertyMap();
				props.put("databaseId", site.getDatabaseId());
								 
				dispatcher.execute(new CreateEntity("Site", props), SiteFormDialog.this, new AsyncCallback<CreateResult>() {

					@Override
					public void onFailure(Throwable caught) {						
					}

					@Override
					public void onSuccess(CreateResult result) {
						hide();
						Info.display(I18N.CONSTANTS.saved(), I18N.CONSTANTS.saved());
						SiteDTO newSite = new SiteDTO(result.getNewId());
						newSite.setProperties(props);
						eventBus.fireEvent(new SiteEvent(SiteEvent.CREATED, this, newSite ));
					}
				});
			}
			
		});
	}   
    

    /**
     * Shows the dialog and handles OK  by updating the given site.
     * EntityEvent.UPDATED is fired by the event bus upon successful completion.
     * 
     * @param site default values for the new site
     */
	public void edit(final SiteDTO site) {
		form.setSite(site);
		show(new FormDialogCallback() {

			@Override
			public void onValidated() {
							 
				final Map<String, Object> modified = form.getModified();
				dispatcher.execute(new UpdateEntity(site, modified), SiteFormDialog.this, new AsyncCallback<VoidResult>() {

					@Override
					public void onFailure(Throwable caught) {						
					}

					@Override
					public void onSuccess(VoidResult result) {
						hide();
						Info.display(I18N.CONSTANTS.saved(), I18N.CONSTANTS.saved());
						site.setProperties(modified);
						eventBus.fireEvent(new SiteEvent(SiteEvent.UPDATED, this, site ));
					}
				});
			}
			
		});
	}   
}
