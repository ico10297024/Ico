package ico.ico.exception;

/**
 * @包名 sunrun.szogemray.mgr
 * @作者 ico
 * @创建日期 2015/1/21 0021
 * @版本 V 1.0
 */

public class IcoSocketCannotCreateException extends Exception {

    public IcoSocketCannotCreateException() {
        super("Socket无法被创建!");
    }

    public IcoSocketCannotCreateException(String detailMessage) {
        super(detailMessage);
    }

    public IcoSocketCannotCreateException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IcoSocketCannotCreateException(Throwable throwable) {
        super(throwable);
    }
}
