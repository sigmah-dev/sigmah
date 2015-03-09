package org.sigmah.client.ui.view.admin.users;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.cellview.client.TreeNode;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;
import com.google.gwt.view.client.TreeViewModel;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * Tree of user permissions.
 * 
 * @author Renato Almeida (renatoaf.ufcg@gmail.com)
 */
public class PermissionTree extends CellTree {
	public static class CategoryCell extends AbstractCell<GlobalPermissionEnum.GlobalPermissionCategory> {
		@Override
		public void render(Context context, GlobalPermissionEnum.GlobalPermissionCategory value, SafeHtmlBuilder sb) {
			if (value != null) {
				sb.appendEscaped(GlobalPermissionEnum.GlobalPermissionCategory.getName(value));
			}
		}
	}
	
	public static class PermissionCell extends CompositeCell<GlobalPermissionEnum> {
		public PermissionCell(MultiSelectionModel<GlobalPermissionEnum> selectionModel) {
			super(createCells(selectionModel));
		}

		@Override
		public void render(Context context, GlobalPermissionEnum value,
				SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<table><tbody><tr>");
			super.render(context, value, sb);
			sb.appendHtmlConstant("</tr></tbody></table>");
		}

		@Override
		protected Element getContainerElement(Element parent) {
			// Return the first TR element in the table.
			return parent.getFirstChildElement().getFirstChildElement()
					.getFirstChildElement();
		}

		@Override
		protected <X> void render(Context context, GlobalPermissionEnum value,
				SafeHtmlBuilder sb, HasCell<GlobalPermissionEnum, X> hasCell) {
			Cell<X> cell = hasCell.getCell();
			sb.appendHtmlConstant("<td>");
			cell.render(context, hasCell.getValue(value), sb);
			sb.appendHtmlConstant("</td>");
		}

		private static List<HasCell<GlobalPermissionEnum, ?>> createCells(final MultiSelectionModel<GlobalPermissionEnum> selectionModel) {
			List<HasCell<GlobalPermissionEnum, ?>> cells = new LinkedList<HasCell<GlobalPermissionEnum,?>>();
			cells.add(new HasCell<GlobalPermissionEnum, Boolean>() {
				@Override
				public Cell<Boolean> getCell() {
					return new CheckboxCell();
				}

				@Override
				public FieldUpdater<GlobalPermissionEnum, Boolean> getFieldUpdater() {
					return null;
				}

				@Override
				public Boolean getValue(GlobalPermissionEnum object) {
					return selectionModel.isSelected(object);
				}
			});
			cells.add(new HasCell<GlobalPermissionEnum, String>() {
				@Override
				public Cell<String> getCell() {
					return new TextCell();
				}

				@Override
				public FieldUpdater<GlobalPermissionEnum, String> getFieldUpdater() {
					return null;
				}

				@Override
				public String getValue(GlobalPermissionEnum object) {
					return GlobalPermissionEnum.getName(object);
				}
			});
			return cells;
		}
	}

	public static class PermissionTreeModel implements TreeViewModel {
		private final DefaultSelectionEventManager<GlobalPermissionEnum> selectionManager = DefaultSelectionEventManager.createCheckboxManager();
		
		private MultiSelectionModel<GlobalPermissionEnum> selectionModel = new MultiSelectionModel<GlobalPermissionEnum>(new ProvidesKey<GlobalPermissionEnum>() {
			@Override
			public Object getKey(GlobalPermissionEnum item) {
				return item.name();
			}
		});
		
		public PermissionTreeModel() {
			selectionModel.addSelectionChangeHandler(new Handler() {
				@Override
				public void onSelectionChange(SelectionChangeEvent event) {
					for (GlobalPermissionEnum permission : GlobalPermissionEnum.values()) {
						boolean selected = selectionModel.isSelected(permission);

						GlobalPermissionEnum parent = permission.getParent();
						while (parent != null) {
							selected = selected && selectionModel.isSelected(parent);
							parent = parent.getParent();
						}
						
						selectionModel.setSelected(permission, selected);
					}
				}
			});
		}
		
		public MultiSelectionModel<GlobalPermissionEnum> getSelectionModel() {
			return selectionModel;
		}
		
		@Override
		public <T> NodeInfo<?> getNodeInfo(T value) {
		    if (value == null) {
				ListDataProvider<GlobalPermissionEnum.GlobalPermissionCategory> categories = new ListDataProvider<GlobalPermissionEnum.GlobalPermissionCategory>();
				for (GlobalPermissionEnum.GlobalPermissionCategory category : GlobalPermissionEnum.GlobalPermissionCategory.values()) {
					if (!category.isEmpty()) {
						categories.getList().add(category);
					}
				}
		        return new DefaultNodeInfo<GlobalPermissionEnum.GlobalPermissionCategory>(categories, new CategoryCell());
				
		    } else if (value instanceof GlobalPermissionEnum.GlobalPermissionCategory) {
		    	GlobalPermissionEnum.GlobalPermissionCategory category = (GlobalPermissionEnum.GlobalPermissionCategory) value;
				ListDataProvider<GlobalPermissionEnum> rootPermissions = new ListDataProvider<GlobalPermissionEnum>(category.getChildren());
				return new DefaultNodeInfo<GlobalPermissionEnum>(rootPermissions, new PermissionCell(selectionModel), selectionModel, selectionManager, null);
				
			} else if (value instanceof GlobalPermissionEnum) {
		    	GlobalPermissionEnum permission = (GlobalPermissionEnum) value;
				ListDataProvider<GlobalPermissionEnum> childPermissions = new ListDataProvider<GlobalPermissionEnum>(permission.getChildren());
				return new DefaultNodeInfo<GlobalPermissionEnum>(childPermissions, new PermissionCell(selectionModel), selectionModel, selectionManager, null);
			}
		    throw new IllegalArgumentException("Unknown value.");
		}

		@Override
		public boolean isLeaf(Object value) {
			if (value instanceof GlobalPermissionEnum) {
				GlobalPermissionEnum permission = (GlobalPermissionEnum) value;
				return permission.isLeaf();
			}
			return false;
		}
	}

	private static Resources resources;
	
	private PermissionTreeModel model;
	
	public PermissionTree() {
		this(new PermissionTreeModel());
	}
	
	public PermissionTree(PermissionTreeModel model) {
		super(model, null, createResources());
		this.model = model;
	}

	public void expandAll() {
		setOpen(getRootTreeNode(), true);
	}
	
	public void collapseAll() {
		setOpen(getRootTreeNode(), false);
	}
	
	private void setOpen(TreeNode node, boolean open) {
		if (node != null) {
			for (int i = 0; i < node.getChildCount(); i++) {
				if (!node.isChildLeaf(i)) {
					setOpen(node.setChildOpen(i, open), open);
				}
			}
		}
	}

	public PermissionTreeModel getModel() {
		return model;
	}
	
	public void setPermissions(Set<GlobalPermissionEnum> permissions) {
		for (GlobalPermissionEnum permission : permissions) {
			getModel().getSelectionModel().setSelected(permission, true);
		}
	}
	
	public void setPermission(GlobalPermissionEnum permission, boolean active) {
		getModel().getSelectionModel().setSelected(permission, active);
	}
	
	public Set<GlobalPermissionEnum> getPermissions() {
		return getModel().getSelectionModel().getSelectedSet();
	}
	
	public void clear() {
		getModel().getSelectionModel().clear();
	}
	
	private static Resources createResources() {
		if (resources == null) {
		    resources = GWT.create(Resources.class);
		    StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeItem() + " {padding-top: 2px; padding-bottom: 0px}");
		    StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
		    StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
		    StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeSelectedItem() + " {background:none !important; color:#4B3E2D;}"); // TODO
		}
		return resources;
	}
}
