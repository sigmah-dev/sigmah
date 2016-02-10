/**
 * <p>
 * Data Access Objects for {@link org.sigmah.server.domain} classes.
 * </p>
 * <p>
 * There is some debate (see here: {@code http://www.infoq.com/news/2007/09/jpa-dao}) as to whether a Data Access layer
 * is strictly necessary given the existing level of abstraction of the JPA EntityManager interface.
 * </p>
 * <p>
 * This is probably valid in some cases, but here there are enough complicated queries that its worth centralizing them
 * in one place so that multiple CommandHandlers can share this code.
 * </p>
 * <p>
 * Also, most of the boiler plat... ?
 * </p>
 * 
 * See {@code http://www.infoq.com/news/2007/09/jpa-dao}
 */
package org.sigmah.server.dao;

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
