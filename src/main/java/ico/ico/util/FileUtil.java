package ico.ico.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    public static final String TAG = "FileUtil";

    public static final String SDCARD = Environment.getExternalStorageDirectory() + File.separator;


    //region 创建文件或文件夹

    /**
     * {@link #createDir(File)}
     */
    public static boolean createDir(String path) {
        return FileUtil.createDir(new File(path));
    }

    /**
     * 创建文件夹
     *
     * @param file 文件对象
     * @return boolean 如果为true，代表已存在或者创建成功，如果为false，代表路径不是一个文件夹，或者调用api返回false
     */
    public static boolean createDir(File file) {
        if (file.exists()) {
            //存在
            if (file.isDirectory()) {
                return true;
            } else {
                if (!FileUtil.deleteFile(file)) {
                    log.e("file存在,是个文件，删除失败，" + file.getAbsolutePath(), TAG);
                    return false;
                }
            }
        }
        //不存在
        if (!file.mkdirs()) {
            log.e("文件夹创建失败," + file.getAbsolutePath(), TAG);
            return false;
        }
        return true;
    }

    /**
     * 创建文件
     *
     * @param filePath 文件绝对路径
     * @return int 0失败，1已存在，2新创建成功
     */
    public static int createFile(String filePath) {
        return FileUtil.createFile(new File(filePath));
    }

    /**
     * 创建文件
     *
     * @param file 文件对象
     * @return int 0失败，1已存在，2新创建成功
     */
    public static int createFile(File file) {
        //不是文件
        if (!FileUtil.isFile(file.getAbsolutePath())) {
            return 0;
        }
        if (file.exists()) {
            if (file.isFile()) {//文件
                return 1;
            } else {//文件夹
                if (!FileUtil.deleteDir(file)) {
                    log.e("file存在，不是文件，" + file.getAbsolutePath(), TAG);
                    return 0;
                }
            }
        }

        //父文件夹
        if (!file.getParentFile().exists()) {
            if (!FileUtil.createDir(file.getParentFile())) {
                return 0;
            }
        }
        try {
            if (file.createNewFile()) {
                return 2;
            } else {
                log.e("文件创建失败，" + file.getAbsolutePath(), TAG);
                return 0;
            }
        } catch (Exception e) {
//            e.printStackTrace();
            log.ew("文件创建异常，" + file.getAbsolutePath(), e, TAG);
            return 0;
        }
    }

    /**
     * 创建文件,如果文件以存在，则重命名再创建
     *
     * @param file 文件对象
     * @return int 0失败，1已存在，2新创建成功
     */
    public static File createFileOrRename(File file) {
        //不是文件
        if (!FileUtil.isFile(file.getAbsolutePath())) {
            return null;
        }
        //检查文件夹
        if (!file.getParentFile().exists() && !FileUtil.createDir(file.getParentFile())) {
            return null;
        }
        File finalFile = null;
        if (file.exists()) {
            /* 重命名 */
            //文件名
            String _fileName = getFileName(file.getAbsolutePath());
            //通过split拆分，插入占位符
            String[] tmps = _fileName.split("\\.");
            tmps[0] += "(%i)";
            //格式化字符串
            String formatName = concat(".", tmps);
            String tmpName;
            File tmpFile;
            for (int i = 0; i < 999; i++) {
                tmpName = String.format(formatName, i);
                tmpFile = new File(file.getParent(), tmpName);
                if (!tmpFile.exists()) {
                    finalFile = tmpFile;
                    break;
                }
            }
        } else {
            finalFile = file;
        }
        if (createFile(finalFile) == 0) {
            return null;
        } else {
            return finalFile;
        }
    }
    //endregion

    //region 删除文件或文件夹

    /** {@link #delete(File)} */
    public static boolean delete(String filePath) {
        return FileUtil.delete(new File(filePath));
    }

    /**
     * 删除指定路径文件
     *
     * @param file 文件对象
     */
    public static boolean delete(File file) {
        if (file.exists() && !file.delete()) {
            log.e("删除一个文件或文件夹失败," + file.getAbsolutePath(), TAG);
            return false;
        }
        return true;
    }

    /** {@link #deleteFile(File)} */
    public static boolean deleteFile(String filePath) {
        return FileUtil.deleteFile(new File(filePath));
    }

    /**
     * 删除指定路径文件
     *
     * @param file 文件对象
     */
    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        }
        if (!file.isFile()) {
            log.e("file存在，不是文件，" + file.getAbsolutePath(), TAG);
            return false;
        }
        if (!file.delete()) {
            log.e("文件无法删除," + file.getAbsolutePath(), TAG);
            return false;
        }
        return true;
    }

    /** {@link FileUtil#deleteFiles(File, boolean)} */
    public static boolean deleteFiles(String dirPath, boolean recursion) {
        return deleteFiles(new File(dirPath), recursion);
    }

    /**
     * 删除指定目录下的所有文件
     *
     * @param file      目录对象
     * @param recursion 是否递归
     */
    public static boolean deleteFiles(File file, boolean recursion) {
        if (!file.exists()) {
            return true;
        }
        if (!file.isDirectory()) {
            log.e("file存在，不是文件夹，" + file.getAbsolutePath(), TAG);
            return true;
        }
        //获取子文件
        File[] files = file.listFiles();
        //没有子文件
        if (files == null || files.length == 0) {
            return true;
        }
        //遍历子文件
        for (int i = 0; i < files.length; i++) {
            if (!files[i].exists()) {
                continue;
            }
            if (files[i].isFile()) {//文件
                if (!files[i].delete()) {
                    log.e("删除指定目录下的所有文件时，个别文件删除失败，" + file.getAbsolutePath(), TAG);
                    return false;
                }
            } else {//文件夹
                //递归
                if (recursion) {
                    // 文件为目录的情况，需要进行递归删除
                    if (!deleteDir(files[i])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /** {@link FileUtil#deleteDir(File)} */
    public static boolean deleteDir(String dirPath) {
        return FileUtil.deleteDir(new File(dirPath));
    }

    /**
     * 删除指定文件夹
     *
     * @param file 文件夹路径
     * @return
     */
    public static boolean deleteDir(File file) {
        if (!file.exists() || !file.isDirectory()) {
            return true;
        }
        //删除文件夹内的所有文件和子文件夹
        if (!FileUtil.deleteFiles(file, true)) {
            return false;
        }
        //删除目录
        return FileUtil.delete(file);
    }
    //endregion

    //region 构造文件路径

    /**
     * 将一个字符串数组以当前系统的文件分隔符进行拼接，返回拼接后的文件路径
     *
     * @param texts 要被拼接的字符串数组
     * @return String 返回的是一个文件路径
     */
    public static String genFilePath(String... texts) {
        StringBuilder sb = new StringBuilder(File.separator);
        for (int i = 0; i < texts.length; i++) {
            String tmp = texts[i];
            sb.append(tmp);
            if (i < texts.length - 1 && !TextUtils.equals(sb.substring(sb.length() - 1), File.separator)) {
                sb.append(File.separator);
            }
        }
        return sb.toString();
    }

    /**
     * 将一个字符串数组以当前系统的文件分隔符进行拼接，返回拼接后的文件夹路径
     *
     * @param texts 要被拼接的字符串数组
     * @return String 返回的是一个文件夹路径
     */
    public static String genDirPath(String... texts) {
        StringBuilder sb = new StringBuilder();
        if (!texts[0].startsWith(File.separator)) {
            sb.append(File.separator);
        }
        for (int i = 0; i < texts.length; i++) {
            String tmp = texts[i];
            sb.append(tmp);
            if (!TextUtils.equals(sb.substring(sb.length() - 1), File.separator)) {
                sb.append(File.separator);
            }
        }
        return sb.toString();
    }

    /**
     * 将一个字符串数组以当前系统的文件分隔符进行拼接，返回拼接后文件的File对象
     *
     * @param texts 要被拼接的字符串数组
     * @return File 文件对象，这是一个文件
     */
    public static File genFile(String... texts) {
        return new File(FileUtil.genFilePath(texts));
    }

    /**
     * 将一个字符串数组以当前系统的文件分隔符进行拼接，返回拼接后文件夹的File对象
     *
     * @param texts 要被拼接的字符串数组
     * @return File 文件对象，这是一个文件夹
     */
    public static File genDir(String... texts) {
        return new File(FileUtil.genDirPath(texts));
    }
    //endregion

    //region 在sd卡内创建文件或文件夹

    /**
     * 在SD卡上创建文件
     *
     * @throws IOException
     */
    public File creatSDFile(String fileName) {
        if (FileUtil.createFile(SDCARD + fileName) != 0) {
            return new File(SDCARD + fileName);
        }
        return null;
    }

    /**
     * 在SD卡上创建目录
     *
     * @param dirName
     */
    public File creatSDDir(String dirName) {
        File dir = new File(SDCARD + dirName);
        if (!FileUtil.createDir(dir)) {
            return null;
        }
        return dir;
    }
    //endregion

    //region 判断文件

    /**
     * 判断路径是否为一个文件
     *
     * @param filePath
     * @return
     */
    public static boolean isFile(String filePath) {
        return !filePath.endsWith(File.separator);

    }

    /**
     * 判断路径是否为一个文件夹
     *
     * @param filePath
     * @return
     */
    public static boolean isDirectory(String filePath) {
        return filePath.endsWith(File.separator);
    }

    /** 判断文件是否存在 */
    public static boolean isFileExist(String filePath) {
        return isFileExist(new File(filePath));
    }

    /** 判断文件是否存在 */
    public static boolean isFileExist(File file) {
        return file.exists() && file.isFile();
    }


    /** 判断文件夹是否存在 */
    public static boolean isDirExist(String filePath) {
        return isFileExist(new File(filePath));
    }

    /** 判断文件夹是否存在 */
    public static boolean isDirExist(File file) {
        return file.exists() && file.isDirectory();
    }
    //endregion

    //region 设置文件信息
    public static boolean setLastModified(String filePath, long time) {
        return FileUtil.setLastModified(new File(filePath), time);
    }

    public static boolean setLastModified(File file, long time) {
        if (!file.setLastModified(time)) {
            log.e(String.format("设置文件修改时间失败，filePath：%s", file.getAbsolutePath()), TAG);
            return false;
        }
        return true;
    }
    //endregion

    //region 重命名

    /**
     * 文件重命名
     *
     * @param oldPath 旧文件的绝对路径
     * @param newPath 新文件的绝对路径
     * @return
     */
    public static boolean renameTo(String oldPath, String newPath) {
        return FileUtil.renameTo(new File(oldPath), new File(newPath));
    }

    /**
     * 文件重命名
     *
     * @param oldFile 旧文件
     * @param newFile 新文件
     * @return
     */
    public static boolean renameTo(File oldFile, File newFile) {
        if (!oldFile.exists()) {
            log.e(String.format("文件重命名失败，旧文件不存在，旧文件：%s，新文件：%s", oldFile.getAbsolutePath(), newFile.getAbsolutePath()), TAG);
            return false;
        }
        boolean renameFlg = oldFile.renameTo(newFile);
        if (!renameFlg) {
            log.e(String.format("文件重命名失败，旧文件：%s，新文件：%s", oldFile.getAbsolutePath(), newFile.getAbsolutePath()), TAG);
        }
        return renameFlg;
    }
    //endregion

    //region 获取绝对路径中各部分的数据，包括文件名，后缀名，文件全名，存储文件夹

    /** 获取文件的文件名 */
    public static String getFileName(String filaPath) {
        return filaPath.substring(filaPath.lastIndexOf(File.separator) + 1);
    }

    /** 获取文件所在文件夹 */
    public static String getPathName(String filePath) {
        return filePath.substring(0, filePath.lastIndexOf(File.separator));
    }

    /** 获取文件后缀,不包含. */
    public static String getSuffix(String filePath) {
        if (filePath.lastIndexOf(".") == -1) {
            return "";
        }
        String suffix = "";
        suffix = filePath.substring(filePath.lastIndexOf(".") + 1);
        return suffix;
    }
    //endregion

    //region 获取系统文件路径和系统缓存路径

    /**
     * 获取系统缓存路径<br>
     * 当SD卡存在或者SD卡不可被移除时，获取SD卡路径；否则获取内存路径
     *
     * @param context 上下文
     * @return 缓存路径
     */
    @SuppressLint("NewApi")
    public static String getDiskCachePath(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getAbsolutePath();
        } else {
            cachePath = context.getCacheDir().getAbsolutePath();
        }
        return cachePath;
    }

    /**
     * 获取系统文件路径<br>
     * 当SD卡存在或者SD卡不可被移除时，获取SD卡路径；否则获取内存路径
     *
     * @param context 上下文
     * @return 缓存路径
     */
    public static String getDiskFilePath(Context context) {
        String filePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            filePath = context.getExternalFilesDir(null).getAbsolutePath();
        } else {
            filePath = context.getFilesDir().getAbsolutePath();
        }
        return filePath;
    }
    //endregion

    //region 获取子文件和子目录

    /**
     * 获取指定目录下的所有子目录
     *
     * @param dir
     * @return
     */
    public static List<File> getSubdir(File dir) {
        FileFilter ff = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        return Arrays.asList(dir.listFiles(ff));
    }

    /**
     * 获取后缀名符合正则表达式的文件,匹配将转化为小写再进行匹配
     *
     * @param dir   要查找的目录
     * @param regex 要匹配的正则表达式
     * @param flag  是否匹配子目录下的文件
     * @return List<File>
     */
    public static List<String> getFiles(File dir, String regex, boolean flag) {
        List<String> files = new ArrayList<String>();
        File[] subFiles = dir.listFiles();
        if (subFiles == null || subFiles.length == 0) {
            return null;
        }
        for (int i = 0; i < subFiles.length; i++) {
            if (subFiles[i].isFile()) {//文件
                if (TextUtils.isEmpty(regex) || FileUtil.getSuffix(subFiles[i].getAbsolutePath()).toLowerCase().matches(regex)) {
                    files.add(subFiles[i].getAbsolutePath());
                }
            } else if (subFiles[i].isDirectory() && flag) {//目录
                List<String> _files = FileUtil.getFiles(subFiles[i], regex, flag);
                files.addAll(_files);
            }
        }
        return files;
    }

    /**
     * 获取后缀名符合正则表达式的文件,匹配将转化为小写再进行匹配
     *
     * @param dirs  要查找的目录
     * @param regex 要匹配的正则表达式
     * @param flag  是否匹配子目录下的文件
     * @return List<File>
     */
    public static List<String> getFiles(List<File> dirs, String regex, boolean flag) {
        if (dirs == null || dirs.size() == 0) {
            return null;
        }
        List<String> files = new ArrayList<String>();
        for (int i = 0; i < dirs.size(); i++) {
            files.addAll(FileUtil.getFiles(dirs.get(i), regex, flag));
        }
        return files;
    }
    //endregion

    //region 拷贝和移动

    /** 拷贝文件 */
    public static boolean copyFile(File file, File newFile) {
        //旧文件不存在，或者不是文件
        if (!file.exists() || !file.isFile()) {
            return false;
        }
        //创建新文件所在文件夹
        if (!FileUtil.createDir(newFile.getParentFile())) {
            return false;
        }
        //创建新文件
        if (FileUtil.createFile(newFile) == 0) {
            return false;
        }

        //创建输入输出流
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(file);
            fos = new FileOutputStream(newFile);
            while (true) {
                byte[] buffer = new byte[1024 * 1024 * 4];
                int len = fis.read(buffer);
                if (len == -1) {
                    break;
                }
                fos.write(buffer, 0, len);
            }
            fos.flush();
            return true;
        } catch (IOException e) {
            log.ew(e.toString(), TAG);
        } finally {
            SafeCloseUtil.close(fos);
            SafeCloseUtil.close(fis);
        }
        return false;
    }

    /** 移动文件 */
    public static boolean moveFile(File file, File newFile) {
        if (!copyFile(file, newFile)) {
            return false;
        }
        if (!deleteFile(file)) {
            return false;
        }
        return true;
    }

    /**
     * 将asset中的文件拷贝至app的本地存储目录中
     *
     * @param context  本地存储目录
     * @param fileName 文件名
     * @throws IOException
     */
    public static void coptAFile(Context context, String fileName) throws IOException {
//        String addstr = "/data/data/" + c.getPackageName() + File.separator;
        String path = genFilePath(context.getFilesDir().getAbsolutePath(), fileName);
        if (isFileExist(path)) {
            return;
        }
        AssetManager am = context.getAssets();
        InputStream is = null;
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        try {
            is = am.open(fileName);
            fos = new FileOutputStream(path);
            bos = new BufferedOutputStream(fos);

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
            bos.flush();
        } catch (IOException e) {
            log.e(e.getMessage(), e, TAG);
            throw e;
        } finally {
            SafeCloseUtil.close(bos);
            SafeCloseUtil.close(fos);
            SafeCloseUtil.close(is);
            SafeCloseUtil.close(am);
        }
    }
    //endregion

    //region 读写操作

    /** 向指定文件中写入字节数组 */
    public static boolean writeFile(File file, byte[] buffer, boolean append) {
        if (createFile(file) == 0) {
            return false;
        }
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file, append);
            bos = new BufferedOutputStream(fos);
            bos.write(buffer);
            bos.flush();
            return true;
        } catch (Exception e) {
            log.ew(e.toString(), TAG);
        } finally {
            SafeCloseUtil.close(bos);
            SafeCloseUtil.close(fos);
        }
        return false;
    }


    /** 读取文件 */
    public static byte[] readFile(File file) {
        BufferedInputStream bis = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            bis = new BufferedInputStream(fis);
            return readInputStream(bis);
        } catch (Exception e) {
            log.ew(e.toString(), TAG);
        } finally {
            SafeCloseUtil.close(bis);
            SafeCloseUtil.close(fis);
        }
        return null;
    }

    /** 读取流 */
    public static byte[] readInputStream(BufferedInputStream bis) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            while (true) {
                byte[] buffer = new byte[1024 * 4 * 4];
                int len = bis.read(buffer);
                if (len == -1) {
                    break;
                }
                byteArrayOutputStream.write(buffer, 0, len);
            }
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            log.ew(e.toString(), TAG);
        } finally {
            SafeCloseUtil.close(bis);
            SafeCloseUtil.close(byteArrayOutputStream);
        }
        return null;
    }

    /**
     * 读取assets中的文件文件,返回字节数组
     *
     * @param assetManager asset管理器
     * @param filename     文件名称
     * @return
     * @throws IOException
     */
    public static byte[] readFileAssets(AssetManager assetManager, String filename) {
        ByteArrayOutputStream baos = null;
        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            baos = new ByteArrayOutputStream();
            is = assetManager.open(filename);
            bis = new BufferedInputStream(is);
            while (true) {
                byte[] buffer = new byte[1024 * 4 * 4];
                int len = bis.read(buffer);
                if (len == -1) {
                    break;
                }
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            log.ew(e.toString(), TAG);
        } finally {
            SafeCloseUtil.close(bis);
            SafeCloseUtil.close(is);
            SafeCloseUtil.close(baos);
        }
        return null;
    }
    //endregion

    //region 函数分支，为了解耦合从其他工具类中拷贝而来

    /**
     * 将一个字符串数组根据某个字符串连接
     *
     * @param str   要插入的字符串
     * @param texts 要被拼接的字符串数组,如果传入null或者空数组，则将返回空字符串
     * @return
     */
    public static String concat(String str, String... texts) {
        if (texts == null || texts.length == 0) return "";
        if (texts.length == 1) return texts[0];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < texts.length; i++) {
            String tmp = texts[i];
            sb.append(tmp);
            if (i < texts.length - 1) {
                sb.append(str);
            }
        }
        return sb.toString();
    }
    //endregion


    //region 文件安全

    /**
     * 获取单个文件的MD5值！
     *
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 获取文件夹中文件的MD5值
     *
     * @param file
     * @param listChild ;true递归子目录中的文件
     * @return
     */
    public static Map<String, String> getDirMD5(File file, boolean listChild) {
        if (!file.isDirectory()) {
            return null;
        }
        Map<String, String> map = new HashMap<String, String>();
        String md5;
        File files[] = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory() && listChild) {
                map.putAll(getDirMD5(f, listChild));
            } else {
                md5 = getFileMD5(f);
                if (md5 != null) {
                    map.put(f.getPath(), md5);
                }
            }
        }
        return map;
    }
    //endregion

    //region 计算文件大小

    /**
     * 获取文件大小
     * Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
     * Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
     */
    public static long getFolderSize(File file) {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }

    /**
     * 获取文件大小
     * Context.getExternalFilesDir() --> SDCard/Android/data/你的应用的包名/files/ 目录，一般放一些长时间保存的数据
     * Context.getExternalCacheDir() --> SDCard/Android/data/你的应用包名/cache/目录，一般存放临时缓存数据
     */
    public static long getFolderSize(File... files) {
        long size = 0;
        for (int i = 0; i < files.length; i++) {
            try {
                size += getFolderSize(files[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size;
    }
    //endregion
}
