/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.dispatch.monitor;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.i18n.I18N;

import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Container;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.ProgressBar;

/**
 * Uses a GXT loading mask (with a progress bar) on a component to keep the user
 * updated on the progress of an asynchronous call.
 * 
 * @author tmi
 */
public class ProgressingAsyncMonitor implements AsyncMonitor {

    private final Component panel;
    private final ProgressBar bar;
    private int counter;
    private int max;

    public ProgressingAsyncMonitor(Component panel) {
        this.panel = panel;
        this.bar = buildBar();
    }

    private ProgressBar buildBar() {
        final ProgressBar bar = new ProgressBar();
        bar.addStyleName("monitor-progress-bar");

        // When the bar is complete, the monitor is disabled.
        bar.addListener(Events.Update, new Listener<ComponentEvent>() {

            @Override
            public void handleEvent(ComponentEvent be) {

                if (bar.getValue() >= 1) {
                    unmask();
                }
            }
        });

        return bar;
    }

    @Override
    public void beforeRequest() {
        mask();
    }

    private void mask() {

        bar.updateText(I18N.CONSTANTS.loading());

        /*
         * If the component is not yet rendered, wait until after it is all
         * layed out before applying the mask.
         */
        if (panel.isRendered()) {
            bar.render(panel.el().dom);
            bar.el().center(panel.el().dom);
            panel.el().mask();
        } else {
            panel.addListener(panel instanceof Container ? Events.AfterLayout : Events.Render,
                    new Listener<ComponentEvent>() {
                        public void handleEvent(ComponentEvent be) {
                            /*
                             * If the call is still in progress, apply the mask
                             * now.
                             */
                            bar.render(panel.el().dom);
                            bar.el().center(panel.el().dom);
                            panel.el().mask();
                            panel.removeListener(Events.Render, this);
                        }
                    });
        }
    }

    @Override
    public void onConnectionProblem() {
        bar.updateText(I18N.CONSTANTS.connectionProblem());
        mask();
    }

    @Override
    public boolean onRetrying() {
        bar.updateText(I18N.CONSTANTS.retrying());
        mask();
        return true;
    }

    @Override
    public void onServerError() {
        MessageBox.alert(I18N.CONSTANTS.error(), I18N.CONSTANTS.serverError(), null);
        unmask();
    }

    private void unmask() {
        if (panel.isRendered()) {
            panel.unmask();
            bar.el().remove();
        }
    }

    @Override
    public void onCompleted() {
        // Nothing.
    }

    /**
     * Sets the number of elements.
     * 
     * @param max
     *            The number of elements.
     */
    public void initCounter(int max) {
        this.max = max;
        updateProgressBar();
    }

    /**
     * Add a loaded element.
     */
    public void increment() {
        increment(1);
    }

    /**
     * Add n loaded elements.
     * 
     * @param count
     *            The number of loaded elements.
     */
    public void increment(int count) {
        counter += count;
        updateProgressBar();
    }

    /**
     * Update the progress bar state.
     */
    private void updateProgressBar() {

        if (max == 0) {
            bar.updateProgress(1, "");
        } else {
            bar.updateProgress(new Double(counter) / new Double(max),
                    counter + " / " + max + " " + I18N.CONSTANTS.refreshProjectListProjectLoaded() + ".");
        }
    }
}
