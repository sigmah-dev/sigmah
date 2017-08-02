package org.sigmah.client.computation;

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.GetValueFromLinkedProjects;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.computation.ValueResolver;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.DependencyVisitor;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.computation.value.CollectionValue;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.computation.value.DoubleValue;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.util.Collections;

/**
 * Client implementation of {@link ValueResolver}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class ClientValueResolver implements ValueResolver {

	@Inject
	private DispatchAsync dispatch;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resolve(final Collection<Dependency> dependencies, final int containerId, final Integer layoutGroupIterationId, final AsyncCallback<Map<Dependency, ComputedValue>> callback) {
		
		final BatchCommand batchCommand = new BatchCommand();
		final Map<Command<?>, Dependency> commandToDependencyMap = new HashMap<Command<?>, Dependency>();
		
		for (final Dependency dependency : dependencies) {
			dependency.accept(new DependencyVisitor() {
				
				@Override
				public void visit(SingleDependency dependency) {
					final FlexibleElementDTO element = dependency.getFlexibleElement();
					
					// TODO: Should support core versions.
					final GetValue command = new GetValue(containerId, element.getId(), element.getEntityName());
					
					commandToDependencyMap.put(command, dependency);
					batchCommand.add(command);
				}

				@Override
				public void visit(CollectionDependency dependency) {
					final GetValueFromLinkedProjects command = new GetValueFromLinkedProjects(containerId, dependency.getScope().getLinkedProjectType(), dependency.getFlexibleElement());
					
					commandToDependencyMap.put(command, dependency);
					batchCommand.add(command);
				}

				@Override
				public void visit(ContributionDependency dependency) {
					final GetLinkedProjects command = new GetLinkedProjects(containerId, dependency.getScope().getLinkedProjectType(), ProjectDTO.Mode.BASE);
					
					commandToDependencyMap.put(command, dependency);
					batchCommand.add(command);
				}
			});
		}
		
		dispatch.execute(batchCommand, new SuccessCallback<ListResult<Result>>(callback) {

			@Override
			public void onSuccess(final ListResult<Result> result) {
				callback.onSuccess(getComputedValueMap(batchCommand, result, commandToDependencyMap));
			}

		});
	}
	
	/**
	 * Creates a map associating each dependency to its value.
	 * 
	 * @param batchCommand
	 *			Batch command executed.
	 * @param results
	 *			Results of the given batch command.
	 * @param commandToDependencyMap
	 *			Association between a command and its dependency.
	 * @return A new map associating each dependency to its value.
	 * @throws UnsupportedOperationException If an unknown command is given.
	 */
	private Map<Dependency, ComputedValue> getComputedValueMap(final BatchCommand batchCommand, final ListResult<Result> results, final Map<Command<?>, Dependency> commandToDependencyMap) throws UnsupportedOperationException {
		
		final Map<Dependency, ComputedValue> values = new HashMap<Dependency, ComputedValue>();
		
		final int size = results.getSize();
		for (int index = 0; index < size; index++) {
			final Command<?> command = batchCommand.getCommands().get(index);
			final ComputedValue computedValue;

			if (command instanceof GetValue) {
				computedValue = getComputedValueForSingleDependency(results, index);
			}
			else if(command instanceof GetLinkedProjects) {
				computedValue = getComputedValueForContributionDependency(results, index);
			}
			else if (command instanceof GetValueFromLinkedProjects) {
				computedValue = getComputedValueForCollectionDependency(results, index);
			}
			else {
				throw new UnsupportedOperationException("Command type '" + command.getClass() + "' is not supported.");
			}
			values.put(commandToDependencyMap.get(command), computedValue);
		}
		
		return values;
	}
	
	/**
	 * Transform the result at the given index to a <code>ComputedValue</code>
	 * suitable for a {@link SingleDependency}.
	 * 
	 * @param results
	 *			List of results.
	 * @param index
	 *			Index of the result to use.
	 * @return A new <code>ComputedValue</code>.
	 */
	private ComputedValue getComputedValueForSingleDependency(ListResult<Result> results, int index) {
		
		final ValueResult valueResult = (ValueResult) results.getList().get(index);
		return ComputedValues.from(valueResult);
	}
	
	/**
	 * Transform the result at the given index to a <code>ComputedValue</code>
	 * suitable for a {@link ContributionDependency}.
	 * 
	 * @param results
	 *			List of results.
	 * @param index
	 *			Index of the result to use.
	 * @return A new <code>ComputedValue</code>.
	 */
	private ComputedValue getComputedValueForContributionDependency(ListResult<Result> results, int index) {
		
		@SuppressWarnings("unchecked")
		final ListResult<ProjectFundingDTO> projectFundings = (ListResult<ProjectFundingDTO>) results.getList().get(index);
		
		final List<ComputedValue> computedValues = new ArrayList<ComputedValue>();
		for (final ProjectFundingDTO projectFunding : projectFundings.getList()) {
			final Double contribution = projectFunding.getPercentage();
			if (contribution != null) {
				computedValues.add(new DoubleValue(contribution));
			}
		}
		return new CollectionValue(computedValues);
	}
	
	/**
	 * Transform the result at the given index to a <code>ComputedValue</code>
	 * suitable for a {@link CollectionDependency}.
	 * 
	 * @param results
	 *			List of results.
	 * @param index
	 *			Index of the result to use.
	 * @return A new <code>ComputedValue</code>.
	 */
	private ComputedValue getComputedValueForCollectionDependency(ListResult<Result> results, int index) {
		
		@SuppressWarnings("unchecked")
		final ListResult<String> strings = (ListResult<String>) results.getList().get(index);
		
		final List<ComputedValue> computedValues = Collections.map(strings.getList(), new Collections.Mapper<String, ComputedValue>() {

			@Override
			public ComputedValue forEntry(String entry) {
				return ComputedValues.from(entry);
			}

		});
		return new CollectionValue(computedValues);
	}
	
}
