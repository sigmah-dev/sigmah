package org.sigmah.client.ui.view.project.indicator;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sigmah.client.i18n.I18N;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.SchemaDTO;
import org.sigmah.shared.dto.SiteDTO;
import org.sigmah.shared.dto.UserDatabaseDTO;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import org.sigmah.client.ui.presenter.project.indicator.IndicatorNumberFormats;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.shared.dto.referential.DimensionType;
import org.sigmah.shared.util.Filter;


/**
 * Helper class that builds the ColumnModel for the SiteGrid, based 
 * on the provided filter.
 * <p/>
 * Migration Note: this file has been migrated (almost) as is. It does not follow
 * the 2.0 convention because of its "temporary" nature (as requested by Olivier Sarrat).
 * 
 * @author Alexander Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class SiteColumnModelBuilder {

    private final AsyncCallback<ColumnModel> callback;
	private final SchemaDTO schema;
    private final Filter filter;
    private final Integer mainSiteId;
    private final List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
    private final Collection<IndicatorDTO> indicators;
	
	public SiteColumnModelBuilder(SchemaDTO schema, Filter filter, Integer mainSiteId, Collection<IndicatorDTO> indicators, AsyncCallback<ColumnModel> callback) {
		this.callback = callback;
		this.filter = filter;
		this.indicators = indicators;
		this.schema = schema;
		this.mainSiteId = mainSiteId;
    }

	public void buildColumnModel() {
		if(!filterIncludesSingleDatabase()) {
        	columns.add(createDatabaseColumn());
        }
        
        columns.add(createMappedColumn());
        columns.add(createMainSiteColumn());
        
        if(filterIncludesSingleActivity()) {
        	addActivitySpecificColumns();
        	
        } else if(filterIncludesSingleDatabase()) {
            columns.add(createLocationColumn());
            columns.add(createLocation2Column());
            addIndicatorColumns(indicators);
        	addGeographicColumnsForDatabaseId(filteredDatabaseId());
        
        } else {
        	columns.add(createLocationColumn());
        	columns.add(createLocation2Column());
        	callback.onSuccess(new ColumnModel(columns));
        }
	}

	private void addActivitySpecificColumns() {
		addActivitySpecificColumns(schema.getActivityById(filteredActivityId()));
	}
	
	private void addActivitySpecificColumns(ActivityDTO activity) {


        if(activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {
            columns.add(createDateColumn());		
        }

        if(activity.getDatabase().isViewAllAllowed()) {
            columns.add(new ColumnConfig("partner", I18N.CONSTANTS.partner(), 100));
        }				
        
        addIndicatorColumns(activity);
        addGeographicColumns(activity.getDatabase());
	}
	
	private void addIndicatorColumns(Collection<IndicatorDTO> indicators) {
		for(IndicatorDTO indicator : indicators) {
			columns.add(createIndicatorColumn(indicator, indicator.getCode()));
		}
	}
	
    private void addIndicatorColumns(ActivityDTO activity) {
        /*
        * Add columns for all indicators that have a queries heading
        */

        for (IndicatorDTO indicator : activity.getIndicators()) {
            if(indicator.getCode() != null && !indicator.getCode().isEmpty()) {

                columns.add(createIndicatorColumn(indicator, indicator.getCode()));
            }
        }
    }
    
    private void addGeographicColumnsForDatabaseId(final int databaseId) {
		addGeographicColumns(schema.getDatabaseById(databaseId));
    }

	private void addGeographicColumns(UserDatabaseDTO database) {
   
    	if(database!=null){	
           for (AdminLevelDTO level : database.getCountry().getAdminLevels()) {
               ColumnConfig adminColumn = new ColumnConfig(level.getPropertyName() + ".name", level.getName(), 75);
               columns.add(adminColumn);
           }
    	}
       
       callback.onSuccess(new ColumnModel(columns));
	}
	

	private ColumnConfig createDateColumn() {
		DateField dateField = new DateField();
		dateField.getPropertyEditor().setFormat(DateTimeFormat.getFormat("MM/dd/y"));

		ColumnConfig dateColumn = new ColumnConfig("date2", I18N.CONSTANTS.date(), 100);
		dateColumn.setDateTimeFormat(DateTimeFormat.getFormat("yyyy-MMM-dd"));
		dateColumn.setEditor(new CellEditor(dateField));
		return dateColumn;
	}
	
	private boolean filterIncludesSingleActivity() {
		return filter.getRestrictions(DimensionType.Activity).size() == 1;
	}

	private boolean filterIncludesSingleDatabase() {
		return filter.getRestrictions(DimensionType.Database).size() == 1 ||
			   filter.getRestrictions(DimensionType.Activity).size() == 1 ||
			   filter.getRestrictions(DimensionType.Indicator).size() == 1;
	}

	private Integer filteredActivityId() {
		return filter.getRestrictions(DimensionType.Activity).iterator().next();
	}
	
	private Integer filteredDatabaseId() {
		return filter.getRestrictions(DimensionType.Database).iterator().next();
	}
	
	private ColumnConfig createMappedColumn() {
		ColumnConfig mapColumn = new ColumnConfig("x", "", 25);
        mapColumn.setRenderer(new GridCellRenderer<SiteDTO>() {
            @Override
            public Object render(SiteDTO model, String property, ColumnData config, int rowIndex, int colIndex, ListStore listStore, Grid grid) {
                if(model.hasCoords()) {
                    return "<div class='mapped'>&nbsp;&nbsp;</div>";
                } else {
                    return "<div class='unmapped'>&nbsp;&nbsp;</div>";
                }
            }
        });
		return mapColumn;
	}

	private ColumnConfig createDatabaseColumn() {
		return new ColumnConfig("database", I18N.CONSTANTS.database(), 50);
	}
	
	private ColumnConfig createMainSiteColumn() {
       
        final ColumnConfig mainSiteColumn = new ColumnConfig("siteType", "", 25);
        mainSiteColumn.setRenderer(new GridCellRenderer<SiteDTO>() {

            @Override
            public Object render(SiteDTO model, String property, ColumnData config,
            		int rowIndex, int colIndex, ListStore listStore, Grid grid) {
            	
            	// Main site icon
                final Image icon;
                                             
                if (mainSiteId != null && mainSiteId.equals(model.getId())) {
                	//star
                    icon = IconImageBundle.ICONS.mainSite().createImage();
                    icon.setTitle("Main Site");
                    
                    icon.addStyleName("project-starred-icon");
                } else {
                	// Display no icon for simple sites
                	icon = null;
                }
                
                return icon;
            }
        });
        
        return mainSiteColumn;
	}

    protected ColumnConfig createIndicatorColumn(IndicatorDTO indicator, String header) {

    	final NumberFormat format = IndicatorNumberFormats.getNumberFormat(indicator);
    	
        NumberField indicatorField = new NumberField();
       

        ColumnConfig indicatorColumn = new ColumnConfig(indicator.getPropertyName(),
                header, 50);

        indicatorColumn.setNumberFormat(format);
        indicatorColumn.setEditor(new CellEditor(indicatorField));
        indicatorColumn.setAlignment(Style.HorizontalAlignment.RIGHT);

        // For SUM indicators, don't show ZEROs in the Grid
        // (it looks better if we don't)
        if(indicator.getAggregation() == IndicatorDTO.AGGREGATE_SUM) {
            indicatorColumn.setRenderer(new GridCellRenderer() {
                @Override
                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore listStore, Grid grid) {
                    Double value = model.get(property);
                    if(value != null && value != 0) {
                        return format.format(value);
                    } else {
                        return "";
                    }
                }
            });
        }

        return indicatorColumn;
    }

	private ColumnConfig createLocation2Column() {
		TextField<String> locationAxeField = new TextField<String>();

		ColumnConfig axeColumn = new ColumnConfig("locationAxe", I18N.CONSTANTS.axeColumnTitle(), 75);
		axeColumn.setEditor(new CellEditor(locationAxeField));
		return axeColumn;
	}

	private ColumnConfig createLocationColumn() {
		TextField<String> locationField = new TextField<String>();
		locationField.setAllowBlank(false);

		ColumnConfig locationColumn = new ColumnConfig("locationName", I18N.CONSTANTS.location(), 100);
		locationColumn.setEditor(new CellEditor(locationField));
		return locationColumn;
	}

	
}
