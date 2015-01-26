/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.ui;

import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;

/**
 * VBoxLayout that can be customized through css styles.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @see VBoxLayout
 */
public class StylableVBoxLayout extends VBoxLayout {
    private String customStyle;

    /**
     * Creates a new VBoxLayout and initializes its style with the default values
     * plus the custom style defined here.
     * @param customStyle Name of the style to add to this layout.
     * @see VBoxLayout#VBoxLayout()
     */
    public StylableVBoxLayout(String customStyle) {
        this.customStyle = customStyle;
    }

    /**
     * Calls super and applies the custom style to this layout.
     * @see Layout#initTarget()
     */
    @Override
    protected void initTarget() {
        super.initTarget();
        target.addStyleName(customStyle);
    }
}
