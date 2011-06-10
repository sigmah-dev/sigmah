/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;

import java.util.HashSet;
import java.util.Set;

public class IndicatorGroupCombo extends ComboBox<ModelData> {

    private final Dispatcher dispatcher;

    public IndicatorGroupCombo(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        setMaxLength(50);
        setDisplayField("value");
        setTriggerAction(ComboBox.TriggerAction.ALL);
        setUseQueryCache(false);
        setForceSelection(false);
        setEditable(true);
        setStore(new ListStore<ModelData>());
    }

    public void addGroup(String group) {
        store.add(newModel(group));
    }

    private static ModelData newModel(String group) {
        ModelData model = new BaseModelData();
        model.set("value", group);
        return model;
    }

    public void clear() {
        store.removeAll();
    }

    public void loadGroups(int databaseId) {
        clear();
        dispatcher.execute(GetIndicators.forDatabase(databaseId), null, new AsyncCallback<IndicatorListResult>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(IndicatorListResult result) {
                Set<String> groups = new HashSet<String>();
                for(IndicatorDTO indicator : result.getData()) {
                    if(indicator.getCategory() != null && !groups.contains(indicator.getCategory())) {
                        addGroup(indicator.getCategory());
                        groups.add(indicator.getCategory());
                    }
                }
            }
        });
    }

    public static class Binding extends FieldBinding {
        public Binding(IndicatorGroupCombo field, String property) {
            super(field, property);
        }

        @Override
        protected Object onConvertFieldValue(Object value) {
            if(field.getValue() == null) {
                return null;
            }
            ModelData model = (ModelData)field.getValue();
            return model.get("value");
        }

        @Override
        protected Object onConvertModelValue(Object value) {
            return newModel((String) value);
        }
    }
}
