package geometries;

import primitives.Point3D;
import primitives.Ray;

import java.util.List;
import java.util.Objects;

/**
 * interface Intersectable for all intersectable objects
 */
public interface Intersectable {
    List<GeoPoint> findIntersections(Ray ray);

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
            return Objects.equals(geometry, geoPoint.geometry) &&
                    Objects.equals(point, geoPoint.point);
        }

    }
}
