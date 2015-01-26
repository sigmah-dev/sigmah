/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;

import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.AdminLevelDTO;

import com.extjs.gxt.ui.client.widget.form.TextField;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 */
public class LocationFieldSet extends AdminFieldSet {


    public LocationFieldSet(List<AdminLevelDTO> adminLevels, String locationLabel) {
        super(adminLevels);

        TextField<String> nameField = new TextField<String>();
        nameField.setName("locationName");
        nameField.setFieldLabel(locationLabel);
        nameField.setAllowBlank(false);
        add(nameField);

        TextField<String> axeField = new TextField<String>();
        axeField.setName("locationAxe");
        axeField.setFieldLabel(I18N.CONSTANTS.axe());
        add(axeField);
    }
}
