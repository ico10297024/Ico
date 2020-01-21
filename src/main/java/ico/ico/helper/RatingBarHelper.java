package ico.ico.helper;

import android.widget.ImageView;

/**
 * 评分工具的帮助类
 * <p>
 * 由于自带的控件RatingBar扩展性低，每一个级别的图片不会跟imageView一样进行缩放，并且间距需要通过图片来进行控制
 * <p>
 * 所以通常我会使用layout包裹多个imageview，然后通过业务逻辑来实现级别的UI显示
 * <p>
 * 这个工具类默认一个级别为一个图片，没有半个图片的情况
 */
public class RatingBarHelper {

    private int level = 0;
    private ImageView[] levels = null;
    private int imageRes1, imageRes0;

    public RatingBarHelper(ImageView[] levels, int imageRes1, int imageRes0) {
        this.levels = levels;
        this.imageRes1 = imageRes1;
        this.imageRes0 = imageRes0;
    }

    public int getLevel() {
        return level;
    }

    public RatingBarHelper setLevel(int level) {
        this.level = level;
        for (int i = 0; i < levels.length; i++) {
            if (i < level) {
                levels[i].setImageResource(imageRes1);
            } else {
                levels[i].setImageResource(imageRes0);
            }
        }
        return this;
    }
}
