package org.sigmah.server.security;

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
