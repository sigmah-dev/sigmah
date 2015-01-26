package org.sigmah.linker;

import org.sigmah.shared.Language;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Manifest {
	private final String userAgent;
	private final String locale;

	public Manifest() {
		this(null, (String)null);
	}
	
	public Manifest(String userAgent, Language language) {
		this(userAgent, language.getLocale());
	}
	
	public Manifest(String userAgent, String locale) {
		if(userAgent != null) {
			this.userAgent = userAgent;
		} else {
			this.userAgent = "default";
		}
		
		if(locale != null) {
			this.locale = locale;
		} else {
			this.locale = "default";
		}
	}

	public String getUserAgent() {
		return userAgent;
	}

	public String getLocale() {
		return locale;
	}

	public String toFileName() {
		return userAgent + '.' + locale + ".manifest";
	}

	@Override
	public String toString() {
		return locale + '@' + userAgent;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.userAgent != null ? this.userAgent.hashCode() : 0);
		hash = 97 * hash + (this.locale != null ? this.locale.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Manifest other = (Manifest) obj;
		if ((this.userAgent == null) ? (other.userAgent != null) : !this.userAgent.equals(other.userAgent)) {
			return false;
		}
		if ((this.locale == null) ? (other.locale != null) : !this.locale.equals(other.locale)) {
			return false;
		}
		return true;
	}
}
