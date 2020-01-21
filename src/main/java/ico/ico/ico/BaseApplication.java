package ico.ico.ico;

import android.app.Application;

import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import ico.ico.util.Common;
import ico.ico.util.log;

//
//                            _ooOoo_
//                           o8888888o
//                           88" . "88
//                           (| -_- |)
//                            O\ = /O
//                        ____/`---'\____
//                      .   ' \\| |// `.
//                       / \\||| : |||// \
//                     / _||||| -:- |||||- \
//                       | | \\\ - /// | |
//                     | \_| ''\---/'' | |
//                      \ .-\__ `-` ___/-. /
//                   ___`. .' /--.--\ `. . __
//                ."" '< `.___\_<|>_/___.' >'"".
//               | | : `- \`.;`\ _ /`;.`/ - ` : | |
//                 \ \ `-. \_ __\ /__ _/ .-` / /
//         ======`-.____`-.___\_____/___.-`____.-'======
//                            `=---='
//
//         .............................................
//                  佛祖镇楼                  BUG辟易
//          佛曰:
//                  写字楼里写字间，写字间里程序员；
//                  程序人员写程序，又拿程序换酒钱。
//                  酒醒只在网上坐，酒醉还来网下眠；
//                  酒醉酒醒日复日，网上网下年复年。
//                  但愿老死电脑间，不愿鞠躬老板前；
//                  奔驰宝马贵者趣，公交自行程序员。
//                  别人笑我忒疯癫，我笑自己命太贱；
//                  不见满街漂亮妹，哪个归得程序员？
public class BaseApplication extends Application {
    /**
     * 自身
     */
    private static BaseApplication APPLICATION;
    private String localMac = "00:00:00:00:00:02";

    public static BaseApplication getInstance() {
        return APPLICATION;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApplication.APPLICATION = this;
        log.w("全局变量开始启动！", BaseApplication.class.getSimpleName(), "onCreate");
        //自定义的崩溃处理，在SD卡内做崩溃记录
        CrashHandler crashHandler = new CrashHandler(BaseApplication.APPLICATION);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        //初始化imageloader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                getApplicationContext())
//                .memoryCacheExtraOptions(480, 800) // 保存每个缓存图片的最大长和宽
                .threadPoolSize(3) // 线程池的大小 这个其实默认就是3
                .threadPriority(Thread.NORM_PRIORITY - 2)//设置线程优先级
                .denyCacheImageMultipleSizesInMemory() // 当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)// 设置缓存的最大字节
                .tasksProcessingOrder(QueueProcessingType.LIFO)//设置图片下载和显示的工作队列排序
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//                .imageDownloader(
//                        new BaseImageDownloader(getApplicationContext(),
//                                5 * 1000, 30 * 1000))// connectTimeout 超时时间
//                .writeDebugLogs()
                .imageDownloader(new AuthImageDownloader(getApplicationContext()))//支持HTTPS协议
                .build();// 开始构建
        ImageLoader.getInstance().init(config);// 全局初始化此配置
        ImageLoader.getInstance().clearDiskCache();
        ImageLoader.getInstance().clearMemoryCache();
        //获取mac地址保存
        initLocalMac();
    }

    /**
     * 由于安卓的碎片化,再加上不打开wifi难以获取到设备的mac地址
     * 所以在程序启动时获取mac地址进行保存
     */
    public void initLocalMac() {
        new Thread() {
            @Override
            public void run() {
                Common.getLocalMac(BaseApplication.getInstance(), new Common.LocalMacCallback() {
                    @Override
                    public void onLocalMac(String result) {
                        log.w("localMac = " + result);
                        setLocalMac(result);
                    }
                });
            }
        }.start();
    }

    public String getLocalMac() {
        return localMac;
    }

    public void setLocalMac(String localMac) {
        this.localMac = localMac;
    }
}
