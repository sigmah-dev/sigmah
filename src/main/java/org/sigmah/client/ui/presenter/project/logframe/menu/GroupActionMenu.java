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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.project.logframe.grid.FlexTableView;
import org.sigmah.client.ui.view.project.logframe.grid.RowsGroup;

import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * Represents an actions menu for a group in the log frame grid.
 * 
 * @author tmi (v1.3)
 * @author HUZHE (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public abstract class GroupActionMenu extends ActionsMenu {

	/**
	 * The row.
	 */
	private final RowsGroup<?> group;

	/**
	 * Builds this menu.
	 * 
	 * @param view
	 *          The view where this menu is displayed.
	 * @param group
	 *          The group managed by this menu.
	 */
	public GroupActionMenu(FlexTableView view, RowsGroup<?> group) {
		super(view);

		this.group = group;

		// Rename action.
		final MenuItem renameMenuItem = createRenameAction();

		// Menu.
		menu.add(renameMenuItem);

		// Remove action.
		final MenuItem removeMenuItem = createRemoveAction();

		// Add remove menu
		menu.add(removeMenuItem);
	}

	/**
	 * Create a menu to remove a log group from a view
	 * 
	 * @return A MenuItem
	 */
	private MenuItem createRemoveAction() {

		final MenuAction action = new MenuAction() {

			@Override
			public String getText() {
				return I18N.CONSTANTS.logFrameActionRemove();
			}

			@Override
			public AbstractImagePrototype getIcon() {
				return IconImageBundle.ICONS.delete();
			}

			@Override
			public String canBePerformed() {

				final boolean canBeRemoved = canBeRemoved();

				if (canBeRemoved) {
					return null;
				} else {
					return I18N.CONSTANTS.logFrameActionDeleteUnavailable();
				}
			}

			@Override
			public void perform() {

				if (beforeRemove()) {
					// remove from the view in order to refresh
					view.removeGroup(group);
				}
			}

		};

		// Add the remove action to the action list
		actions.add(action);
		return action.getMenuItem();
	}

	/**
	 * Builds and returns the rename action.
	 * 
	 * @return The rename action.
	 */
	private MenuItem createRenameAction() {

		final MenuAction action = new MenuAction() {

			@Override
			public void perform() {

				// Tries to rename the element.
				beforeRename(new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						// nothing.
					}

					@Override
					public void onSuccess(String result) {
						view.refreshGroupWidget(group);
					}
				});
			}

			@Override
			public String getText() {
				return I18N.CONSTANTS.logFrameActionRename();
			}

			@Override
			public AbstractImagePrototype getIcon() {
				return IconImageBundle.ICONS.rename();
			}

			@Override
			public String canBePerformed() {

				final boolean canBeRenamed = canBeRemaned();

				if (canBeRenamed) {
					return null;
				} else {
					return I18N.CONSTANTS.logFrameActionRenameUnavailable();
				}
			}
		};

		action.setInactivationPolicy(inactivationPolicy);

		// Adds it locally.
		actions.add(action);

		return action.getMenuItem();
	}

	/**
	 * Returns if the element managed by this menu can be renamed.
	 * 
	 * @return If the element can be renamed.
	 */
	public abstract boolean canBeRemaned();

	/**
	 * Method called just before renaming the element managed by this menu. If this method returns <code>true</code>, the
	 * corresponding group will be renamed in the view. Otherwise, this method has no effect.
	 * 
	 * @param callback
	 *          Called after the group has been renamed.
	 */
	public abstract void beforeRename(AsyncCallback<String> callback);

	/**
	 * Returns if the element managed by this menu can be removed.
	 * 
	 * @return If the element can be removed.
	 */
	public abstract boolean canBeRemoved();

	/**
	 * Method called just before removing the group.
	 */
	public abstract boolean beforeRemove();

}
