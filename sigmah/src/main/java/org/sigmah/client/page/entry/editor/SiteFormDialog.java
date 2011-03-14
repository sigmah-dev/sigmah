/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;

import com.extjs.gxt.ui.client.store.ListStore;
import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.page.common.dialog.FormDialogImpl;
import org.sigmah.client.page.common.toolbar.UIActions;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.dto.SiteDTO;

import java.util.Map;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class SiteFormDialog extends FormDialogImpl<SiteForm>  {

    private SiteFormPresenter presenter;

    public SiteFormDialog(SiteForm form) {
        super(form);

        int clientHeight = com.google.gwt.user.client.Window.getClientHeight();

        this.setHeight((int) (clientHeight * 0.95));
        this.setWidth(450);
    }   
    
}
