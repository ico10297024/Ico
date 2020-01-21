package ico.ico.util;


import java.net.SocketTimeoutException;

/**
 * 自定义线程
 * 增加了结束标记和超时标记
 * 增加了同步运行的方法，需要设置超时时间
 */
public class IcoThread extends Thread {
    public IcoThread mThread;
    protected boolean exitFlag = false;
    protected boolean timeoutFlag = false;

    public IcoThread() {
        mThread = this;
    }

    /**
     * 设置该线程已运行完毕，并调用interrupt
     */
    public void close() {
        this.exitFlag = true;
        notifyContinue();
    }

    /**
     * 标志该线程已超时，并调用interrupt
     */
    public void timeout() {
        this.timeoutFlag = true;
        notifyContinue();
    }

    /** 通知继续运行 */
    public void notifyContinue() {
        if (this.getState() == State.WAITING || this.getState() == State.TIMED_WAITING) {
            synchronized (this) {
                if (this.getState() == State.WAITING || this.getState() == State.TIMED_WAITING) {
                    try {
                        this.notify();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (this.getState() == State.BLOCKED) {
            synchronized (this) {
                if (this.getState() == State.BLOCKED) {
                    try {
                        this.interrupt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 判断当前线程是否已运行结束，关闭
     *
     * @return 标志当前线程是否已运行结束
     */
    public boolean isClosed() {
        return (this.exitFlag || this.timeoutFlag || mThread.getState() == State.TERMINATED ? true : false);
    }

    /**
     * 同步执行线程
     *
     * @param timeout 设置线程运行超时时间
     * @throws SocketTimeoutException
     */
    public void execute(Long timeout) throws SocketTimeoutException {
        IcoThread.this.start();
        try {
            IcoThread.this.join(timeout);
        } catch (InterruptedException e) {
            log.e("sleep-->" + e.toString(), mThread.getClass().getSimpleName(), "execute");
            // e.printStackTrace();
        }
        if (IcoThread.this.timeoutFlag) {
            throw new SocketTimeoutException();
        }
    }
}
