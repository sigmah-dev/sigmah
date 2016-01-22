package org.sigmah.client.ui.presenter.project.logframe.menu;

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

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Defines a menu action.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public abstract class MenuAction {

	/**
	 * Defines the different types of policies that the menu can follow to inactive actions.
	 * 
	 * @author tmi
	 */
	public static enum InactivationPolicy {

		/**
		 * The inactive action will be disabled.
		 */
		DISABLE_POLICY,

		/**
		 * The inactive action will be hidden.
		 */
		HIDE_POLICY;

	}

	/**
	 * The menu item symbolizing this action.
	 */
	private final MenuItem item;

	/**
	 * The inactivation policy.
	 */
	private InactivationPolicy inactivationPolicy;

	/**
	 * Builds this action.
	 */
	public MenuAction() {

		item = new MenuItem(getText(), getIcon());

		// Action.
		item.addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				perform();
			}
		});

		// Defines the default policy.
		inactivationPolicy = InactivationPolicy.DISABLE_POLICY;
	}

	/**
	 * Sets the inactivation policy.
	 * 
	 * @param inactivationPolicy
	 *          The new inactivation policy.
	 */
	public void setInactivationPolicy(InactivationPolicy inactivationPolicy) {

		if (inactivationPolicy == null) {
			return;
		}

		this.inactivationPolicy = inactivationPolicy;
	}

	/**
	 * Inactive this action considering the inactivation policy.
	 * 
	 * @param inactivationMessage
	 *          The inactivation message used as a tool tip to inform the user.
	 */
	public void inactive(String inactivationMessage) {

		switch (inactivationPolicy) {
			case DISABLE_POLICY:
				item.setEnabled(false);
				break;
			case HIDE_POLICY:
				item.setVisible(false);
				break;
		}

		item.setTitle(inactivationMessage);
	}

	/**
	 * Active this action.
	 */
	public void active() {

		switch (inactivationPolicy) {
			case DISABLE_POLICY:
				item.setEnabled(true);
				break;
			case HIDE_POLICY:
				item.setVisible(true);
				break;
		}

		item.setTitle(null);
	}

	/**
	 * Returns the menu item which symbolizes this menu action.
	 * 
	 * @return The menu item of this action.
	 */
	public MenuItem getMenuItem() {
		return item;
	}

	/**
	 * Gets the action's label.
	 * 
	 * @return The action's label.
	 */
	public abstract String getText();

	/**
	 * Gets the action's icon.
	 * 
	 * @return The action's icon.
	 */
	public abstract AbstractImagePrototype getIcon();

	/**
	 * Returns if this action can be now performed. A <code>null</code> or empty string means that the action can be
	 * perform. A non-empty string means that the action cannot be performed for the moment.<br/>
	 * The returned string will be used as the cause of this unavailability.
	 * 
	 * @return The unavailability message, or <code>null</code>.
	 */
	public abstract String canBePerformed();

	/**
	 * Performs this action.
	 */
	public abstract void perform();
}
