package org.sigmah.linker;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

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

/**
 * Generates manifest files for offline mode.
 * A manifest is created for each permutation (named browser.language.manifest).
 * 
 * @author Raphaël Calabro <rcalabro at ideia.fr>
 */
@LinkerOrder(Order.POST)
public class ManifestGenerationLinker extends AbstractLinker {
	public static final Boolean PREFER_ONLINE = true;
	public static final String MODULE_NAME = "sigmah";
	
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
		final HashMap<Manifest, StringBuilder> manifests = new HashMap<>();
		
		final Map<String, Manifest> permutationMap = new HashMap<>();
		for(final CompilationResult compilationResult : compilationResults) {
			final String permutationName = compilationResult.getStrongName();
			
			for(final SortedMap<SelectionProperty, String> map : compilationResult.getPropertyMap()) {
				String userAgent = null;
				String locale = null;
				
				for(Map.Entry<SelectionProperty, String> entry : map.entrySet()) {
					if(entry.getKey().getName().equals("user.agent")) {
						userAgent = entry.getValue();
					}
					if(entry.getKey().getName().equals("locale")) {
						locale = entry.getValue();
					}
				}
				
				final Manifest manifest = new Manifest(userAgent, locale);
				System.out.println("      Permutation '" + permutationName + "' : " + manifest);
				
				permutationMap.put(permutationName, manifest);
				addManifest(manifests, manifest, dateFormat);
			}
		}
		
		if(permutationMap.isEmpty()) {
			addManifest(manifests, new Manifest(), dateFormat);
		}
		
		appendToAll(manifests, MODULE_NAME + ".nocache.js\n");
		appendToAll(manifests, MODULE_NAME + ".extra.nocache.js\n");
		
		for(EmittedArtifact artifact : emittedArtifacts) {
			if(EmittedArtifact.Visibility.Public.matches(artifact.getVisibility())) {
				final String partialPath = artifact.getPartialPath();
				Manifest manifest = null;
				for(Map.Entry<String, Manifest> entry : permutationMap.entrySet()) {
					if(partialPath.contains(entry.getKey())) {
						manifest = entry.getValue();
						break;
					}
				}
				
				final StringBuilder manifestBuilder = manifests.get(manifest);
				if(manifestBuilder != null) {
					manifestBuilder.append(artifact.getPartialPath())
							.append("\n");
				} else if(!partialPath.startsWith("manuals/")) {
					appendToAll(manifests, artifact.getPartialPath())
						.appendToAll(manifests, "\n");
				}
			}
		}
		
		// Generating offline fallback sources
		appendToAll(manifests, "FALLBACK:\nonline.nocache.json offline.nocache.json\n");
		artifactSet.add(emitString(logger, "{\"online\": false}", "offline.nocache.json"));
		artifactSet.add(emitString(logger, "{\"online\": true}", "online.nocache.json"));
		
		appendToAll(manifests, "is_online.nocache.js is_offline.nocache.js\n");
		artifactSet.add(emitString(logger, "window.online = false;", "is_offline.nocache.js"));
		artifactSet.add(emitString(logger, "window.online = true;", "is_online.nocache.js"));
		
		appendToAll(manifests, "export export_offline.html\n");
		artifactSet.add(emitString(logger, "<html><header><title>Sigmah</title></header><body>Export functionality is only available when online.</body></html>", "export_offline.html"));
		
		if(PREFER_ONLINE) {
			appendToAll(manifests, "SETTINGS:\nprefer-online\n");
		}

		appendToAll(manifests, "NETWORK:\n*");
	
		for(Map.Entry<Manifest, StringBuilder> entry : manifests.entrySet()) {
			artifactSet.add(emitString(logger, entry.getValue().toString(), entry.getKey().toFileName()));
		}
		return artifactSet;
	}

	private void addManifest(final HashMap<Manifest, StringBuilder> manifests, final Manifest manifest, final SimpleDateFormat dateFormat) {
		StringBuilder userAgentManifest = manifests.get(manifest);
		if(userAgentManifest == null) {
			userAgentManifest = new StringBuilder("CACHE MANIFEST\n")
					.append("# Generation date: ")
					.append(dateFormat.format(new Date()))
					.append("\n");
			manifests.put(manifest, userAgentManifest);
		}
	}

	private ManifestGenerationLinker appendToAll(Map<Manifest, StringBuilder> map, String s) {
		for(StringBuilder stringBuilder : map.values()) {
			stringBuilder.append(s);
		}
		return this;
	}
}
