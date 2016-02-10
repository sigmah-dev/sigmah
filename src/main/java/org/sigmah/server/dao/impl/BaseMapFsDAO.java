package org.sigmah.server.dao.impl;

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


import com.google.inject.Inject;

import org.sigmah.server.dao.BaseMapDAO;
import org.sigmah.shared.dto.map.BaseMap;
import org.sigmah.shared.dto.map.LocalBaseMap;

import java.io.File;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads a list of base maps (tile sets) from a path on the local disk Searches the following three paths:
 * <ul>
 * <li>The path specified by the "basemaps.root" property in tomcat/conf/activityinfo.conf</li>
 * <li>e:\tiles</li>
 * <li>c:\tiles</li>
 * </ul>
 * Ultimately needs to be replaced by a database table with URLs to WMS/TMS services.
 * 
 * @author Alex Bertram
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public class BaseMapFsDAO implements BaseMapDAO {

	private final static Logger LOGGER = LoggerFactory.getLogger(BaseMapFsDAO.class);
	private final Map<String, BaseMap> baseMaps;

	@Inject
	public BaseMapFsDAO(Properties serverProperties) {
		baseMaps = new HashMap<String, BaseMap>();

		// What about remote sources? This should probably be moved into the
		// database
		File tileRoot = null;
		if (serverProperties.getProperty("basemaps.root") != null) {
			tileRoot = new File(serverProperties.getProperty("basemaps.root"));
			if (!tileRoot.exists()) {
				LOGGER.warn("Base map folder specified in properties at " + tileRoot.getAbsolutePath() + " does not exist");
				return;
			}
		} else {
			LOGGER.warn("No basemap root set, trying defaults at c:\\tiles and e:\\tiles");
			tileRoot = new File("e://tiles");
			if (!tileRoot.exists()) {
				tileRoot = new File("c://tiles"); // for the development machine
			}
			if (!tileRoot.exists()) {
				LOGGER.warn("Could not find basemaps folder anywhere!");
				return;
			}
		}

		for (File tileSet : tileRoot.listFiles()) {
			if (tileSet.isDirectory()) {

				LocalBaseMap baseMap = new LocalBaseMap();
				baseMap.setId(tileSet.getName());
				baseMap.setVersion(getLatestVersion(tileSet));
				baseMap.setMinZoom(Integer.MAX_VALUE);
				baseMap.setMaxZoom(Integer.MIN_VALUE);

				baseMap.setTileRoot(tileRoot.getAbsolutePath());

				for (String file : tileSet.list()) {
					if (file.endsWith(".name")) {
						baseMap.setName(file.substring(0, file.length() - 5));
					} else if (file.endsWith(".copyright")) {
						baseMap.setCopyright(file.substring(0, file.length() - ".copyright".length()));
					}
				}

				if (baseMap.getVersion() != -1) {

					File versionFolder = new File(tileSet.getAbsolutePath() + "/v" + baseMap.getVersion());
					for (File zoomLevel : versionFolder.listFiles()) {
						if (zoomLevel.isDirectory() && zoomLevel.getName().startsWith("z")) {
							int level = Integer.parseInt(zoomLevel.getName().substring(1));
							if (level > baseMap.getMaxZoom()) {
								baseMap.setMaxZoom(level);
							}
							if (level < baseMap.getMinZoom()) {
								baseMap.setMinZoom(level);
							}
						}
					}

					baseMaps.put(baseMap.getId(), baseMap);
				}
			}
		}

	}

	private int getLatestVersion(File tileSet) {
		int latestVersion = -1;
		for (File file : tileSet.listFiles()) {
			if (file.isDirectory() && file.getName().startsWith("v")) {
				int version = Integer.parseInt(file.getName().substring(1));
				if (version > latestVersion) {
					latestVersion = version;
				}
			}
		}
		return latestVersion;
	}

	public BaseMap getBaseMap(String id) {
		return baseMaps.get(id);
	}

	public List<BaseMap> getBaseMaps() {
		return new ArrayList<BaseMap>(baseMaps.values());
	}
}
