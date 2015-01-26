package org.sigmah.offline.appcache;

/**
 *
 */
public interface ApplicationCacheEventHandler {

	void onStatusChange(ApplicationCache.Status status);

	void onProgress(int loaded, int total);
	
}
