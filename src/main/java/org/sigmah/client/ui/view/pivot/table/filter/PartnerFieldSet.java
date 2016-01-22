package org.sigmah.client.ui.view.pivot.table.filter;

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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.dto.SchemaDTO;

/**
 * 
 * @author Alex Bertram (akbertram@gmail.com)
 * @author Raphaël Calabro (rcalabro@ideia.fr) v2.0
 */
public class PartnerFieldSet extends AbstractFilterFieldSet {
	
    protected ListView<PartnerDTO> checkList;

    public PartnerFieldSet(SchemaDTO schema) {

        setHeadingHtml(I18N.CONSTANTS.filterByPartner());

        setLayout(new FitLayout() {
			@Override
			protected void setItemSize(Component item, Size size) {
				// Leaves room for a FieldSet's header.
				size.height = size.height - 30;
				super.setItemSize(item, size);
			}
		});
        setScrollMode(Style.Scroll.AUTO);

        TreeStore<PartnerDTO> store = new TreeStore<PartnerDTO>();
        store.add(schema.getVisiblePartnersList(), false);

        TreePanel<PartnerDTO> tree = new TreePanel<PartnerDTO>(store);
        tree.setCheckable(true);
        tree.setIconProvider(new ModelIconProvider<PartnerDTO>() {
            public AbstractImagePrototype getIcon(PartnerDTO model) {
                return IconImageBundle.ICONS.group();
            }
        });

        add(tree);

        setHeight(250);

    }

}
