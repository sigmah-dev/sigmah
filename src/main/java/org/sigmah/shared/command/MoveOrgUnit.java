package org.sigmah.shared.command;

import org.sigmah.shared.command.result.VoidResult;

public class MoveOrgUnit implements Command<VoidResult> {

    private static final long serialVersionUID = 1L;

    private int id;

    private int parentId;

    public MoveOrgUnit() {
        // serialization.
    }

    public MoveOrgUnit(int id, int parentId) {
        super();
        this.id = id;
        this.parentId = parentId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentId() {
        return parentId;
    }
}
