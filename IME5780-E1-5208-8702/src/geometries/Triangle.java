package geometries;

import primitives.*;

import java.util.ArrayList;
import java.util.List;

import static primitives.Util.isZero;

/**
 * class Triangle for describe triangle object
 * this class extends from Polygon
 *
 * @author AhronS, IsraelN
 */
public class Triangle extends Polygon {

    //****************************** Constructors *****************************/

    /**
     * Triangle constructor by super
     *
     * @param p1 one point
     * @param p2 one point
     * @param p3 one point
     */
    public Triangle(Point3D p1, Point3D p2, Point3D p3) {
        super(p1, p2, p3);
    }

    /**
     * triangle constructor with color
     *
     * @param emission emission color
     * @param p1        ver of triangle
     * @param p2        ver of triangle
     * @param p3        ver of triangle
     */
    public Triangle(Color emission, Point3D p1, Point3D p2, Point3D p3) {
        this(p1, p2, p3);
        this._emission = emission;
    }

    /**
     * triangle constructor with color and material
     *
     * @param material material
     * @param emission emission color
     * @param p1        ver of triangle
     * @param p2        ver of triangle
     * @param p3        ver of triangle
     */
    public Triangle(Color emission, Material material, Point3D p1, Point3D p2, Point3D p3) {
        this(emission, p1, p2, p3);
        this._material = material;
    }

    // ****************************** Overrides *****************************//

    @Override
    public List<GeoPoint> findIntersections(Ray ray) {
        List<GeoPoint> intersections = _plane.findIntersections(ray);
        if (intersections == null)
            return null;
        Point3D p0 = ray.getP0();
        Vector v = ray.getDir();
        Vector v1 = _vertices.get(0).subtract(p0);
        Vector v2 = _vertices.get(1).subtract(p0); //  0,0,-1
        Vector v3 = _vertices.get(2).subtract(p0); // -1,1,-1

        double s1 = v.dotProduct(v1.crossProduct(v2));
        if (isZero(s1)) return null;
        double s2 = v.dotProduct(v2.crossProduct(v3)); //1,1,0
        if (isZero(s2)) return null;
        double s3 = v.dotProduct(v3.crossProduct(v1));
        if (isZero(s3)) return null;

        if ((s1 > 0 && s2 > 0 && s3 > 0) || (s1 < 0 && s2 < 0 && s3 < 0)) {
            List<GeoPoint> result = new ArrayList<>();
            for (GeoPoint geo : intersections) {
                result.add(new GeoPoint(this, geo.point));
            }
            result.get(0).geometry = this;
            return result;
        }
        return null;
    }
}