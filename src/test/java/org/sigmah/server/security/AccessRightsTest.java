package org.sigmah.server.security;

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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * Access rights tests.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class AccessRightsTest {

	/**
	 * Tests the {@code contains} logic used with permissions collections.
	 */
	@Test
	public void testContainsPermissions() {

		final Set<GlobalPermissionEnum> permissions = new HashSet<GlobalPermissionEnum>();
		permissions.add(GlobalPermissionEnum.CREATE_PROJECT);
		permissions.add(GlobalPermissionEnum.EDIT_PROJECT);

		Assert.assertTrue(CollectionUtils.containsAll(permissions, set()));
		Assert.assertTrue(CollectionUtils.containsAll(permissions, set((GlobalPermissionEnum) null)));
		Assert.assertTrue(CollectionUtils.containsAll(permissions, set((GlobalPermissionEnum[]) null)));
		Assert.assertTrue(CollectionUtils.containsAll(permissions, set(GlobalPermissionEnum.CREATE_PROJECT)));
		Assert.assertTrue(CollectionUtils.containsAll(permissions, set(GlobalPermissionEnum.EDIT_PROJECT)));
		Assert.assertTrue(CollectionUtils.containsAll(permissions, set(GlobalPermissionEnum.CREATE_PROJECT, GlobalPermissionEnum.EDIT_PROJECT)));

		Assert.assertFalse(CollectionUtils.containsAll(permissions,
			set(GlobalPermissionEnum.CREATE_PROJECT, GlobalPermissionEnum.EDIT_PROJECT, GlobalPermissionEnum.DELETE_PROJECT)));

		Assert.assertFalse(CollectionUtils.containsAll(permissions,
			set(GlobalPermissionEnum.CREATE_PROJECT, GlobalPermissionEnum.DELETE_PROJECT, GlobalPermissionEnum.VIEW_ADMIN)));

		Assert.assertFalse(CollectionUtils.containsAll(permissions, set(GlobalPermissionEnum.DELETE_PROJECT, GlobalPermissionEnum.VIEW_ADMIN)));
	}

	/**
	 * Transforms the given {@code gpes} into a {@code Set} collection.<br/>
	 * {@code null} values are ignored.
	 * 
	 * @param gpes
	 *          The persmission(s), may be {@code null} or contain {@code null} value(s).
	 * @return the given {@code gpes} transformed into a {@code Set} collection.
	 */
	private static Collection<GlobalPermissionEnum> set(final GlobalPermissionEnum... gpes) {

		final Set<GlobalPermissionEnum> permissions = new HashSet<GlobalPermissionEnum>();

		if (gpes != null) {
			for (final GlobalPermissionEnum gpe : gpes) {
				if (gpe == null) {
					continue;
				}
				permissions.add(gpe);
			}
		}
		return permissions;
	}

}
