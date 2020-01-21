package learn.zhujl.geo.util;

/**
 * 类Point.java的实现描述：地理坐标点
 *
 * @author JianLin.Zhu 2015-10-15 下午3:20:36
 */
public class Point {

    private double lon;
    private double lat;

    public Point() {
    }

    /**
     * 构造函数
     *
     * @param lon 经度
     * @param lat 纬度
     */
    public Point(double lon, double lat) {
        super();
        this.lon = lon;
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Point other = (Point) obj;
        if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat)) {
            return false;
        }
        if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Point [lon=" + lon + ", lat=" + lat + "]";
    }

}
