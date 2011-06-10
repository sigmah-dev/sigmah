/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.entry.editor;


import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.config.form.ModelFormPanel;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.BoundingBoxDTO;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.dto.SiteDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.maps.client.Maps;

/**
 * GXT Form for editing Site objects
 * 
 * @author alexander
 *
 */
public class SiteForm extends ModelFormPanel  {


    private ActivityFieldSet activityFieldSet;
    private LocationFieldSet locationFieldSet;
    private MapPresenter.View mapView;
    private AttributeFieldSet attributeFieldSet;
    private IndicatorFieldSet indicatorFieldSet;
    private CommentFieldSet commentFieldSet;

    private MapPresenter mapPresenter;
    private AdminFieldSetPresenter adminPresenter;
  
    private CountryDTO country;
    
    private SiteForm() {
    	
    	this.setBodyStyle("padding: 3px");
        this.setIcon(IconImageBundle.ICONS.editPage());
        this.setHeading(I18N.CONSTANTS.loading());
        
        setLayout(new FlowLayout());
        setScrollMode(Scroll.AUTOY);
    }
    
    /**
     * Constructs a generic SiteForm
     * @param dispatcher
     * @param levels
     */
    public SiteForm(Dispatcher dispatcher, CountryDTO country) {
    	this();
	  addLocationFieldSet(dispatcher, country, I18N.CONSTANTS.location());
      addGeoFieldSet(country);
      
      registerAll();
    }
    
    /**
     * Constructs a SiteForm for a specific activity
     * 
     * @param activity
     */
    public SiteForm(Dispatcher dispatcher, ActivityDTO activity) {
    	this();
    	
        setHeading(activity.getName());

       
        // ACTIVITY fieldset

        activityFieldSet = new ActivityFieldSet(activity, createPartnerStore(activity));
        add(activityFieldSet);

        addLocationFieldSet(dispatcher, activity.getDatabase().getCountry(), 
        			activity.getLocationType().getName());
        addGeoFieldSet(activity.getDatabase().getCountry());

        // ATTRIBUTE fieldset

        if (activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {

            attributeFieldSet = new AttributeFieldSet(activity);
            registerFieldSet(attributeFieldSet);
            add(attributeFieldSet);

            // INDICATOR fieldset

            indicatorFieldSet = new IndicatorFieldSet(activity);
            registerFieldSet(indicatorFieldSet);
            add(indicatorFieldSet);

            // COMMENT

            commentFieldSet = new CommentFieldSet();
            add(commentFieldSet);
        }

        registerAll();

        layout();
    }

	private void addGeoFieldSet(CountryDTO country) {
        MapFieldSet mapFieldSet = new MapFieldSet(country);
        this.mapView = mapFieldSet;
     
        mapPresenter = new MapPresenter(mapView);
        add((FieldSet) mapView);
        
        mapPresenter.setBounds(country.getName(), country.getBounds());

        registerField(((MapFieldSet) mapView).getLngField());
        registerField(((MapFieldSet) mapView).getLatField());
    
	}

	private void addLocationFieldSet(Dispatcher dispatcher, CountryDTO country, String locationLabel) {
		locationFieldSet = new LocationFieldSet(country.getAdminLevels(), locationLabel);
        add(locationFieldSet);

        adminPresenter = new AdminFieldSetPresenter(dispatcher, country, locationFieldSet);
        adminPresenter.setListener(new AdminFieldSetPresenter.Listener() {
            @Override
            public void onBoundsChanged(String name, BoundingBoxDTO bounds) {
                mapPresenter.setBounds(name, bounds);
            }

            @Override
            public void onModified() {
            }
        });
	}
	


    public void setSite(SiteDTO site) {
        updateForm(site);
    }

    private ListStore<PartnerDTO> createPartnerStore(ActivityDTO activity) {
        ListStore<PartnerDTO> store = new ListStore<PartnerDTO>();

        if (activity.getDatabase().isEditAllAllowed()) {

            for (PartnerDTO partner : activity.getDatabase().getPartners()) {
                if (partner.isOperational()) {
                    store.add(partner);
                }
            }
            store.sort("name", Style.SortDir.ASC);

        } else {

            store.add(activity.getDatabase().getMyPartner());

        }
        return store;
    }
    

    public Map<String, Object> getPropertyMap() {
        return super.getAllValues();
    }
    
    
}
