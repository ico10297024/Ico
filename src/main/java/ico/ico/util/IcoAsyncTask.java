package ico.ico.util;

import android.os.AsyncTask;

/**
 * Created by ico on 2015/3/8 0008.
 */
public abstract class IcoAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {
    protected boolean exitFlag = false;
    protected boolean timeoutFlag = false;

    /**
     * 关闭任务
     *
     * @param cancel 是否调用cancel方法
     */
    public void close(boolean cancel) {
//        Log.e("ico", "state-->" + this.getState());
        this.exitFlag = true;
        if (cancel) {
            this.cancel(true);
        }
    }

    public void timeout() {
        this.timeoutFlag = true;
    }

    public boolean isClosed() {
        return (this.exitFlag || this.timeoutFlag ? true : false);
    }
}
