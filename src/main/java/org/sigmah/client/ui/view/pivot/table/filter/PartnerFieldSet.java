package org.sigmah.client.ui.view.pivot.table.filter;

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
