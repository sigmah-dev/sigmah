package org.sigmah.shared.exception;

public class MoveException extends CommandException {

    private static final long serialVersionUID = -5464441274688027687L;

    public static final int CYCLE_ERR_CODE = 1;
    public static final int ROOT_ERR_CODE = 2;
    public static final int ITSELF_ERR_CODE = 4;

    private int code;

    public MoveException() {
        super();
    }

    public MoveException(String msg, int code) {
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
