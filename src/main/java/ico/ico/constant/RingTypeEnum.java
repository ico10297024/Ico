package ico.ico.constant;

import android.content.Context;

/**
 * 该枚举列举了几种铃声设置
 * {@link ico.ico.util.Common#setRing(Context, String, RingTypeEnum)}
 */
public enum RingTypeEnum {
    /** 所有铃声 */
    ALL(0)
    /** 手机铃声 */
    , RINGTONE(1)
    /** 手机闹铃 */
    , ALARM(2)
    /** 通知铃声 */
    , NOTIFY(3);

    int index;

    RingTypeEnum(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
