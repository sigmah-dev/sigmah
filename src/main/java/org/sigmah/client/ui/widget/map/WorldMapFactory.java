package org.sigmah.client.ui.widget.map;

/**
 * Factory for WorldMap objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class WorldMapFactory {
	
	protected WorldMapFactory() {
	}
	
	/**
	 * Create a new WorldMap instance.
	 * 
	 * @return a new WorldMap instance.
	 */
	public static WorldMap createInstance() {
		return new GoogleWorldMap();
	}
}
