package org.sigmah.shared.dto.profile;

import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity profile.PrivacyGroupPermission.
 * 
 * @author nrebiai
 * 
 */
public class PrivacyGroupPermDTO extends BaseModelData implements EntityDTO {

    private static final long serialVersionUID = -8951877538079370046L;

    @Override
    public String getEntityName() {
        return "profile.PrivacyGroupPermission";
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

    public String getPermission() {
        return get("permission");
    }
    
    public void setPermission(String permission) {
        set("permission", permission);
    }
}
