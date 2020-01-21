package ico.ico.widget;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;

import ico.ico.ico.BaseDialogFrag;
import ico.ico.ico.R;
import ico.ico.util.Common;

/**
 * 用于货源发布里的DialogFrag
 *
 * @author ICO
 */
public abstract class BottomDialogFrag extends BaseDialogFrag {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.Theme_Dialog_None);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setLayout(Common.getScreenWidth(getActivity()), getDialog().getWindow().getAttributes().height);
        getDialog().getWindow().setGravity(Gravity.BOTTOM);
    }
}
