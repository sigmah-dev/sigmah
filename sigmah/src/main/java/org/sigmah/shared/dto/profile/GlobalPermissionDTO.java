package org.sigmah.shared.dto.profile;

import org.sigmah.shared.domain.profile.GlobalPermissionEnum;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity profile.Profile.
 * 
 * @author nrebiai
 * 
 */
public class GlobalPermissionDTO extends BaseModelData implements EntityDTO {

    private static final long serialVersionUID = 4319548689359747450L;

    @Override
    public String getEntityName() {
        return "profile.GlobalPermission";
    }

    // Id.
    @Override
    public int getId() {
        final Integer id = (Integer) get("id");
        return id != null ? id : -1;
    }

    public void setId(int id) {
        set("id", id);
    }

    // Global permissions.
    public GlobalPermissionEnum getGlobalPermission() {
        return get("globalPermission");
    }

    public void setGlobalPermissions(GlobalPermissionEnum globalPermission) {
        set("globalPermission", globalPermission);
    }
}
