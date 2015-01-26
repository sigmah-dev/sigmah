/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.linker;

import com.google.gwt.core.ext.LinkerContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.linker.AbstractLinker;
import com.google.gwt.core.ext.linker.ArtifactSet;
import com.google.gwt.core.ext.linker.CompilationResult;
import com.google.gwt.core.ext.linker.EmittedArtifact;
import com.google.gwt.core.ext.linker.LinkerOrder;
import com.google.gwt.core.ext.linker.LinkerOrder.Order;
import com.google.gwt.core.ext.linker.SelectionProperty;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
@LinkerOrder(Order.POST)
public class ManifestGenerationLinker extends AbstractLinker {
	public static final Boolean PREFER_ONLINE = false;
	public static final String MODULE_NAME = "Sigmah";
	
	@Override
	public String getDescription() {
		return "Generates HTML5 manifests for each browsers.";
	}

	@Override
	public ArtifactSet link(TreeLogger logger, LinkerContext context, ArtifactSet artifacts) throws UnableToCompleteException {
		final ArtifactSet artifactSet = new ArtifactSet(artifacts);
		
		final SortedSet<EmittedArtifact> emittedArtifacts = artifacts.find(EmittedArtifact.class);
		final SortedSet<CompilationResult> compilationResults = artifacts.find(CompilationResult.class);
		
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		final HashMap<String, StringBuilder> manifests = new HashMap<String, StringBuilder>();
		
		final Map<String, String> permutationMap = new HashMap<String, String>();
		for(final CompilationResult compilationResult : compilationResults) {
			final String permutationName = compilationResult.getStrongName();
//			
			for(final SortedMap<SelectionProperty, String> map : compilationResult.getPropertyMap()) {
				for(Map.Entry<SelectionProperty, String> entry : map.entrySet()) {
					
					if(entry.getKey().getName().equals("user.agent")) {
						final String userAgent = entry.getValue();
						permutationMap.put(permutationName, userAgent);
						
						StringBuilder userAgentManifest = manifests.get(userAgent);
						if(userAgentManifest == null) {
							userAgentManifest = new StringBuilder("CACHE MANIFEST\n")
								.append("# Generation date: ")
								.append(dateFormat.format(new Date()))
								.append("\n");
							manifests.put(userAgent, userAgentManifest);
						}
					}
				}
			}
		}
		
		appendToAll(manifests, MODULE_NAME + ".nocache.js\n");
		
		for(EmittedArtifact artifact : emittedArtifacts) {
			if(EmittedArtifact.Visibility.Public.matches(((EmittedArtifact)artifact).getVisibility())) {
				final String partialPath = artifact.getPartialPath();
				String browser = null;
				for(Map.Entry<String, String> entry : permutationMap.entrySet()) {
					if(partialPath.contains(entry.getKey())) {
						browser = entry.getValue();
						break;
					}
				}
				
				final StringBuilder manifest = manifests.get(browser);
				if(manifest != null) {
					manifest.append(((EmittedArtifact)artifact).getPartialPath())
							.append("\n");
				} else {
					appendToAll(manifests, ((EmittedArtifact)artifact).getPartialPath())
						.appendToAll(manifests, "\n");
				}
			}
		}
		
		appendToAll(manifests, "FALLBACK:\nonline.json offline.json\n");
		artifactSet.add(emitString(logger, "{\"online\": false}", "offline.json"));
		artifactSet.add(emitString(logger, "{\"online\": true}", "online.json"));
		
		if(PREFER_ONLINE) {
			appendToAll(manifests, "SETTINGS:\nprefer-online\n");
		}

		appendToAll(manifests, "NETWORK:\n*");
	
		for(Map.Entry<String, StringBuilder> manifest : manifests.entrySet()) {
			artifactSet.add(emitString(logger, manifest.getValue().toString(), manifest.getKey() + ".manifest"));
		}
		return artifactSet;
	}
	
	private ManifestGenerationLinker appendToAll(Map<String, StringBuilder> map, String s) {
		for(StringBuilder stringBuilder : map.values()) {
			stringBuilder.append(s);
		}
		return this;
	}
}
