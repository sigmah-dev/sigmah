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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.GetValue;
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
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

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
	public void resolve(final Collection<Dependency> dependencies, final int containerId, final AsyncCallback<Map<Dependency, ComputedValue>> callback) {
		final BatchCommand batchCommand = new BatchCommand();
		final HashMap<Integer, Dependency> elementIdToDependencyMap = new HashMap<Integer, Dependency>();
		
		for (final Dependency dependency : dependencies) {
			dependency.accept(new DependencyVisitor() {
				
				@Override
				public void visit(SingleDependency dependency) {
					final FlexibleElementDTO element = dependency.getFlexibleElement();
					elementIdToDependencyMap.put(element.getId(), dependency);
					
					// TODO: Should support core versions.
					batchCommand.add(new GetValue(containerId, element.getId(), element.getEntityName()));
				}

				@Override
				public void visit(CollectionDependency dependency) {
					throw new UnsupportedOperationException("Not supported yet.");
				}

				@Override
				public void visit(ContributionDependency dependency) {
					batchCommand.add(new GetLinkedProjects(containerId, dependency.getScope().getLinkedProjectType(), ProjectDTO.Mode.BASE));
				}
			});
		}
		
		dispatch.execute(batchCommand, new AsyncCallback<ListResult<Result>>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ListResult<Result> result) {
				final HashMap<Dependency, ComputedValue> values = new HashMap<Dependency, ComputedValue>();
				
				final int size = result.getSize();
				for (int index = 0; index < size; index++) {
					final Command<?> command = batchCommand.getCommands().get(index);
					if (command instanceof GetValue) {
						final GetValue getValue = (GetValue) batchCommand.getCommands().get(index);
						final ValueResult valueResult = (ValueResult) result.getList().get(index);

						values.put(elementIdToDependencyMap.get(getValue.getElementId()), ComputedValues.from(valueResult));
					}
					else if(command instanceof GetLinkedProjects) {
						// TODO: À continuer.
					}
				}
				
				callback.onSuccess(values);
			}
			
		});
	}
	
	
	
}
