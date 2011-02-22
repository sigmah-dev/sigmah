/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.client.ui;

import org.sigmah.client.icon.IconImageBundle;

import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;

/**
 *
 * @author nrebiai
 */
public class ClickableLabel extends AdapterField{
	
	private Grid grid;		
	private Image deleteIcon;
	
	public ClickableLabel(){
		super(null);
		
		Grid grid = new Grid(1,2);
		deleteIcon = IconImageBundle.ICONS.deleteIcon().createImage();
		
		grid.setWidget(0, 0, deleteIcon);
		grid.setCellSpacing(0);
		grid.getCellFormatter().setWidth(0, 0, "5px");
		grid.getCellFormatter().addStyleName(0, 0, "delete-cell");
		grid.getCellFormatter().addStyleName(0, 1, "delete-cell");
		
		this.grid = grid;
		this.widget = grid;
		setLabelSeparator(" ");
		
	}
	
	
	public void setText(String s){
		grid.setText(0, 1, s);
	}
	
	public Grid getGrid(){
		return grid;
	}

	public void addClickHandler(ClickHandler handler){
		deleteIcon.addClickHandler(handler);
	}
		
}

