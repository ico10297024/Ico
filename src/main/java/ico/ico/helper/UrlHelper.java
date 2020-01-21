package ico.ico.helper;

import java.util.HashMap;

public class UrlHelper {

    /**
     * 解析url中的数据部分
     *
     * @param url
     * @return
     */
    public static HashMap<String, String> getQuerys(String url) {
        String query = url.substring(url.lastIndexOf("?") + 1);
        String[] params = query.split("&");
        if (params == null || params.length == 0) {
            return null;
        }
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < params.length; i++) {
            String[] tmp = params[i].split("=");
            if (tmp.length > 1) {
                map.put(tmp[0], tmp[1]);
            }
        }
        return map;
    }
}
