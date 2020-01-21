package ico.ico.exception;

/**
 * @包名 sunrun.szogemray.mgr
 * @作者 ico
 * @创建日期 2015/1/21 0021
 * @版本 V 1.0
 */

public class IcoMethodParamException extends Exception {

    public IcoMethodParamException() {
        super("方法参数错误");
    }

    public IcoMethodParamException(String detailMessage) {
        super(detailMessage);
    }

    public IcoMethodParamException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public IcoMethodParamException(Throwable throwable) {
        super(throwable);
    }
}
