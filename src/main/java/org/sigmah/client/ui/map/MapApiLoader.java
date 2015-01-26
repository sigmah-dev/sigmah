/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.ui.map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.ajaxloader.client.AjaxLoader;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.maps.client.Maps;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience wrapper for loading the Google Maps API asynchronously
 *
 * @author Alex Bertram
 */
public class MapApiLoader {
    private static final int TIMEOUT = 10 * 1000;
    // TODO: externalize this, should come from page
    private static final String API_VERSION = "2";
    private static final boolean USING_SENSOR = false;

    private static boolean loadInProgress = false;
    private static List<AsyncCallback> waitingCallbacks;

    public static void load(final AsyncCallback<Void> callback) {
        if(Maps.isLoaded()) {
            if(callback != null) {
                callback.onSuccess(null);
            }
        } else {
            if(!loadInProgress) {
                startLoad();
            }
            addListener(callback);
        }
        load();
    }

    public static void load() {
        Log.debug("MapApiLoader: load()");
        if(!Maps.isLoaded() && !loadInProgress) {
            startLoad();
        }
    }

    public static String getApiKey() {
        return Dictionary.getDictionary("GoogleMapsAPI").get("key");
    }

    private static void startLoad() {
        loadInProgress = true;
        waitingCallbacks = new ArrayList<AsyncCallback>();

        AjaxLoader.AjaxLoaderOptions options = AjaxLoader.AjaxLoaderOptions.newInstance();
        options.setLanguage(LocaleInfo.getCurrentLocale().getLocaleName());

        Maps.loadMapsApi(getApiKey(), API_VERSION, USING_SENSOR, options, new Runnable() {
            @Override
            public void run() {
                onApiLoaded();
            }
        });
        startFailureTimer();
    }

    private static void startFailureTimer() {
        Timer timer = new Timer() {
            @Override
            public void run() {
                if (!Maps.isLoaded()) {
                    onApiLoadFailure();
                }
            }
        };
        timer.schedule(TIMEOUT);
    }

    private static void onApiLoaded() {
        loadInProgress = false;
        for(AsyncCallback callback : waitingCallbacks) {
            callback.onSuccess(null);
        }
    }

    private static void onApiLoadFailure() {
        loadInProgress = false;
        for(AsyncCallback callback : waitingCallbacks) {
            callback.onSuccess(null);
        }
    }

    private static void addListener(AsyncCallback<Void> callback) {
        if(callback != null) {
            waitingCallbacks.add(callback);
        }
    }
}
