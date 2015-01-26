package org.sigmah.shared.dto.pivot.content;

import com.extjs.gxt.ui.client.widget.layout.TableData;
import java.util.List;
import org.sigmah.shared.command.result.Content;

/**
 * @author Alex Bertram (akbertram@gmail.com) 
 */
public class TableContent implements Content {

    private List<FilterDescription> filterDescriptions;
    private TableData data;

    public TableContent() {
    }

    public List<FilterDescription> getFilterDescriptions() {
        return filterDescriptions;
    }

    public void setFilterDescriptions(List<FilterDescription> filterDescriptions) {
        this.filterDescriptions = filterDescriptions;
    }

    public TableData getData() {
        return data;
    }

    public void setData(TableData data) {
        this.data = data;
    }

}
