package ico.ico.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ico.ico.util.Common;

/**
 * 图片上传的帮助类
 * <p>
 * 功能包括显示单选对话框、处理相册和拍照选择照片（需要手动调用handlerResult）、保存选择的所有图片
 * <p>
 * 现在一般直接使用rxgalleryfinal,这个帮助类已经基本属于过时了
 */
@Deprecated
public class UploadPictureHelper {
    public final static int RC_PHOTO = 0x100;
    public final static int RC_CAMERA = 0x200;
    private Activity mActivity;
    private Fragment mFragment;
    //存放图片地址的列表
    private List<String> files = new ArrayList<>();
    //用来临时存放图片
    private File tmpFile;
    //监控图片列表的更改
    private OnFilesChangeListener onFilesChangeListener;
    //用于保存当前操作的是第几个点击按钮，主要用于图片替换
    private int index = -1;
    //是否启用删除功能
    private boolean deleteEnable = true;

    public UploadPictureHelper(Activity activity, OnFilesChangeListener onFilesChangeListener) {
        this.mActivity = activity;
        this.onFilesChangeListener = onFilesChangeListener;
    }

    public UploadPictureHelper(Fragment fragment, OnFilesChangeListener onFilesChangeListener) {
        this.mFragment = fragment;
        this.mActivity = mFragment.getActivity();
        this.onFilesChangeListener = onFilesChangeListener;
    }

    public void setDeleteEnable(boolean enable) {
        deleteEnable = enable;
    }

    public void showPictureDialog(final int index) {
        this.index = index;
        String[] items = new String[]{"相册", "拍照"};
        if (files.size() > index && files.get(index) != null && deleteEnable) {
            items = new String[]{"相册", "拍照", "删除"};
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://相册
                        if (mFragment != null) {
                            mFragment.startActivityForResult(Common.getIntentByPhoto(), RC_PHOTO);
                        } else {
                            mActivity.startActivityForResult(Common.getIntentByPhoto(), RC_PHOTO);
                        }
                        break;
                    case 1://拍照
                        if (!TextUtils.equals(Environment.getExternalStorageState(), Environment.MEDIA_MOUNTED)) {
                            Toast.makeText(mActivity, "请插入SD卡", Toast.LENGTH_LONG).show();
                            return;
                        }
                        //初始化文件保存路径
                        tmpFile = new File(Environment.getExternalStorageDirectory() + "/" + mActivity.getPackageName() + "/" + "IMG_" + System.currentTimeMillis() + ".png");
                        try {
                            if (mFragment != null) {
                                mFragment.startActivityForResult(Common.getIntentByCamera(tmpFile), RC_CAMERA);
                            } else {
                                mActivity.startActivityForResult(Common.getIntentByCamera(tmpFile), RC_CAMERA);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mActivity, "无法创建文件夹，请确认SD卡！", Toast.LENGTH_LONG).show();
                        }
                        break;
                    case 2://删除
                        if (files.get(index) != null) {
                            String str = files.remove(index);
                            onFilesChangeListener.onChanged(files, str, index);
                        }
                        break;
                }
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    /**
     * 处理图片相关的返回结果
     *
     * @param requestCode
     * @param data
     */
    public void handlerResult(int requestCode, Intent data) {
        if (requestCode == RC_PHOTO) {
            tmpFile = new File(Common.handleResultForPhoto(data, mActivity));
        }
        if (tmpFile == null || !tmpFile.exists()) {
            Toast.makeText(mActivity, "未找到图片!", Toast.LENGTH_LONG).show();
            return;
        }
        if (files.contains(tmpFile.getAbsolutePath())) {
            Toast.makeText(mActivity, "您已添加过该图片", Toast.LENGTH_LONG).show();
            return;
        }
        if (index > -1 && index < files.size()) {
            files.remove(index);
            files.add(index, tmpFile.getAbsolutePath());
        } else {
            files.add(tmpFile.getAbsolutePath());
        }
        onFilesChangeListener.onChanged(files, tmpFile.getAbsolutePath(), index);
    }

    /**
     * 添加一个本地文件路径或url
     */
    public void addFile(String pathOrUrl) {
        if (files.contains(pathOrUrl)) {
            return;
        }
        files.add(pathOrUrl);
        onFilesChangeListener.onChanged(files, pathOrUrl, files.size() - 1);
    }

    public List<String> getFiles() {
        return files;
    }

    /**
     * 获取数组形式的文件列表
     *
     * @return
     */
    public File[] getFileArray() {
        if (files.size() == 0) {
            return null;
        }
        List<File> tmpFiles = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            //需要判断是本地文件地址还是url
            String pathOrUrl = files.get(i);
            if (!pathOrUrl.toUpperCase().startsWith("HTTP")) {
                tmpFiles.add(new File(files.get(i)));
            }
        }
        if (tmpFiles.size() == 0) {
            return null;
        }
        File[] _files = new File[tmpFiles.size()];
        tmpFiles.toArray(_files);
        return _files;
    }

    /**
     * 获取数组形式的url列表
     *
     * @return
     */
    public String[] getUrlArray() {
        if (files.size() == 0) {
            return null;
        }
        List<String> tmpFiles = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            //需要判断是本地文件地址还是url
            String pathOrUrl = files.get(i);
            if (pathOrUrl.toUpperCase().startsWith("HTTP")) {
                tmpFiles.add(files.get(i));
            }
        }
        if (tmpFiles.size() == 0) {
            return null;
        }
        String[] _files = new String[tmpFiles.size()];
        tmpFiles.toArray(_files);
        return _files;
    }

    /**
     * 当文件列表改动时
     */
    public interface OnFilesChangeListener {
        void onChanged(List<String> files, String file, int index);
    }

}
