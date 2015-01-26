/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.project.reports;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import java.util.HashMap;
import java.util.List;
import org.sigmah.client.EventBus;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.Authentication;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.shared.domain.profile.PrivacyGroupPermissionEnum;
import org.sigmah.shared.dto.profile.ProfileUtils;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectDTO.LocalizedElement;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.element.ReportElementDTO;
import org.sigmah.shared.dto.element.ReportListElementDTO;

/**
 * Builds menus to attach documents and reports to flexibles elements.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class AttachMenuBuilder {
	public interface AttachDocumentHandler {
		/**
		 * Retrieves the attach dialog box and clears its fields.
		 * @return The dialog box, cleared.
		 */
		Dialog getDialog(
				ListStore<ReportReference> documentsStore,
				ProjectDTO project,
				FlexibleElementDTO flexibleElement,
				MenuItem menuItem,
				String phaseName,
				Authentication authentication,
				Dispatcher dispatcher,
				EventBus eventBus);

		/**
		 * Returns <code>true</code> if the given menu item can be accessed by
		 * the user.<br>
		 * Typically, this method will return <code>true</code> if the
		 * flexible element has a <code>null</code> value.<br>
		 * <br>
		 * This method is called only if the given menu item can be enabled
		 * (for example, it will ne be called if the phase it is in is closed).
		 * <br>
		 * <br>
		 * If you need to, you can defer this setting by returning
		 * <code>false</code> and by enabling the <code>menuItem</code> instance
		 * yourself.
		 *
		 * @param menuItem Menu item that will be enabled / disabled.
		 * @param element Flexible element associated with the <code>menuItem</code>.
		 * @param dispatcher Command dispatcher.
		 * @return <code>true</code> to enable the menu item, <code>false</code> otherwise.
		 */
		boolean shouldEnableMenuItem(MenuItem menuItem, LocalizedElement element, Dispatcher dispatcher);
	}

	private final static HashMap<Class<? extends FlexibleElementDTO>, AttachDocumentHandler> handlers;

	static {
		final HashMap<Class<? extends FlexibleElementDTO>, AttachDocumentHandler> map = new HashMap<Class<? extends FlexibleElementDTO>, AttachDocumentHandler>();
		map.put(FilesListElementDTO.class, new AttachFileHandler());

		final AttachReportHandler attachReportHandler = new AttachReportHandler();
		map.put(ReportElementDTO.class, attachReportHandler);
		map.put(ReportListElementDTO.class, attachReportHandler);

		handlers = map;
	}

	public static void createMenu(
			final ProjectDTO project,
			final Class<? extends FlexibleElementDTO> type,
			final Button button,
			final ListStore<ReportReference> documentsStore,
			final Authentication authentication,
			final Dispatcher dispatcher,
			final EventBus eventBus) {

		final AttachDocumentHandler handler = handlers.get(type);

		final Menu menu = new Menu();

		boolean menuEnabled = false;

		// Retrieves all the files lists elements in the current project.
		final List<LocalizedElement> filesLists = project.getLocalizedElements(type);

		if (filesLists != null) {
			// For each files list.
			for (final LocalizedElement filesList : filesLists) {
				final PrivacyGroupPermissionEnum perm = ProfileUtils.getPermission(authentication, filesList.getElement().getPrivacyGroup());
				if(perm == PrivacyGroupPermissionEnum.WRITE){
					boolean itemEnabled = false;

					// Builds the item label.
					final StringBuilder sb = new StringBuilder();
					if (filesList.getPhaseModel() != null) {
						sb.append(filesList.getPhaseModel().getName());
					} else {
						sb.append(I18N.CONSTANTS.projectDetails());
					}
					sb.append(" | ");
					sb.append(filesList.getElement().getLabel());

					// Builds the corresponding menu item.
					final MenuItem item = new MenuItem(sb.toString());

					// If the phase is the details page.
					if (filesList.getPhase() == null) {
						item.addSelectionListener(getAttachListener(type, I18N.CONSTANTS.projectDetails(),
								filesList.getElement(), item, project, documentsStore, authentication,
								dispatcher, eventBus));
						itemEnabled = true;
					}
					// If the phase is closed.
					else if (filesList.getPhase().isEnded()) {
						item.setIcon(IconImageBundle.ICONS.close());
						item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseClosed());
					}
					// If the phase is active.
					else if (filesList.getPhase() == project.getCurrentPhaseDTO()) {
						item.setIcon(IconImageBundle.ICONS.activate());
						item.addSelectionListener(getAttachListener(type, filesList.getPhaseModel().getName(),
								filesList.getElement(), item, project, documentsStore, authentication,
								dispatcher, eventBus));
						itemEnabled = true;
					}
					// If the phase is a successor of the active one.
					else if (filesList.getPhase().isSuccessor(project.getCurrentPhaseDTO())) {
						item.addSelectionListener(getAttachListener(type, filesList.getPhaseModel().getName(),
								filesList.getElement(), item, project, documentsStore, authentication,
								dispatcher, eventBus));
						itemEnabled = true;
					}
					// Future phase, not yet accessible.
					else {
						item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseInactive());
					}

					if(itemEnabled) {
						item.setEnabled(handler.shouldEnableMenuItem(item, filesList, dispatcher));
						menuEnabled = true;
					}

					menu.add(item);
				}
			}
		}

		// Adds this menu to the button.
		button.setEnabled(menuEnabled);

		if (menuEnabled) {
			button.setMenu(menu);
		}
	}

	public static void createMenu(
			final ProjectDTO project,
			final List<LocalizedElement> filesLists,
			final Button button,
			final ListStore<ReportReference> documentsStore,
			final Authentication authentication,
			final Dispatcher dispatcher,
			final EventBus eventBus) {

		final Menu menu = new Menu();

		boolean menuEnabled = false;

		if (filesLists != null) {
			// For each files list.
			for (final LocalizedElement filesList : filesLists) {
				final PrivacyGroupPermissionEnum perm = ProfileUtils.getPermission(authentication, filesList.getElement().getPrivacyGroup());
				if(perm == PrivacyGroupPermissionEnum.WRITE){
					final Class<? extends FlexibleElementDTO> type = filesList.getElement().getClass();
					final AttachDocumentHandler handler = handlers.get(type);

					boolean itemEnabled = false;

					// Builds the item label.
					final StringBuilder sb = new StringBuilder();
					if (filesList.getPhaseModel() != null) {
						sb.append(filesList.getPhaseModel().getName());
					} else {
						sb.append(I18N.CONSTANTS.projectDetails());
					}
					sb.append(" | ");
					sb.append(filesList.getElement().getLabel());

					// Builds the corresponding menu item.
					final MenuItem item = new MenuItem(sb.toString());

					// If the phase is the details page.
					if (filesList.getPhase() == null) {
						item.addSelectionListener(getAttachListener(type, I18N.CONSTANTS.projectDetails(),
								filesList.getElement(), item, project, documentsStore, authentication,
								dispatcher, eventBus));
						itemEnabled = true;
					}
					// If the phase is closed.
					else if (filesList.getPhase().isEnded()) {
						item.setIcon(IconImageBundle.ICONS.close());
						item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseClosed());
					}
					// If the phase is active.
					else if (filesList.getPhase() == project.getCurrentPhaseDTO()) {
						item.setIcon(IconImageBundle.ICONS.activate());
						item.addSelectionListener(getAttachListener(type, filesList.getPhaseModel().getName(),
								filesList.getElement(), item, project, documentsStore, authentication,
								dispatcher, eventBus));
						itemEnabled = true;
					}
					// If the phase is a successor of the active one.
					else if (filesList.getPhase().isSuccessor(project.getCurrentPhaseDTO())) {
						item.addSelectionListener(getAttachListener(type, filesList.getPhaseModel().getName(),
								filesList.getElement(), item, project, documentsStore, authentication,
								dispatcher, eventBus));
						itemEnabled = true;
					}
					// Future phase, not yet accessible.
					else {
						item.setTitle(I18N.CONSTANTS.flexibleElementFilesListAddErrorPhaseInactive());
					}

					if(itemEnabled) {
						item.setEnabled(handler.shouldEnableMenuItem(item, filesList, dispatcher));
						menuEnabled = true;
					}

					menu.add(item);
				}
			}
		}

		// Adds this menu to the button.
		button.setEnabled(menuEnabled);

		if (menuEnabled) {
			button.setMenu(menu);
		}
	}

	/**
	 * Create a attach listener to manage the file upload.
	 *
	 * @return The listener.
	 */
	private static SelectionListener<MenuEvent> getAttachListener(
			final Class<? extends FlexibleElementDTO> type,
			final String phaseName,
			final FlexibleElementDTO element,
			final MenuItem menuItem,
			final ProjectDTO project,
			final ListStore<ReportReference> documentsStore,
			final Authentication authentication,
			final Dispatcher dispatcher,
			final EventBus eventBus) {

		return new SelectionListener<MenuEvent>() {

			@Override
			public void componentSelected(MenuEvent ce) {
				final AttachDocumentHandler dialogSingleton = handlers.get(type);

				if(dialogSingleton != null) {
					final Dialog dialog = dialogSingleton.getDialog(documentsStore,
							project, element, menuItem, phaseName, authentication,
							dispatcher, eventBus);

					dialog.show();

				} else
					Log.debug("No dialog box for the type " + type);
			}
		};
	}
}
