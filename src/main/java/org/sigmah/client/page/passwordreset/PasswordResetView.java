/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.page.passwordreset;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.login.PasswordManagementService;
import org.sigmah.client.page.login.PasswordManagementServiceAsync;
import org.sigmah.client.util.Notification;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.inject.Inject;

/**
 * Reset password view is shown after following a link which is sent to user's
 * email as a way to reset user's password
 * 
 * @author Sherzod Muratov (sherzod.muratov@gmail.com)
 */
public class PasswordResetView extends Composite implements
		PasswordResetPresenter.View {

	private final PasswordManagementServiceAsync service;
	
	@Inject
	public PasswordResetView() {

		service = GWT.create(PasswordManagementService.class);
		
		final SimplePanel panel = new SimplePanel();
		panel.setStyleName("login-background");
		
		final Grid grid = new Grid(1, 2);	
		grid.setStyleName("login-box");

		// Logo
		grid.setWidget(0, 0, new Image("image/login-logo.png"));

		// Form
		final FlexTable form = new FlexTable();
		form.setWidth("90%");				

		// Adding the form to the orange box
		grid.getCellFormatter().setHorizontalAlignment(0, 1,HasHorizontalAlignment.ALIGN_CENTER);
		grid.setWidget(0, 1, form);

		// Styles
		grid.getCellFormatter().setStyleName(0, 0, "login-box-logo");
		grid.getCellFormatter().setStyleName(0, 1, "login-box-form");
 
		panel.add(grid);
		initWidget(panel);
		 
		
		
		
		//Validate URL token and conditionally show password update form or explanation message
		service.validateAndGetUserEmailByToken(Window.Location.getParameter("token"), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				showFailureMessage(form);
			}

			@Override
			public void onSuccess(String result) {
				showPasswordUpdateForm(form, result);
			}
		});
	}
	
	void showFailureMessage(final FlexTable form){
		form.setText(0, 0, I18N.CONSTANTS.invalidLink());
		form.getFlexCellFormatter().setStyleName(0, 0, "update-form-message-title");
		
		form.setText(1, 0, I18N.CONSTANTS.invalidLinkDetail());
		form.getFlexCellFormatter().setStyleName(1, 0, "update-form-message-content");
		
		final Anchor loginPageLink = new Anchor(I18N.CONSTANTS.visitToLoginPage());
		loginPageLink.setStyleName("login-box-form-forgotten");
		loginPageLink.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				Window.Location.replace(GWT.getModuleBaseURL()); 
				
			}
		});
		form.setWidget(2, 0, loginPageLink);
		form.getFlexCellFormatter().setWidth(2, 0, "100%");
		form.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	void showPasswordUpdateForm(final FlexTable form,final String email){
		
		int y = 0;

		// E-Mail field
		form.setText(y, 0, I18N.CONSTANTS.loginLoginField());
		form.getCellFormatter().setStyleName(y, 0, "login-box-form-label");

		final TextBox loginTextBox = new TextBox();
		loginTextBox.setWidth("100%");
		loginTextBox.setValue(email);
		loginTextBox.setEnabled(false);
		form.setWidget(y, 1, loginTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator
		for (int i = 0; i < 3; i++)
			form.getCellFormatter().setStyleName(y, i,"login-box-form-separator");
		y++;

		// Password field
		form.setText(y, 0, I18N.CONSTANTS.newPassword()+"*");
		form.getCellFormatter().setStyleName(y, 0, "login-box-form-label");

		final PasswordTextBox passwordTextBox = new PasswordTextBox();
		passwordTextBox.setWidth("100%");
		form.setWidget(y, 1, passwordTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator
		for (int i = 0; i < 3; i++)
			form.getCellFormatter().setStyleName(y, i,"login-box-form-separator");
		y++;
		
		// Confirm Password field
		form.setText(y, 0, I18N.CONSTANTS.confirmPassword()+"*");
		form.getCellFormatter().setStyleName(y, 0, "login-box-form-label");
		final PasswordTextBox confirmPasswordTextBox = new PasswordTextBox();
		confirmPasswordTextBox.setWidth("100%");
		form.setWidget(y, 1, confirmPasswordTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator
		for (int i = 0; i < 2; i++)
			form.getCellFormatter().setStyleName(y, i,
					"login-box-form-separator");
		y++;
		 
		// Update button
		final HorizontalPanel bottomPanel=new HorizontalPanel();
		bottomPanel.setSpacing(6);
		
		final Image loader = new Image("image/login-loader.gif");
		loader.getElement().getStyle().setVisibility(Visibility.HIDDEN);
	 
		bottomPanel.add(loader); 
						
		final Button updateButton = new Button(I18N.CONSTANTS.confirmUpdate());
		updateButton.setWidth("130px");
		bottomPanel.add(updateButton);
		
		form.setWidget(y, 1, bottomPanel);
		form.getCellFormatter().setHorizontalAlignment(y, 1,HasHorizontalAlignment.ALIGN_RIGHT);
		form.getCellFormatter().setWidth(y, 1, "100%");
		y++;

		// Login actions
		updateButton.addListener(Events.Select, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				String password=passwordTextBox.getValue();
				
				if(password.length()==0){
					 MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.formWindowFieldsUnfilledDetails(), null);
					return;
				}

				if(!password.equals(confirmPasswordTextBox.getValue())){
					 MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.passwordNotMatch(), null);
					 return;
				}
				
				updateButton.setEnabled(false);
			     loader.getElement().getStyle().setVisibility(Visibility.VISIBLE);
				
				
				service.updatePassword(email, password,
						new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								updateButton.setEnabled(true);
								 MessageBox.alert(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError(), null);
							}

							@Override
							public void onSuccess(Void result) {	
								Notification.show(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.passwordUpdated());
								
								//Redirect to login page after 3 sec
								Timer timer = new Timer() {
								    public void run() {
								    	Window.Location.replace(GWT.getModuleBaseURL());
								    }
								  };
								  timer.schedule(3000);
								
							}
						});
			}
		});

		final KeyDownHandler handler = new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					updateButton.fireEvent(Events.Select);
				}
			}
		};
		confirmPasswordTextBox.addKeyDownHandler(handler);
		passwordTextBox.addKeyDownHandler(handler);
	}

}
