package org.sigmah.offline.sync;

import com.google.gwt.storage.client.Storage;
import java.util.Date;
import org.sigmah.shared.command.result.Authentication;

/**
 * Handle saving and loading update dates of Offline Mode data.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateDates {
    
    private static final String ITEM_DATABASE_UPDATE_DATE = ".database-update-date";
    private static final String ITEM_SIGMAH_UPDATE_DATE = "sigmah.update-date";
    
    private UpdateDates() {
    }
    
    public static Date getDatabaseUpdateDate(Authentication authentication) {
        Date updateDate = null;
        
        final Storage storage = Storage.getLocalStorageIfSupported();
        if(authentication != null && storage != null) {
            final String date = storage.getItem(authentication.getUserEmail() + ITEM_DATABASE_UPDATE_DATE);
            if(date != null) {
                updateDate = new Date(Long.parseLong(date));
            }
        }

        return updateDate;
    }
    
    public static void setDatabaseUpdateDate(Authentication authentication, Date date) {
		if(authentication != null) {
			setDatabaseUpdateDate(authentication.getUserEmail(), date);
		}
	}
	
    public static void setDatabaseUpdateDate(String email, Date date) {
        final Storage storage = Storage.getLocalStorageIfSupported();
        if(storage != null) {
            final String item = email + ITEM_DATABASE_UPDATE_DATE;
            
            if(date != null) {
                storage.setItem(item, Long.toString(date.getTime()));
            } else {
                storage.removeItem(item);
            }
        }
    }
    
    public static Date getSigmahUpdateDate() {
        Date updateDate = null;
        
        final Storage storage = Storage.getLocalStorageIfSupported();
        if(storage != null) {
            final String date = storage.getItem(ITEM_SIGMAH_UPDATE_DATE);
            if(date != null) {
                updateDate = new Date(Long.parseLong(date));
            }
        }
        
        return updateDate;
    }
    
    public static void setSigmahUpdateDate(Date date) {
        final Storage storage = Storage.getLocalStorageIfSupported();
        if(storage != null) {
            storage.setItem(ITEM_SIGMAH_UPDATE_DATE, Long.toString(date.getTime()));
        }
    }
}
