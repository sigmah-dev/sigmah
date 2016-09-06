package org.sigmah.shared.computation.dependency;

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

import com.google.gwt.core.client.GWT;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.util.ValueResultUtils;

/**
 * Dependency to the given or received contribution throught funding links.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class ContributionDependency implements Dependency {
	
	public static final String REFERENCE = "@contribution";
	
	private Scope scope;
	
	private Integer projectModelId;

	/**
	 * Empty constructor, required for serialization.
	 */
	public ContributionDependency() {
		// Empty.
	}

	public ContributionDependency(final Scope scope) {
		this.scope = scope;
		
		final String modelName = scope.getModelName();
		if (modelName != null && modelName.length() > 1 && modelName.charAt(0) == Instructions.ID_PREFIX) {
			final String[] parts = modelName.split(ValueResultUtils.BUDGET_VALUE_SEPARATOR);
			if (parts.length == 2) {
				try {
					projectModelId = Integer.parseInt(parts[0].substring(1));
					scope.setModelName(parts[1]);
				} catch (NumberFormatException e) {
					GWT.log("Given model name starts by the identifier prefix but is not an identifier: " + modelName, e);
				}
			}
		}
	}

	public Scope getScope() {
		return scope;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isResolved() {
		return scope.getModelName() == null || projectModelId != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
		
		if (projectModelId != null) {
			stringBuilder.append(Instructions.ID_PREFIX)
					.append(projectModelId)
					.append(ValueResultUtils.BUDGET_VALUE_SEPARATOR);
		}
		stringBuilder.append(scope.getModelName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(REFERENCE);
		
		return stringBuilder.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toHumanReadableString() {
		return new StringBuilder()
				.append(scope.getLinkedProjectTypeName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(scope.getModelName())
				.append(ValueResultUtils.DEFAULT_VALUE_SEPARATOR)
				.append(REFERENCE)
				.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(final DependencyVisitor visitor) {
		visitor.visit(this);
	}

	public void setProjectModelId(final Integer projectModelId) {
		this.projectModelId = projectModelId;
	}

	public Integer getProjectModelId() {
		return projectModelId;
	}
	
}
