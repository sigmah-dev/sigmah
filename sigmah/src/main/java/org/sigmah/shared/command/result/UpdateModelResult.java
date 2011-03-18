/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command.result;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * Result of commands which update a project model.
 *
 * @author nrebiai
 */
public class UpdateModelResult extends CreateResult {

	private static final long serialVersionUID = 1187744725232973038L;
	
	private BaseModelData annexEntity;

    protected UpdateModelResult() {

    }
    
    public UpdateModelResult(int newId) {
        super(newId);
    }

    public UpdateModelResult(BaseModelData entity) {
    	 super(entity);
    }
    
    public void setAnnexEntity(BaseModelData entity) {
        this.annexEntity = entity;
    }

    public BaseModelData getAnnexEntity() {
        return annexEntity;
    }
}
