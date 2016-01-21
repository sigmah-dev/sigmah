package org.sigmah.server.servlet.exporter.models;

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

import java.io.InputStream;
import java.io.OutputStream;

import javax.persistence.EntityManager;

import org.sigmah.server.domain.User;

/**
 * Handle the export and import operations of models.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr) v1.3
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) v2.0
 */
public interface ModelHandler {

	/**
	 * Read a model from the given input stream and persist it.
	 * 
	 * @param inputStream
	 *          Data stream.
	 * @param em
	 * @param authentication
	 *          Current user.
	 */
	void importModel(InputStream inputStream, EntityManager em, User user) throws Exception;

	/**
	 * Write the content of the model identified by the given properties into the given output stream.
	 * 
	 * @param outputStream
	 *          Output stream.
	 * @param properties
	 *          Map of properties, must identify a model.
	 * @param em
	 * @return The name of the exported model.
	 */
	String exportModel(OutputStream outputStream, String identifier, EntityManager em) throws Exception;
}
