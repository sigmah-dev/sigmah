/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 *  Data transfer object for datasources of Indicators. This is a projection
 *  of the {@link org.sigmah.shared.domain.Indicator} and {@link org.sigmah.shared.domain.UserDatabase}
 *  entities.
 */
public class IndicatorDataSourceDTO extends BaseModelData  {


    public int getIndicatorId() {
        return (Integer)get("indicatorId");
    }

    public void setIndicatorId(int id) {
        set("indicatorId", id);
    }

    public int getDatabaseId() {
        return (Integer)get("databaseId");
    }

    public void setDatabaseId(int id) {
        set("databaseId", id);
    }

    public String getDatabaseName() {
        return get("databaseName");
    }

    public void setDatabaseName(String name) {
        set("databaseName", name);
    }

    public String getIndicatorName() {
        return get("indicatorName");
    }

    public void setIndicatorName(String name) {
        set("indicatorName", name);
    }

    public String getIndicatorCode() {
        return get("indicatorCode");
    }

    public void setIndicatorCode(String code) {
        set("indicatorCode", code);
    }

}


