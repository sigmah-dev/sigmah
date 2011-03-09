/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.offline.sigmah.sync.Synchronizer;
import org.sigmah.client.util.Notification;

/**
 * Factory for the "online" label.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineLabelFactory {
    
    public static Widget getLabel(final DispatchOperator dispatchOperator, final OnlineMode onlineMode) {
        
        final HTML label = new HTML("status");
        label.getElement().getStyle().setCursor(Cursor.POINTER);

        setText(label, onlineMode.isOnline());

        label.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                // Checking current status
                boolean status = onlineMode.isOnline();

                // Dialog box
                final MessageBox box = MessageBox.progress(I18N.CONSTANTS.synchronizerTitle(), I18N.CONSTANTS.loading(), "0 %");
                final ProgressBar bar = box.getProgressBar();

                // Listening the progression
                final Synchronizer.Listener callback = new Synchronizer.Listener() {

                    @Override
                    public void onUpdate(double progress) {
                        Log.debug("Progress : "+(int)(progress*100.0)+" %");
                        bar.updateProgress(progress, (int)(progress*100.0)+" %");
                    }

                    @Override
                    public void onComplete() {
                        boolean status = onlineMode.isOnline();

                        Log.debug("Complete");

                        box.close();
                        if(status)
                            Notification.show(I18N.CONSTANTS.sigmahOfflineOnlineMode(), I18N.CONSTANTS.synchronizerFinished());
                        else
                            Notification.show(I18N.CONSTANTS.sigmahOfflineOfflineMode(), I18N.CONSTANTS.synchronizerFinished());

                        setText(label, status);
                    }

                    @Override
                    public void onFailure(boolean critical, String reason) {
                        Log.debug("Failure");

                        if(critical) {
                            box.close();
                            MessageBox.alert(I18N.CONSTANTS.synchronizerError(), reason, null);
                                    
                        } else
                            Notification.show(I18N.CONSTANTS.synchronizerError(), reason);
                    }

                    @Override
                    public void onStart() {
                        Log.debug("Starting...");
                    }

                    @Override
                    public void onTaskChange(String taskName) {
                        //box.setMessage(taskName);
                        Log.debug("Task change : " + taskName);
                        setDialogMessage(box.getDialog(), taskName);
                    }

                };

                // Switching mode
                try {
                    dispatchOperator.setOnlineMode(!status, callback);
                    
                } catch(JavaScriptException e) {
                    Log.debug("Google Gears error while setting online mode to "+!status, e);

                    box.close();
                    Notification.show(I18N.CONSTANTS.synchronizerError(), I18N.CONSTANTS.sigmahOfflineDenied());
                }
            }
        });
        
        return label;
    }

    private static void setText(final HTML label, boolean online) {
        if(online)
            label.setHTML("<span style='color: rgb(238,147,41)'>•</span> online");
        else
            label.setHTML("<span style='color: white'>•</span> offline");
    }

    private static void setDialogMessage(Dialog dialog, String message) {
        final El body = new El(dialog.getElement("body"));
        final Element contentEl = body.dom.getChildNodes().getItem(1).cast();
        final Element msgEl = contentEl.getFirstChild().cast();
        
        msgEl.setInnerHTML(message);
    }
}
