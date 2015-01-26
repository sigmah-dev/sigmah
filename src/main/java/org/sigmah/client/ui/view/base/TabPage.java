package org.sigmah.client.ui.view.base;

/**
 * V1.3
 * A Page/PageState that implements this interface can be displayed in a tab.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
/**
 * V2.0
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
public interface TabPage {

	/**
	 * Returns the title to display in the tab bar.
	 * 
	 * @return the title to display in the tab bar.
	 */
	String getTabTitle();
}
