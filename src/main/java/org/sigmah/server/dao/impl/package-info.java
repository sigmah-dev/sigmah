/**
 * JPA/Hibernate implementation of the Data Access layer
 *
 * The goal is to use the Java Persistence (JPA) API as much as possible,
 * not because there's any interest in replacing Hibernate, but it makes it easier
 * to leverage third-party tools targeting JPA and leverage existing knowledge of
 * people working on the code base.
 *
 * There are still Hibernate-specific things that we use like the Criterion API and
 * Filters which are not (yet) part of JPA.
 */
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
