package org.sigmah.shared.exception;

public class RemoveException extends CommandException {

    private static final long serialVersionUID = -1050363338292980548L;

    public static final int HAS_CHILDREN_ERR_CODE = 1;
    public static final int HAS_PROJECTS_ERR_CODE = 2;
    public static final int IS_ROOT_ERR_CODE = 4;

    private int code;

    public RemoveException() {
        super();
    }

    public RemoveException(String msg, int code) {
        super(msg);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
