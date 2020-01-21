package ico.ico.helper;

import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;

import ico.ico.widget.SimpleTextWatcher;

/**
 * 文本限制工具,可以限制文本的长度或输入的数据
 * <p>
 * limit限制文本长度，默认长度30，等同QQ昵称
 * <p>
 * format限制输入的数据,若输入的数据不符合格式则将自动删除
 */
public class LimitTextWatcherHelper {

    public static void bind(EditText editText) {
        LimitTextWatcher watcher = new LimitTextWatcher(editText);
        editText.addTextChangedListener(watcher);
    }

    public static void bind(EditText editText, int limit) {
        LimitTextWatcher watcher = new LimitTextWatcher(editText, limit);
        editText.addTextChangedListener(watcher);
    }

    public static void bind(EditText editText, Integer limit, String format) {
        LimitTextWatcher watcher = new LimitTextWatcher(editText, limit, format);
        editText.addTextChangedListener(watcher);
    }

    static class LimitTextWatcher extends SimpleTextWatcher {
        private int mLimit = 30;
        private String format;
        private String beforeText;
        private int beforeSelection;

        private EditText mEditText;

        private LimitTextWatcher(EditText editText) {
            this(editText, 30);
        }

        private LimitTextWatcher(EditText editText, int limit) {
            this.mEditText = editText;
            this.mLimit = limit;
        }

        private LimitTextWatcher(EditText editText, Integer limit, String format) {
            this.mEditText = editText;
            if (limit != null && limit >= 0) {
                this.mLimit = limit;
            }
            this.format = format;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            super.beforeTextChanged(s, start, count, after);
            beforeText = s.toString();
            beforeSelection = mEditText.getSelectionStart();
        }

        @Override
        public void afterTextChanged(Editable s) {
            super.afterTextChanged(s);
            if (s.toString().equalsIgnoreCase(beforeText)) {
                return;
            }
            //超出限制
            if (s.toString().getBytes().length > mLimit) {
                mEditText.removeTextChangedListener(this);
                mEditText.setText(beforeText);
                if (beforeSelection >= beforeText.length()) {
                    mEditText.setSelection(beforeText.length());
                } else {
                    mEditText.setSelection(beforeSelection);
                }
                mEditText.addTextChangedListener(this);
            }
            if (!TextUtils.isEmpty(format) && !s.toString().matches(format + "*")) {
                mEditText.removeTextChangedListener(this);
                mEditText.setText(beforeText);
                if (beforeSelection >= beforeText.length()) {
                    mEditText.setSelection(beforeText.length());
                } else {
                    mEditText.setSelection(beforeSelection);
                }
                mEditText.addTextChangedListener(this);
            }
        }
    }
}
