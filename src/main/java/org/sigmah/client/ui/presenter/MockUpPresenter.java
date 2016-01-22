package org.sigmah.client.ui.presenter;

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
import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.ConfirmCallback;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.MockUpView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Panel;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Mock-up presenter.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class MockUpPresenter extends AbstractPagePresenter<MockUpPresenter.View> {

	/**
	 * View interface.
	 */
	@ImplementedBy(MockUpView.class)
	public static interface View extends ViewInterface {

		/**
		 * Clears the view.
		 */
		void clear();

		/**
		 * Adds a new section.
		 * 
		 * @param title
		 *          The section's title.
		 */
		void addSection(String title);

		/**
		 * Adds an element to the current section.
		 * 
		 * @param w
		 *          The new element.
		 */
		void addWidget(IsWidget w);

	}

	/**
	 * Flexible element for file upload.
	 */
	private FilesListElementDTO filesListElementDTO;

	@Inject
	public MockUpPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.MOCKUP;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		Button b;
		Panel p;

		// --------------------------------------
		// -- Application message.
		// --------------------------------------

		view.addSection("Application message");
		p = new FlowPanel();

		for (final MessageType t : MessageType.values()) {
			b = new Button(t.name());
			b.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					eventBus.updateZoneRequest(Zone.MESSAGE_BANNER.requestWith(RequestParameter.CONTENT, randomString()).addData(RequestParameter.TYPE, t));
				}

			});
			p.add(b);
		}
		b = new Button("Hide");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.updateZoneRequest(Zone.MESSAGE_BANNER.requestWith(RequestParameter.CONTENT, null).addData(RequestParameter.TYPE, null));
			}

		});
		p.add(b);

		view.addWidget(p);

		// --------------------------------------
		// -- Page message.
		// --------------------------------------

		view.addSection("Page message");
		p = new FlowPanel();

		for (final MessageType t : MessageType.values()) {
			b = new Button(t.name());
			b.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					displayPageMessage(randomString(), t);
				}

			});
			p.add(b);
		}
		b = new Button("Hide");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				displayPageMessage(null, null);
			}

		});
		p.add(b);

		view.addWidget(p);

		// --------------------------------------
		// -- Messages (popup).
		// --------------------------------------

		view.addSection("Messages (popup)");
		p = new FlowPanel();

		for (final MessageType t : MessageType.values()) {
			b = new Button(t.name());
			b.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					N10N.message(MessageType.getTitle(t), randomString(), t);
				}

			});
			p.add(b);
		}
		b = new Button("List");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.message("List", randomString(), randomList(3), MessageType.DEFAULT);
			}

		});
		p.add(b);
		b = new Button("Map");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.message("Map", randomMap(3), MessageType.DEFAULT);
			}

		});
		p.add(b);

		view.addWidget(p);

		// --------------------------------------
		// -- Notifications.
		// --------------------------------------

		view.addSection("Notifications");
		p = new FlowPanel();

		for (final MessageType t : MessageType.values()) {
			b = new Button(t.name());
			b.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					N10N.notification(MessageType.getTitle(t), randomString(), t);
				}

			});
			p.add(b);
		}
		b = new Button("List");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.notification("List", randomString(), randomList(3), MessageType.DEFAULT);
			}

		});
		p.add(b);
		b = new Button("Map");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.notification("Map", randomMap(3), MessageType.DEFAULT);
			}

		});
		p.add(b);

		view.addWidget(p);

		// --------------------------------------
		// -- Confirmations.
		// --------------------------------------

		view.addSection("Confirmations");
		p = new FlowPanel();

		b = new Button("String (2 callbacks)");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.confirmation(MessageType.getTitle(MessageType.QUESTION), randomString(), yesCallback(), noCallback());
			}

		});
		p.add(b);

		b = new Button("String (1 callback)");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.confirmation(MessageType.getTitle(MessageType.QUESTION), randomString(), yesCallback());
			}

		});
		p.add(b);

		b = new Button("List");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.confirmation("List", randomString(), randomList(3), yesCallback(), noCallback());
			}

		});
		p.add(b);

		b = new Button("Map");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				N10N.confirmation("Map", randomMap(3), yesCallback(), noCallback());
			}

		});
		p.add(b);

		view.addWidget(p);

		// --------------------------------------
		// -- Project creations.
		// --------------------------------------

		view.addSection("Project creations");
		p = new FlowPanel();

		b = new Button("Project");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final PageRequest request = new PageRequest(Page.CREATE_PROJECT);
				request.addParameter(RequestParameter.TYPE, CreateProjectPresenter.Mode.PROJECT);
				eventBus.navigateRequest(request);
			}

		});
		p.add(b);

		b = new Button("Test project");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final PageRequest request = new PageRequest(Page.CREATE_PROJECT);
				request.addParameter(RequestParameter.TYPE, CreateProjectPresenter.Mode.TEST_PROJECT);
				eventBus.navigateRequest(request);
			}

		});
		p.add(b);

		b = new Button("Funded project");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final PageRequest request = new PageRequest(Page.CREATE_PROJECT);
				request.addParameter(RequestParameter.TYPE, CreateProjectPresenter.Mode.FUNDED_BY_ANOTHER_PROJECT);
				request.addData(RequestParameter.DTO, randomBaseProject());
				eventBus.navigateRequest(request);
			}

		});
		p.add(b);

		b = new Button("Funding project");
		b.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				final PageRequest request = new PageRequest(Page.CREATE_PROJECT);
				request.addParameter(RequestParameter.TYPE, CreateProjectPresenter.Mode.FUNDING_ANOTHER_PROJECT);
				request.addData(RequestParameter.DTO, randomBaseProject());
				eventBus.navigateRequest(request);
			}

		});
		p.add(b);

		view.addWidget(p);

		// --------------------------------------
		// -- File transferts.
		// --------------------------------------
		view.addSection("File transfert");

		final ProjectDTO projectDTO = new ProjectDTO();
		projectDTO.setId(7776);

		filesListElementDTO = new FilesListElementDTO();
		filesListElementDTO.setService(dispatch);
		filesListElementDTO.setAuthenticationProvider(injector.getAuthenticationProvider());
		filesListElementDTO.setCurrentContainerDTO(projectDTO);
		filesListElementDTO.setTransfertManager(injector.getTransfertManager());
		filesListElementDTO.setEventBus(eventBus);

		filesListElementDTO.setId(2168);
		filesListElementDTO.setAmendable(false);
		filesListElementDTO.setFilledIn(false);
		filesListElementDTO.setGloballyExportable(false);
		filesListElementDTO.setHistorable(false);
		filesListElementDTO.setLabel("Test de l'envoi de fichier");
		filesListElementDTO.setLimit(5);
		filesListElementDTO.setValidates(false);

		filesListElementDTO.init();

		final ValueResult valueResult = null;

		view.addWidget(filesListElementDTO.getElementComponent(valueResult));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		// Refreshing the file list
		filesListElementDTO.updateComponent();
	}

	/**
	 * Generates an unique random message.
	 * 
	 * @return The message.
	 */
	private static String randomString() {
		return ClientUtils.formatDate(ClientUtils.now(), "EEEE, MMMM dd, yyyy - HH:mm:ss,SSS") + Random.nextInt();
	}

	/**
	 * Generates a random list of strings.
	 * 
	 * @param size
	 *          The size of the list.
	 * @return The list.
	 */
	private static Collection<String> randomList(int size) {
		size = size <= 0 ? 1 : size;
		final ArrayList<String> l = new ArrayList<String>(size);
		for (int i = 0; i < size; i++) {
			l.add(randomString());
		}
		return l;
	}

	/**
	 * Generates a random map of strings.
	 * 
	 * @param size
	 *          The size of the map.
	 * @return The list.
	 */
	private static Map<String, Collection<String>> randomMap(int size) {
		size = size <= 0 ? 1 : size;
		final HashMap<String, Collection<String>> m = new HashMap<String, Collection<String>>(size);
		for (int i = 0; i < size; i++) {
			m.put(randomString(), randomList(size));
		}
		return m;
	}

	/**
	 * Builds and returns a default 'YES' callback.
	 * 
	 * @return the callback.
	 */
	private static ConfirmCallback yesCallback() {
		return new ConfirmCallback() {

			@Override
			public void onAction() {
				N10N.validNotif("You clicked 'YES'.");
			}

		};
	}

	/**
	 * Builds and returns a default 'NO' callback.
	 * 
	 * @return the callback.
	 */
	private static ConfirmCallback noCallback() {
		return new ConfirmCallback() {

			@Override
			public void onAction() {
				N10N.errorNotif("You clicked 'NO'.");
			}

		};
	}

	/**
	 * Generates a random base project for funding / funded creation modes.
	 * 
	 * @return The base project.
	 */
	private static ProjectDTO randomBaseProject() {

		final ProjectDTO p = new ProjectDTO();
		p.setName(randomString());
		p.setPlannedBudget(1000.0d);

		return p;

	}

}
