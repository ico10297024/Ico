package learn.zhujl.geo.util;

import java.util.HashMap;
import java.util.Map;

//import ch.hsr.geohash.GeoHash;

/**
 * 类GeoUtil.java的实现描述：地理位置相关工具
 *
 * @author JianLin.Zhu 2015-8-30 下午2:54:08
 */
public class GeoUtil {

    public final static double R = CoordinateConverter.AXIS;  // 地球的半径
    private final static double PI = CoordinateConverter.PI;   // 圆周率

    /**
     * 获取geohash值
     *
     * @param latitude
     * @param longitude
     * @param numberOfCharacters 需要精确到第几位 1~12
     * @return
     */
//    public static GeoHash getGeoHash(double latitude, double longitude, int numberOfCharacters) {
//        if (latitude < -90 || latitude > 90 || longitude > 180 || longitude < -180) {
//            latitude = 0;
//            longitude = 0;
//        }
//        return GeoHash.withCharacterPrecision(latitude, longitude, numberOfCharacters);
//    }

    /**
     * 坐标之间的距离
     *
     * @param lat1
     * @param lng1
     * @param lat2
     * @param lng2
     * @return 单位米
     */
    public static double getDistance(double lat1, double lng1, double lat2, double lng2) {
        lat1 = Math.toRadians(lat1);
        lng1 = Math.toRadians(lng1);
        lat2 = Math.toRadians(lat2);
        lng2 = Math.toRadians(lng2);
        double d1 = Math.abs(lat1 - lat2);
        double d2 = Math.abs(lng1 - lng2);
        double p = Math.pow(Math.sin(d1 / 2), 2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(d2 / 2), 2);
        double dis = R * 2 * Math.asin(Math.sqrt(p));
        return dis;
    }

    /**
     * 坐标半径raidus米范围的角点坐标
     *
     * @param lat
     * @param lon
     * @param raidus 单位 米
     * @return {minLat:xx,minLng:xx,maxLat:xx,maxLng:xx}
     */
    public static Map<String, Double> getAround(double lat, double lon, int raidus) {

        Double latitude = lat;
        Double longitude = lon;

        Double degree = (24901 * 1609) / 360.0;
        double raidusMile = raidus;

        Double dpmLat = 1 / degree;
        Double radiusLat = dpmLat * raidusMile;
        Double minLat = latitude - radiusLat;
        Double maxLat = latitude + radiusLat;

        Double mpdLng = degree * Math.cos(latitude * (PI / 180));
        Double dpmLng = 1 / mpdLng;
        Double radiusLng = dpmLng * raidusMile;
        Double minLng = longitude - radiusLng;
        Double maxLng = longitude + radiusLng;
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("minLat", minLat);
        map.put("minLng", minLng);
        map.put("maxLat", maxLat);
        map.put("maxLng", maxLng);
        return map;
    }

    // -----------------------------------------------------------------------
    // -------转换坐标 开始-----------------------------------------------------

    /**
     * 从火星坐标系转换为地球坐标系
     *
     * @param marsLat
     * @param marsLon
     * @return
     */
    public static Point convertMars2Earth(double marsLat, double marsLon) {
        double[] p = CoordinateConverter.gcj2WGSExactly(marsLat, marsLon);
        return new Point(p[1], p[0]);
    }

    /**
     * 从火星坐标系转换为地球坐标系
     *
     * @param point
     * @return
     */
    public static Point convertMars2Earth(Point point) {
        return convertMars2Earth(point.getLat(), point.getLon());
    }

    /**
     * 从地球坐标转换为火星坐标,例如:苹果坐标转高德坐标
     *
     * @param earthLat
     * @param earthLon
     * @return
     */
    public static Point convertEarth2Mars(double earthLat, double earthLon) {
        double[] p = CoordinateConverter.wgs2GCJ(earthLat, earthLon);
        return new Point(p[1], p[0]);
    }

    /**
     * 从地球坐标转换为火星坐标,例如:苹果坐标转高德坐标
     *
     * @param p
     * @return
     */
    public static Point convertEarth2Mars(Point p) {
        return convertEarth2Mars(p.getLat(), p.getLon());
    }

    /**
     * 百度坐标转火星坐标
     *
     * @param baiduLat
     * @param baiduLon
     * @return
     */
    public static Point convertBaidu2Mars(double baiduLat, double baiduLon) {
        double[] p = CoordinateConverter.bd092GCJ(baiduLat, baiduLon);
        return new Point(p[1], p[0]);
    }

    /**
     * 百度坐标转火星坐标
     *
     * @param point
     * @return
     */
    public static Point convertBaidu2Mars(Point point) {
        return convertBaidu2Mars(point.getLat(), point.getLon());
    }

    /**
     * 火星坐标转百度坐标
     *
     * @param marsLat
     * @param marsLon
     * @return
     */
    public static Point convertMars2Baidu(double marsLat, double marsLon) {
        double[] p = CoordinateConverter.gcj2BD09(marsLat, marsLon);
        return new Point(p[1], p[0]);
    }

    /**
     * 火星坐标转百度坐标
     *
     * @param point
     * @return
     */
    public static Point convertMars2Baidu(Point point) {
        return convertMars2Baidu(point.getLat(), point.getLon());
    }

    /**
     * 百度坐标转地球坐标
     *
     * @param baiduLat
     * @param baiduLon
     * @return
     */
    public static Point convertBaidu2Earth(double baiduLat, double baiduLon) {
        return convertMars2Earth(convertBaidu2Mars(baiduLat, baiduLon));
    }

    /**
     * 百度坐标转地球坐标
     *
     * @param point 百度坐标
     * @return
     */
    public static Point convertBaidu2Earth(Point point) {
        return convertBaidu2Earth(point.getLat(), point.getLon());
    }

    /**
     * 地球坐标转百度坐标
     *
     * @param earthLat
     * @param earthLon
     * @return
     */
    public static Point convertEarth2Baidu(double earthLat, double earthLon) {
        return convertMars2Baidu(convertEarth2Mars(earthLat, earthLon));
    }

    /**
     * 地球坐标转百度坐标
     *
     * @param point
     * @return
     */
    public static Point convertEarth2Baidu(Point point) {
        return convertEarth2Baidu(point.getLat(), point.getLon());
    }

    /**
     * 图吧坐标转地球坐标
     *
     * @param point
     * @return
     */
    public static Point convertMapbar2Earth(Point point) {
        return convertMapbar2Earth(point.getLat(), point.getLon());
    }

    /**
     * 图吧坐标转地球坐标
     *
     * @param mapbarLat
     * @param mapbarLon
     * @return
     */
    public static Point convertMapbar2Earth(double mapbarLat, double mapbarLon) {
        double[] p = CoordinateConverter.mapBar2WGS84(mapbarLon, mapbarLat);
        return new Point(p[1], p[0]);
    }

    /**
     * 图吧坐标转火星坐标
     *
     * @param point
     * @return
     */
    public static Point convertMapbar2Mars(Point point) {
        return convertMapbar2Mars(point.getLat(), point.getLon());
    }

    /**
     * 图吧坐标转火星坐标
     *
     * @param mapbarLat
     * @param mapbarLon
     * @return
     */
    public static Point convertMapbar2Mars(double mapbarLat, double mapbarLon) {
        return convertEarth2Mars(convertMapbar2Earth(mapbarLat, mapbarLon));
    }

    /**
     * 通用转换接口
     *
     * @param lat
     * @param lon
     * @param from
     * @param to
     * @return
     */
    public static Point convertCoord(double lat, double lon, CoordType from, CoordType to) {
        Point result = new Point(lon, lat);
        switch (from) {
            case BAIDU: {
                switch (to) {
                    case BAIDU:
                        break;
                    case EARTH:
                        result = convertBaidu2Earth(result);
                        break;
                    case MARS:
                        result = convertBaidu2Mars(result);
                        break;
                    case SOGOU:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    case MAPBAR:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    default:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                }
                break;
            }
            case EARTH: {
                switch (to) {
                    case BAIDU:
                        result = convertEarth2Baidu(result);
                        break;
                    case EARTH:
                        break;
                    case MARS:
                        result = convertEarth2Mars(result);
                        break;
                    case SOGOU:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    case MAPBAR:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    default:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                }
                break;
            }
            case MARS: {
                switch (to) {
                    case BAIDU:
                        result = convertMars2Baidu(result);
                        break;
                    case EARTH:
                        result = convertMars2Earth(result);
                        break;
                    case MARS:
                        break;
                    case SOGOU:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    case MAPBAR:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    default:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                }
                break;
            }
            case SOGOU: {
                throw new UnsupportedOperationException("Convert From " + from + " To " + to);
            }
            case MAPBAR: {
                switch (to) {
                    case BAIDU:
                        break;
                    case EARTH:
                        result = convertMapbar2Earth(result);
                        break;
                    case MARS:
                        result = convertMapbar2Mars(result);
                        break;
                    case SOGOU:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                    case MAPBAR:
                        break;
                    default:
                        throw new UnsupportedOperationException("Convert From " + from + " To " + to);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Convert From " + from + " To " + to);
            }
        }
        return result;
    }

    /**
     * 通用转换接口
     *
     * @param point
     * @param from
     * @param to
     * @return
     */
    public static Point convertCoord(Point point, CoordType from, CoordType to) {
        return convertCoord(point.getLat(), point.getLon(), from, to);
    }

    // -----------------------------------------------------------------------
    // -------转换坐标 结束-----------------------------------------------------
}
