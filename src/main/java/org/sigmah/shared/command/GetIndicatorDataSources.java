/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.command;

import org.sigmah.shared.command.result.IndicatorDataSourceList;

/**
 * Retrieves the indicator DataSources for the given indicator
 */
public class GetIndicatorDataSources implements Command<IndicatorDataSourceList> {
    private int indicatorId;

    public GetIndicatorDataSources() {
    }

    public GetIndicatorDataSources(int indicatorId) {
        this.indicatorId = indicatorId;
    }

    public int getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(int indicatorId) {
        this.indicatorId = indicatorId;
    }
}
