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