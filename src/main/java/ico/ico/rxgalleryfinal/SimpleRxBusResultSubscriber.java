package ico.ico.rxgalleryfinal;

import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.BaseResultEvent;

/**
 * Created by root on 17-6-19.
 */
public class SimpleRxBusResultSubscriber extends RxBusResultDisposable<BaseResultEvent> {

    @Override
    protected void onEvent(BaseResultEvent baseResultEvent) throws Exception {
    }
}