package geometries;

import primitives.Point3D;
import primitives.Ray;

import java.util.List;
import java.util.Objects;

/**
 * interface Intersectable for all intersectable objects
 */
public interface Intersectable {

    default List<GeoPoint> findIntersections(Ray ray) {
        return findIntersections(ray, Double.POSITIVE_INFINITY);
    }

    /**
     * filter shadow not in the other side
     *
     * @param ray ray
     * @param max max of the dist
     * @return the geo point closest
     */
    List<GeoPoint> findIntersections(Ray ray, double max);


    /**
     * static class Geo Point
     */
    public static class GeoPoint {
        public Geometry geometry;
        public Point3D point;

        // ****************************** Constructors *****************************//

        /**
         * GeoPoint constructor
         *
         * @param geometry geometry
         * @param point    point
         */
        public GeoPoint(Geometry geometry, Point3D point) {
            this.geometry = geometry;
            this.point = point;
        }

        // ****************************** Overrides *****************************//

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (!(o instanceof GeoPoint)) return false;
            GeoPoint geoPoint = (GeoPoint) o;
            return geometry == geoPoint.geometry && point.equals(geoPoint.point);
        }

    }
}
