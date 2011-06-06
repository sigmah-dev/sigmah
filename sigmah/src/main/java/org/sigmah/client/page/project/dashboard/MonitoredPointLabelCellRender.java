package org.sigmah.client.page.project.dashboard;

import java.util.Date;
import java.util.HashMap;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.project.dashboard.EditFormWindow.FormSubmitListener;
import org.sigmah.client.page.project.dashboard.ProjectDashboardPresenter.View;
import org.sigmah.client.util.Notification;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A cell renderer for label column in a monitored point grid 
 * 
 * @author HUZHE
 *
 */
public class MonitoredPointLabelCellRender implements GridCellRenderer<MonitoredPointDTO> {

	
	private ProjectDashboardPresenter.View view;
	private Dispatcher dispatcher;
	
	/**
	 * @param view
	 * @param dispatcher
	 */
	public MonitoredPointLabelCellRender(View view, Dispatcher dispatcher) {
		super();
		this.view = view;
		this.dispatcher = dispatcher;
	}

	@Override
	public Object render(final MonitoredPointDTO model, String property,
			ColumnData config, int rowIndex, int colIndex,
			ListStore<MonitoredPointDTO> store, Grid<MonitoredPointDTO> grid) {
		
		
		//Create a lable with a hyperlink style 
		com.google.gwt.user.client.ui.Label l = new com.google.gwt.user.client.ui.Label(model.getLabel());
		l.addStyleName("hyperlink_style_label");
		if (model.isCompleted()) {
			//When the monitored point is completed,change the label style
			l.addStyleName("points-completed");
		}
		
		//Add a click handler to response a cleck event
		l.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				// Create a new FormWindow to edit the monitred point
				final EditFormWindow window = new EditFormWindow();
				window.addTextField(I18N.CONSTANTS.monitoredPointLabel(),model.getLabel(), false);
				window.addDateField(I18N.CONSTANTS.monitoredPointExpectedDate(),model.getExpectedDate(), false);

				window.show(I18N.CONSTANTS.monitoredPointUpdate(),I18N.CONSTANTS.monitoredPointUpdateDetails());
				
				//SubmitLister, see the definition of FormWindow for details
				window.addFormSubmitListener(new FormSubmitListener() {

					
					// ---------Updating Handler-------------------------
					// --------------------------------------------------	
					
					@Override
					public void formSubmitted(Object... values) {

						// Checks that the values are in correct type.
						final Object element0 = values[1];
						if (!(element0 instanceof Date)) {
							return;
						}

						final Object element1 = values[0];
						if (!(element1 instanceof String)) {
							return;
						}

						//Retrive all values 
						final Date expectedDate = (Date) element0;
						final String label = (String) element1;
						final HashMap<String, Object> properties = new HashMap<String, Object>();
						properties.put("expectedDate",
								expectedDate.getTime());
						properties.put("label", label);
						properties.put("deleted", model.isDeleted());

						// RPC to update by using the command UpdateEntity
						dispatcher.execute(new UpdateEntity(model, properties),new MaskingAsyncMonitor(view.getMonitoredPointsGrid(), I18N.CONSTANTS.loading()),
								new AsyncCallback<VoidResult>() {

									@Override
									public void onFailure(Throwable caught) {						 
									
									  MessageBox.alert(
													I18N.CONSTANTS
															.monitoredPointUpdateError(),
													I18N.CONSTANTS
															.monitoredPointUpdateErrorDetails(),
													null);
								  
									}

									@Override
									public void onSuccess(VoidResult result) {

										// After the RPC,modify the DTO model of the grid
										model.setExpectedDate(expectedDate);
										model.setLabel(label);
										
										//Refresh the grid 
									    ListStore<MonitoredPointDTO> pointDTOStore = view.getMonitoredPointsGrid().getStore();
									    pointDTOStore.update(model);
									    
									    
										   Notification.show(
												I18N.CONSTANTS
														.infoConfirmation(),
												I18N.CONSTANTS
														.monitoredPointUpdateConfirm());
									       
									}
								});

					}

					// ---------Updating End-----------------------------
					
					// ---------Deletion Handler-------------------------
				    // --------------------------------------------------	
					
					@Override
					public void deleteModelObject() {
					
						
					     //Create a listener for the confirm message box
					    Listener<MessageBoxEvent> confirmListener =new Listener<MessageBoxEvent>() {  
					           public void handleEvent(MessageBoxEvent be) {  
					             
					        	   Button btn = be.getButtonClicked();
					        	   
					        	   //If user clicks the Yes button,begin to delete
					        	   if(btn.getText().equals(I18N.CONSTANTS.yes()))
					        	    {				        		            
					        		   HashMap<String, Object> properties = new HashMap <String,Object>();
				                       properties.put("expectedDate",model.getExpectedDate().getTime());
									   properties.put("label", model.getLabel());
								       properties.put("deleted", true);
					        		   
										// RPC to update by using the command UpdateEntity
										dispatcher.execute(new UpdateEntity(model, properties),new MaskingAsyncMonitor(view.getMonitoredPointsGrid(), I18N.CONSTANTS.loading()),
												new AsyncCallback<VoidResult>() {

													@Override
													public void onFailure(Throwable caught) {						 
													
													  MessageBox.alert(
																	I18N.CONSTANTS
																			.deletionError(),
																	I18N.CONSTANTS
																			.monitoredPointDeletionErrorDetails(),
																	null);
												  
													}

													@Override
													public void onSuccess(VoidResult result) {
														
														//Refresh the grid 
														model.setDeleted(true);
													    ListStore<MonitoredPointDTO> pointDTOStore = view.getMonitoredPointsGrid().getStore();
													    pointDTOStore.remove(model);
													    
													    window.hide();
													     Notification.show(
																	I18N.CONSTANTS
																			.infoConfirmation(),
																	I18N.CONSTANTS
																			.monitoredPointDeletionConfirm());
													}
												});
					        		   
					        	    }
					        	   	        	   
					           }  
					         };  
					       							
				       //Create a confirm messagebox with the listener
				       MessageBox confirmMessageBox = MessageBox.confirm(I18N.CONSTANTS.deleteConfirm(),I18N.CONSTANTS.deleteConfirmMessage(), confirmListener);
				       confirmMessageBox.setButtons(MessageBox.YESNO);
				       ((Button)confirmMessageBox.getDialog().getButtonBar().getItem(0)).setText(I18N.CONSTANTS.yes());
				       ((Button)confirmMessageBox.getDialog().getButtonBar().getItem(1)).setText(I18N.CONSTANTS.no());
				       confirmMessageBox.setIcon(MessageBox.WARNING);
				       confirmMessageBox.show();		
						
					}

					// ---------Deletion End-----------------------------
					
				});
				
			

			}
		});
		return l;
		
		
		
		
		
	}

}
