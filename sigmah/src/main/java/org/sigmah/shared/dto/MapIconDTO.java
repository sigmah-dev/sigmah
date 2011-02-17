/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * One-to-One DTO for the {@link org.sigmah.shared.report.model.MapIcon} report class
 *
 * @author Alex Bertram
 */
public class MapIconDTO extends BaseModelData {


    private static final long serialVersionUID = -98374293830234259L;

    public MapIconDTO() {
    }

    public MapIconDTO(String id) {
        setId(id);
    }

    public void setId(String name) {
        set("id", name);
    }

    public String getId() {
        return get("id");
    }

}
