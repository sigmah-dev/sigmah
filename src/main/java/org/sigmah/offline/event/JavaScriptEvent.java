package org.sigmah.offline.event;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <E> Event type
 */
public interface JavaScriptEvent<E extends JavaScriptObject> {
	/**
	 * Le contexte est complétement perdu lors de l'appel à onEvent.
	 * Il n'est PAS possible d'utiliser des variables définies en dehors du scope.
	 * @param event 
	 */
	void onEvent(E event);
}
