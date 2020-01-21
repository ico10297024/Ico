package ico.ico.helper;

import android.content.Context;
import android.os.Handler;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import ico.ico.util.FileUtil;

public class CacheHelper {
    /** 单例对象 */
    private static volatile CacheHelper instance;
    /** 保存计算的缓存大小 */
    private long cacheSize = 0l;
    /** 存储所有需要被管理的缓存文件夹 */
    private File[] cacheFiles;
    /** 上下文 */
    private Context mContext;

    /** 获取单例的对象 */
    public static CacheHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheHelper.class) {
                if (instance == null) {
                    instance = new CacheHelper(context);
                }
            }
        }
        return instance;
    }

    private CacheHelper(Context context) {
        this.mContext = context.getApplicationContext();
        cacheFiles = new File[]{
                ImageLoader.getInstance().getDiskCache().getDirectory(), //图片缓存目录
        };
    }

    /** 计算缓存大小，并存储在内存中 */
    public void computeCacheSize(Handler handler) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                long size = 0;
                try {
                    size = FileUtil.getFolderSize(cacheFiles);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cacheSize = size;
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /** 获取缓存大小，字节单位 */
    public long getCacheSize() {
        return cacheSize;
    }

    /** 获取格式化后的缓存大小，对应ui需要显示的格式 */
    public String getCacheSize1() {
        return String.format("%.1fMB", cacheSize / 1024d / 1024d);
    }

    /** 清除缓存，通过handler发送what 0 通知完成 */
    public void clearCache(Handler handler) {
        for (int i = 0; i < cacheFiles.length; i++) {
            FileUtil.deleteFiles(cacheFiles[i], true);
        }
        handler.sendEmptyMessage(0);
    }
}
