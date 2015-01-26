package org.sigmah.shared.command;

import org.sigmah.shared.command.result.VoidResult;

public class RemoveOrgUnit implements Command<VoidResult> {

    private static final long serialVersionUID = 1L;

    private int id;

    public RemoveOrgUnit() {
        // serialization.
    }

    public RemoveOrgUnit(int id) {
        super();
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
