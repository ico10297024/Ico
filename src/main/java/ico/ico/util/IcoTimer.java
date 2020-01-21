package ico.ico.util;

import java.util.Timer;

/**
 * 自定义定时器
 * 增加结束标记
 */
public class IcoTimer extends Timer {
    protected boolean exitFlag = false;
    protected boolean timeoutFlag = false;

    /**
     * 设置该线程已运行完毕，并调用cancel
     */
    public void close() {
//        Log.e("ico", "state-->" + this.getState());
        this.exitFlag = true;
        this.cancel();
    }

    /**
     * 判断当前的计时器是否已执行完毕，请在异步任务的结束处调用该方法
     *
     * @return boolean 标志该定时器是否已执行完毕
     */
    public boolean isClosed() {
        return (this.exitFlag ? true : false);
    }
}
