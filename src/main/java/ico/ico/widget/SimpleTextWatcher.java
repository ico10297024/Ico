package ico.ico.widget;

import android.text.Editable;
import android.text.TextWatcher;

import ico.ico.util.log;

/**
 * Created by ICO on 2016/7/11 0011.
 */
public class SimpleTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int delLength, int addLength) {
        log.i(String.format("==s=%s|start=%d|delLength=%d|addLength=%d", s, start, delLength, addLength), SimpleTextWatcher.class.getSimpleName(), "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int delLength, int addLength) {
        log.i(String.format("==s=%s|start=%d|delLength=%d|addLength=%d", s, start, delLength, addLength), SimpleTextWatcher.class.getSimpleName(), "onTextChanged");
    }

    @Override
    public void afterTextChanged(Editable s) {
        log.i("==" + s.toString(), SimpleTextWatcher.class.getSimpleName(), "afterTextChanged");

    }
}
