package ico.ico.helper;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import ico.ico.widget.SimpleTextWatcher;

/**
 * 针对android中一些编辑框具有清空按钮，提供的一个帮助类
 * <p>
 * 使用{@link ClearTextWatcherHelper#bind(EditText, View)}对两者进行绑定
 */
public class ClearTextWatcherHelper {

    /**
     * 对editText绑定一个文本观察器，同时对清除按钮设置点击事件
     *
     * @param editText
     * @param v
     */
    public static void bind(EditText editText, View v) {
        ClearTextWatcher watcher = new ClearTextWatcher(editText, v);
        editText.addTextChangedListener(watcher);
        v.setOnClickListener(watcher);
    }

    private static class ClearTextWatcher extends SimpleTextWatcher implements View.OnClickListener {
        private EditText mEditText;
        private View v;

        private ClearTextWatcher(EditText editText, View v) {
            this.mEditText = editText;
            this.v = v;
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 0) {
                v.setVisibility(View.INVISIBLE);
            } else {
                v.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            mEditText.setText("");
            v.setVisibility(View.INVISIBLE);
        }
    }
}
