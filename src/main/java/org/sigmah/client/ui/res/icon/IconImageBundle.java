package org.sigmah.client.ui.res.icon;

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


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.ImageBundle;

/**
 * Provides access to the application's icons through GWT's magic ImageBundle generator.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@SuppressWarnings("deprecation")
public interface IconImageBundle extends ImageBundle {

	/**
	 * Bundle providing access to icons resources.
	 */
	IconImageBundle ICONS = (IconImageBundle) GWT.create(IconImageBundle.class);

	AbstractImagePrototype add();

	AbstractImagePrototype delete();

	AbstractImagePrototype editPage();

	AbstractImagePrototype save();

	AbstractImagePrototype database();

	AbstractImagePrototype design();

	AbstractImagePrototype addDatabase();

	AbstractImagePrototype editDatabase();

	AbstractImagePrototype excel();

	AbstractImagePrototype activity();

	AbstractImagePrototype addActivity();

	AbstractImagePrototype deleteActivity();

	@Resource(value = "editPage.png")
	AbstractImagePrototype editActivity();

	@Resource(value = "link_edit.png")
	AbstractImagePrototype editLinkedProject();

	AbstractImagePrototype user();

	AbstractImagePrototype editUser();

	AbstractImagePrototype addUser();

	AbstractImagePrototype deleteUser();

	/**
	 * @return Icon for a user group
	 */
	AbstractImagePrototype group();

	AbstractImagePrototype table();

	AbstractImagePrototype report();

	AbstractImagePrototype sum();

	AbstractImagePrototype curveChart();

	AbstractImagePrototype map();

	AbstractImagePrototype filter();

	@Resource(value = "key.png")
	AbstractImagePrototype login();

	AbstractImagePrototype cancel();

	AbstractImagePrototype barChart();

	@Resource(value = "barChart.png")
	AbstractImagePrototype analysis();

	@Resource(value = "keyboard.png")
	AbstractImagePrototype dataEntry();

	@Resource(value = "ruler.png")
	AbstractImagePrototype indicator();

	AbstractImagePrototype attributeGroup();

	AbstractImagePrototype attribute();

	AbstractImagePrototype refresh();

	@Resource(value = "wrench_orange.png")
	AbstractImagePrototype setup();

	AbstractImagePrototype mapped();

	AbstractImagePrototype unmapped();

	@Resource(value = "gs.png")
	AbstractImagePrototype graduatedSymbol();

	AbstractImagePrototype ppt();

	AbstractImagePrototype image();

	AbstractImagePrototype msword();

	AbstractImagePrototype pdf();

	AbstractImagePrototype pieChart();

	AbstractImagePrototype checked();

	AbstractImagePrototype unchecked();

	AbstractImagePrototype offline();

	AbstractImagePrototype onlineSynced();

	AbstractImagePrototype onlineSyncing();

	AbstractImagePrototype up();

	AbstractImagePrototype down();

	AbstractImagePrototype attach();
	
	@Resource(value = "link_delete.png")
	AbstractImagePrototype remove();

	@Resource(value = "bullet_green.png")
	AbstractImagePrototype elementCompleted();

	@Resource(value = "bullet_red.png")
	AbstractImagePrototype elementUncompleted();

	@Resource(value = "bullet_star_new.png")
	AbstractImagePrototype activate();

	@Resource(value = "bullet_star_black.png")
	AbstractImagePrototype close();

	@Resource(value = "cog.png")
	AbstractImagePrototype create();

	@Resource(value = "page_edit.png")
	AbstractImagePrototype rename();

	@Resource(value = "link.png")
	AbstractImagePrototype select();

	@Resource(value = "information.png")
	AbstractImagePrototype info();

	AbstractImagePrototype expand();

	AbstractImagePrototype collapse();

	@Resource(value = "filter-check.png")
	AbstractImagePrototype checkboxChecked();

	@Resource(value = "filter-uncheck.png")
	AbstractImagePrototype checkboxUnchecked();

	@Resource(value = "hourglass.png")
	AbstractImagePrototype history();

	@Resource(value = "points2.png")
	AbstractImagePrototype openedPoint();

	@Resource(value = "points3.png")
	AbstractImagePrototype overduePoint();

	@Resource(value = "points1.png")
	AbstractImagePrototype closedPoint();

	@Resource(value = "rappels2.png")
	AbstractImagePrototype openedReminder();

	@Resource(value = "rappels3.png")
	AbstractImagePrototype overdueReminder();

	@Resource(value = "rappels1.png")
	AbstractImagePrototype closedReminder();

	@Resource(value = "delete_icon.png")
	AbstractImagePrototype deleteIcon();

	@Resource(value = "control_fastforward.png")
	AbstractImagePrototype forward();

	@Resource(value = "control_rewind.png")
	AbstractImagePrototype back();

	AbstractImagePrototype ods();

	AbstractImagePrototype csv();

	@Resource(value = "loading.png")
	AbstractImagePrototype loading();

	AbstractImagePrototype connection();

	AbstractImagePrototype warning();
	
	AbstractImagePrototype warningSmall();

	@Resource(value = "star.png")
	AbstractImagePrototype mainSite();

	@Resource(value = "markerStar.png")
	AbstractImagePrototype markerStar();

	AbstractImagePrototype amendment();

	AbstractImagePrototype DNABrownGreen();

	AbstractImagePrototype DNABrownRed();

	AbstractImagePrototype JumpToLatestVersion();

	AbstractImagePrototype lock();

	AbstractImagePrototype unlock();

	AbstractImagePrototype validate();
	
	AbstractImagePrototype history16();
	
	AbstractImagePrototype disable();
	
	AbstractImagePrototype left();
	
	AbstractImagePrototype right();
	
	AbstractImagePrototype transfer();
	
	AbstractImagePrototype update();
	
}
