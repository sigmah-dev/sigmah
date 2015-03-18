package org.sigmah.offline.js;

import com.google.gwt.core.client.JavaScriptObject;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Javascript version of {@link SiteDTO}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class SiteJS extends JavaScriptObject {
	
	protected SiteJS() {
		
	}
	
	public static SiteJS toJavaScript(SiteDTO siteDTO) {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
	public SiteDTO toDTO() {
		throw new UnsupportedOperationException("Not implemented yet.");
	}
	
}
