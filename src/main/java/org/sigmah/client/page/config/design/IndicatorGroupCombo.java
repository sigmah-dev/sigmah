/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.config.design;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.command.GetIndicators;
import org.sigmah.shared.command.result.IndicatorListResult;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndicatorGroupCombo extends ComboBox<IndicatorGroup> {

    private final Dispatcher dispatcher;
    
    public IndicatorGroupCombo(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        setMaxLength(50);
        setDisplayField("name");
        setTriggerAction(ComboBox.TriggerAction.ALL);
        setUseQueryCache(false);
        setForceSelection(false);
        setEditable(false);
        setStore(new ListStore<IndicatorGroup>());
    }

    public void clear() {
        store.removeAll();
    }
    
    public void loadGroups(final IndicatorDTO indicator) {
        clear();
        setEmptyText(I18N.CONSTANTS.loading());
        dispatcher.execute(GetIndicators.forDatabase(indicator.getDatabaseId()), null, new AsyncCallback<IndicatorListResult>() {
            @Override
            public void onFailure(Throwable caught) {
            }

            @Override
            public void onSuccess(IndicatorListResult result) {
            	store.add(result.getGroups());
            	if(indicator.getGroupId() != null) {
	        		for(IndicatorGroup group : result.getGroups()) {
	        			if(group.getId() == indicator.getGroupId()) {
	        				setValue(group);
	        				return;
	        			}
	        		}
            	}
            	setEmptyText("");
            }
        });
    }
    
    public Binding newBinding(String property) {
    	return new Binding(property);
    }
    

    public class Binding extends FieldBinding {
        private Binding(String property) {
            super(IndicatorGroupCombo.this, property);
        }

        @Override
        protected Object onConvertFieldValue(Object value) {
            if(field.getValue() == null) {
                return null;
            }
            IndicatorGroup model = (IndicatorGroup)field.getValue();
            return model.getId();
        }

        @Override
        protected Object onConvertModelValue(Object value) {
        	// handled above
        	return null;
        }
    }
}
